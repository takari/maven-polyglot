/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class Organization(
    val name: Option[String],
    val url: Option[String]
)

object Organization {
  def apply(
      name: String = null,
      url: String = null
  ): Organization =
    new Organization(
      Option(name),
      Option(url)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedOrganization(im: Organization) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    im.name.foreach(args += assignString("name", _))
    im.url.foreach(args += assignString("url", _))
    `object`("Organization", args.toList)
  }
}

import org.apache.maven.model.{Organization => MavenOrganization}

class ConvertibleMavenOrganization(mo: MavenOrganization) {
  def asScala: Organization = {
    Organization(
      mo.getName,
      mo.getUrl
    )
  }
}

class ConvertibleScalaOrganization(o: Organization) {
  def asJava: MavenOrganization = {
    val mo = new MavenOrganization
    mo.setName(o.name.orNull)
    mo.setUrl(o.url.orNull)
    mo
  }
}
