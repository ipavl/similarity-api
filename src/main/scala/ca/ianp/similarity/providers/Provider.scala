package ca.ianp.similarity.providers

import ca.ianp.similarity.models.CheckRequestBody
import ca.ianp.similarity.models.Submission

trait Provider {
    def supportedLanguages: Array[String]

    def translateLanguageIdentifier(language: String): String

    def convertOutput(outputData: String): Array[Submission]

    def runChecker(settings: CheckRequestBody): String
}
