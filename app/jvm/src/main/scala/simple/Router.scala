package simple

import upickle.default.{Reader, Writer}


object Router extends autowire.Server[String, Reader, Writer] {

  def read[Result: Reader](p: String) = upickle.default.read[Result](p)

  def write[Result: Writer](r: Result) = upickle.default.write(r)

}
