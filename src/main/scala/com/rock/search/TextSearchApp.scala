package com.rock.search

import com.rock.search.SearchConsoleManager._
import com.rock.search.engine.SearchEngine
import com.rock.search.file.FileHandler

object TextSearchApp extends App {
  FileHandler
    .getDirectory(args)
    .fold(println, directory => initSearchConsole(SearchEngine.index(FileHandler.crawlFiles(directory))))
}
