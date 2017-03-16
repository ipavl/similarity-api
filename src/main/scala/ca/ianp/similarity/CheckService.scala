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
          val results = jPlagProvider.convertOutput(jPlagProvider.runChecker(settings)).sortWith(_.jPlagResult < _.jPlagResult)

          // Always store the max result
          Submission.add(results.last)

          // Store all results above the given threshold excluding the already-stored max
          for (result <- results.filter(_.jPlagResult >= settings.threshold).dropRight(1)) {
              Submission.add(result)
          }
        }

        Ok()
      })

    case GET -> Root / "results" / IntVar(submissionId) => {
      val stored = redis.get(submissionId)

      stored match {
        case Some(result) => {
          Ok(result)
        }

        case None => {
          val result = Submission.getById(submissionId).map(_.toList.asJson)

          result map {
            redis.setex(submissionId, 3600, _)
          }

          Ok(result)
        }
      }
    }
  }
}
