package eu.radusw.models

import java.time.Instant
import java.util.UUID

case class TodoId(value: UUID) extends AnyVal
case class Todo(id: TodoId, name: String, timestamp: Instant, text: String)
