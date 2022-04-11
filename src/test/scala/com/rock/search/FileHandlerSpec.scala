package com.rock.search

import com.rock.search.file.FileHandler._
import org.scalatest.{BeforeAndAfterEach, EitherValues, OptionValues}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import FileHelper._
import com.rock.search.engine.NormalizedWord
import com.rock.search.file.FileHandler.{DirectoryNotFound, MissingPathArgument, NotADirectory}
import com.test.search.engine.NormalizedWord

import java.io.File

class FileHandlerSpec extends AnyWordSpec with Matchers with EitherValues with OptionValues with BeforeAndAfterEach {

  private val testDirectoryPath = "src/test/resources"
  private val textFileExtension = ".txt"

  "FileHandler's getDirectory() method" should {

    "successfully return a directory when valid path to directory is provided to getDirectory() method" in {
      getDirectory(Array(testDirectoryPath)).value shouldBe a[File]
    }

    "return NotADirectory error when valid path to a file is provided to getDirectory() method" in {
      val fileCreated =
        createFile(testDirectoryPath, CreateFile("test", textFileExtension, "this is a test file")).value
      val getDirectoryResult = getDirectory(Array(fileCreated.getPathStr))
      getDirectoryResult.left.value shouldBe a[NotADirectory]
    }

    "return MissingPathArgument error when path is not provided to getDirectory() method" in {
      getDirectory(Array.empty[String]).left.value shouldBe MissingPathArgument
    }

    "return DirectoryNotFound error when invalid path is provided to getDirectory() method" in {
      getDirectory(Array(null)).left.value shouldBe a[DirectoryNotFound]
    }

  }

  "FileHandler's crawlFiles() method" should {

    "crawl through all available valid files when a valid path to directory is provided to crawlFiles() method" in {
      val testSetup = List(
        ("one", textFileExtension, List("this", "is", "one")),
        ("two", textFileExtension, List("it", "is", "two")),
        ("three", textFileExtension, List("these", "are", "three")),
        ("four", textFileExtension, List("these", "are", "four"))
      )

      val createFilesInput = testSetup.map {
        case (fileName, extension, words) => CreateFile(fileName, extension, words)
      }

      val filesCreated: List[FileCreated] = createFiles(testDirectoryPath, createFilesInput)
      val fileNames: List[String]         = filesCreated.map(_.fileName)

      val crawledResult: List[NormalizedWord] = crawlFiles(getFile(testDirectoryPath))

      crawledResult.map(_.word) should contain theSameElementsAs testSetup.flatMap(_._3)
      crawledResult.map(_.fileName).distinct should contain theSameElementsAs fileNames
    }

  }

  override def beforeEach() = {
    clearDirectory(testDirectoryPath)
  }

  override def afterEach() = {
    clearDirectory(testDirectoryPath)
  }

}
