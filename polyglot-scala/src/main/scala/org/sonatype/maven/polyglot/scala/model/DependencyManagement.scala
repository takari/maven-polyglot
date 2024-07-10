/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class DependencyManagement(val dependencies: immutable.Seq[Dependency])

object DependencyManagement {
  def apply(dependencies: immutable.Seq[Dependency] = Nil) = new DependencyManagement(dependencies)
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._
import java.util

class PrettiedDependencyManagement(dm: DependencyManagement) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    Some(dm.dependencies).filterNot(_.isEmpty).foreach(ps =>
      args += assign("dependencies", seq(ps.map(_.asDoc)))
    )
    `object`("DependencyManagement", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.jdk.CollectionConverters._
import org.apache.maven.model.{
  Dependency => MavenDependency,
  DependencyManagement => MavenDependencyManagement
}

class ConvertibleMavenDependencyManagement(mdm: MavenDependencyManagement) {
  def asScala: DependencyManagement = {
    DependencyManagement(
      mdm.getDependencies.asScala.map(_.asScala).toList
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaDependencyManagement(dm: DependencyManagement) {
  def asJava: MavenDependencyManagement = {
    val mdm = new MavenDependencyManagement
    // Wrap the list in a mutable structure to circumvent an issue with Maven assuming this to
    // be a mutable structure: http://jira.codehaus.org/browse/MNG-5529
    // FIXME: Remove the need for composing with util.ArrayList once the Maven issue is fixed.
    mdm.setDependencies(new util.ArrayList[MavenDependency](dm.dependencies.map(_.asJava).asJava))
    mdm
  }
}
