import com.typesafe.sbt.packager.docker._
import Dependencies._

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin, AshScriptPlugin)
  .settings(commonSettings ++ buildInfoSettings ++ dockerSettings)

lazy val commonSettings = Seq(
  organization := "eu.radusw",
  scalaVersion := "2.12.4",
  version := "1.0",
  name := "todo-list",
  resolvers ++= projectResolvers,
  libraryDependencies ++= dependencies,
  scalacOptions ++= compileSettings,
  scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports",
                                              "-Xfatal-warnings"),
  scalafmtOnCompile := true,
  fork in run := true,
  fork in Test := true,
  fork in testOnly := true,
  connectInput in run := true,
  javaOptions in run ++= forkedJvmOption,
  javaOptions in Test ++= forkedJvmOption,
  mappings in Universal ++= (baseDirectory.value / "conf" * "*").get.map(x =>
    x -> ("conf/" + x.getName)),
  javaOptions in Universal ++= Seq(
    "-server",
    "-Dfile.encoding=UTF8",
    "-Duser.timezone=UTC",
    "-Dpidfile.path=/dev/null",
    "-J-Xss1m",
    "-J-XX:+CMSClassUnloadingEnabled",
    "-J-XX:ReservedCodeCacheSize=256m",
    "-J-XX:+DoEscapeAnalysis",
    "-J-XX:+UseConcMarkSweepGC",
    "-J-XX:+UseParNewGC",
    "-J-XX:+UseCodeCacheFlushing",
    "-J-XX:+UseCompressedOops"
  )
)

lazy val compileSettings = Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xfuture", // Turn on future language features.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match", // Pattern match may not be typesafe.
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification", // Enable partial unification in type constructor inference
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
)

lazy val forkedJvmOption = Seq(
  "-server",
  "-Dfile.encoding=UTF8",
  "-Duser.timezone=UTC",
  "-Xss1m",
  "-Xms2048m",
  "-Xmx2048m",
  "-XX:+CMSClassUnloadingEnabled",
  "-XX:ReservedCodeCacheSize=256m",
  "-XX:+DoEscapeAnalysis",
  "-XX:+UseConcMarkSweepGC",
  "-XX:+UseParNewGC",
  "-XX:+UseCodeCacheFlushing",
  "-XX:+UseCompressedOops"
)

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
  buildInfoPackage := "api",
  buildInfoOptions += BuildInfoOption.ToJson,
  buildInfoOptions += BuildInfoOption.BuildTime
)

lazy val dockerSettings = Seq(
  dockerUpdateLatest := true,
  defaultLinuxInstallLocation in Docker := "/opt/todo-list",
  dockerCommands := Seq(
    Cmd("FROM", "alpine:3.5"),
    Cmd("RUN apk upgrade --update && apk add --update openjdk8-jre"),
    Cmd("ADD", "opt /opt"),
    Cmd("WORKDIR", "/opt/todo-list"),
    ExecCmd("ENTRYPOINT", "bin/todo-list", "conf/docker.conf")
  ),
  dockerExposedPorts := Seq(9000),
  version in Docker := version.value,
  maintainer in Docker := "Radu Gancea <radu.gancea@gmail.com>",
  dockerRepository := Some("radusw")
)
