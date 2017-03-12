package ca.ianp.similarity

import ca.ianp.similarity.models.CheckRequestBody

trait Provider {
    def supportedLanguages: Array[String]

    def translateLanguageIdentifier(language: String): String

    def runChecker(settings: CheckRequestBody): String
}
