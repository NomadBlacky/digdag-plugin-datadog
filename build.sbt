import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / organization := "dev.nomadblacky"
ThisBuild / organizationName := "NomadBlacky"
ThisBuild / resolvers ++= Seq(
  Resolver.bintrayRepo("digdag", "maven")
)

val versions = new {
  val digdag = IO.read(file("./digdag.version")).trim
}

val digdagDeps = Seq(
  "io.digdag"        % "digdag-client"       % versions.digdag % Provided,
  "io.digdag"        % "digdag-spi"          % versions.digdag % Provided,
  "io.digdag"        % "digdag-plugin-utils" % versions.digdag % Provided,
  "org.slf4j"        % "slf4j-api"           % "1.7.29",
  "javax.inject"     % "javax.inject"        % "1",
  "com.google.guava" % "guava"               % "27.1-jre"
)

lazy val digdagPlguinDatadog = (project in file("."))
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
    Test / scalacOptions -= "-Ywarn-dead-code",
    libraryDependencies ++= digdagDeps,
    libraryDependencies ++= Seq(
      "dev.nomadblacky"            %% "scaladog"                % "0.5.1",
      "com.typesafe.scala-logging" %% "scala-logging"           % "3.9.2",
      "com.beachape"               %% "enumeratum"              % "1.6.1",
      "org.scalatest"              %% "scalatest"               % "3.2.1"  % Test,
      "ch.qos.logback"              % "logback-classic"         % "1.2.3"  % Test,
      "org.mockito"                %% "mockito-scala-scalatest" % "1.10.4" % Test
    ),
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("publishSigned"),
      releaseStepCommand("sonatypeBundleRelease"),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )
