package countries

import countries.domain.{Country, CountryInput}
import io.circe.parser._
import io.circe.syntax._
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.ext.Ajax
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalajs.js.annotation.{JSExport, JSExportTopLevel}


@JSExportTopLevel("CountriesClient")
object CountriesClient {

  @JSExport("main")
  def main(container: html.Div): Unit = {
    // form
    val nameInput = input(
      cls := "input",
      placeholder := "name"
    ).render

    val localNameInput = input(
      cls := "input",
      placeholder := "local name"
    ).render

    val createButton = button(
      cls := "button is-info",
      "Add country"
    ).render

    // tiles
    val countryTiles = div(
      cls := "tile is-ancestor",
      div(
        id := "left-tile",
        cls := "tile is-parent is-vertical is-3"
      ),
      div(
        id := "centre-tile",
        cls := "tile is-parent is-vertical is-3"
      ),
      div(
        id := "right-tile",
        cls := "tile is-parent is-vertical is-3"
      )
    ).render

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
          case 201 => list(countryTiles, page = 1, perPage = 10)
          case _   => () // todo: Add error handling
        }
      }
      nameInput.value = ""
      localNameInput.value = ""
    }

    nameInput.onkeyup = (e: dom.KeyboardEvent) => if (e.keyCode == 13) create()
    localNameInput.onkeyup = (e: dom.KeyboardEvent) => if (e.keyCode == 13) create()
    createButton.onclick = (e: dom.Event) => create()
    list(countryTiles, page = 1, perPage = 10)

    container.appendChild(
      div(
        cls := "container",
        h1(
          cls := "title is-1",
          style := "font-weight: 400; padding-top: 100px;",
          "Countries"
        ),
        div(
          cls := "field is-grouped",
          div(
            cls := "control",
            nameInput
          ),
          div(
            cls := "control",
            localNameInput
          ),
          div(
            cls := "control",
            createButton
          )
        ),
        br,
        countryTiles
      ).render
    )
  }

  def delete(countryTiles: html.Div, id: Country.Id): Unit = {
    Ajax.delete(s"/api/countries/${id.value.toString}").foreach { xhr =>
      xhr.status match {
        case 200 => list(countryTiles, page = 1, perPage = 10)
        case _   => () // todo: Add error handling
      }
    }
  }

  def list(countryTiles: html.Div, page: Int, perPage: Int): Unit = {
    Ajax.get(s"/api/countries?page=$page&per_page=$perPage").foreach { xhr =>
      val data = decode[List[Country]](xhr.responseText)
      countryTiles.querySelector("#left-tile").innerHTML = ""
      countryTiles.querySelector("#centre-tile").innerHTML = ""
      countryTiles.querySelector("#right-tile").innerHTML = ""
      data match {
        case Right(countries) =>
          for ((country, index) <- countries.zipWithIndex) {
            val countryCard = renderCountryCard(countryTiles, country)
            val tileSelector =
              if (index % 3 == 0) "#left-tile"
              else if (index % 3 == 1) "#centre-tile"
              else "#right-tile"
            countryTiles.querySelector(tileSelector).appendChild(
              div(
                cls := "tile is-child",
                countryCard
              ).render
            )
          }
        case Left(_) =>
          countryTiles.appendChild(
            p(
              color := "red",
              "error loading countries"
            ).render
          )
      }
    }
  }

  def renderCountryCard(countryTiles: html.Div, country: Country): html.Div = {
    val cardImage = constructCardImage(country)
    val cardContent = constructCardContent(country)
    val cardFooter = constructCardFooter(countryTiles, country.id)
    div(
      cls := "card",
      cardImage,
      cardContent,
      cardFooter
    ).render
  }

  private def constructCardImage(country: Country): TypedTag[html.Div] = {
    div(
      cls := "card-image",
      figure(
        cls := "image is-4by3",
        img(
          src := "https://bulma.io/images/placeholders/1280x960.png",
          alt := "picture"
        )
      )
    )
  }

  private def constructCardContent(country: Country): TypedTag[html.Div] = {
    val media = div(
      cls := "media",
      div(
        cls := "media-content",
        p(
          cls := "title is-4",
          style := "font-weight: 400;",
          country.name
        ),
        p(
          cls := "subtitle is-6",
          i({
            // for some reason this only compiles if first assigned to a val
            val localName = country.localName.getOrElse("")
            localName
          })
        )
      )
    )
    val content = div(
      cls := "content",
      raw(
        """
          Lorem ipsum dolor sit amet, consectetur adipiscing elit.
          Phasellus nec iaculis mauris.
          """.stripMargin
      )
    )
    div(
      cls := "card-content",
      media,
      content
    )
  }

  private def constructCardFooter(countryTiles: html.Div, countryId: Country.Id): TypedTag[html.Element] = {
    val editButton = a(cls := "card-footer-item", "Edit")
    val deleteButton = a(cls := "card-footer-item", "Delete").render
    deleteButton.onclick = (e: dom.Event) => delete(countryTiles, countryId)
    footer(
      cls := "card-footer",
      editButton,
      deleteButton
    )
  }

}
