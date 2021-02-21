package countries.domain


final case class CountryInput(name: String, localName: Option[String], picture: Option[String], about: String)


object CountryInput {

  import io.circe.generic.semiauto._
  import io.circe.{Decoder, Encoder}

  implicit val encoder: Encoder[CountryInput] = deriveEncoder

  implicit val decoder: Decoder[CountryInput] = deriveDecoder

}
