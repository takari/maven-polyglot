/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class Site(
    val id: Option[String],
    val name: Option[String],
    val url: Option[String]
)

object Site {
  def apply(
      id: String = null,
      name: String = null,
      url: String = null
  ): Site =
    new Site(
      Option(id),
      Option(name),
      Option(url)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedSite(s: Site) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    s.id.foreach(args += assignString("id", _))
    s.name.foreach(args += assignString("name", _))
    s.url.foreach(args += assignString("url", _))
    `object`("Site", args.toList)
  }
}

import org.apache.maven.model.{Site => MavenSite}

class ConvertibleMavenSite(ms: MavenSite) {
  def asScala: Site = {
    Site(
      ms.getId,
      ms.getName,
      ms.getUrl
    )
  }
}

class ConvertibleScalaSite(s: Site) {
  def asJava: MavenSite = {
    val ms = new MavenSite
    ms.setId(s.id.orNull)
    ms.setName(s.name.orNull)
    ms.setUrl(s.url.orNull)
    ms
  }
}
