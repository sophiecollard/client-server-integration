package simple

import scalatags.JsDom.all._
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.ext.Ajax

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalajs.js.annotation.{JSExport, JSExportTopLevel}


@JSExportTopLevel("Client")
object Client {

  @JSExport("main")
  def main(container: html.Div) = {
    val inputBox = input.render
    val outputBox = ul.render

    def update() = Ajax.post("/ajax/list", inputBox.value).foreach { xhr =>
      println(s"[debug] response text: ${xhr.responseText}")
      val data = upickle.default.read[Seq[FileData]](xhr.responseText)
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
