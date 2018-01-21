package eu.radusw.util

import java.util.UUID

import doobie._

object DoobieHelper {
  implicit val UuidType = Meta.other[UUID]("uuid")
}
