import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "guive"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "com.twitter" %% "finagle-core" % "6.5.2",
    "com.twitter" %% "finagle-http" % "6.5.2"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
