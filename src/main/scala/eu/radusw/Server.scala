package eu.radusw

import akka.http.javadsl.model.headers.CacheControl
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.CacheDirectives
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import doobie.hikari.HikariTransactor
import eu.radusw.repositories.TodoRepository
import eu.radusw.resources.TodoResource
import monix.eval.Task
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.Scheduler
import org.flywaydb.core.Flyway

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

object Server extends App with StrictLogging {

  Try(args(0)).foreach(System.setProperty("config.file", _))

  // Configuration
  val config: Config = ConfigFactory.load()

  val dbProps = config.getConfig("db")
  val dbDriver = dbProps.getString("driver")
  val dbUrl = dbProps.getString("url")
  val dbUser = dbProps.getString("user")
  val dbPass = dbProps.getString("password")

  val interface = config.getString("http.interface")
  val port = config.getInt("http.port")

  // Db migrations
  val flyway = new Flyway()
  flyway.setDataSource(dbUrl, dbUser, dbPass)
  logger.info(s"Running any migrations...")
  flyway.migrate()

  // Blocking operation scheduler
  implicit val blockingOpsScheduler =
    Scheduler(Contexts.blockingOpsDispatcher, AlwaysAsyncExecution)

  object Repositories {
    implicit val xa = Await.result(
      HikariTransactor
        .newHikariTransactor[Task](dbDriver, dbUrl, dbUser, dbPass)
        .runAsync,
      1.minute
    )

    val todoRepository = new TodoRepository
  }

  object Services {
    // import Repositories._

  }

  object Resources {
    import Repositories._
    // import Services._

    val todoResource = new TodoResource(todoRepository)
  }

  // Akka HTTP
  implicit val system = Contexts.akkaSystem
  implicit val mat = ActorMaterializer()

  val cacheControlHeader =
    CacheControl.create(CacheDirectives.`no-cache`, CacheDirectives.`no-store`)
  val routes = {
    import Resources._

    encodeResponse {
      respondWithDefaultHeaders(cacheControlHeader) {
        pathPrefix("api") {
          todoResource.route()
        }
      }
    }
  }

  logger.info(s"Listening on $interface:$port")
  Http().bindAndHandle(Route.seal(routes), interface, port)
}
