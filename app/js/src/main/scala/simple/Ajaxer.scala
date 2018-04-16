package simple

import org.scalajs.dom
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import upickle.default.{Reader, Writer}

import scala.concurrent.Future


object Ajaxer extends autowire.Client[String, Reader, Writer] {

  override def doCall(req: Request): Future[String] = {
    dom.ext.Ajax.post(
      url = "/ajax/" + req.path.mkString("/"),
      data = upickle.default.write(req.args)
    ).map(_.responseText)
  }

  def read[Result: Reader](p: String) = upickle.default.read[Result](p)

  def write[Result: Writer](r: Result) = upickle.default.write(r)

}
