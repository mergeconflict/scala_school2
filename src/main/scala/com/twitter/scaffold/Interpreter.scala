package com.twitter.scaffold

import akka.actor.{ Actor, Props }

class Interpreter extends Actor {
  import Interpreter._

  import java.io.ByteArrayOutputStream
  import scala.tools.nsc._
  import scala.tools.nsc.interpreter._

  private[this] val interpreter = new SandboxedIMain
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

  import com.atlassian.levee.runtime.LeveeProperties._
  import scala.tools.nsc._
  import scala.tools.nsc.interpreter._

  sys.props(LEVEE_TIMEOUT_PROPERTY) = "1000"
  sys.props(LEVEE_WHITELIST_PACKAGES_PROPERTY) = "$line,java/lang,scala"

  class SandboxedIMain extends IMain({
    val settings = new Settings
    settings.usejavacp.value = true
    settings
  }) {
    override protected def parentClassLoader: ClassLoader =
      new AbstractFileClassLoader(virtualDirectory, super.parentClassLoader) {
        override def classBytes(name: String): Array[Byte] = {
          val original = super.classBytes(name)
          val instrumented = com.atlassian.levee.instrumentor.Instrumentor.instrument(original)
          instrumented
        }
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
