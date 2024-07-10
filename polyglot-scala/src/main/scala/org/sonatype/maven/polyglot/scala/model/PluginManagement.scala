/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class PluginManagement(plugins: immutable.Seq[Plugin]) extends PluginContainer(plugins)

object PluginManagement {
  def apply(plugins: immutable.Seq[Plugin] = immutable.Seq.empty) = new PluginManagement(plugins)
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedPluginManagement(p: PluginManagement) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    args ++= p.asDocArgs
    `object`("PluginManagement", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.jdk.CollectionConverters._
import org.apache.maven.model.{PluginManagement => MavenPluginManagement}

class ConvertibleMavenPluginManagement(mpm: MavenPluginManagement) {
  def asScala: PluginManagement = {
    PluginManagement(
      mpm.getPlugins.asScala.map(_.asScala).toList
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaPluginManagement(pm: PluginManagement) {
  def asJava: MavenPluginManagement = {
    val mpm = new MavenPluginManagement
    mpm.setPlugins(pm.plugins.map(_.asJava).asJava)
    mpm
  }
}
