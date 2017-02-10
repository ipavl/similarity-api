package ca.ianp.similarity

import ca.ianp.similarity.data.CheckRequestBody

trait Provider {
    def supportedLanguages: Array[String]

    def translateLanguageIdentifier(language: String): String

    def runChecker(settings: CheckRequestBody): String
}
