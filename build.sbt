ThisBuild / scalaVersion     := "3.5.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val zioVersion = "2.1.1"
val zioHttpVersion = "3.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "ziomicroservices",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio"       %% "zio-json"       % "0.7.3",
      "dev.zio"       %% "zio-http"       % zioHttpVersion,
      "dev.zio" %% "zio-test" % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt"      % zioVersion % Test,
      "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
      "dev.zio" %% "zio-http-testkit" % zioHttpVersion % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
