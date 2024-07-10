/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.sonatype.maven.polyglot.scala.ScalaConverters._
import org.sonatype.maven.polyglot.scala.model._
import org.codehaus.plexus.util.IOUtil
import java.io.StringWriter
import java.util.Collections

import scala.collection.immutable

@RunWith(classOf[JUnitRunner])
class ScalaModelSpec extends Specification {

  val writer = new ScalaModelWriter

  def writeAndCompare(m: Model, f: String): Boolean = {
    val sw = new StringWriter
    writer.write(sw, Collections.emptyMap[String, AnyRef](), m.asJava)
    sw.toString must_== IOUtil.toString(getClass.getClassLoader.getResourceAsStream(f))
  }

  implicit val scalaVersion: ScalaVersion = ScalaVersion("2.10.2")

  "The ScalaModel" should {
    "configure a project with the minimal requirements" in {
      val m = ScalaModel("someGroupId" % "someArtifactId" % "someVersion")

      writeAndCompare(m, "minimal-scala-pom.scala")
    }
    "configure a project and not have its scala settings overridden" in {
      val m = ScalaModel(
        "" % "tesla-polyglot-scala",
        dependencies = immutable.Seq(
          "org.scala-lang" % "scala-library" % "0",
          "org.specs2" % "specs2_2.10" % "2.1.1" % "test",
          "junit" % "junit" % "" % "test"
        ),
        build = Build(
          sourceDirectory = "src/main/scala2",
          testSourceDirectory = "src/test/scala2",
          pluginManagement =
            PluginManagement(immutable.Seq(Plugin("org.eclipse.m2e" % "lifecycle-mapping" % "0"))),
          plugins = immutable.Seq(
            Plugin("org.apache.maven.plugins" % "maven-compiler-plugin" % "0"),
            Plugin("net.alchim31.maven" % "scala-maven-plugin" % "0"),
            Plugin("org.apache.maven.plugins" % "maven-surefire-plugin" % "0")
          )
        )
      )

      m.dependencies.size must_== 3
      m.dependencies.head.gav.version must beSome("0")
      m.build.get.sourceDirectory must beSome("src/main/scala2")
      m.build.get.testSourceDirectory must beSome("src/test/scala2")
      m.build.get.pluginManagement.get.plugins.size must_== 1
      m.build.get.pluginManagement.get.plugins.head.gav.version must beSome("0")
      m.build.get.plugins.size must_== 3
      m.build.get.plugins.head.gav.artifactId must_== "maven-compiler-plugin"
      m.build.get.plugins.head.gav.version must beSome("0")
      m.build.get.plugins(1).gav.artifactId must_== "scala-maven-plugin"
      m.build.get.plugins(1).gav.version must beSome("0")
      m.build.get.plugins(2).gav.artifactId must_== "maven-surefire-plugin"
      m.build.get.plugins(2).gav.version must beSome("0")
    }
  }
}
