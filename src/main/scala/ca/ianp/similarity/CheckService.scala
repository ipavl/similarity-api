package ca.ianp.similarity

import ca.ianp.similarity.providers._
import ca.ianp.similarity.data.CheckRequestBody
import ca.ianp.similarity.data.tables._
import ca.ianp.similarity.Utilities._

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
  val db = Database.forURL("jdbc:sqlite:similarity.sqlite3", driver = "org.sqlite.JDBC")
  val submissions = TableQuery[Submissions]

  val service = HttpService {
    case req @ POST -> Root / "check" =>
      req.as(jsonOf[CheckRequestBody]).flatMap(settings => {
        val jPlagProvider = new JPlagProvider()
        val output = jPlagProvider.runChecker(settings)

        db.run(submissions += Submission("test", 39.24))

        Ok()
      })

    case GET -> Root / "results" / IntVar(submissionId) =>
      val query = db.run(submissions.filter(_.id === submissionId).result)

      Utilities.futureToTask(query) flatMap { submission =>
        Ok(submission.toList.asJson)
      }
  }
}
