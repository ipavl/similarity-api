similarity-api
==============

Similarity API is a RESTful API wrapper around software similarity services written in Scala. It is intended for use alongside a learning management system (LMS) as a microservice to detect software plagiarism in programming courses. The standalone version of [JPlag](https://jplag.ipd.kit.edu/) is currently the only supported service, however the wrapper could be extended to support others in the future.

Prerequisites
-------------

A MySQL database will need to be set up and configured in `src/main/resources/application.conf`. These values can also be set by setting the `SIMILARITY_DB_URL`, `SIMILARITY_DB_USER`, and `SIMILARITY_DB_PASSWORD` environment variables. [Redis](https://redis.io/) will also need to be installed and running on localhost port 6379.

In order to set up JPlag for use by the wrapper, download a [.jar release](https://github.com/jplag/jplag/releases) and place it alongside the wrapper with the file name `jplag.jar`. Version v2.11.8-SNAPSHOT of JPlag is currently supported.

Usage
-----

Using [sbt](http://www.scala-sbt.org/):

* `sbt run` to run the service
* `sbt ~re-start run` to run the service with [triggered restarts](https://github.com/spray/sbt-revolver#triggered-restart)

The service will be available on http://localhost:4910/.

Building
--------

To create a .jar file with dependencies for distribution, run `sbt assembly`.
