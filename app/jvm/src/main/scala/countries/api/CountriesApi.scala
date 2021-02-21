package countries.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import countries.domain.{Country, CountryInput}
import countries.repositories.CountriesRepository
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport

import scala.concurrent.ExecutionContext


final class CountriesApi(repository: CountriesRepository)(implicit ec: ExecutionContext)
  extends ErrorAccumulatingCirceSupport {

  val countryIdMatcher = JavaUUID.map(Country.Id(_))

  val routes: Route = pathPrefix("countries") {
    pathEndOrSingleSlash {
      get {
        parameters("page".as[Int].?(1), "per_page".as[Int].?(10)) { (page, perPage) =>
          val countriesF = repository.list(page, perPage)
          onSuccess(countriesF) {
            complete(_)
          }
        }
      } ~ post {
        entity(as[CountryInput]) { input =>
          val country = Country.from(input)
          val doneF = repository.upsert(country)
          onSuccess(doneF) {
            complete(StatusCodes.Created, country)
          }
        }
      }
    } ~ path(countryIdMatcher) { id =>
      get {
        val countryF = repository.get(id)
        onSuccess(countryF) {
          case Some(country) => complete(country)
          case None => complete(StatusCodes.NotFound)
        }
      } ~ delete {
        val doneF = repository.delete(id)
        onSuccess(doneF) {
          complete(StatusCodes.OK)
        }
      }
    }
  }

}
