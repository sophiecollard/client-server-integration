package countries

import countries.domain.{Country, CountryInput}
import io.circe.parser._
import io.circe.syntax._
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.ext.Ajax
import scalatags.JsDom.all._

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalajs.js.annotation.{JSExport, JSExportTopLevel}


@JSExportTopLevel("CountriesClient")
object CountriesClient {

  @JSExport("main")
  def main(container: html.Div): Unit = {
    val countriesList = ul.render
    val nameInput = input(placeholder:="name").render
    val localNameInput = input(placeholder:="local name").render
    val createButton = button("add").render

    def list(page: Int, perPage: Int): Unit =
      Ajax.get(s"/api/countries?page=$page&per_page=$perPage").foreach { xhr =>
        val data = decode[List[Country]](xhr.responseText)
        countriesList.innerHTML = ""
        data match {
          case Right(countries) =>
            for (Country(id, name, localName) <- countries) {
              val localNameAppendix: String = localName.map(" (" ++ _ ++ ")").getOrElse("")
              countriesList.appendChild(
                li(
                  b(name), localNameAppendix
                ).render
              )
            }
          case Left(_) =>
            countriesList.appendChild(
              li(
                color:="red",
                "error loading countries"
              ).render
            )
        }
      }

    def create(): Unit = {
      val name = nameInput.value
      val localName = localNameInput.value match {
        case "" => None
        case s => Some(s)
      }
      Ajax.post(
        url = s"/api/countries",
        data = CountryInput(name, localName).asJson.spaces2,
        headers = Map("Content-Type" -> "application/json")
      ).foreach { xhr =>
        xhr.status match {
          case 201 => list(page = 1, perPage = 10)
          case _   => () // todo: Add error handling
        }
      }
      nameInput.value = ""
      localNameInput.value = ""
    }

    createButton.onclick = (e: dom.Event) => create()
    list(page = 1, perPage = 10)

    container.appendChild(
      div(
        h1("Countries"),
        countriesList,
        h3("Add a new country"),
        nameInput,
        localNameInput,
        createButton
      ).render
    )
  }

}
