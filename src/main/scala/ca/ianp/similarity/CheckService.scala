package ca.ianp.similarity

import ca.ianp.similarity.providers._
import ca.ianp.similarity.data.CheckRequestBody

import org.http4s._
import org.http4s.server._
import org.http4s.dsl._

import _root_.argonaut._, Argonaut._, ArgonautShapeless._
import org.http4s.argonaut._

object CheckService {
  val service = HttpService {
    case req @ POST -> Root / "check" =>
      req.as(jsonOf[CheckRequestBody]).flatMap(settings => {
        val jPlagProvider = new JPlagProvider()
        val output = jPlagProvider.runChecker(settings)

        Ok(jSingleObject("similarity", jString(s"${output}")))
      })
  }
}
