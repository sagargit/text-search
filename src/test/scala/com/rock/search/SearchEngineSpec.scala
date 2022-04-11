package com.rock.search

import com.rock.search.engine.{FileScore, InvertedIndexRecord, NormalizedWord, SearchContext, SearchInput}
import org.scalatest.{BeforeAndAfterEach, EitherValues, OptionValues}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.rock.search.engine.SearchEngine._
import TestDataProvider._

class SearchEngineSpec extends AnyWordSpec with Matchers with EitherValues with OptionValues with BeforeAndAfterEach {

  "Search Engine's index() method" should {

    "index all normalized words by constructing Inverted Index" in {

      /**
       * Produces:
       *  List(
       *    NormalizedWord("word1", "file1"),
       *    NormalizedWord("word3", "file1"),
       *    NormalizedWord("word1", "file2"),
       *    NormalizedWord("word2", "file2"),
       *    NormalizedWord("word4", "file2"),
       *    NormalizedWord("word3", "file3")
       *  )
       */
      val normalizedWords: List[NormalizedWord] = testNormalizedWords

      /**
       * Produces:
       *  List(
       *     InvertedIndexRecord("word1", Set("file1", "file2")),
       *     InvertedIndexRecord("word2", Set("file2", "file3")),
       *     InvertedIndexRecord("word3", Set("file1")),
       *     InvertedIndexRecord("word4", Set("file2"))
       *   )
       */
      val expectedIndex: List[InvertedIndexRecord] = testInvertedIndex

      index(normalizedWords).records should contain theSameElementsAs expectedIndex

    }

  }

  "Search Engine's search() method" should {

    "generate 100 % score when search `word` matches `equals` comparison" in {
      val searchInput   = SearchInput("word1")
      val searchContext = SearchContext(testInvertedIndex)
      val searchResult  = search(searchInput, searchContext)

      searchResult.scores should
        contain theSameElementsAs List(
          FileScore("file1", 100.0), // contains exact word: `word1`
          FileScore("file2", 100.0), // contains exact word: `word1`
          FileScore(
            "file3",
            0.0
          ) // // doesn't contain `word1` & neither any words that satisfy starts_with, contains comparisons
        )
    }

    "generate 90 % score when search `word` matches `starts_with` comparison" in {
      val searchInput   = SearchInput("wor")
      val searchContext = SearchContext(testInvertedIndex)
      val searchResult  = search(searchInput, searchContext)

      searchResult.scores should
        contain theSameElementsAs List(
          FileScore("file1", 90.0), // all words contains `wor`
          FileScore("file2", 90.0), // all words contains `wor`
          FileScore("file3", 90.0)  // all words contains `wor`
        )
    }

    "generate 80 % score when search `word` matches `contains` comparison" in {
      val searchInput   = SearchInput("ord")
      val searchContext = SearchContext(testInvertedIndex)
      val searchResult  = search(searchInput, searchContext)

      searchResult.scores should
        contain theSameElementsAs List(
          FileScore("file1", 80.0), // all words starts_with `ord`
          FileScore("file2", 80.0), // all words starts_with `ord`
          FileScore("file3", 80.0)  // all words starts_with `ord`
        )
    }

    "generate 0 % score when search `word` doesn't match any comparisons" in {
      val searchInput   = SearchInput("zz")
      val searchContext = SearchContext(testInvertedIndex)
      val searchResult  = search(searchInput, searchContext)

      searchResult.scores should
        contain theSameElementsAs List(
          FileScore("file1", 0.0), // no words match `zz`
          FileScore("file2", 0.0), // no words match `zz`
          FileScore("file3", 0.0)  // no words match `zz`
        )
    }

    "generate overall score by doing the average score for each word when search `phrase` is passed for equality comparisons" in {
      val searchInput   = SearchInput("word1 word2")
      val searchContext = SearchContext(testInvertedIndex)
      val searchResult  = search(searchInput, searchContext)

      searchResult.scores should
        contain theSameElementsAs List(
          FileScore("file2", 100.0), // contains both words i.e (100 + 100) / 2 = 100
          FileScore("file1", 50.0),  // contains only one word i.e (100 + 0) / 2 = 50
          FileScore("file3", 0.0)    // contains none of the word i.e (0 + 0) / 2 = 0
        )
    }

    "generate overall score by doing the average score for each word when search `phrase` is provided for mixed comparisons" in {
      val searchInput   = SearchInput("word1 wor rd3")
      val searchContext = SearchContext(testInvertedIndex)
      val searchResult  = search(searchInput, searchContext)

      searchResult.scores should
        contain theSameElementsAs List(
          FileScore(
            "file1",
            90.0
          ), // matches `word1`(100), starts_with `wor`(90), contains `rd3`(80). So, (100 + 90 + 80) / 3 = 90
          FileScore(
            "file2",
            63.33
          ), // matches `word1`(100), starts_with `wor`(90), doesn't match `rd3`(0). So, (100 + 90 + 0) / 3 = 63.33
          FileScore(
            "file3",
            56.67
          ) // doesn't match `word1`(0), starts_with `wor`(90), contains `rd3`(80). So, (0 + 90 + 80)/ 3 = 56.67
        )
    }
  }

  private lazy val testInvertedIndex   = produceInvertedIndex(testFiles)
  private lazy val testNormalizedWords = produceNormalizedWords(testFiles)

}
