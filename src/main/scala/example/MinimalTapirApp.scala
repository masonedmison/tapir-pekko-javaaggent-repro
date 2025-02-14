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

object MinimalTapirApp extends App with RouteConcatenation {
  // Domain models
  case class Item(id: String, name: String)
  case class SubItem(id: String, name: String, itemId: String)
  case class DetailedItem(id: String, details: String)

  // Mock service
  class DataService {
    def getItems(): List[Item]                            = List(Item("1", "First"), Item("2", "Second"))
    def getSubItems(): List[SubItem]                      = List(SubItem("1", "Sub First", "1"))
    def getDetailedItem(id: String): Option[DetailedItem] = Some(DetailedItem(id, "Details"))
  }

  class ItemEndpointsV1(service: DataService)(implicit ec: ExecutionContext) {

    private class GetItemsEndpoint(service: DataService)(implicit ec: ExecutionContext) {
      private val _endpoint = endpoint.get
        .in("api" / "v1" / "items")
        .out(jsonBody[List[Item]])
        .errorOut(stringBody)

      val route: Route = PekkoHttpServerInterpreter().toRoute(
        _endpoint.serverLogicPure[Future](_ => Right(service.getItems()))
      )
    }

    private class GetItemByIdEndpoint(service: DataService)(implicit ec: ExecutionContext) {
      private val _endpoint = endpoint.get
        .in("api" / "v1" / "items" / path[String]("id"))
        .out(jsonBody[DetailedItem])
        .errorOut(stringBody)

      val route: Route = PekkoHttpServerInterpreter().toRoute(
        _endpoint.serverLogic(id =>
          Future.successful(
            service.getDetailedItem(id).toRight("Not found")
          )
        )
      )
    }

    val routes: Route =
      new GetItemsEndpoint(service).route ~
        new GetItemByIdEndpoint(service).route
  }

  class SubItemEndpointsV1(service: DataService)(implicit ec: ExecutionContext) {
    private class GetSubItemsEndpoint(service: DataService)(implicit ec: ExecutionContext) {
      private val _endpoint = endpoint.get
        .in("api" / "v1" / "subitems")
        .out(jsonBody[List[SubItem]])
        .errorOut(stringBody)

      val route: Route = PekkoHttpServerInterpreter().toRoute(
        _endpoint.serverLogicPure[Future](_ => Right(service.getSubItems()))
      )
    }

    private class GetSubItemByIdEndpoint(service: DataService)(implicit ec: ExecutionContext) {
      private val endpoint: PublicEndpoint[String, String, SubItem, Any] = sttp.tapir.endpoint.get
        .in("api" / "v1" / "subitems" / path[String]("id"))
        .out(jsonBody[SubItem])
        .errorOut(stringBody)

      val route: Route = PekkoHttpServerInterpreter().toRoute(
        endpoint.serverLogic(id =>
          Future.successful(
            service
              .getSubItems()
              .find(_.id == id)
              .toRight("SubItem not found")
          )
        )
      )
    }

    val routes: Route =
      new GetSubItemsEndpoint(service).route ~
        new GetSubItemByIdEndpoint(service).route
  }

  implicit val system: ActorSystem  = ActorSystem("minimal-tapir-app")
  implicit val ec: ExecutionContext = system.dispatcher

  val service = new DataService()

  val routes: Route = new ItemEndpointsV1(service).routes ~
    new SubItemEndpointsV1(service).routes

  // Start the server
  val bindingFuture = Http()
    .newServerAt("localhost", 8080)
    .bind(routes)

  println("Server running at http://localhost:8080/")
}
