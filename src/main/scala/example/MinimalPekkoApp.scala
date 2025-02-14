package example

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.{ Directives, Route, RouteConcatenation }
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import scala.concurrent.{ ExecutionContext, Future }
import spray.json.RootJsonFormat

object MinimalPekkoApp extends App with RouteConcatenation with Directives {
  // Domain models
  case class Item(id: String, name: String)
  case class SubItem(id: String, name: String, itemId: String)
  case class DetailedItem(id: String, details: String)

  // JSON formatting
  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val itemFormat: RootJsonFormat[Item]                 = jsonFormat2(Item)
    implicit val subItemFormat: RootJsonFormat[SubItem]           = jsonFormat3(SubItem)
    implicit val detailedItemFormat: RootJsonFormat[DetailedItem] = jsonFormat2(DetailedItem)
  }

  // Mock service
  class DataService {
    def getItems(): List[Item]                            = List(Item("1", "First"), Item("2", "Second"))
    def getSubItems(): List[SubItem]                      = List(SubItem("1", "Sub First", "1"))
    def getDetailedItem(id: String): Option[DetailedItem] = Some(DetailedItem(id, "Details"))
  }

  class ItemEndpointsV1(service: DataService)(implicit ec: ExecutionContext) extends Directives with JsonSupport {
    private class GetItemsEndpoint(service: DataService)(implicit ec: ExecutionContext)
        extends Directives
        with JsonSupport {
      val route: Route =
        path("api" / "v1" / "items") {
          get {
            complete(service.getItems())
          }
        }
    }

    private class GetItemByIdEndpoint(service: DataService)(implicit ec: ExecutionContext)
        extends Directives
        with JsonSupport {
      val route: Route =
        path("api" / "v1" / "items" / Segment) { id =>
          get {
            onSuccess(Future.successful(service.getDetailedItem(id))) {
              case Some(item) => complete(item)
              case None       => complete(StatusCodes.NotFound -> "Not found")
            }
          }
        }
    }

    val routes: Route =
      new GetItemsEndpoint(service).route ~
        new GetItemByIdEndpoint(service).route
  }

  class SubItemEndpointsV1(service: DataService)(implicit ec: ExecutionContext) extends Directives with JsonSupport {
    private class GetSubItemsEndpoint(service: DataService)(implicit ec: ExecutionContext)
        extends Directives
        with JsonSupport {
      val route: Route =
        path("api" / "v1" / "subitems") {
          get {
            complete(service.getSubItems())
          }
        }
    }

    private class GetSubItemByIdEndpoint(service: DataService)(implicit ec: ExecutionContext)
        extends Directives
        with JsonSupport {
      val route: Route =
        path("api" / "v1" / "subitems" / Segment) { id =>
          get {
            onSuccess(
              Future.successful(
                service
                  .getSubItems()
                  .find(_.id == id)
                  .toRight("SubItem not found")
                  .map(Right(_))
                  .getOrElse(Left("SubItem not found"))
              )
            ) {
              case Right(item) => complete(item)
              case Left(error) => complete(StatusCodes.NotFound -> error)
            }
          }
        }
    }

    val routes: Route =
      new GetSubItemsEndpoint(service).route ~
        new GetSubItemByIdEndpoint(service).route
  }

  // Main app setup
  implicit val system: ActorSystem  = ActorSystem("minimal-pekko-app")
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
