package ca.ianp.similarity.providers

import ca.ianp.similarity.Provider
import ca.ianp.similarity.models.CheckRequestBody

import scala.sys.process._

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

  def runChecker(settings: CheckRequestBody): String = {
    val language = translateLanguageIdentifier(settings.language)

    val cmd = s"java -jar jplag.jar -l ${language} -s corpus/"
    val output = cmd.!!

    output
  }

}
