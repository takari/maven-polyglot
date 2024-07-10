/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class BuildBase(
    val defaultGoal: Option[String],
    val resources: immutable.Seq[Resource],
    val testResources: immutable.Seq[Resource],
    val directory: Option[String],
    val finalName: Option[String],
    val filters: immutable.Seq[String],
    val pluginManagement: Option[PluginManagement],
    val plugins: immutable.Seq[Plugin],
    val tasks: immutable.Seq[Task]
)

object BuildBase {
  def apply(
      defaultGoal: String = null,
      resources: immutable.Seq[Resource] = Nil,
      testResources: immutable.Seq[Resource] = Nil,
      directory: String = null,
      finalName: String = null,
      filters: immutable.Seq[String] = Nil,
      pluginManagement: PluginManagement = null,
      plugins: immutable.Seq[Plugin] = Nil,
      tasks: immutable.Seq[Task] = Nil
  ) =
    new BuildBase(
      Option(defaultGoal),
      resources,
      testResources,
      Option(directory),
      Option(finalName),
      filters,
      Option(pluginManagement),
      plugins,
      tasks
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedBuildBase(b: BuildBase) {
  def asDoc: Doc = {
    `object`("BuildBase", asDocArgs)
  }

  def asDocArgs: immutable.Seq[Doc] = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    b.defaultGoal.foreach(args += assignString("defaultGoal", _))
    Some(b.resources).filterNot(_.isEmpty).foreach(r =>
      args += assign("resources", seq(r.map(_.asDoc)))
    )
    Some(b.testResources).filterNot(_.isEmpty).foreach(trs =>
      args += assign("testResources", seq(trs.map(_.asDoc)))
    )
    b.directory.foreach(args += assignString("directory", _))
    b.finalName.foreach(args += assignString("finalName", _))
    Some(b.filters).filterNot(_.isEmpty).foreach(f => args += assign("filters", seqString(f)))
    b.pluginManagement.foreach(pm => args += assign("pluginManagement", pm.asDoc))
    Some(b.plugins).filterNot(_.isEmpty).foreach(ps =>
      args += assign("plugins", seq(ps.map(_.asDoc)))
    )
    Some(b.tasks).filterNot(_.isEmpty).foreach(ps => args += assign("tasks", seq(ps.map(_.asDoc))))
    args.toList
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.jdk.CollectionConverters._
import org.apache.maven.model.{BuildBase => MavenBuildBase}

class ConvertibleMavenBuildBase(mb: MavenBuildBase) {
  def asScala: BuildBase = {
    BuildBase(
      mb.getDefaultGoal,
      mb.getResources.asScala.map(_.asScala).toList,
      mb.getTestResources.asScala.map(_.asScala).toList,
      mb.getDirectory,
      mb.getFinalName,
      mb.getFilters.asScala.toList,
      Option(mb.getPluginManagement).map(_.asScala).orNull,
      mb.getPlugins.asScala.map(_.asScala).toList
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaBuildBase(b: BuildBase) {
  def asJava: MavenBuildBase = {
    val mb = new MavenBuildBase
    mb.setDefaultGoal(b.defaultGoal.orNull)
    mb.setDirectory(b.directory.orNull)
    mb.setFilters(b.filters.asJava)
    mb.setFinalName(b.finalName.orNull)
    mb.setPluginManagement(b.pluginManagement.map(_.asJava).orNull)
    mb.setPlugins(b.plugins.map(_.asJava).asJava)
    mb.setResources(b.resources.map(_.asJava).asJava)
    mb.setTestResources(b.testResources.map(_.asJava).asJava)
    mb
  }
}
