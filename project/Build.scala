import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object KafkaUtilsBuild extends Build {

  def sharedSettings = Defaults.defaultSettings ++ assemblySettings ++ Seq(
    version := "0.2.2-snapshot",
    scalaVersion := "2.10.3",
    organization := "com.quantifind",
    scalacOptions := Seq("-deprecation", "-unchecked", "-optimize"),
    unmanagedJars in Compile <<= baseDirectory map { base => (base / "lib" ** "*.jar").classpath },
    retrieveManaged := true,
    transitiveClassifiers in Scope.GlobalScope := Seq("sources"),
    mergeStrategy in assembly := mergeFirst,
    resolvers ++= Seq(
      "sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
      "sonatype-releases" at "http://oss.sonatype.org/content/repositories/releases",
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/"),
    libraryDependencies ++= Seq(
      "log4j" % "log4j" % "1.2.17",
      "org.scalatest" %% "scalatest" % "1.9.1" % "test",
      "org.apache.kafka" %% "kafka" % "0.8.1"))

  val slf4jVersion = "1.6.1"

  //offsetmonitor project

  lazy val offsetmonitor = Project("offsetmonitor", file("."), settings = offsetmonSettings)

  lazy val mergeFirst: String => MergeStrategy = {
      case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
      case PathList("org", "apache", "jasper", xs @ _*) => MergeStrategy.first
      case PathList("org", "fusesource", xs @ _*) => MergeStrategy.first
      case PathList("org", "apache", "commons", xs @ _*) => MergeStrategy.first
      case PathList("org", "apache", "commons", "beanutils", xs @ _*) => MergeStrategy.first
      case PathList("org", "apache", "commons", "collections", xs @ _*) => MergeStrategy.first
      case PathList("com", "esotericsoftware", "minlog", xs @ _*) => MergeStrategy.first
      case PathList("org", "eclipse", xs @ _*) => MergeStrategy.first
      case PathList("META-INF", xs @ _*) =>
        (xs map {_.toLowerCase}) match {
          case ("changes.txt" :: Nil ) =>
            MergeStrategy.discard
          case ("manifest.mf" :: Nil) | ("eclipsef.rsa" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
            MergeStrategy.discard
          case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") || ps.last.endsWith("pom.properties") || ps.last.endsWith("pom.xml") =>
            MergeStrategy.discard
          case "plexus" :: xs =>
            MergeStrategy.discard
          case "services" :: xs =>
            MergeStrategy.filterDistinctLines
          case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
            MergeStrategy.filterDistinctLines
          case ps @ (x :: xs) if ps.last.endsWith(".jnilib") || ps.last.endsWith(".dll") =>
            MergeStrategy.first
          case ps @ (x :: xs) if ps.last.endsWith(".txt") =>
            MergeStrategy.discard
          case ("notice" :: Nil) | ("license" :: Nil) | ("mailcap" :: Nil )=>
            MergeStrategy.discard
          case _ => MergeStrategy.deduplicate
        }
      case "application.conf" => MergeStrategy.concat
      case "about.html" => MergeStrategy.discard
      case "plugin.properties" => MergeStrategy.first
      case _ => MergeStrategy.first
    }


  def offsetmonSettings = sharedSettings ++ Seq(
    name := "KafkaOffsetMonitor",
    libraryDependencies ++= Seq(
      "net.databinder" %% "unfiltered-filter" % "0.8.4",
      "net.databinder" %% "unfiltered-jetty" % "0.8.4",
      "net.databinder" %% "unfiltered-json4s" % "0.8.4",
      "com.quantifind" %% "sumac" % "0.3.0",
      "com.typesafe.slick" %% "slick" % "2.0.0",
      "org.xerial" % "sqlite-jdbc" % "3.7.2",
      "com.twitter" % "util-core" % "3.0.0"),
    resolvers ++= Seq(
      "java m2" at "http://download.java.net/maven/2",
      "twitter repo" at "http://maven.twttr.com"))
}

