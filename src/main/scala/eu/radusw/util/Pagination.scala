package eu.radusw.util

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}

case class Pagination(page: Int, perPage: Int) {
  val offset: Int = (page - 1) * perPage
}
object Pagination {
  val page = "page"
  val perPage = "per_page"
  val NoPagination: Pagination = Pagination(1, Int.MaxValue)

  implicit val fooDecoder: Decoder[Pagination] = deriveDecoder[Pagination]
  implicit val fooEncoder: Encoder[Pagination] = deriveEncoder[Pagination]
}

case class PaginationResult[T](
    page: Int,
    perPage: Int,
    totalCount: Int,
    items: List[T]
) {
  val firstPage = page == 1
  val lastPage = totalCount <= page * perPage
}
object PaginationResult {

  implicit def encodeFoo[T](implicit en: Encoder[List[T]]): Encoder[PaginationResult[T]] =
    pr =>
      Json.obj(
        ("page", Json.fromInt(pr.page)),
        ("perPage", Json.fromInt(pr.perPage)),
        ("totalCount", Json.fromInt(pr.totalCount)),
        ("firstPage", Json.fromBoolean(pr.firstPage)),
        ("lastPage", Json.fromBoolean(pr.lastPage)),
        ("items", en(pr.items))
    )
}
