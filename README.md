similarity-api
==============

Usage
-----

A MySQL database will need to be set up and configured in `src/main/resources/application.conf`.

Using [sbt](http://www.scala-sbt.org/):

* `sbt run` to run the service
* `sbt ~re-start run` to run the service with [triggered restarts](https://github.com/spray/sbt-revolver#triggered-restart)

The service will be available on http://localhost:4910/.
