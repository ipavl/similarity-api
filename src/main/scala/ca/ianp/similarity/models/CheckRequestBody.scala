package ca.ianp.similarity.models

case class CheckRequestBody(
  assignmentId: Int,
  threshold: Double,
  directory: String,
  language: String
)
