import com.typesafe.tools.mima.core._
import sbtcrossproject.crossProject
import org.typelevel.sbt.gha.WorkflowStep.Run
import org.typelevel.sbt.gha.WorkflowStep.Sbt

ThisBuild / tlBaseVersion := "1.2"

ThisBuild / tlCiHeaderCheck := false

ThisBuild / crossScalaVersions := Seq("2.13.16", "3.3.6")
ThisBuild / tlVersionIntroduced := List("2.13", "3").map(_ -> "1.1.0").toMap

ThisBuild / licenses := List(
  ("BSD-3-Clause", url("https://github.com/scodec/scodec-cats/blob/main/LICENSE"))
)

ThisBuild / developers := List(
  tlGitHubDev("mpilquist", "Michael Pilquist"),
  tlGitHubDev("durban", "Daniel Urban")
)

ThisBuild / tlFatalWarnings := false
ThisBuild / githubOwner := "igor-ramazanov-typelevel"
ThisBuild / githubRepository := "scodec"

ThisBuild / githubWorkflowPublishPreamble := List.empty
ThisBuild / githubWorkflowUseSbtThinClient := true
ThisBuild / githubWorkflowPublish := List(
  Run(
    commands = List("echo \"$PGP_SECRET\" | gpg --import"),
    id = None,
    name = Some("Import PGP key"),
    env = Map("PGP_SECRET" -> "${{ secrets.PGP_SECRET }}"),
    params = Map(),
    timeoutMinutes = None,
    workingDirectory = None
  ),
  Sbt(
    commands = List("+ publish"),
    id = None,
    name = Some("Publish"),
    cond = None,
    env = Map("GITHUB_TOKEN" -> "${{ secrets.GB_TOKEN }}"),
    params = Map.empty,
    timeoutMinutes = None,
    preamble = true
  )
)
ThisBuild / gpgWarnOnFailure := false

lazy val root = tlCrossRootProject.aggregate(core)

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("."))
  .settings(name := "scodec-cats")
  .settings(
    libraryDependencies ++= Seq(
      "org.scodec" %%% "scodec-bits" % "1.2.1",
      "org.scodec" %%% "scodec-core" % (if (tlIsScala3.value) "2.3.2" else "1.11.11"),
      "org.typelevel" %%% "cats-core" % "2.13.0",
      "org.typelevel" %%% "cats-laws" % "2.13.0" % Test,
      "org.typelevel" %%% "discipline-munit" % "2.0.0" % Test
    ),
    publishTo := githubPublishTo.value,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
  )
  .nativeSettings(
    tlVersionIntroduced := List("2.13", "3").map(_ -> "1.2.0").toMap
  )
