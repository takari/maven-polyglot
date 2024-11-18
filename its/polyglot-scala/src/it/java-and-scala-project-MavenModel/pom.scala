import org.sonatype.maven.polyglot.scala.model._
import scala.collection.immutable.Seq

Model(
  gav = "it.scala-maven-plugin" % "testJavaAndScala" % "1.0-SNAPSHOT",
  name = "Test for Java + Scala compilation",
  description = "Test for Java + Scala compilation",
  dependencies = Seq(
    "org.scala-lang" % "scala-library" % "2.13.12"
  ),
  build = Build(
    pluginManagement = PluginManagement(
      plugins = Seq(
        Plugin(
          "net.alchim31.maven" % "scala-maven-plugin" % "4.8.1"
        ),
        Plugin(
          "org.apache.maven.plugins" % "maven-compiler-plugin" % "3.11.0"
        )
      )
    ),
    plugins = Seq(
      Plugin(
        "net.alchim31.maven" % "scala-maven-plugin" % "4.8.1",
        executions = Seq(
          Execution(
            id = "scala-compile-first",
            phase = "process-resources",
            goals = Seq(
              "compile"
            )
          ),
          Execution(
            id = "scala-test-compile",
            phase = "process-test-resources",
            goals = Seq(
              "testCompile"
            )
          )
        )
      )
    )
  ),
  modelVersion = "4.0.0"
)
