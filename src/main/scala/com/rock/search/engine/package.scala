package com.rock.search

package object engine {

  implicit class RichString(input: String) {

    /**
     * Removes all Punctuation characters from the String.
     * Punctuation characters are one of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
     */
    def normalize: String = {
      input.trim
        .toLowerCase()
        .replaceAll("""[\p{Punct}]""", "")
    }

    /**
     * Splits the given string based on one or more whitespaces.
     */
    def splitByWhitespaces: List[String] = {
      input.split("\\s+").toList
    }

  }

}
