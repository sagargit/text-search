package com.rock.search.engine

import com.rock.search.engine

object SearchEngine {

  /**
   * The purpose of indexing here is to prepare an Inverted Index for each word found in files.
   * Each word will hold a reference to all files where it exists.
   * The outcome of this process is to come up with a structure something like below:
   *
   *    [
   *      { "word1", ["file1, file2"] }
   *      { "word2", ["file2, file3"] }
   *      { "word3", ["file4"] }
   *      .........
   *      .........
   *    ]
   */
  def index(list: List[NormalizedWord]): SearchContext = {
    val grouped = list.groupBy(_.word)
    val rows    = grouped.map { case (word, files) => InvertedIndexRecord(word, files.map(_.fileName).toSet) }
    engine.SearchContext(rows.toList)
  }

  /**
   * The Search Algorithm produces Search Result as follow:
   * - Step 1: The input text from the User is split into normalized words.
   * - Step 2: Each word is searched and compared across the Inverted Index Records:
   *      - Searching is done using different [[ComparisonType]].
   *      - Comparison scores for each Word is tracked through [[WordComparisonScores]]
   * - Step 3: The comparison scores are now grouped per File
   * - Step 4: Relevance score for each File is calculated by taking the average of maximum Score for each input text.
   *           - The maximum score calculation is needed because the same input text can match different words of the
   *             same file through [[ComparisonType]]. So, from those matches, we select the one with highest score
   *             because that provides highest relevance.
   * - Step 5: Max 10 files with highest relevant scores are returned.
   */
  def search(searchInput: SearchInput, searchContext: SearchContext): SearchResult = {

    val wordComparisonScores = hierarchicalComparisonResults(searchInput, searchContext)

    val flattenedWordMatchScoresPerFile: List[FileScorePerInputWord] =
      wordComparisonScores.flatMap { wordComparisonScore =>
        wordComparisonScore.recordScores.flatMap { recordScore =>
          recordScore.comparedRecord.files.map(
            FileScorePerInputWord(wordComparisonScore.inputWord, _, recordScore.score)
          )
        }
      }

    val topAggregatedFileScores: List[FileScore] = flattenedWordMatchScoresPerFile
      .groupBy(_.fileName)
      .map {
        case (fileName, scorePerFiles) =>
          val highestScoresForEachInputWord: List[Int] = getHighestScoreForEachWord(scorePerFiles)
          FileScore(fileName = fileName, score = average(highestScoresForEachInputWord))
      }
      .toList
      .sortBy(-_.score)
      .take(maxSearchResultSetSize)

    SearchResult(topAggregatedFileScores)
  }

  private def getHighestScoreForEachWord(scoresForAFile: List[FileScorePerInputWord]): List[Int] = {
    scoresForAFile
      .groupBy(_.inputWord)
      .map {
        case (_, scores) => scores.map(_.score).max
      }
      .toList
  }

  private def hierarchicalComparisonResults(
      searchInput: SearchInput,
      searchContext: SearchContext
  ): List[WordComparisonScores] = {
    val inputWords: List[String] = searchInput.getWords
    inputWords.map { inputWord =>
      val allScores = {
        ComparisonType.All.foldLeft(List.empty[RecordComparisonScore]) { (scoresAccumulator, currentComparisonType) =>
          val matchedRecords           = currentComparisonType.getMatchedRecords(inputWord, searchContext)
          val matchedRecordsWithScores = matchedRecords.map(RecordComparisonScore(_, currentComparisonType.point))
          scoresAccumulator ++ matchedRecordsWithScores
        }
      }
      WordComparisonScores(inputWord, allScores)
    }
  }

  private def average(values: List[Int]): Double = {
    val average: scala.Double = values.sum.toDouble / values.size.toDouble
    roundUpToTwoDecimalPoints(average)
  }

  private def roundUpToTwoDecimalPoints(double: Double): Double = {
    BigDecimal(double).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  private val maxSearchResultSetSize = 10

}
