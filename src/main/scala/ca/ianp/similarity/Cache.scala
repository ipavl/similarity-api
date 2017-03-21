package ca.ianp.similarity

import ca.ianp.similarity.models.Submission

import _root_.argonaut._, Argonaut._
import com.redis._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Cache {

  /** Default cache expiry time in seconds */
  val DefaultExpiry = 3600

  /** Redis client to be used by all methods */
  val redis = new RedisClient("localhost", 6379)

  /** Returns a value from the cache if it exists, otherwise creates it as such and returns it.
   * 
   *  Returns the associated value for a given cacheKey if it exists, otherwise returns the value
   *  created via the private createAndSet method.
   *
   *  @param cacheKey the key to look up
   *  @param fn the function to run if no value was found
   *  @param expiry how long to keep values for in seconds if setting a key
   *
   *  @return a Future[argonaut.Json] containing the value that would be
   *          returned by the given fn if run itself
   */
  def getOrFetch(cacheKey: String,
                 fn: Future[Seq[Submission]],
                 expiry: Long = DefaultExpiry): Future[Json] = {

    val stored = redis.get(cacheKey)

    stored match {
      case Some(result) => {
        // Parse.parse returns a Scalaz "Either" of \/[String, Json] representing an
        // error (Left) or the JSON (Right)
        Parse.parse(result) match {
          case Right(r) => Future { r }
          case Left(r) => {
            // This should only happen if the stored data is malformed for some reason,
            // so just handle it as if the key was not found in the cache
            println(s"Error getting cached data for key ${cacheKey}: ${r}")
            createAndSet(cacheKey, fn, expiry)
          }
        }
      }

      case None => {
        createAndSet(cacheKey, fn, expiry)
      }
    }
  }

  /** Returns the value returned by fn and sets it in the cache for cacheKey.
   *
   *  @param cacheKey the key to look up
   *  @param fn the function to run if no value was found
   *  @param expiry how long to keep values for in seconds if setting a key
   *
   *  @return a Future[argonaut.Json] containing the value that would be
   *          returned by the given fn if run itself
   */
  private def createAndSet(cacheKey: String,
                   fn: Future[Seq[Submission]],
                   expiry: Long = DefaultExpiry): Future[Json] = {

    val result = fn.map(_.toList.asJson)

    result map {
      redis.setex(cacheKey, expiry, _)
    }

    result
  }
}
