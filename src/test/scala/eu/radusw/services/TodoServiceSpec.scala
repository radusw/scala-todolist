package eu.radusw.services

import org.scalatest.{Matchers, WordSpec}

class TodoServiceSpec extends WordSpec with Matchers {

  "a complicated piece of code" should {
    "work" in {
      1 + 1 shouldBe 2
    }

    "work again" in {
      List(1, 1).sum shouldBe 2
    }
  }
}
