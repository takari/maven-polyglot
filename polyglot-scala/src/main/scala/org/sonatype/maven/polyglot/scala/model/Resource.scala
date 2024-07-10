/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class Resource(
    val targetPath: Option[String],
    val filtering: Boolean,
    val directory: Option[String],
    val includes: immutable.Seq[String],
    val excludes: immutable.Seq[String]
)

object Resource {
  def apply(
      targetPath: String = null,
      filtering: Boolean = false,
      directory: String = null,
      includes: immutable.Seq[String] = Nil,
      excludes: immutable.Seq[String] = Nil
  ): Resource =
    new Resource(
      Option(targetPath),
      filtering,
      Option(directory),
      includes,
      excludes
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedResource(r: Resource) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    r.targetPath.foreach(args += assignString("targetPath", _))
    Some(r.filtering).filter(_ == true).foreach(f => args += assign("filtering", f.toString))
    r.directory.foreach(args += assignString("directory", _))
    Some(r.includes).filterNot(_.isEmpty).foreach(is => args += assign("includes", seqString(is)))
    Some(r.excludes).filterNot(_.isEmpty).foreach(ex => args += assign("excludes", seqString(ex)))
    `object`("Resource", args.toList)
  }
}

import scala.jdk.CollectionConverters._
import org.apache.maven.model.{Resource => MavenResource}

class ConvertibleMavenResource(mr: MavenResource) {
  def asScala: Resource = {
    Resource(
      mr.getTargetPath,
      mr.isFiltering,
      mr.getDirectory,
      mr.getIncludes.asScala.toList,
      mr.getExcludes.asScala.toList
    )
  }
}

class ConvertibleScalaResource(r: Resource) {
  def asJava: MavenResource = {
    val mr = new MavenResource
    mr.setFiltering(r.filtering)
    mr.setTargetPath(r.targetPath.orNull)
    mr.setDirectory(r.directory.orNull)
    mr.setExcludes(r.excludes.asJava)
    mr.setIncludes(r.includes.asJava)
    mr
  }
}
