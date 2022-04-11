package com.rock.search

import com.rock.search.engine.{InvertedIndexRecord, NormalizedWord}

object TestDataProvider {

  val testDirectoryPath = "src/test/resources"
  val textFileExtension = ".txt"

  type FileName = String
  type Words    = List[String]

  val testFiles: Map[FileName, Words] =
    Map("file1" -> List("word1", "word3"), "file2" -> List("word1", "word2", "word4"), "file3" -> List("word3"))

  def produceInvertedIndex(testFiles: Map[String, Words]): List[InvertedIndexRecord] = {
    testFiles.toList
      .flatMap {
        case (fileName, words) => words.map((_, fileName))
      }
      .groupBy(_._1)
      .map {
        case (word, wordFileNamesPair) => InvertedIndexRecord(word, wordFileNamesPair.map(_._2).toSet)
      }
      .toList
  }

  def produceNormalizedWords(testFiles: Map[String, Words]): List[NormalizedWord] = {
    testFiles.flatMap {
      case (fileName, words) => words.map(NormalizedWord(_, fileName))
    }.toList
  }

}
