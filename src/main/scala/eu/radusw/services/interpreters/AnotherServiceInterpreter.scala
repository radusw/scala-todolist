package eu.radusw.services.interpreters

import eu.radusw.services.AnotherService
import monix.eval.Task

class AnotherServiceInterpreter extends AnotherService[Task] {
  override def doSomething(value: String) = Task(value)
}
