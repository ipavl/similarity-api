similarity-api
==============

Prerequisites
-------------

A MySQL database will need to be set up and configured in `src/main/resources/application.conf`. These values can also be set by setting the `SIMILARITY_DB_USER`, `SIMILARITY_DB_USER`, and `SIMILARITY_DB_PASSWORD` environment variables.

[Redis](https://redis.io/) will also need to be installed and running on localhost port 6379.

Usage
-----

Using [sbt](http://www.scala-sbt.org/):

* `sbt run` to run the service
* `sbt ~re-start run` to run the service with [triggered restarts](https://github.com/spray/sbt-revolver#triggered-restart)

The service will be available on http://localhost:4910/.

Building
--------

To create a .jar file with dependencies for distribution, run `sbt assembly`.
