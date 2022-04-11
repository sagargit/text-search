package com.rock.search

import com.rock.search.engine.{SearchContext, SearchEngine, SearchInput}

object SearchConsoleManager {

  def readLineAndSearch(searchContext: SearchContext): SearchInteractionResult = {
    val quitCommand = ":quit"
    println(s"""
               |Welcome to Search Console.
               |Enter `$quitCommand` to exit the Console anytime.
               |Please enter your search input.
               |""".stripMargin)
    print("search>")
    val readInput = io.StdIn.readLine()
    readInput match {
      case `quitCommand` => QuitCommandReceived
      case normalInput =>
        println("") // print results in new line
        SearchEngine.search(SearchInput(normalInput), searchContext).scores.foreach(println)
        SearchResultProvided
    }
  }

  def initSearchConsole(searchContext: SearchContext): Unit = {
    readLineAndSearch(searchContext) match {
      case QuitCommandReceived =>
        System.exit(0)
      case SearchResultProvided => initSearchConsole(searchContext)
    }
  }

  sealed trait SearchInteractionResult

  private case object QuitCommandReceived  extends SearchInteractionResult
  private case object SearchResultProvided extends SearchInteractionResult

}
