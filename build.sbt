resolvers += "Flink 1.0.0 RC repository" at "https://repository.apache.org/content/repositories/orgapacheflink-1062/"

libraryDependencies ++= {
  val flinkVersion = "1.0.0"
  Seq(
    "org.apache.flink" %% "flink-streaming-scala" % flinkVersion,
    "org.apache.flink" %% "flink-clients" % flinkVersion,
    "joda-time" % "joda-time" % "2.9.2",
    "org.joda" % "joda-convert" % "1.2"
  )
}
