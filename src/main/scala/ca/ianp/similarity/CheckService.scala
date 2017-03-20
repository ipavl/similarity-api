package ca.ianp.similarity

import ca.ianp.similarity.providers._
import ca.ianp.similarity.models._
import ca.ianp.similarity.Cache._

import org.http4s._
import org.http4s.server._
import org.http4s.dsl._

import _root_.argonaut._, Argonaut._, ArgonautShapeless._
import org.http4s.argonaut._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object CheckService {
  val service = HttpService {
    case req @ POST -> Root / "check" =>
      req.as(jsonOf[CheckRequestBody]).flatMap(settings => {
        // Process submissions asynchronous to not block the consuming application
        Future {
          val jPlagProvider = new JPlagProvider()
          val output = jPlagProvider.runChecker(settings)
          val results = jPlagProvider.convertOutput(settings, output).sortWith(_.jPlagResult < _.jPlagResult)

          // Always store the max result
          Submission.add(results.last)

          // Store all results above the given threshold excluding the already-stored max
          for (result <- results.filter(_.jPlagResult >= settings.threshold).dropRight(1)) {
              Submission.add(result)
          }
        }

        Ok()
      })

    case GET -> Root / "assignments" / IntVar(assignmentId) / "students" / studentId => {
      val cacheKey = s"result:${assignmentId}:${studentId}"
      val result = Cache.getOrFetch(cacheKey,
                                    Submission.getStudentAssignmentResults(assignmentId, studentId))

      result match {
        case Right(x) => Ok(x)
        case Left(x) => InternalServerError(x)
      }
    }
  }
}
