package eu.radusw.resources

import java.time.ZonedDateTime

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import eu.radusw.models.{Todo, TodoId}
import eu.radusw.services.TodoService
import eu.radusw.util.Pagination
import eu.radusw.util.JsonHelper._
import io.circe.generic.auto._
import monix.eval.Task
import monix.execution.Scheduler

class TodoResource(service: TodoService[Task])(implicit scheduler: Scheduler) {

  def route(): Route = pathPrefix("todos") {
    pathEndOrSingleSlash {
      post {
        entity(as[(TodoId, ZonedDateTime) => Todo]) { partialTodo =>
          complete(service.create(partialTodo).runAsync)
        }
      } ~
        get {
          parameters(
            (Pagination.page.as[Int] ? 1, Pagination.perPage.as[Int] ? 10)) {
            (page, perPage) =>
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
        }
      }
  }
}
