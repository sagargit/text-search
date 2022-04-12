package com.rock.search.engine

sealed trait SearchEngineModel

/**
 * Model used to represent an Inverted Index Record for each word in the file.
 * @param word A word present in a file.
 * @param files A set of fileNames where `word` is present.
 */
final case class InvertedIndexRecord(word: String, files: Set[String]) extends SearchEngineModel

final case class FileScorePerInputWord(inputWord: String, fileName: String, score: Int) extends SearchEngineModel

final case class RecordComparisonScore(comparedRecord: InvertedIndexRecord, score: Int) extends SearchEngineModel

final case class SearchContext(records: List[InvertedIndexRecord]) extends SearchEngineModel

final case class SearchResult(fileScores: List[FileScore]) extends SearchEngineModel {
  private val noMatchScore = 0.0
  private def hasNoMatch(fileScore: FileScore): Boolean = {
    fileScore.score == noMatchScore
  }
  def formattedResult: String = {
    if (fileScores.forall(hasNoMatch)) "0 records matched"
    else {
      fileScores.filterNot(hasNoMatch).map(_.toString).mkString("\n")
    }
  }
}

final case class WordComparisonScores(inputWord: String, recordScores: List[RecordComparisonScore])
    extends SearchEngineModel {

  def appendScores(scores: List[RecordComparisonScore]): WordComparisonScores = {
    this.copy(recordScores = this.recordScores ++ scores)
  }

}

final case class SearchInput(rawInput: String) extends SearchEngineModel {
  def getWords: List[String] = rawInput.splitByWhitespaces.map(_.normalize)
}

final case class FileScore(fileName: String, score: Double) extends SearchEngineModel {
  override def toString: String = {
    s"File => $fileName. Match Score => $score %"
  }
}

final case class NormalizedWord(word: String, fileName: String) extends SearchEngineModel

object NormalizedWord {
  def parseLine(line: String, fileName: String): List[NormalizedWord] = {
    line.splitByWhitespaces.map(rawWord => NormalizedWord(rawWord.normalize, fileName))
  }
}
