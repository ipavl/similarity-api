package ca.ianp.similarity.providers

import ca.ianp.similarity.models.CheckRequestBody
import ca.ianp.similarity.models.Submission

trait Provider {
  /** An array of strings used to set the provider's language to check,
   *  if applicable.
   */
  def supportedLanguages: Array[String]

  /** Return the appropriate language identifier for the provider given the
   *  generic version used by this wrapper service.
   *
   *  Generally this will be implemented as:
   *
   *  {{{
   *  if (supportedLanguages contains language)
   *    language
   *  else
   *    language match {
   *      case "java" => // provider's identifier for Java
   *      case "c" =>    // provider's identifier for C
   *      ...
   *    }
   *  }}}
   *
   *  @param language the API's generic identifier for a language
   *
   *  @return the provider-specific language identifier
   */
  def translateLanguageIdentifier(language: String): String

  /** Parse the output of running the provider's check for the results.
   *
   *  @param settings the original request body containing configuration
   *  @param outputData the output returned by the runChecker method
   *
   *  @return an array of Submission results
   */
  def convertOutput(settings: CheckRequestBody, outputData: String): Array[Submission]

  /** Execute the provider's similarity check using the given settings.
   *
   *  @param settings the original request body containing configuration
   *
   *  @return a parsable string representation of the results
   */
  def runChecker(settings: CheckRequestBody): String
}
