/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class Parent(
    val gav: Option[Gav],
    val relativePath: String
)

object Parent {
  def apply(
      gav: Gav = null,
      relativePath: String = "../pom.xml"
  ): Parent =
    new Parent(
      Option(gav),
      relativePath
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedParent(p: Parent) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    p.gav.foreach(gav => args += assign("gav", gav.asDoc))
    Some(p.relativePath).filterNot(_ == "../pom.xml").foreach(args += assignString(
      "relativePath",
      _
    ))
    `object`("Parent", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import org.apache.maven.model.{Parent => MavenParent}

class ConvertibleMavenParent(mp: MavenParent) {
  def asScala: Parent = {
    Parent(
      Option(mp.getArtifactId).map(artifactId =>
        (mp.getGroupId, artifactId, mp.getVersion).asScala
      ).orNull,
      mp.getRelativePath
    )
  }
}

class ConvertibleScalaParent(p: Parent) {
  def asJava: MavenParent = {
    val mp = new MavenParent
    p.gav.foreach {
      gav =>
        mp.setGroupId(gav.groupId.orNull)
        mp.setArtifactId(gav.artifactId)
        mp.setVersion(gav.version.orNull)
    }
    mp.setRelativePath(p.relativePath)
    mp
  }
}
