package eu.radusw.util

import com.typesafe.config.{Config, ConfigFactory}
import doobie.hikari.HikariTransactor
import eu.radusw.Contexts
import monix.eval.Task
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.Scheduler
import org.flywaydb.core.Flyway

import scala.concurrent.Await
import scala.concurrent.duration._

trait WithDatabase {
  val config: Config = ConfigFactory.load()

  val dbProps = config.getConfig("db")
  val dbDriver = dbProps.getString("driver")
  val dbUrl = dbProps.getString("url")
  val dbUser = dbProps.getString("user")
  val dbPass = dbProps.getString("password")

  val scheduler = Scheduler(Contexts.blockingOpsDispatcher, AlwaysAsyncExecution)
  val xa = Await.result(
    HikariTransactor.newHikariTransactor[Task](dbDriver, dbUrl, dbUser, dbPass).runAsync(scheduler),
    1.minute
  )

  // Db clean and migration
  val flyway = new Flyway()
  flyway.setDataSource(dbUrl, dbUser, dbPass)
  flyway.clean()
  flyway.migrate()
}
