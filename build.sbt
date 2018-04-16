import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.scalaJSUseMainModuleInitializer

name := "client-server-integration"

version := "0.1"

val app = crossProject.settings(
  unmanagedSourceDirectories in Compile +=
    baseDirectory.value  / "shared" / "main" / "scala",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "autowire"  % "0.2.6",
    "com.lihaoyi" %%% "scalatags" % "0.6.7",
    "com.lihaoyi" %%% "upickle"   % "0.6.5"
  ),
  scalaVersion := "2.12.5"
).jsSettings(
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.2"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http"  % "10.0.10",
    "com.typesafe.akka" %% "akka-actor" % "2.4.19",
    "org.webjars"       %  "bootstrap"  % "3.2.0"
  )
)

lazy val appJS = app.js
lazy val appJVM = app.jvm.settings(
  // Adds the output of fastOptJS on the client side to the resources folder on the server side
  (resources in Compile) += (fastOptJS in (appJS, Compile)).value.data
)

// This is an application with a main method
scalaJSUseMainModuleInitializer := true
