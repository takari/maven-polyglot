/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class Prerequisites(val maven: String)

object Prerequisites {
  def apply(maven: String = "2.0") = new Prerequisites(maven)
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedPrerequisites(ps: Prerequisites) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    Option(ps.maven).filterNot(_ == "2.0").foreach(args += assignString("maven", _))
    `object`("Prerequisites", args.toList)
  }
}

import org.apache.maven.model.{Prerequisites => MavenPrerequisites}

class ConvertibleMavenPrerequisites(mps: MavenPrerequisites) {
  def asScala: Prerequisites = {
    Prerequisites(
      mps.getMaven
    )
  }
}

class ConvertibleScalaPrerequisites(ps: Prerequisites) {
  def asJava: MavenPrerequisites = {
    val mps = new MavenPrerequisites
    mps.setMaven(ps.maven)
    mps
  }
}
