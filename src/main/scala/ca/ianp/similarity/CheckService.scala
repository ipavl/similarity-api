package ca.ianp.similarity

import ca.ianp.similarity.providers._
import ca.ianp.similarity.models._

import org.http4s._
import org.http4s.server._
import org.http4s.dsl._

import _root_.argonaut._, Argonaut._, ArgonautShapeless._
import org.http4s.argonaut._

import slick.driver.SQLiteDriver.api._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object CheckService {
  val service = HttpService {
    case req @ POST -> Root / "check" =>
      req.as(jsonOf[CheckRequestBody]).flatMap(settings => {
        val jPlagProvider = new JPlagProvider()
        val output = jPlagProvider.runChecker(settings)

        Submission.add(Submission("test", 39.24))

        Ok()
      })

    case GET -> Root / "results" / IntVar(submissionId) =>
      Ok(Submission.getById(submissionId).map(_.toList.asJson))
  }
}
