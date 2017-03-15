package ca.ianp.similarity.models

import argonaut._, Argonaut._
import slick.driver.SQLiteDriver.api._
import slick.lifted.{ProvenShape}

case class Submission(
  studentA: String,
  studentB: String,
  jPlagResult: Double,
  id: Int = 0
)

object Submission {
  import scala.concurrent.Future

  val submissions = TableQuery[Submissions]

  implicit def SubmissionCodecJson: CodecJson[Submission] =
    casecodec4(Submission.apply, Submission.unapply)("studentA", "studentB", "jPlagResult", "id")

  def add(submission: Submission): Unit = {
    db.run(submissions += submission)
  }

  def getById(submissionId: Int): Future[Seq[Submission]] = {
    val query = submissions.filter(_.id === submissionId)
    db.run(query.result)
  }
}

class Submissions(tag: Tag) extends Table[Submission](tag, "submissions") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def studentA: Rep[String] = column[String]("student_a")
  def studentB: Rep[String] = column[String]("student_b")
  def jPlagResult: Rep[Double] = column[Double]("jplag_result")
  
  def * = (studentA, studentB, jPlagResult, id) <> ((Submission.apply _).tupled, Submission.unapply)

}
