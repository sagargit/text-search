package com.rock.search

import java.io.File
import java.nio.file.{FileSystems, Files, Path}
import java.nio.charset.StandardCharsets
import scala.util.Try

case class CreateFile(prefix: String, extension: String, addLine: String)

object CreateFile {
  def apply(prefix: String, extension: String, line: List[String]): CreateFile = {
    CreateFile(prefix, extension, line.mkString(" "))
  }
}

case class FileCreated(file: File, fileName: String, input: CreateFile) {
  def getPathStr: String = file.getPath
}

object FileHelper {

  def createFile(directoryPathStr: String, createFile: CreateFile): Option[FileCreated] = {
    createTempFile(directoryPathStr, createFile.prefix, createFile.extension)
      .flatMap { tempFile =>
        write(tempFile.toPath, createFile.addLine).map { _ =>
          FileCreated(tempFile, tempFile.getName, createFile)
        }
      }
  }

  def createFiles(directoryPathStr: String, createFiles: List[CreateFile]): List[FileCreated] = {
    createFiles.flatMap(createFile(directoryPathStr, _))
  }

  def getFile(directoryPathStr: String): File = directoryPathStr.toFile

  def clearDirectory(directoryPathStr: String): Unit = {
    getFile(directoryPathStr).listFiles().foreach(_.delete())
  }

  private def write(path: Path, line: String): Option[Path] = {
    Try(Files.write(path, line.getBytes(StandardCharsets.UTF_8))).toOption
  }

  private def createTempFile(path: Path, prefix: String, extension: String): Option[File] = {
    Try(Files.createTempFile(path, prefix, extension).toFile).toOption
  }

  private implicit def toPath(pathStr: String): Path = {
    FileSystems.getDefault().getPath(pathStr)
  }

}
