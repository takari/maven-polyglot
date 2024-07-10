/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class Extension(val gav: Gav)

object Extension {
  def apply(gav: Gav) = new Extension(gav)
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedExtension(e: Extension) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer(e.gav.asDoc)
    `object`("Extension", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import org.apache.maven.model.{Extension => MavenExtension}

class ConvertibleMavenExtension(me: MavenExtension) {
  def asScala: Extension = {
    Extension(
      (me.getGroupId, me.getArtifactId, me.getVersion).asScala
    )
  }
}

class ConvertibleScalaExtension(e: Extension) {
  def asJava: MavenExtension = {
    val me = new MavenExtension
    me.setGroupId(e.gav.groupId.orNull)
    me.setArtifactId(e.gav.artifactId)
    me.setVersion(e.gav.version.orNull)
    me
  }
}
