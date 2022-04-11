package com.rock.search

import com.rock.search.SearchConsoleManager._
import com.rock.search.TestDataProvider._
import com.rock.search.engine.{InvertedIndexRecord, SearchContext}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayOutputStream, StringReader}

class SearchConsoleItSpec extends AnyWordSpec with Matchers {

  "Search Console" should {
    "successfully read search `word` as input from Console and print the results back" in {

      val testInvertedIndex: List[InvertedIndexRecord] = produceInvertedIndex(testFiles)
      val searchContext: SearchContext                 = SearchContext(testInvertedIndex)

      val inputSearchString =
        """|word1
      """.stripMargin

      val in  = new StringReader(inputSearchString)
      val out = new ByteArrayOutputStream()

      overrideInputOutputStream(out, in, searchContext)

      out.toString shouldBe
        """
          |Welcome to Search Console.
          |Enter `:quit` to exit the Console anytime.
          |Please enter your search input.
          |
          |search>
          |File => file2. Match Score => 100.0 %
          |File => file1. Match Score => 100.0 %
          |File => file3. Match Score => 0.0 %
          |""".stripMargin
    }
  }

  "successfully read search `phrase` as input from Console and print the results back" in {

    val testInvertedIndex: List[InvertedIndexRecord] = produceInvertedIndex(testFiles)
    val searchContext: SearchContext                 = SearchContext(testInvertedIndex)

    val inputSearchString =
      """|word1 d3
      """.stripMargin

    val in  = new StringReader(inputSearchString)
    val out = new ByteArrayOutputStream()

    overrideInputOutputStream(out, in, searchContext)

    out.toString shouldBe
      """
        |Welcome to Search Console.
        |Enter `:quit` to exit the Console anytime.
        |Please enter your search input.
        |
        |search>
        |File => file1. Match Score => 90.0 %
        |File => file2. Match Score => 50.0 %
        |File => file3. Match Score => 40.0 %
        |""".stripMargin
  }

  /**
   * Switches the default output stream to `out`. So, all the print statements will be sending values to `out`.
   * Switches the default input stream to `in`. All the readLine() operations will be reading from `in`.
   */
  private def overrideInputOutputStream(out: ByteArrayOutputStream, in: StringReader, searchContext: SearchContext) = {
    Console.withOut(out) {
      Console.withIn(in) {
        readLineAndSearch(searchContext)
      }
    }
  }

}
