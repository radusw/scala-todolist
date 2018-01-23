package eu.radusw.services

import cats.Monad
import cats.data.OptionT
import eu.radusw.models.{Todo, TodoId}

class ComposeThemService[F[_]: Monad](todoService: TodoService[F], anotherService: AnotherService[F]) {

  def composedOp(todoId: TodoId, str: String): F[Option[Todo]] = {
    val result = for {
      r1 <- OptionT(todoService.find(todoId))
      r2 <- OptionT.liftF(anotherService.doSomething(str))
    } yield r1.copy(text = s"${r1.text} ~ $r2")

    result.value
  }
}
