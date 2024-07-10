/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class DistributionManagement(
    val repository: Option[DeploymentRepository],
    val snapshotRepository: Option[DeploymentRepository],
    val site: Option[Site],
    val downloadUrl: Option[String],
    val relocation: Option[Relocation],
    val status: Option[String]
)

object DistributionManagement {
  def apply(
      repository: DeploymentRepository = null,
      snapshotRepository: DeploymentRepository = null,
      site: Site = null,
      downloadUrl: String = null,
      relocation: Relocation = null,
      status: String = null
  ): DistributionManagement =
    new DistributionManagement(
      Option(repository),
      Option(snapshotRepository),
      Option(site),
      Option(downloadUrl),
      Option(relocation),
      Option(status)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedDistributionManagement(dm: DistributionManagement) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    dm.repository.foreach(r => args += assign("repository", r.asDoc))
    dm.snapshotRepository.foreach(sr => args += assign("snapshotRepository", sr.asDoc))
    dm.site.foreach(s => args += assign("site", s.asDoc))
    dm.downloadUrl.foreach(args += assignString("downloadUrl", _))
    dm.relocation.foreach(r => args += assign("relocation", r.asDoc))
    dm.status.foreach(args += assignString("status", _))
    `object`("DistributionManagement", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import org.apache.maven.model.{DistributionManagement => MavenDistributionManagement}

class ConvertibleMavenDistributionManagement(mdm: MavenDistributionManagement) {
  def asScala: DistributionManagement = {
    DistributionManagement(
      Option(mdm.getRepository).map(_.asScala).orNull,
      Option(mdm.getSnapshotRepository).map(_.asScala).orNull,
      Option(mdm.getSite).map(_.asScala).orNull,
      mdm.getDownloadUrl,
      Option(mdm.getRelocation).map(_.asScala).orNull,
      mdm.getStatus
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaDistributionManagement(dm: DistributionManagement) {
  def asJava: MavenDistributionManagement = {
    val mdm = new MavenDistributionManagement
    mdm.setRepository(dm.repository.map(_.asJava).orNull)
    mdm.setSnapshotRepository(dm.snapshotRepository.map(_.asJava).orNull)
    mdm.setSite(dm.site.map(_.asJava).orNull)
    mdm.setDownloadUrl(dm.downloadUrl.orNull)
    mdm.setRelocation(dm.relocation.map(_.asJava).orNull)
    mdm.setStatus(dm.status.orNull)
    mdm
  }
}
