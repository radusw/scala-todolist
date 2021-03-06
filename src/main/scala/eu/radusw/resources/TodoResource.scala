package eu.radusw.resources

import java.time.Instant

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import eu.radusw.models.{Todo, TodoId}
import eu.radusw.services.{ComposeThemService, TodoService}
import eu.radusw.util.Pagination
import eu.radusw.util.Pagination._
import eu.radusw.util.JsonHelper._
import io.circe.generic.auto._
import monix.eval.Task
import monix.execution.Scheduler

class TodoResource(
    service: TodoService[Task],
    composeService: ComposeThemService[Task]
)(implicit scheduler: Scheduler) {

  def route(): Route = pathPrefix("todos") {
    pathEndOrSingleSlash {
      post {
        entity(as[(TodoId, Instant) => Todo]) { partialTodo =>
          complete(service.create(partialTodo).runAsync)
        }
      } ~
        get {
          parameters((page.as[Int] ? 1, perPage.as[Int] ? 10)) { (page, perPage) =>
            complete(service.findAll(Pagination(page, perPage)).runAsync)
          }
        }
    } ~
      pathPrefix(JavaUUID) { uuid =>
        val todoId = TodoId(uuid)
        pathEndOrSingleSlash {
          rejectEmptyResponse {
            get {
              complete(service.find(todoId).runAsync)
            } ~
              put {
                entity(as[Todo => Todo]) { todoPatch =>
                  complete(service.update(todoId, todoPatch).runAsync)
                }
              } ~
              delete {
                complete(service.delete(todoId).runAsync)
              }
          }
        } ~
          pathPrefix("compose") {
            pathEndOrSingleSlash {
              get {
                parameter("value".as[String] ? "composed") { value =>
                  complete(composeService.composedOp(todoId, value).runAsync)
                }
              }
            }
          }
      }
  }
}
