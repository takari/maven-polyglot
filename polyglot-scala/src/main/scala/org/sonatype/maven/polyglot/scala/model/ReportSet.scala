/**
 * Copyright (c) 2018 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class ReportSet(
    val id: String,
    val reports: immutable.Seq[String],
    override val inherited: Boolean,
    override val configuration: Option[Config]
) extends ConfigurationContainer(inherited, configuration)

object ReportSet {
  def apply(
      id: String = "default",
      reports: immutable.Seq[String] = immutable.Seq.empty,
      inherited: Boolean = true,
      configuration: Config = null
  ) =
    new ReportSet(
      id = id,
      reports = reports,
      inherited = inherited,
      configuration = Option(configuration)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedReportSet(s: ReportSet) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    args += assignString("id", s.id)
    Some(s.reports).filterNot(_.isEmpty).foreach(r => args += assign("reports", seqString(r)))
    args ++= s.asDocArgs
    `object`("ReportSet", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.jdk.CollectionConverters._

import org.apache.maven.model.{ReportSet => MavenReportSet}

class ConvertibleMavenReportSet(ms: MavenReportSet) {
  def asScala: ReportSet = {
    ReportSet(
      id = ms.getId(),
      reports = ms.getReports().asScala.toList,
      inherited = ms.isInherited(),
      configuration = Option(ms.getConfiguration).map(_.asScala).orNull
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaReportSet(s: ReportSet) {
  def asJava: MavenReportSet = {
    val ms = new MavenReportSet()
    ms.setId(s.id)
    ms.setReports(s.reports.asJava)
    ms.setInherited(s.inherited)
    ms.setConfiguration(s.configuration.map(_.asJava).orNull)
    ms
  }
}
