package com.rock.search.file

import com.rock.search.engine.NormalizedWord

import java.io.File
import scala.util.{Try, Using}

object FileHandler {

  def getDirectory(args: Array[String]): Either[ReadDirectoryError, File] = {
    for {
      path <- args.headOption.toRight(MissingPathArgument)
      directory <- Try(new java.io.File(path)).fold(
        throwable => Left(DirectoryNotFound(throwable)),
        file =>
          if (file.isDirectory) Right(file)
          else Left(NotADirectory(s"Path [$path] is not a directory"))
      )
    } yield directory
  }

  /**
   * The method crawls through the contents of all the the files in the directory. For each `word` in the file,
   * it constructs a pair of (`word`, `fileName`).
   *
   * It skips through files if exception is encountered while reading the lines from the file.
   *
   * @param directory holds reference to the directory.
   * @return List of [[NormalizedWord]]
   */
  def crawlFiles(directory: File): List[NormalizedWord] = {
    val allFilesInDirectory = directory.listFiles()
    allFilesInDirectory.foldLeft(List.empty[NormalizedWord]) { (accumulator, currentFile) =>
      crawlFile(currentFile).fold(
        throwable => {
          println(
            s"Skipped file: ${currentFile.getName} during crawl. Cause: ${throwable.getClass}, ${throwable.getMessage}"
          )
          accumulator
        },
        normalizedWords => {
          accumulator ::: normalizedWords
        }
      )
    }
  }

  private def crawlFile(file: File): Try[List[NormalizedWord]] = {
    Using(scala.io.Source.fromFile(file)) { bufferedSource =>
      bufferedSource
        .getLines()
        .flatMap(NormalizedWord.parseLine(_, file.getName))
        .toList
    }
  }

  sealed trait ReadDirectoryError

  case object MissingPathArgument extends ReadDirectoryError

  case class NotADirectory(error: String) extends ReadDirectoryError

  case class DirectoryNotFound(throwable: Throwable) extends ReadDirectoryError

}
