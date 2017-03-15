package ca.ianp.similarity.models

import ca.ianp.similarity.models._

import slick.driver.MySQLDriver.api._

object Schema {
  def create() = {
    val submissions = TableQuery[Submissions]
    db.run(submissions.schema.create)
  }
}
