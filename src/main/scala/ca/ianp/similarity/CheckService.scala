package ca.ianp.similarity

import ca.ianp.similarity.providers._
import ca.ianp.similarity.data.CheckRequestBody
import ca.ianp.similarity.data.tables.Submissions

import org.http4s._
import org.http4s.server._
import org.http4s.dsl._

import _root_.argonaut._, Argonaut._, ArgonautShapeless._
import org.http4s.argonaut._

import slick.driver.SQLiteDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

object CheckService {
  val service = HttpService {
    case req @ POST -> Root / "check" =>
      req.as(jsonOf[CheckRequestBody]).flatMap(settings => {
        val jPlagProvider = new JPlagProvider()
        val output = jPlagProvider.runChecker(settings)

        val submissions = TableQuery[Submissions]
        val db = Database.forURL("jdbc:sqlite:similarity.sqlite3", driver = "org.sqlite.JDBC")

        db.run(submissions += (0, "test", 39.24))

        Ok()
      })
  }
}
