package ca.ianp.similarity.models

import argonaut._, Argonaut._
import slick.driver.MySQLDriver.api._

case class Submission(
  studentA: String,
  studentAVersion: String,
  studentB: String,
  studentBVersion: String,
  assignmentId: String,
  jPlagResult: Double,
  timestamp: Long = System.currentTimeMillis() / 1000L,
  id: Int = 0
)

object Submission {
  import scala.concurrent.Future

  val submissions = TableQuery[Submissions]

  implicit def SubmissionCodecJson: CodecJson[Submission] =
    casecodec8(Submission.apply, Submission.unapply)("studentA",
                                                     "studentAVersion",
                                                     "studentB",
                                                     "studentBVersion",
                                                     "assignmentId",
                                                     "jPlagResult",
                                                     "timestamp",
                                                     "id")

  def add(submission: Submission): Unit = {
    db.run(submissions += submission)
  }

  def getById(submissionId: Int): Future[Seq[Submission]] = {
    val query = submissions.filter(_.id === submissionId)
    db.run(query.result)
  }

  def getAssignmentResults(assignmentId: String): Future[Seq[Submission]] = {
    val query = submissions.filter(_.assignmentId === assignmentId)
    db.run(query.result)
  }

  def getStudentAssignmentResults(assignmentId: String, studentId: String): Future[Seq[Submission]] = {
    val query = submissions.filter(s => s.assignmentId === assignmentId
      && (s.studentA === studentId || s.studentB === studentId))
    db.run(query.result)
  }
}

class Submissions(tag: Tag) extends Table[Submission](tag, "submissions") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def studentA: Rep[String] = column[String]("student_a")
  def studentAVersion: Rep[String] = column[String]("student_a_version")
  def studentB: Rep[String] = column[String]("student_b")
  def studentBVersion: Rep[String] = column[String]("student_b_version")
  def assignmentId: Rep[String] = column[String]("assignment_id")
  def jPlagResult: Rep[Double] = column[Double]("jplag_result")
  def timestamp: Rep[Long] = column[Long]("timestamp")
  
  def * = (studentA,
           studentAVersion,
           studentB,
           studentBVersion,
           assignmentId,
           jPlagResult,
           timestamp,
           id) <> ((Submission.apply _).tupled, Submission.unapply)

}
