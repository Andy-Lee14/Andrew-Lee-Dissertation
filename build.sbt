
import sbt.Keys._

import sbtdocker.BuildOptions.Remove.Always
import sbtrelease.ReleaseStateTransformations._

name := "safer-gambling-app"

version := "0.1"

scalaVersion := "2.12.11"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "org.codehaus.janino" % "janino" % "3.1.2",
  "net.logstash.logback" % "logstash-logback-encoder" % "5.1",

  "com.skybettingandgaming" %% "ngp-toolkit" % "2.2",

  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalaj" % "scalaj-http_2.12" % "2.3.0",
)

resolvers ++= Seq(

  "Confluent" at "https://packages.confluent.io/maven/",
  "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  "Artima Maven Repository" at "https://repo.artima.com/releases",
  "Artifactory" at "https://artifactory.euw.platformservices.io/artifactory/sbg-next-gen-promotions-sbt/",
  "GPROM_Artifactory" at "https://artifactory.euw.platformservices.io/artifactory/sbg-gaming-promotions-sbt-local/",
  "jitpack" at "https://jitpack.io/",
  "gseitz@github" at "https://gseitz.github.com/maven/"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

assemblyMergeStrategy in assembly := {
  case PathList("org", "slf4j", xs@_*) => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

mainClass in assembly := Some("myApp.Application")
test in assembly := {}

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(releaseStepTask(DockerKeys.dockerBuildAndPush in docker)),
  setNextVersion,
  commitNextVersion
)

enablePlugins(DockerPlugin)

docker := docker.dependsOn(assembly).value

val dockerRegistry = "docker.artifactory.euw.platformservices.io/"

credentials += Credentials("Artifactory Realm", "artifactory.euw.platformservices.io", sys.env.getOrElse("ARTIFACTORY_USERNAME", "n/a"), sys.env.getOrElse("ARTIFACTORY_APIKEY", "n/a"))

dockerfile in docker := {
  val artifact = (assemblyOutputPath in assembly).value
  val artifactTargetPath = s"/app/${artifact.name}"

  val args = List(
    "java",
    "-javaagent:/jmx/jmx_prometheus_javaagent-0.3.1.jar=8088:/jmx/jmx-config.yml",
    "-Dcom.sun.management.jmxremote",
    "-Dcom.sun.management.jmxremote.port=9999",
    "-Dcom.sun.management.jmxremote.rmi.port=9999",
    "-Dcom.sun.management.jmxremote.local.only=false",
    "-Dcom.sun.management.jmxremote.authenticate=false",
    "-Djava.rmi.server.hostname=0.0.0.0",
    "-Dcom.sun.management.jmxremote.ssl=false",
    "-jar", artifactTargetPath
  )

  new Dockerfile {
    from(s"${dockerRegistry}sbg-next-gen-promotions/java-sbg:8")
    add(assembly.value, artifactTargetPath)
    copy(baseDirectory(_ / "jmx-config.yml").value, "/jmx/")
    copy(baseDirectory(_ / "identified-problem-gamblers.txt").value, "/jmx/")
    sys.props.getOrElse("VERSION_NUMBER", "Remove-exec") match {
      case "Remove-exec" => runRaw(s"""echo 'exit;' >> /root/.bashrc""")
      case _ => println("Feature branch, not removing exec!")
    }
    cmd(args: _*)
  }
}

buildOptions in docker := BuildOptions(removeIntermediateContainers = Always, pullBaseImage = sbtdocker.BuildOptions.Pull.Always)

imageNames in docker := Seq(
  ImageName(s"${dockerRegistry}sbg-next-gen-promotions/${name.value}:${sys.props.getOrElse("VERSION_NUMBER", version.value)}")
)

def publishSnapshot = Command.command("publish-snapshot") { state =>
  val ourVersion = sys.props.get("VERSION_NUMBER").get
  val newState = Command.process(s"""set version := "$ourVersion-SNAPSHOT" """, state)
  val (s, _) = Project.extract(newState).runTask(publish in Compile, newState)
  s
}

commands += publishSnapshot

publishTo := version { v: String =>
  if (v.trim.endsWith("SNAPSHOT"))
    Some("Artifactory" at "https://artifactory.euw.platformservices.io/artifactory/sbg-next-gen-promotions-sbt;build.timestamp=" + System.currentTimeMillis())
  else
    Some("Artifactory" at "https://artifactory.euw.platformservices.io/artifactory/sbg-next-gen-promotions-sbt")
}.value