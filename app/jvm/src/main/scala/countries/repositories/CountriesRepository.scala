package countries.repositories

import countries.domain.Country

import scala.concurrent.{ExecutionContext, Future}


final class CountriesRepository(implicit ec: ExecutionContext) {

  private var Store: Map[Country.Id, Country] = Map.empty

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
