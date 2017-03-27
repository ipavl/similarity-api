package ca.ianp.similarity.models

case class CheckRequestBody(
  assignmentId: Int,
  threshold: Double,
  studentId: String,
  directory: String,
  language: String
)
