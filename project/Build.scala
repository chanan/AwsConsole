import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "AwsConsole"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.google.inject" % "guice" % "3.0",
    "org.webjars" % "webjars-play" % "2.1.0",
    "org.webjars" % "jquery" % "1.9.1",
    "org.webjars" % "bootswatch" % "2.3.1",
    "org.webjars" % "font-awesome" % "3.0.2",
    "org.webjars" % "bootstrap-timepicker" % "0.2.1",
    "securesocial" % "securesocial" % "master-SNAPSHOT",
    "mysql" % "mysql-connector-java" % "5.1.18",
    "org.javatuples" % "javatuples" % "1.2", 
    javaCore,
    javaJdbc,
    javaEbean
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
  )

}
