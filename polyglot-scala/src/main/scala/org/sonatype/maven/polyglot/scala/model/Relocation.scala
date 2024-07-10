/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class Relocation(
    val groupId: Option[String],
    val artifactId: Option[String],
    val version: Option[String],
    val message: Option[String]
)

object Relocation {
  def apply(
      groupId: String = null,
      artifactId: String = null,
      version: String = null,
      message: String = null
  ): Relocation =
    new Relocation(
      Option(groupId),
      Option(artifactId),
      Option(version),
      Option(message)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedRelocation(r: Relocation) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    r.groupId.foreach(args += assignString("groupId", _))
    r.artifactId.foreach(args += assignString("artifactId", _))
    r.version.foreach(args += assignString("version", _))
    r.message.foreach(args += assignString("message", _))
    `object`("Relocation", args.toList)
  }
}

import org.apache.maven.model.{Relocation => MavenRelocation}

class ConvertibleMavenRelocation(mr: MavenRelocation) {
  def asScala: Relocation = {
    Relocation(
      mr.getGroupId,
      mr.getArtifactId,
      mr.getVersion,
      mr.getMessage
    )
  }
}

class ConvertibleScalaRelocation(r: Relocation) {
  def asJava: MavenRelocation = {
    val mr = new MavenRelocation
    mr.setGroupId(r.groupId.orNull)
    mr.setArtifactId(r.artifactId.orNull)
    mr.setVersion(r.version.orNull)
    mr.setMessage(r.message.orNull)
    mr
  }
}
