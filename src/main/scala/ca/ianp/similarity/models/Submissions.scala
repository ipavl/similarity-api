package ca.ianp.similarity.models

import argonaut._, Argonaut._
import slick.driver.MySQLDriver.api._

case class Submission(
  studentA: String,
  studentB: String,
  assignmentId: Int,
  jPlagResult: Double,
  id: Int = 0
)

object Submission {
  import scala.concurrent.Future

  val submissions = TableQuery[Submissions]

  implicit def SubmissionCodecJson: CodecJson[Submission] =
    casecodec5(Submission.apply, Submission.unapply)("studentA", "studentB", "assignmentId", "jPlagResult", "id")

  def add(submission: Submission): Unit = {
    db.run(submissions += submission)
  }

  def getById(submissionId: Int): Future[Seq[Submission]] = {
    val query = submissions.filter(_.id === submissionId)
    db.run(query.result)
  }

  def getAssignmentResults(assignmentId: Int): Future[Seq[Submission]] = {
    val query = submissions.filter(_.assignmentId === assignmentId)
    db.run(query.result)
  }

  def getStudentAssignmentResults(assignmentId: Int, studentId: String): Future[Seq[Submission]] = {
    val query = submissions.filter(s => s.assignmentId === assignmentId
      && (s.studentA === studentId || s.studentB === studentId))
    db.run(query.result)
  }
}

class Submissions(tag: Tag) extends Table[Submission](tag, "submissions") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def studentA: Rep[String] = column[String]("student_a")
  def studentB: Rep[String] = column[String]("student_b")
  def assignmentId: Rep[Int] = column[Int]("assignment_id")
  def jPlagResult: Rep[Double] = column[Double]("jplag_result")
  
  def * = (studentA, studentB, assignmentId, jPlagResult, id) <> ((Submission.apply _).tupled, Submission.unapply)

}
