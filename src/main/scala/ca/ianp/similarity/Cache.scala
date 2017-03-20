package ca.ianp.similarity

import ca.ianp.similarity.models.Submission

import _root_.argonaut._, Argonaut._
import com.redis._

import scala.Either
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Cache {

  /** Returns a value from the cache if it exists, otherwise creates it as such and returns it.
   * 
   *  Returns the associated value for a given cacheKey if it exists, otherwise creates the
   *  value using the given fn, adds it to the cache, and returns the value.
   *
   *  An error message will be returned if the data in the cache is malformed and then the
   *  affected key will be removed from the cache.
   *
   *  @param cacheKey the key to look up
   *  @param fn the function to run if no value was found
   *  @param expiry how long to keep values for in seconds if setting a key
   *
   *  @return a [[scala.Either]] representing Left(error) as [[argonaut.Json]] or
   *          Right(value) as [[scala.concurrent.Future[argonaut.Json]]]
   */
  def getOrFetch(cacheKey: String,
                 fn: Future[Seq[Submission]],
                 expiry: Long = 3600): Either[Json, Future[Json]] = {

    val redis = new RedisClient("localhost", 6379)
    val stored = redis.get(cacheKey)

    stored match {
      case Some(result) => {
        // Parse.parse returns a Scalaz "Either" of \/[String, Json] representing an
        // error (Left) or the JSON (Right)
        Parse.parse(result) match {
          case Right(r) => {
            // The Future in this instance is simply to keep to the pattern of
            // Either[error, results]
            Right(Future { r })
          }
          case Left(r) => {
            // This should only happen if the stored data is malformed for some reason,
            // so just remove the faulty entry and ask the client to try again so they
            // get a fresh value
            println(s"Error getting cached data for key ${cacheKey}: ${r}")
            redis.del(cacheKey)
            Left(jSingleObject("error", "Internal server error. Please try again.".asJson))
          }
        }
      }

      case None => {
        val result = fn.map(_.toList.asJson)

        result map {
          redis.setex(cacheKey, expiry, _)
        }

        Right(result)
      }
    }
  }
}
