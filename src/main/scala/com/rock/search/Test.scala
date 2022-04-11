package com.rock.search

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, StringReader}
import scala.io.StdIn

object Test extends App {

  def vulcanIO(): Unit = {
    println("Welcome to Vulcan. What's your name?")
    val name = StdIn.readLine()
    println("What planet do you come from?")
    val planet = StdIn.readLine()
    println(s"Live Long and Prosper 🖖, $name from $planet.")
  }

  val inputStr =
    """|Jean-Luc Picard
       |Earth
      """.stripMargin
  val in  = new StringReader(inputStr)
  val out = new ByteArrayOutputStream()
  Console.withOut(out) {
    Console.withIn(in) {
      vulcanIO()
    }
  }

  println(out.toString)
}
