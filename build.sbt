ThisBuild / scalaVersion := "2.13.1"
ThisBuild / organization := "dev.nomadblacky"
ThisBuild / organizationName := "NomadBlacky"
ThisBuild / resolvers ++= Seq(
  Resolver.bintrayRepo("digdag", "maven")
)

val versions = new {
  val digdag = "0.9.39"
}

lazy val root = (project in file("."))
  .settings(
    name := "digdag-plugin-datadog",
    scalacOptions ++= Seq(
        "-deprecation",
        "-feature",
        "-unchecked",
        "-Xlint",
        "-Xfatal-warnings",
        "-Ywarn-dead-code",
        "-Ywarn-numeric-widen"
      ),
    libraryDependencies ++= Seq(
        "io.digdag"                  % "digdag-client"            % versions.digdag % Provided,
        "io.digdag"                  % "digdag-spi"               % versions.digdag % Provided,
        "io.digdag"                  % "digdag-plugin-utils"      % versions.digdag % Provided,
        "dev.nomadblacky"            %% "scaladog"                % "0.4.1",
        "com.typesafe.scala-logging" %% "scala-logging"           % "3.9.2",
        "org.slf4j"                  % "slf4j-api"                % "1.7.26",
        "javax.inject"               % "javax.inject"             % "1",
        "org.scalatest"              %% "scalatest"               % "3.0.8" % Test,
        "ch.qos.logback"             % "logback-classic"          % "1.2.3" % Test,
        "org.mockito"                %% "mockito-scala-scalatest" % "1.7.1" % Test
      )
  )
