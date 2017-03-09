package ca.ianp.similarity

import ca.ianp.similarity.data.tables.Submissions

import org.http4s.server.blaze.BlazeBuilder
import slick.driver.SQLiteDriver.api._

object Server extends App {
  val db = Database.forURL("jdbc:sqlite:similarity.sqlite3", driver = "org.sqlite.JDBC")
  val submissions = TableQuery[Submissions]
  db.run(submissions.schema.create)

  BlazeBuilder.bindHttp(8080)
    .mountService(CheckService.service, "/")
    .run
    .awaitShutdown()
}
