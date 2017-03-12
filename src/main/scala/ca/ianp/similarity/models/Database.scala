package ca.ianp.similarity.models

import ca.ianp.similarity.models._

import slick.driver.SQLiteDriver.api._

object Database {
  def create() = {
    val submissions = TableQuery[Submissions]
    db.run(submissions.schema.create)
  }
}
