/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

/**
 * A type to represent Scala versions
 */
class ScalaVersion(val version: String) {
  val binaryVersion: String = version.split("\\.", 3).take(2).mkString(".")
}

object ScalaVersion {
  def apply(version: String) = new ScalaVersion(version)
}
