package ca.ianp.similarity.providers

import ca.ianp.similarity.models.CheckRequestBody
import ca.ianp.similarity.models.Submission

import scala.sys.process._
import scala.compat.Platform.EOL

class JPlagProvider extends Provider {

  def supportedLanguages = Array(
    "java17", "java15", "java15dm", "java12", "java11", "c/c++", "c#-1.2", "char", "text", "scheme"
  )

  def translateLanguageIdentifier(language: String): String = {
    if (supportedLanguages contains language)
      language
    else
      language match {
        case "java" => "java17"
        case "c" | "cpp" => "c/c++"
      }
  }

  def convertOutput(outputData: String): Array[Submission] = {
    val pattern = "Comparing (.*)-(.*): (.*)".r
    val lines = outputData.split(EOL).filter(_.startsWith("Comparing"))

    lines map { line =>
      pattern.findFirstIn(line) match {
        case Some(pattern(user1, user2, similarity)) =>
          new Submission(user1, user2, similarity.toDouble)
      }
    }
  }

  def runChecker(settings: CheckRequestBody): String = {
    val language = translateLanguageIdentifier(settings.language)

    val cmd = s"java -jar jplag.jar -l ${language} -s corpus/"
    val output = cmd.!!

    output
  }

}
