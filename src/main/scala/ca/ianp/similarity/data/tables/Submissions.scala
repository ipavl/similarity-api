package ca.ianp.similarity.data.tables

import slick.driver.SQLiteDriver.api._
import slick.lifted.{ProvenShape}

case class Submission(
  submitter: String,
  jplag_result: Double,
  id: Option[Int] = None
)

class Submissions(tag: Tag) extends Table[(Int, String, Double)](tag, "SUBMISSIONS") {

  def id: Rep[Int] = column[Int]("SUB_ID", O.PrimaryKey, O.AutoInc)
  def submitter: Rep[String] = column[String]("SUBMITTER")
  def jplag_result: Rep[Double] = column[Double]("JPLAG_RESULT")
  
  def * : ProvenShape[(Int, String, Double)] =
    (id, submitter, jplag_result)

}
