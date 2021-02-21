package countries.repositories

import countries.domain.Country

import scala.concurrent.{ExecutionContext, Future}


final class CountriesRepository(implicit ec: ExecutionContext) {

  private var Store: Map[Country.Id, Country] = {
    val loremIpsum = """
      Lorem ipsum dolor sit amet, consectetur adipiscing elit.
      Phasellus nec iaculis mauris.
      """.stripMargin
    val fi = Country(Country.Id.random, "Finland", Some("Suomi"), None, loremIpsum)
    val fr = Country(Country.Id.random, "France", None, Some("https://bit.ly/2HRrZgX"), loremIpsum)
    val nl = Country(Country.Id.random, "Netherlands", Some("Nederland"), Some("https://bit.ly/2HkkUsk"), loremIpsum)
    val se = Country(Country.Id.random, "Sweden", Some("Sverige"), None, loremIpsum)
    val uk = Country(Country.Id.random, "United Kingdom", None, None, loremIpsum)
    Map(fi.id -> fi, fr.id -> fr, nl.id -> nl, se.id -> se, uk.id -> uk)
  }

  def upsert(country: Country): Future[Unit] = Future {
    Store += (country.id -> country)
  }

  def delete(id: Country.Id): Future[Unit] = Future {
    Store -= id
  }

  def get(id: Country.Id): Future[Option[Country]] = Future {
    Store.get(id)
  }

  def list(page: Int, perPage: Int): Future[List[Country]] = Future {
    val offset = (page - 1) * perPage
    Store.values.slice(offset, offset + perPage).toList
  }

}
