package com.twitter.scaffold

import akka.actor.{ Actor, Props }

class Interpreter extends Actor {
  import Interpreter._

  import java.io.ByteArrayOutputStream
  import scala.tools.nsc._
  import scala.tools.nsc.interpreter._

  private[this] val interpreter = new Lol

  private[this] val completion = new JLineCompletion(interpreter)

  def receive = {
    case Complete(expression) =>
      val result = completion.topLevelFor(Parsed.dotted(expression, expression.length) withVerbosity 4)
      sender ! Completions(result)
    case Interpret(expression) =>
      val out = new ByteArrayOutputStream
      val result = Console.withOut(out) { interpreter.interpret(expression) }
      val response = result match {
        case Results.Success => Success(out.toString)
        case Results.Error | Results.Incomplete => Failure(out.toString)
      }
      sender ! response
  }
}

object Interpreter {

  import scala.tools.nsc._
  import scala.tools.nsc.interpreter._

  class Lol extends IMain({
    val settings = new Settings
    settings.usejavacp.value = true
    settings
  }) {
    override protected def parentClassLoader: ClassLoader = new DerpLoader(virtualDirectory, super.parentClassLoader)
  }

  class DerpLoader(root: io.AbstractFile, parent: ClassLoader) extends AbstractFileClassLoader(root, parent) {
    override def classBytes(name: String): Array[Byte] = {
      Console.err.println("omg classBytes: %s".format(name))
      val original = super.classBytes(name)
      val instrumented = com.atlassian.levee.instrumentor.LeveeInstrumentor.instrument(original)
      instrumented
    }
  }

  val props = Props[com.twitter.scaffold.Interpreter]

  // requests
  case class Interpret(expression: String)
  case class Complete(expression: String)

  // responses
  case class Success(output: String)
  case class Failure(output: String)
  case class Completions(results: Seq[String])

}
