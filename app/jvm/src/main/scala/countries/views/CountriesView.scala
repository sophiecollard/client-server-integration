package countries.views

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import countries.Page


class CountriesView {

  val routes: Route = pathPrefix("countries") {
    pathEndOrSingleSlash {
      get {
        complete {
          HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            Page.skeleton.render
          )
        }
      }
    }
  } ~ getFromResourceDirectory("") // needed to get app-fastopt.js on load

}
