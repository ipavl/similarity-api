package ca.ianp.similarity

import ca.ianp.similarity.providers._
import ca.ianp.similarity.models._

import org.http4s._
import org.http4s.server._
import org.http4s.dsl._

import _root_.argonaut._, Argonaut._, ArgonautShapeless._
import org.http4s.argonaut._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import com.redis._

object CheckService {
  val redis = new RedisClient("localhost", 6379)

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
      val stored = redis.get(cacheKey)

      stored match {
        case Some(result) => {
          // Parse returns a Scalaz "Either" of \/[String, Json] representing an error or the JSON
          Parse.parse(result) match {
            case Right(r) => Ok(r)
            case Left(r) => {
              // This should only happen if the stored data is malformed for some reason, so just
              // remove the faulty entry and ask the client to try again so they get a fresh value.
              println(s"Error getting cached data for key ${cacheKey}: ${r}")
              redis.del(cacheKey)
              InternalServerError(jSingleObject("error", "Internal server error. Please try again.".asJson))
            }
          }
        }

        case None => {
          val result = Submission.getStudentAssignmentResults(assignmentId, studentId).map(_.toList.asJson)

          result map {
            redis.setex(cacheKey, 3600, _)
          }

          Ok(result)
        }
      }
    }
  }
}
