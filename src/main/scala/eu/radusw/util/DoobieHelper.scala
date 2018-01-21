package eu.radusw.util

import java.time.{ZoneId, ZonedDateTime}
import java.util.UUID

import doobie._

object DoobieHelper {
  implicit val UuidType = Meta.other[UUID]("uuid")
  implicit val ZonedDateTimeType: Meta[ZonedDateTime] = Meta.JavaTimeInstantMeta
    .xmap(
      i => ZonedDateTime.ofInstant(i, ZoneId.systemDefault()),
      _.toInstant
    )
}
