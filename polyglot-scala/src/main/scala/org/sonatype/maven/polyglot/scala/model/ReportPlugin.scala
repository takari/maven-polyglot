/**
 * Copyright (c) 2018 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class ReportPlugin(
    val gav: Gav,
    val reportSets: immutable.Seq[ReportSet],
    override val inherited: Boolean,
    override val configuration: Option[Config]
) extends ConfigurationContainer(inherited, configuration)

object ReportPlugin {
  def apply(
      gav: Gav,
      reportSets: immutable.Seq[ReportSet] = immutable.Seq.empty,
      inherited: Boolean = true,
      configuration: Config = null
  ) =
    new ReportPlugin(
      gav = gav,
      reportSets = reportSets,
      inherited = inherited,
      configuration = Option(configuration)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedReportPlugin(p: ReportPlugin) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer(p.gav.asDoc)
    Some(p.reportSets).filterNot(_.isEmpty).foreach(rs =>
      args += assign("reportSets", seq(rs.map(_.asDoc)))
    )
    args ++= p.asDocArgs
    `object`("ReportPlugin", args.toList)

  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.jdk.CollectionConverters._

import org.apache.maven.model.{ReportPlugin => MavenReportPlugin}

class ConvertibleMavenReportPlugin(mp: MavenReportPlugin) {
  def asScala: ReportPlugin = {
    ReportPlugin(
      gav = (mp.getGroupId, mp.getArtifactId, mp.getVersion).asScala,
      reportSets = mp.getReportSets().asScala.map(_.asScala).toList,
      inherited = mp.isInherited,
      configuration = Option(mp.getConfiguration).map(_.asScala).orNull
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaReportPlugin(p: ReportPlugin) {
  def asJava: MavenReportPlugin = {
    val mp = new MavenReportPlugin()
    mp.setArtifactId(p.gav.artifactId)
    mp.setGroupId(p.gav.groupId.orNull)
    mp.setVersion(p.gav.version.orNull)
    mp.setReportSets(p.reportSets.map(_.asJava).asJava)
    mp.setInherited(p.inherited)
    mp.setConfiguration(p.configuration.map(_.asJava).orNull)
    mp
  }
}
