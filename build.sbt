
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val DeepIntegrationTest = IntegrationTest.extend(Test)

lazy val textSearch = (project in file("."))
  .configs(DeepIntegrationTest)
  .settings(
    name := "text-search",
    organization := "com.rock",
    scalaVersion := "2.13.8",
    version := "0.1",

    Compile / run / mainClass := Some("com.rock.search.TextSearchApp"),
    Test / parallelExecution := false,
    Defaults.itSettings,
    inConfig(IntegrationTest)(
      ScalafmtPlugin.scalafmtConfigSettings
    ),
    IntegrationTest / fork := true,
    scalafmtOnCompile := true,
    libraryDependencies ++= {
      Seq("org.scalatest" %% "scalatest" % "3.2.11"  % Test)
    }
  )


