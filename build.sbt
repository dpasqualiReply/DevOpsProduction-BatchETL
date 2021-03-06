name := "BatchETL"

version := "0.1"

scalaVersion := "2.10.5"

resolvers ++= Seq(
  "All Spark Repository -> bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/"
)

libraryDependencies ++= Seq(

  "com.typesafe" % "config" % "1.3.2",
  "org.scalatest" % "scalatest_2.10" % "2.2.2" % "test",
  "org.apache.spark" % "spark-core_2.10" % "1.6.0" excludeAll (ExclusionRule(organization = "javax.servlet")),
  "org.apache.spark" % "spark-sql_2.10" % "1.6.0" excludeAll (ExclusionRule(organization = "javax.servlet")),
  "org.apache.hadoop" % "hadoop-common" % "2.7.0" excludeAll (ExclusionRule(organization = "javax.servlet")),
  "org.apache.spark" % "spark-hive_2.10" % "1.6.0" excludeAll (ExclusionRule(organization = "javax.servlet")),
  "org.apache.spark" % "spark-yarn_2.10" % "1.6.0" excludeAll (ExclusionRule(organization = "javax.servlet")),
  "org.apache.kudu" % "kudu-spark_2.10" % "1.5.0" excludeAll (ExclusionRule(organization = "javax.servlet"))
)

/*
-Dhttp.proxyHost=proxy.reply.it
-Dhttp.proxyPort=8080
 */

 assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

test in assembly := {}

