organization := "ca.ianp"
name := "similarity"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % "0.15.3a",
  "org.http4s" %% "http4s-dsl"          % "0.15.3a",
  "org.http4s" %% "http4s-argonaut"     % "0.15.3a"
)

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.6.4"

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies +=
  "com.github.alexarchambault" %% "argonaut-shapeless_6.2" % "1.2.0-M4"
