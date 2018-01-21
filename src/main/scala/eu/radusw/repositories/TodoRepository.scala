package eu.radusw.repositories

import java.time.ZonedDateTime
import java.util.UUID

import cats.data.OptionT
import doobie._
import doobie.implicits._
import eu.radusw.models.{Todo, TodoId}
import eu.radusw.services.TodoService
import eu.radusw.util.{Pagination, PaginationResult}
import eu.radusw.util.DoobieHelper._
import monix.eval.Task

class TodoRepository(implicit xa: Transactor[Task]) extends TodoService[Task] {

  private val selectFr =
    fr"SELECT t.id, t.name, t.timestamp, t.text FROM todos t"
  private def fetchAction(id: TodoId) =
    (selectFr ++ fr"WHERE t.id = $id").query[Todo].option

  def create(
      partialTodo: (TodoId, ZonedDateTime) => Todo): Task[Option[Todo]] = {
    val todoId = TodoId(UUID.randomUUID())
    val todo = partialTodo(todoId, ZonedDateTime.now())
    val insertAction: ConnectionIO[Int] =
      sql"""
            INSERT INTO "public"."todos"("id","name", "timestamp", "text")
            VALUES (${todo.id}, ${todo.name}, ${todo.timestamp}, ${todo.text})
        """.update.run
    val q = for {
      _ <- insertAction
      c <- fetchAction(todoId)
    } yield c
    q.transact(xa)
  }

  def update(id: TodoId, patchTodo: Todo => Todo): Task[Option[Todo]] = {
    val q = for {
      existing <- OptionT(fetchAction(id))
      updated = patchTodo(existing)
        .copy(id = id, timestamp = ZonedDateTime.now())
      r <- OptionT.liftF(
        sql"""
              UPDATE todos SET "name"=${updated.name}, "timestamp"=${updated.timestamp}, "text"=${updated.text}
              WHERE "id"=$id
          """.update.run
      )
    } yield if (r == 1) updated else existing

    q.value.transact(xa)
  }

  def findAll(pagination: Pagination): Task[PaginationResult[Todo]] = {
    val q: ConnectionIO[PaginationResult[Todo]] = for {
      count <- sql"SELECT count(*) from todos".query[Int].unique
      lst <- (
        selectFr ++
          fr"ORDER BY t.timestamp, t.id" ++
          fr"OFFSET ${pagination.offset} LIMIT ${pagination.perPage}"
      ).query[Todo].list
    } yield PaginationResult(pagination.page, pagination.perPage, count, lst)
    q.transact(xa)
  }

  def find(id: TodoId): Task[Option[Todo]] = {
    val q: ConnectionIO[Option[Todo]] =
      (selectFr ++ fr"WHERE t.id = $id").query[Todo].option
    q.transact(xa)
  }

  def delete(id: TodoId): Task[Option[Unit]] = {
    val q: ConnectionIO[Int] =
      sql"DELETE FROM todos t WHERE t.id = $id".update.run
    q.transact(xa).map(r => if (r == 1) Some(()) else None)
  }
}
