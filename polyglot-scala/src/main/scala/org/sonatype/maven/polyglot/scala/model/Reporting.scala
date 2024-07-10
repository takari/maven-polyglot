/**
 * Copyright (c) 2018 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class Reporting(
    val excludeDefaults: Boolean,
    val outputDirectory: Option[String],
    val plugins: immutable.Seq[ReportPlugin]
)

object Reporting {
  def apply(
      excludeDefaults: Boolean = false,
      outputDirectory: String = null,
      plugins: immutable.Seq[ReportPlugin] = immutable.Seq.empty
  ) =
    new Reporting(
      excludeDefaults = excludeDefaults,
      outputDirectory = Option(outputDirectory),
      plugins = plugins
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedReporting(r: Reporting) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    args += assignString("excludeDefaults", (if (r.excludeDefaults) "true" else "false"))
    r.outputDirectory.foreach(args += assignString("outputDirectory", _))
    Some(r.plugins).filterNot(_.isEmpty).foreach(ps =>
      args += assign("plugins", seq(ps.map(_.asDoc)))
    )
    `object`("Reporting", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.jdk.CollectionConverters._
import org.apache.maven.model.{Reporting => MavenReporting}

class ConvertibleMavenReporting(mr: MavenReporting) {
  def asScala: Reporting = {
    Reporting(
      excludeDefaults = mr.getExcludeDefaults() == "true",
      outputDirectory = mr.getOutputDirectory(),
      plugins = mr.getPlugins.asScala.map(_.asScala).toList
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaReporting(r: Reporting) {
  def asJava: MavenReporting = {
    val mr = new MavenReporting()
    mr.setExcludeDefaults(if (r.excludeDefaults) "true" else "false")
    mr.setOutputDirectory(r.outputDirectory.orNull)
    mr.setPlugins(r.plugins.map(_.asJava).asJava)
    mr
  }
}
