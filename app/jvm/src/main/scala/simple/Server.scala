package simple

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties


object Server extends FileData.Api {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val route = {
      get {
        pathEndOrSingleSlash {
          complete {
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              Page.skeleton.render
            )
          }
        } ~ getFromResourceDirectory("") // needed to get app-fastopt.js on load
      } ~ post {
        path("ajax" / Segments) { segments =>
          entity(as[String]) { e =>
            complete {
              Router.route[FileData.Api](Server)(
                autowire.Core.Request(segments, upickle.default.read[Map[String, String]](e))
              )
            }
          }
        }
      }
    }

    val port = Properties.envOrElse("PORT", "8080").toInt
    Http().bindAndHandle(route, "0.0.0.0", port = port)
  }

  def list(path: String): Seq[FileData] = {
    val (dir, last) = path.splitAt(path.lastIndexOf("/") + 1)
    val files = Option(new java.io.File("./" + dir).listFiles()).toSeq.flatten
    for {
      f <- files
      if f.getName.startsWith(last)
    } yield FileData(f.getName, f.length)
  }

}
