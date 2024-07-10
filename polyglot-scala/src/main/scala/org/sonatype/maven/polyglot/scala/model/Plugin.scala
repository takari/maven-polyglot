/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class Plugin(
    val gav: Gav,
    val extensions: Boolean,
    val executions: immutable.Seq[Execution],
    val dependencies: immutable.Seq[Dependency],
    override val inherited: Boolean,
    override val configuration: Option[Config]
) extends ConfigurationContainer(inherited, configuration)

object Plugin {
  def apply(
      gav: Gav,
      extensions: Boolean = false,
      executions: immutable.Seq[Execution] = immutable.Seq.empty,
      dependencies: immutable.Seq[Dependency] = immutable.Seq.empty,
      inherited: Boolean = true,
      configuration: Config = null
  ): Plugin =
    new Plugin(
      gav,
      extensions,
      executions,
      dependencies,
      inherited,
      Option(configuration)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedPlugin(p: Plugin) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer(p.gav.asDoc)
    Some(p.extensions).filter(_ == true).foreach(e => args += assign("extensions", e.toString))
    Some(p.executions).filterNot(_.isEmpty).foreach(es =>
      args += assign("executions", seq(es.map(_.asDoc)))
    )
    Some(p.dependencies).filterNot(_.isEmpty).foreach(es =>
      args += assign("dependencies", seq(es.map(_.asDoc)))
    )
    args ++= p.asDocArgs
    `object`("Plugin", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.jdk.CollectionConverters._

import org.apache.maven.model.{Plugin => MavenPlugin}

class ConvertibleMavenPlugin(mp: MavenPlugin) {
  def asScala: Plugin = {
    Plugin(
      (mp.getGroupId, mp.getArtifactId, mp.getVersion).asScala,
      mp.isExtensions,
      mp.getExecutions.asScala.map(_.asScala).toList,
      mp.getDependencies.asScala.map(_.asScala).toList,
      mp.isInherited,
      Option(mp.getConfiguration).map(_.asScala).orNull
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaPlugin(p: Plugin) {
  def asJava: MavenPlugin = {
    val mp = new MavenPlugin
    mp.setArtifactId(p.gav.artifactId)
    mp.setDependencies(p.dependencies.map(_.asJava).asJava)
    mp.setExecutions(p.executions.map(_.asJava).asJava)
    mp.setExtensions(p.extensions)
    mp.setGroupId(p.gav.groupId.orNull)
    mp.setVersion(p.gav.version.orNull)
    mp.setConfiguration(p.configuration.map(_.asJava).orNull)
    mp.setInherited(p.inherited)
    mp
  }
}
