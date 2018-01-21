import sbt._

object Dependencies {
  val akkaV      = "2.5.8"
  val akkaHttpV  = "10.1.0-RC1"
  val circeV     = "0.9.0"
  val akkaCirceV = "1.20.0-RC1"
  val logbackV   = "1.2.3"
  val configV    = "1.3.2"
  val enumV      = "1.5.12"
  val enumCV     = "1.5.15"
  val monixV     = "3.0.0-M3"
  val doobieV    = "0.5.0-M13"
  val flywayV    = "4.2.0"
  val loggingV   = "3.7.2"
  val scalaTestV = "3.0.4"
  val scheckV    = "1.13.5"


  lazy val projectResolvers = Seq.empty
  lazy val dependencies = testDependencies ++ rootDependencies


  lazy val testDependencies = Seq (
    "org.scalatest"          %% "scalatest"             % scalaTestV % Test,
    "org.scalacheck"         %% "scalacheck"            % scheckV    % Test
  )

  lazy val rootDependencies = Seq(
    "com.typesafe.akka"          %% "akka-http"             % akkaHttpV,
    "de.heikoseeberger"          %% "akka-http-circe"       % akkaCirceV,
    "io.monix"                   %% "monix"                 % monixV,
    "io.circe"                   %% "circe-core"            % circeV,
    "io.circe"                   %% "circe-generic"         % circeV,
    "io.circe"                   %% "circe-parser"          % circeV,
    "com.typesafe"               % "config"                 % configV,
    "com.typesafe.akka"          %% "akka-slf4j"            % akkaV,
    "ch.qos.logback"             % "logback-classic"        % logbackV,
    "com.typesafe.scala-logging" %% "scala-logging"         % loggingV,
    "org.tpolecat"               %% "doobie-core"           % doobieV,
    "org.tpolecat"               %% "doobie-hikari"         % doobieV,
    "org.tpolecat"               %% "doobie-postgres"       % doobieV,
    "org.flywaydb"               % "flyway-core"            % flywayV,
    "com.beachape"               %% "enumeratum"            % enumV,
    "com.beachape"               %% "enumeratum-circe"      % enumCV
  )
}
