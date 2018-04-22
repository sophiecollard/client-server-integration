package countries.domain

import java.util.UUID


final case class Country(id: Country.Id, name: String, localName: Option[String], picture: Option[String])


object Country {

  def from(input: CountryInput): Country =
    Country(
      id = Id(UUID.randomUUID()),
      name = input.name,
      localName = input.localName,
      picture = input.picture
    )

  import io.circe.generic.semiauto._
  import io.circe.{Decoder, Encoder}

  final case class Id(value: UUID)

  object Id {

    def random(): Id = Id(UUID.randomUUID())

    implicit val encoder: Encoder[Id] = Encoder.encodeUUID.contramap(_.value)

    implicit val decoder: Decoder[Id] = Decoder.decodeUUID.map(Id.apply)

  }

  implicit def encoder: Encoder[Country] = deriveEncoder

  implicit def decoder: Decoder[Country] = deriveDecoder

}
