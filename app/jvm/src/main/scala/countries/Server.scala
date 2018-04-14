package countries

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import countries.api.CountriesApi
import countries.repositories.CountriesRepository

import scala.io.StdIn


object Server {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val countriesRepository = new CountriesRepository()
    val countriesApi = new CountriesApi(countriesRepository)

    val routes = pathPrefix("api") {
      countriesApi.routes
    }

    val (host, port) = ("0.0.0.0", 8080)
    val bindingFuture = Http().bindAndHandle(routes, host, port)
    println(s"Started server at http://$host:$port\nPress any key to terminate")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
