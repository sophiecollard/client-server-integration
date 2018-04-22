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

    val pictureLinkInput = input(
      cls := "input",
      placeholder := "picture link"
    ).render

    val aboutInput = textarea(
      cls := "textarea"
    ).render

    val createButton = button(
      cls := "button is-info",
      "Add country"
    ).render

    val formColumn = div(
      cls := "column is-4",
      div(
        cls := "field",
        label(cls := "label", "Name"),
        div(cls := "control", nameInput)
      ),
      div(
        cls := "field",
        label(cls := "label", "Local name"),
        div(cls := "control", localNameInput)
      ),
      div(
        cls := "field",
        label(cls := "label", "Picture link"),
        div(cls := "control", pictureLinkInput)
      ),
      div(
        cls := "field",
        label(cls := "label", "About"),
        div(cls := "control", aboutInput)
      ),
      div(
        cls := "field",
        div(cls := "control", createButton)
      )
    )

    // columns
    val countryColumns = div(
      cls := "column is-8",
      div(
        cls := "columns",
        div(
          id := "left-country-column",
          cls := "column is-4"
        ),
        div(
          id := "centre-country-column",
          cls := "column is-4"
        ),
        div(
          id := "right-country-column",
          cls := "column is-4"
        )
      )
    ).render

    val columns = div(
      cls := "columns",
      countryColumns,
      formColumn
    )

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
          case 201 => list(countryColumns, page = 1, perPage = 10)
          case _   => () // todo: Add error handling
        }
      }
      nameInput.value = ""
      localNameInput.value = ""
    }

    nameInput.onkeyup = (e: dom.KeyboardEvent) => if (e.keyCode == 13) create()
    localNameInput.onkeyup = (e: dom.KeyboardEvent) => if (e.keyCode == 13) create()
    createButton.onclick = (e: dom.Event) => create()
    list(countryColumns, page = 1, perPage = 10)

    container.appendChild(
      div(
        cls := "container",
        h1(
          cls := "title is-1",
          style := "font-weight: 400; padding-top: 100px;",
          "Countries"
        ),
        columns
      ).render
    )
  }

  def delete(countryColumns: html.Div, id: Country.Id): Unit = {
    Ajax.delete(s"/api/countries/${id.value.toString}").foreach { xhr =>
      xhr.status match {
        case 200 => list(countryColumns, page = 1, perPage = 10)
        case _   => () // todo: Add error handling
      }
    }
  }

  def list(countryColumns: html.Div, page: Int, perPage: Int): Unit = {
    Ajax.get(s"/api/countries?page=$page&per_page=$perPage").foreach { xhr =>
      val data = decode[List[Country]](xhr.responseText)
      countryColumns.querySelector("#left-country-column").innerHTML = ""
      countryColumns.querySelector("#centre-country-column").innerHTML = ""
      countryColumns.querySelector("#right-country-column").innerHTML = ""
      data match {
        case Right(countries) =>
          for ((country, index) <- countries.zipWithIndex) {
            val countryCard = renderCountryCard(countryColumns, country)
            val tileSelector = index % 3 match {
              case 0 => "#left-country-column"
              case 1 => "#centre-country-column"
              case _ => "#right-country-column"
            }
            countryColumns
              .querySelector(tileSelector)
              .appendChild(countryCard)
          }
        case Left(_) =>
          countryColumns.appendChild(
            p(
              color := "red",
              "error loading countries"
            ).render
          )
      }
    }
  }

  def renderCountryCard(countryColumns: html.Div, country: Country): html.Div = {
    val cardImage = constructCardImage(country)
    val cardContent = constructCardContent(country)
    val cardFooter = constructCardFooter(countryColumns, country.id)
    div(
      cls := "card",
      style := "margin-bottom: 20px;",
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

  private def constructCardFooter(countryColumns: html.Div, countryId: Country.Id): TypedTag[html.Element] = {
    val editButton = a(cls := "card-footer-item", "Edit")
    val deleteButton = a(cls := "card-footer-item", "Delete").render
    deleteButton.onclick = (e: dom.Event) => delete(countryColumns, countryId)
    footer(
      cls := "card-footer",
      editButton,
      deleteButton
    )
  }

}
