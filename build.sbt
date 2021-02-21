import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.scalaJSUseMainModuleInitializer

name := "client-server-integration"

version := "0.1"

val app = crossProject.settings {
  scalaVersion := "2.12.5"

  unmanagedSourceDirectories in Compile +=
    baseDirectory.value  / "shared" / "main" / "scala"

  val circeV = "0.9.1"

  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "autowire"      % "0.2.6",
    "com.lihaoyi" %%% "scalatags"     % "0.6.7",
    "com.lihaoyi" %%% "upickle"       % "0.6.5",
    "io.circe"    %%% "circe-core"    % circeV,
    "io.circe"    %%% "circe-generic" % circeV,
    "io.circe"    %%% "circe-parser"  % circeV
  )
}.jsSettings {
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.2"
  )
}.jvmSettings {
  val akkaV = "2.4.19"
  val akkaHttpV = "10.0.10"
  val akkaHttpCirceV = "1.18.1"

  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaV,
    "com.typesafe.akka" %% "akka-http"       % akkaHttpV,
    "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceV,
    "org.webjars"       %  "bootstrap"       % "3.2.0"
  )
}

lazy val appJS = app.js
lazy val appJVM = app.jvm.settings(
  // Adds the output of fastOptJS on the client side to the resources folder on the server side
  (resources in Compile) += (fastOptJS in (appJS, Compile)).value.data
)

// This is an application with a main method
scalaJSUseMainModuleInitializer := true
