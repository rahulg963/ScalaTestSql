name := "TestDB"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= {
  val akkaV = "2.4.9"
  Seq(
    "com.google.code.gson" % "gson" % "2.2.4",
    "org.mongodb.scala" %% "mongo-scala-driver" % "1.1.1" withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-http-core" % akkaV withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-actor" % akkaV withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-stream" % akkaV withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-agent" % akkaV withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-slf4j" % "2.3.9" withSources() withJavadoc(),
    "org.reactivemongo" %% "reactivemongo" % "0.11.14" withSources() withJavadoc(),
    "org.scalaj" %% "scalaj-http" % "2.3.0" withSources() withJavadoc(),
    "org.apache.httpcomponents" % "httpclient" % "4.5.2" withSources() withJavadoc(),
    "com.ecwid.consul" % "consul-api" % "1.1.11" withSources() withJavadoc(),
    "org.slf4j" % "slf4j-api" % "1.7.12" % "provided" withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-slf4j" % akkaV withSources() withJavadoc(),
    "mysql" % "mysql-connector-java" % "5.1.12"
  )
}
