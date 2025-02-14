import Dependencies._

ThisBuild / scalaVersion     := "2.13.15"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val Versions = new {
  val Pekko     = "1.1.3"
  val PekkoHttp = "1.1.0"
  val tapir     = "1.11.12"
}

javaOptions ++= Seq(
  "-javaagent:/Users/medmison/opentelemetry-javaagent-2.12.0-SNAPSHOT.jar",
  "-Dotel.javaagent.debug=true",
  "-Dotel.service.name=sports-catalog-api",
  "-Dotel.exporter.otlp.endpoint=http://localhost:4317",
  "-Dotel.exporter.otlp.protocol=grpc",
  "-Dotel.traces.sampler=parentbased_always_on",
  "-Dotel.metrics.exporter=otlp",
  "-Dotel.logs.exporter=otlp",
  "-Dotel.traces.exporter=none",
  "-Dotel.metrics.exporter=otlp"
)

fork := true

lazy val root = (project in file("."))
  .settings(
    name := "tapir-javaagent-repro",
    libraryDependencies ++= Seq(
      // Pekko core dependencies
      "org.apache.pekko" %% "pekko-slf4j"  % Versions.Pekko,
      "org.apache.pekko" %% "pekko-http"   % Versions.PekkoHttp,
      "org.apache.pekko" %% "pekko-stream" % Versions.Pekko,

      // Tapir dependencies
      "com.softwaremill.sttp.tapir" %% "tapir-core"              % Versions.tapir,
      "com.softwaremill.sttp.tapir" %% "tapir-pekko-http-server" % Versions.tapir,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % Versions.tapir, // Added for JSON support

      // Circe dependencies
      "io.circe" %% "circe-core"    % "0.14.5", // Added for Circe
      "io.circe" %% "circe-generic" % "0.14.5", // Added for Circe
      munit       % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
