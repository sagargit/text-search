package com.rock.search

import com.rock.search.engine.{SearchContext, SearchEngine, SearchInput}
import com.rock.search.file.FileHandler

object TextSearchApp extends App {
  FileHandler
    .getDirectory(args)
    .fold(println, directory => initSearchConsole(SearchEngine.index(FileHandler.crawlFiles(directory))))

  def initSearchConsole(searchContext: SearchContext): Unit = {
    val quitCommand = ":quit"
    println(s"""
         |Welcome to search console.
         |Enter `$quitCommand` to exit the Console anytime.
         |Please enter your search input.
         |""".stripMargin)
    print("search>")
    val readInput = io.StdIn.readLine()
    readInput match {
      case `quitCommand` => System.exit(0)
      case _ =>
        SearchEngine.search(SearchInput(readInput), searchContext).scores.foreach(println)
        initSearchConsole(searchContext)
    }
  }
}
