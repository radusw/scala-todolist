package eu.radusw.api

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import eu.radusw.util.{PaginationResult, WithDatabase}
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import eu.radusw.models.{Todo, TodoId}
import eu.radusw.services.interpreters.repositories.TodoRepository
import eu.radusw.resources.TodoResource
import eu.radusw.services.ComposeThemService
import eu.radusw.services.interpreters.AnotherServiceInterpreter
import monix.eval.Task

class TodoSpec extends WordSpec with Matchers with WithDatabase with ScalatestRouteTest {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import eu.radusw.util.JsonHelper._
  import io.circe.generic.auto._

  val todoService = new TodoRepository()(xa)
  val composeThemService = new ComposeThemService[Task](todoService, new AnotherServiceInterpreter)
  val resource = new TodoResource(todoService, composeThemService)(scheduler)
  val route = resource.route()

  var id: TodoId = _

  "POST /api/todos" should {
    "add a new item" in {
      val json =
        """
          |{
          |	"name": "radu",
          |	"text": "kewl"
          |}
        """.stripMargin
      val entity = HttpEntity(MediaTypes.`application/json`, json)
      Post("/todos", entity) ~> route ~> check {
        val result = responseAs[Todo]
        result.name shouldBe "radu"
        result.text shouldBe "kewl"
        id = result.id
      }
    }
  }

  "GET /api/todos" should {
    "return a list of elements" in {
      Get("/todos") ~> route ~> check {
        val result = responseAs[PaginationResult[Todo]]
        result.items.map(i => i.name -> i.text).contains("radu" -> "kewl") shouldBe true
      }
    }
  }

  "GET /api/todos/{id}" should {
    "return the added item" in {
      Get(s"/todos/${id.value}") ~> route ~> check {
        val result = responseAs[Todo]
        result.id shouldBe id
        result.name shouldBe "radu"
        result.text shouldBe "kewl"
      }
    }
  }

  "PUT /api/todos/{id}" should {
    "update the added item" in {
      val json =
        """
          |{
          |	"text": "patched"
          |}
        """.stripMargin
      val entity = HttpEntity(MediaTypes.`application/json`, json)
      Put(s"/todos/${id.value}", entity) ~> route ~> check {
        val result = responseAs[Todo]
        result.id shouldBe id
        result.name shouldBe "radu"
        result.text shouldBe "patched"
      }
    }
  }
}
