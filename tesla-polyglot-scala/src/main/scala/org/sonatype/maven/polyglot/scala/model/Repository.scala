/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

case class Repository(
                       releases: Option[RepositoryPolicy],
                       snapshots: Option[RepositoryPolicy],
                       id: Option[String],
                       name: Option[String],
                       url: Option[String],
                       layout: String
                       )

object Repository {
  def apply(
             releases: RepositoryPolicy = null,
             snapshots: RepositoryPolicy = null,
             id: String = null,
             name: String = null,
             url: String = null,
             layout: String = "default"
             ) =
    new Repository(
      Option(releases),
      Option(snapshots),
      Option(id),
      Option(name),
      Option(url),
      layout
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedRepository(r: Repository) {
  def asDoc: Doc = `object`("Repository", asDocArgs)

  def asDocArgs: Seq[Doc] = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    r.releases.foreach(rs => args += assign("releases", rs.asDoc))
    r.snapshots.foreach(ss => args += assign("snapshots", ss.asDoc))
    r.id.foreach(args += assignString("id", _))
    r.name.foreach(args += assignString("name", _))
    r.url.foreach(args += assignString("url", _))
    Option(r.layout).filterNot(_ == "default").foreach(args += assignString("layout", _))
    args
  }
}


import org.sonatype.maven.polyglot.scala.MavenConverters._
import org.apache.maven.model.{Repository => MavenRepository}

class ConvertibleMavenRepository(mr: MavenRepository) {
  def asScala: Repository = {
    Repository(
      Option(mr.getReleases).map(_.asScala),
      Option(mr.getSnapshots).map(_.asScala),
      Option(mr.getId),
      Option(mr.getName),
      Option(mr.getUrl),
      mr.getLayout
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaRepository(r: Repository) {
  def asJava: MavenRepository = {
    val mr = new MavenRepository
    mr.setReleases(r.releases.map(_.asJava).orNull)
    mr.setSnapshots(r.snapshots.map(_.asJava).orNull)
    mr.setId(r.id.orNull)
    mr.setName(r.name.orNull)
    mr.setUrl(r.url.orNull)
    mr.setLayout(r.layout)
    mr
  }
}