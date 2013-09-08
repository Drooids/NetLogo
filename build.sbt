///
/// ThisBuild -- applies to subprojects too
///

scalaVersion in ThisBuild := "2.10.2"

scalacOptions in ThisBuild ++=
  "-deprecation -unchecked -feature -Xcheckinit -encoding us-ascii -target:jvm-1.6 -Xfatal-warnings -Ywarn-adapted-args -Yinline-warnings"
  .split(" ").toSeq

javacOptions in ThisBuild ++=
  "-g -deprecation -encoding us-ascii -Werror -Xlint:all -Xlint:-serial -Xlint:-fallthrough -Xlint:-path -source 1.6 -target 1.6"
  .split(" ").toSeq

// only log problems plz
ivyLoggingLevel in ThisBuild := UpdateLogging.Quiet

// this makes script-writing easier
retrieveManaged in ThisBuild := true

// we're not cross-building for different Scala versions
crossPaths in ThisBuild := false

nogen in ThisBuild  := { System.setProperty("org.nlogo.noGenerator", "true") }

libraryDependencies in ThisBuild ++= Seq(
  "asm" % "asm-all" % "3.3.1",
  "org.picocontainer" % "picocontainer" % "2.13.6",
  "org.jmock" % "jmock" % "2.5.1" % "test",
  "org.jmock" % "jmock-legacy" % "2.5.1" % "test",
  "org.jmock" % "jmock-junit4" % "2.5.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test",
  "org.scalatest" %% "scalatest" % "2.0.M6" % "test"
)

///
/// top-level project only
///

name := "NetLogo"

artifactName := { (_, _, _) => "NetLogo.jar" }

onLoadMessage := ""

resourceDirectory in Compile <<= baseDirectory(_ / "resources")

scalaSource in Compile <<= baseDirectory(_ / "src" / "main")

scalaSource in Test <<= baseDirectory(_ / "src" / "test")

javaSource in Compile <<= baseDirectory(_ / "src" / "main")

javaSource in Test <<= baseDirectory(_ / "src" / "test")

unmanagedSourceDirectories in Test <+= baseDirectory(_ / "src" / "tools")

unmanagedResourceDirectories in Compile <+= baseDirectory { _ / "resources" }

unmanagedResourceDirectories in Compile <+= baseDirectory { _ / "headless" / "resources" }

mainClass in Compile := Some("org.nlogo.app.App")

sourceGenerators in Compile <+= EventsGenerator.task

Extensions.extensionsTask

libraryDependencies ++= Seq(
  "steveroy" % "mrjadapter" % "1.2" from "http://ccl.northwestern.edu/devel/mrjadapter-1.2.jar",
  "ch.randelshofer" % "quaqua" % "7.3.4" from "http://ccl.northwestern.edu/devel/quaqua-7.3.4.jar",
  "ch.randelshofer" % "swing-layout" % "7.3.4" from "http://ccl.northwestern.edu/devel/swing-layout-7.3.4.jar"
)

all := { () }

all <<= all.dependsOn(
  packageBin in Compile,
  packageBin in Compile in NetLogoBuild.headless,
  compile in Test,
  Extensions.extensions)

///
/// settings from project/*.scala
///

seq(Testing.settings: _*)

seq(Packaging.settings: _*)

seq(Running.settings: _*)

seq(Depend.settings: _*)
