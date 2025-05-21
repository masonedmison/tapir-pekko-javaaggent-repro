package example

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.{ Route, RouteConcatenation }
import org.apache.pekko.http.scaladsl.model.{ HttpMethods, Uri }
import org.apache.pekko.http.scaladsl.server.{ RequestContext, RouteResult }
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter
import io.circe.generic.auto._
import scala.concurrent.{ ExecutionContext, Future }
import io.opentelemetry.api.GlobalOpenTelemetry

object MinimalTapirApp extends App with RouteConcatenation {
  // Future tracing utils
  def traceFuture[A](name: String)(fut: => Future[A])(implicit
      ec: ExecutionContext
  ): Future[A] =
    for {
      (sp, scope) <- Future {
                       val sp =
                         GlobalOpenTelemetry
                           .get()
                           .getTracer("minimal-tapir-app")
                           .spanBuilder(name)
                           .startSpan()
                       sp -> sp.makeCurrent()
                     }
      result      <- fut.andThen { _ =>
                       sp.end
                       scope.close
                     }
    } yield result

  // Domain models
  case class Item(id: String, name: String)

  // Mock service
  class DataService {
    def getItems()(implicit ec: ExecutionContext): Future[List[Item]] =
      Future {
        List(Item("1", "First"), Item("2", "Second"))
      }

    def getItemsTraced()(implicit ec: ExecutionContext): Future[List[Item]] =
      traceFuture("INNERSPAN") {
        Future {
          List(Item("1", "First"), Item("2", "Second"))
        }
      }
  }

  class ItemEndpointsV1(service: DataService)(implicit ec: ExecutionContext) {

    private class GetItemsEndpoint(service: DataService)(implicit ec: ExecutionContext) {
      private val _endpoint = endpoint.get
        .in("api" / "v1" / "items")
        .out(jsonBody[List[Item]])
        .errorOut(stringBody)

      val route: Route = PekkoHttpServerInterpreter().toRoute(
        _endpoint.serverLogic[Future](_ => service.getItemsTraced.map(Right.apply))
      )
    }

    val routes: Route =
      new GetItemsEndpoint(service).route
  }

  implicit val system: ActorSystem  = ActorSystem("minimal-tapir-app")
  implicit val ec: ExecutionContext = system.dispatcher

  val service = new DataService()

  val routes: Route = new ItemEndpointsV1(service).routes

  // Start the server
  val bindingFuture = Http()
    .newServerAt("localhost", 8080)
    .bind(routes)

  println("Server running at http://localhost:8080/")
}
