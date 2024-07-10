/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class ActivationFile(
    val missing: Option[String],
    val exists: Option[String]
)

object ActivationFile {
  def apply(
      missing: String = null,
      exists: String = null
  ) =
    new ActivationFile(
      Option(missing),
      Option(exists)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedActivationFile(aFile: ActivationFile) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    aFile.missing.foreach(args += assignString("missing", _))
    aFile.exists.foreach(args += assignString("exists", _))
    `object`("ActivationFile", args.toList)
  }
}

import org.apache.maven.model.{ActivationFile => MavenActivationFile}

class ConvertibleMavenActivationFile(maFile: MavenActivationFile) {
  def asScala: ActivationFile = {
    ActivationFile(
      maFile.getMissing,
      maFile.getExists
    )
  }
}

class ConvertibleScalaActivationFile(aFile: ActivationFile) {
  def asJava: MavenActivationFile = {
    val maFile = new MavenActivationFile
    maFile.setMissing(aFile.missing.orNull)
    maFile.setExists(aFile.exists.orNull)
    maFile
  }
}
