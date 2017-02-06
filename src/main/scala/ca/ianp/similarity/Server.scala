package ca.ianp.similarity

import org.http4s.server.blaze.BlazeBuilder

object Server extends App {
  BlazeBuilder.bindHttp(8080)
    .mountService(CheckService.service, "/")
    .run
    .awaitShutdown()
}
