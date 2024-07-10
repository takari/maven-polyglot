/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class Activation(
    val activeByDefault: Boolean,
    val jdk: Option[String],
    val os: Option[ActivationOS],
    val property: Option[ActivationProperty],
    val file: Option[ActivationFile]
)

object Activation {
  def apply(
      activeByDefault: Boolean = false,
      jdk: String = null,
      os: ActivationOS = null,
      property: ActivationProperty = null,
      file: ActivationFile = null
  ) =
    new Activation(
      activeByDefault,
      Option(jdk),
      Option(os),
      Option(property),
      Option(file)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedActivation(p: Activation) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    Option(p.activeByDefault).filterNot(_ == false).foreach(abd =>
      args += assign("activeByDefault", abd.toString)
    )
    p.jdk.foreach(args += assignString("jdk", _))
    p.os.foreach(os => args += assign("os", os.asDoc))
    p.property.foreach(p => args += assign("property", p.asDoc))
    p.file.foreach(f => args += assign("file", f.asDoc))
    `object`("Activation", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import org.apache.maven.model.{Activation => MavenActivation}

class ConvertibleMavenActivation(ma: MavenActivation) {
  def asScala: Activation = {
    Activation(
      ma.isActiveByDefault,
      ma.getJdk,
      Option(ma.getOs).map(_.asScala).orNull,
      Option(ma.getProperty).map(_.asScala).orNull,
      Option(ma.getFile).map(_.asScala).orNull
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaActivation(a: Activation) {
  def asJava: MavenActivation = {
    val ma = new MavenActivation
    ma.setActiveByDefault(a.activeByDefault)
    ma.setFile(a.file.map(_.asJava).orNull)
    ma.setJdk(a.jdk.orNull)
    ma.setOs(a.os.map(_.asJava).orNull)
    ma.setProperty(a.property.map(_.asJava).orNull)
    ma
  }
}
