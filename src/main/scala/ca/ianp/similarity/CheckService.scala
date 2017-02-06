package ca.ianp.similarity

import org.http4s._
import org.http4s.server._
import org.http4s.dsl._

import _root_.argonaut._, Argonaut._
import org.http4s.argonaut._

import scala.sys.process._

object CheckService {
  val service = HttpService {
    case GET -> Root / "check" =>
      val cmd = "java -jar jplag.jar -l c/c++ -s corpus/"
      val output = cmd.!!

      Ok(jSingleObject("similarity", jString(s"${output}")))
  }
}
