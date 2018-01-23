package eu.radusw.services

trait AnotherService[F[_]] {
  def doSomething(value: String): F[String]
}
