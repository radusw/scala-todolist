package eu.radusw.models

import java.time.ZonedDateTime
import java.util.UUID

case class TodoId(value: UUID) extends AnyVal
case class Todo(id: TodoId,
                name: String,
                timestamp: ZonedDateTime,
                text: String)
