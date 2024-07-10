/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class ActivationProperty(
    val name: Option[String],
    val value: Option[String]
)

object ActivationProperty {
  def apply(
      name: String = null,
      value: String = null
  ) =
    new ActivationProperty(
      Option(name),
      Option(value)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedActivationProperty(ap: ActivationProperty) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    ap.name.foreach(args += assignString("name", _))
    ap.value.foreach(args += assignString("value", _))
    `object`("ActivationProperty", args.toList)
  }
}

import org.apache.maven.model.{ActivationProperty => MavenActivationProperty}

class ConvertibleMavenActivationProperty(map: MavenActivationProperty) {
  def asScala: ActivationProperty = {
    ActivationProperty(
      map.getName,
      map.getValue
    )
  }
}

class ConvertibleScalaActivationProperty(ap: ActivationProperty) {
  def asJava: MavenActivationProperty = {
    val map = new MavenActivationProperty
    map.setName(ap.name.orNull)
    map.setValue(ap.value.orNull)
    map
  }
}
