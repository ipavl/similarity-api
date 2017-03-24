package ca.ianp.similarity

import ca.ianp.similarity.models.Schema

import org.http4s.server.blaze.BlazeBuilder

object Server extends App {
  Schema.create()

  BlazeBuilder.bindHttp(4910)
    .mountService(WebService.service, "/")
    .run
    .awaitShutdown()
}
