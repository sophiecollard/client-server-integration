package vue

import blog.codeninja.scalajs.vue._

import scalajs.js
import scalajs.js.annotation.{JSExport, JSExportTopLevel}


trait Data extends js.Object {
  var task: String
  var list: js.Array[Task]
}


// needs to extend js.Object because Vue will be access it from HTML
class Task(var text: String, var done: Boolean = false) extends js.Object


@JSExportTopLevel("VueClient")
class VueClient {

  var vue: Vue = _

  @JSExport("main")
  def main(args: Array[String]) = {
    vue = new Vue(
      js.Dynamic.literal(
        el = "#app",

        // This is our js.Object instance of Data.
        data = js.Dynamic.literal(
          task = "",
          list = js.Array[Task](
            new Task("Learn Vue.js"),
            new Task("Create reactive Scala.js app"),
            new Task("Profit!")
          )
        ),

        // Reactive methods that Vue can call.
        methods = js.Dynamic.literal(
          addTask = addTask: js.ThisFunction0[Data, _],
          dropTask = dropTask: js.ThisFunction1[Data, Int, _],
          toggleTask = toggleTask: js.ThisFunction1[Data, Int, _]
        )
      )
    )
  }

  def addTask(data: Data): Unit =
    if (data.task.length > 0) {
      data.list.push(new Task(data.task))
    }

  def dropTask(data: Data, index: Int): Unit =
    data.list.splice(index, 1)

  def toggleTask(data: Data, index: Int): Unit = {
    val prev = data.list.splice(index, 0).headOption match {
      case None =>
        ()
      case Some(task) =>
        val toggledTask = new Task(task.text, !task.done)
        data.list.splice(index, 1, toggledTask)
    }
  }

}
