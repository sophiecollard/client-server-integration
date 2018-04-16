package simple

import autowire._
import org.scalajs.dom
import org.scalajs.dom.html
import scalatags.JsDom.all._

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalajs.js.annotation.{JSExport, JSExportTopLevel}


@JSExportTopLevel("Client")
object Client {

  @JSExport("main")
  def main(container: html.Div): Unit = {
    val inputBox = input.render
    val outputBox = ul.render

    def update(): Unit = Ajaxer[FileData.Api].list(inputBox.value).call().foreach { data =>
      outputBox.innerHTML = ""
      for (FileData(name, size) <- data) {
        outputBox.appendChild(
          li(
            b(name), " - ", size, " bytes"
          ).render
        )
      }
    }

    inputBox.onkeyup = (e: dom.Event) => update()
    update()

    container.appendChild(
      div(
        h1("File Search"),
        inputBox,
        outputBox
      ).render
    )

  }

}
