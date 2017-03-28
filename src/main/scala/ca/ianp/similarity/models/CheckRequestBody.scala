package ca.ianp.similarity.models

case class CheckRequestBody(
  assignmentId: String,
  threshold: Double,
  studentId: String,
  directory: String,
  language: String
)
