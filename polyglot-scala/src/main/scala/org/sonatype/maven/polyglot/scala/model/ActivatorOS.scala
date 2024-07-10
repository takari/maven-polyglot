/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class ActivationOS(
    val name: Option[String],
    val family: Option[String],
    val arch: Option[String],
    val version: Option[String]
)

object ActivationOS {
  def apply(
      name: String = null,
      family: String = null,
      arch: String = null,
      version: String = null
  ) =
    new ActivationOS(
      Option(name),
      Option(family),
      Option(arch),
      Option(version)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedActivationOS(aos: ActivationOS) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    aos.name.foreach(args += assignString("name", _))
    aos.family.foreach(args += assignString("family", _))
    aos.arch.foreach(args += assignString("arch", _))
    aos.version.foreach(args += assignString("version", _))
    `object`("ActivationOS", args.toList)
  }
}

import org.apache.maven.model.{ActivationOS => MavenActivationOS}

class ConvertibleMavenActivationOS(maos: MavenActivationOS) {
  def asScala: ActivationOS = {
    ActivationOS(
      maos.getName,
      maos.getFamily,
      maos.getArch,
      maos.getVersion
    )
  }
}

class ConvertibleScalaActivationOS(aos: ActivationOS) {
  def asJava: MavenActivationOS = {
    val maos = new MavenActivationOS
    maos.setName(aos.name.orNull)
    maos.setFamily(aos.family.orNull)
    maos.setArch(aos.arch.orNull)
    maos.setVersion(aos.version.orNull)
    maos
  }
}
