package ca.ianp.similarity.models

import argonaut._, Argonaut._
import slick.driver.SQLiteDriver.api._
import slick.lifted.{ProvenShape}

case class Submission(
  submitter: String,
  jplag_result: Double,
  id: Int = 0
)

object Submission {
  import scala.concurrent.Future

  val submissions = TableQuery[Submissions]

  implicit def SubmissionCodecJson: CodecJson[Submission] =
    casecodec3(Submission.apply, Submission.unapply)("submitter", "jplag_result", "id")

  def add(submission: Submission): Unit = {
    db.run(submissions += submission)
  }

  def getById(submissionId: Int): Future[Seq[Submission]] = {
    val query = submissions.filter(_.id === submissionId)
    db.run(query.result)
  }
}

class Submissions(tag: Tag) extends Table[Submission](tag, "SUBMISSIONS") {

  def id: Rep[Int] = column[Int]("SUB_ID", O.PrimaryKey, O.AutoInc)
  def submitter: Rep[String] = column[String]("SUBMITTER")
  def jplag_result: Rep[Double] = column[Double]("JPLAG_RESULT")
  
  def * = (submitter, jplag_result, id) <> ((Submission.apply _).tupled, Submission.unapply)

}
