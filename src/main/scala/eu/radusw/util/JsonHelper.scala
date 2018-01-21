package eu.radusw.util

import java.time.{LocalDate, Instant}

import cats.syntax.either._
import io.circe.{Decoder, Encoder}
import shapeless.Unwrapped

object JsonHelper {
  implicit val encodeInstant: Encoder[Instant] =
    Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] =
    Decoder.decodeString.emap { str =>
      Either.catchNonFatal(Instant.parse(str)).leftMap(_ => "err")
    }

  implicit val encodeDate: Encoder[LocalDate] =
    Encoder.encodeString.contramap[LocalDate](_.toString)
  implicit val decodeDate: Decoder[LocalDate] =
    Decoder.decodeString.emap { str =>
      Either.catchNonFatal(LocalDate.parse(str)).leftMap(_ => "err")
    }

  implicit def decodeAnyVal[T, U](implicit ev: T <:< AnyVal,
                                  unwrapped: Unwrapped.Aux[T, U],
                                  decoder: Decoder[U]): Decoder[T] =
    Decoder.instance[T] { cursor =>
      decoder(cursor).map { value =>
        val r = unwrapped.wrap(value)
        ev(r) // no-op needed to get rid of unused parameter value `ev`
        r
      }
    }

  implicit def encodeAnyVal[T, U](implicit ev: T <:< AnyVal,
                                  unwrapped: Unwrapped.Aux[T, U],
                                  encoder: Encoder[U]): Encoder[T] =
    Encoder.instance[T] { value =>
      ev(value) // no-op needed to get rid of unused parameter value `ev`
      encoder(unwrapped.unwrap(value))
    }
}
