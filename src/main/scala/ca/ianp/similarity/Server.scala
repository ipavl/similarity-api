package ca.ianp.similarity

import ca.ianp.similarity.models.Database

import org.http4s.server.blaze.BlazeBuilder

object Server extends App {
  Database.create()

  BlazeBuilder.bindHttp(4910)
    .mountService(CheckService.service, "/")
    .run
    .awaitShutdown()
}
