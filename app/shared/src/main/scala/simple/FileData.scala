package simple

import upickle.default.{ReadWriter, macroRW}


case class FileData(name: String, size: Long)


object FileData {

  trait Api {

    def list(path: String): Seq[FileData]

  }

  implicit def readWriter: ReadWriter[FileData] = macroRW

}
