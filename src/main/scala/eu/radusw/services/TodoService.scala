package eu.radusw.services

import java.time.ZonedDateTime

import eu.radusw.models.{Todo, TodoId}
import eu.radusw.util.{Pagination, PaginationResult}

trait TodoService[F[_]] {
  def create(partialTodo: (TodoId, ZonedDateTime) => Todo): F[Option[Todo]]
  def update(id: TodoId, patchTodo: Todo => Todo): F[Option[Todo]]
  def delete(todoId: TodoId): F[Option[Unit]]
  def find(todoId: TodoId): F[Option[Todo]]
  def findAll(pagination: Pagination): F[PaginationResult[Todo]]
}
