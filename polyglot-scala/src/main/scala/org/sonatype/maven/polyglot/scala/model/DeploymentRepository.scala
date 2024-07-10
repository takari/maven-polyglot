/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class DeploymentRepository(
    val uniqueVersion: Boolean,
    override val releases: Option[RepositoryPolicy],
    override val snapshots: Option[RepositoryPolicy],
    override val id: Option[String],
    override val name: Option[String],
    override val url: Option[String],
    override val layout: String
) extends Repository(
      releases,
      snapshots,
      id,
      name,
      url,
      layout
    )

object DeploymentRepository {
  def apply(
      uniqueVersion: Boolean = true,
      releases: RepositoryPolicy = null,
      snapshots: RepositoryPolicy = null,
      id: String = null,
      name: String = null,
      url: String = null,
      layout: String = "default"
  ) =
    new DeploymentRepository(
      uniqueVersion,
      Option(releases),
      Option(snapshots),
      Option(id),
      Option(name),
      Option(url),
      layout
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedDeploymentRepository(dr: DeploymentRepository) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    Option(dr.uniqueVersion).filterNot(_ == true).foreach(uv =>
      args += assign("uniqueVersion", uv.toString)
    )
    args ++= dr.asInstanceOf[Repository].asDocArgs
    `object`("DeploymentRepository", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import org.apache.maven.model.{DeploymentRepository => MavenDeploymentRepository}

class ConvertibleMavenDeploymentRepository(mdr: MavenDeploymentRepository) {
  def asScala: DeploymentRepository = {
    DeploymentRepository(
      mdr.isUniqueVersion,
      Option(mdr.getReleases).map(_.asScala).orNull,
      Option(mdr.getSnapshots).map(_.asScala).orNull,
      mdr.getId,
      mdr.getName,
      mdr.getUrl,
      mdr.getLayout
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaDeploymentRepository(dr: DeploymentRepository) {
  def asJava: MavenDeploymentRepository = {
    val mdr = new MavenDeploymentRepository
    mdr.setUniqueVersion(dr.uniqueVersion)
    mdr.setReleases(dr.releases.map(_.asJava).orNull)
    mdr.setSnapshots(dr.snapshots.map(_.asJava).orNull)
    mdr.setId(dr.id.orNull)
    mdr.setName(dr.name.orNull)
    mdr.setUrl(dr.url.orNull)
    mdr.setLayout(dr.layout)
    mdr
  }
}
