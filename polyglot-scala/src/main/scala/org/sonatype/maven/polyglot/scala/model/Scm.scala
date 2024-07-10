/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class Scm(
    val connection: Option[String],
    val developerConnection: Option[String],
    val tag: String,
    val url: Option[String]
)

object Scm {
  def apply(
      connection: String = null,
      developerConnection: String = null,
      tag: String = "HEAD",
      url: String = null
  ): Scm =
    new Scm(
      Option(connection),
      Option(developerConnection),
      tag,
      Option(url)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedScm(scm: Scm) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    scm.connection.foreach(args += assignString("connection", _))
    scm.developerConnection.foreach(args += assignString("developerConnection", _))
    Option(scm.tag).filterNot(_ == "HEAD").foreach(args += assignString("tag", _))
    scm.url.foreach(args += assignString("url", _))
    `object`("Scm", args.toList)
  }
}

import org.apache.maven.model.{Scm => MavenScm}

class ConvertibleMavenScm(mscm: MavenScm) {
  def asScala: Scm = {
    Scm(
      mscm.getConnection,
      mscm.getDeveloperConnection,
      mscm.getTag,
      mscm.getUrl
    )
  }
}

class ConvertibleScalaScm(scm: Scm) {
  def asJava: MavenScm = {
    val mscm = new MavenScm
    mscm.setConnection(scm.connection.orNull)
    mscm.setDeveloperConnection(scm.developerConnection.orNull)
    mscm.setTag(scm.tag)
    mscm.setUrl(scm.url.orNull)
    mscm
  }
}
