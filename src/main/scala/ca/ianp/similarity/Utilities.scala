package ca.ianp.similarity

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Success, Failure}
import scalaz.concurrent.Task
import scalaz.Scalaz._

object Utilities {
  def futureToTask[A](future: => Future[A])(implicit ec: ExecutionContext): Task[A] = {
    Task.async { callback =>
      future.onComplete {
        case Success(a) => callback(a.right)
        case Failure(t) => callback(t.left)
      }
    }
  }
}
