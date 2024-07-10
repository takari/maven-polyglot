/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class Model(
    val gav: Gav,
    val build: Option[Build],
    val ciManagement: Option[CiManagement],
    val contributors: immutable.Seq[Contributor],
    dependencyManagement: Option[DependencyManagement],
    dependencies: immutable.Seq[Dependency],
    val description: Option[String],
    val developers: immutable.Seq[Developer],
    distributionManagement: Option[DistributionManagement],
    val inceptionYear: Option[String],
    val issueManagement: Option[IssueManagement],
    val licenses: immutable.Seq[License],
    val mailingLists: immutable.Seq[MailingList],
    val modelEncoding: String,
    val modelVersion: Option[String],
    modules: immutable.Seq[String],
    val name: Option[String],
    val organization: Option[Organization],
    val packaging: String,
    val parent: Option[Parent],
    pluginRepositories: immutable.Seq[Repository],
    val pomFile: Option[File],
    val prerequisites: Option[Prerequisites],
    val profiles: immutable.Seq[Profile],
    val properties: Map[String, String],
    val reporting: Option[Reporting],
    repositories: immutable.Seq[Repository],
    val scm: Option[Scm],
    val url: Option[String]
) extends ModelBase(
      dependencyManagement,
      dependencies,
      distributionManagement,
      modules,
      pluginRepositories,
      repositories
    ) {
  def copy(pomFile: Option[File]): Model =
    new Model(
      gav,
      build,
      ciManagement,
      contributors,
      dependencyManagement,
      dependencies,
      description,
      developers,
      distributionManagement,
      inceptionYear,
      issueManagement,
      licenses,
      mailingLists,
      modelEncoding,
      modelVersion,
      modules,
      name,
      organization,
      packaging,
      parent,
      pluginRepositories,
      pomFile,
      prerequisites,
      profiles,
      properties,
      reporting,
      repositories,
      scm,
      url
    )
}

object Model {
  def apply(
      gav: Gav,
      build: Build = null,
      ciManagement: CiManagement = null,
      contributors: immutable.Seq[Contributor] = Nil,
      dependencyManagement: DependencyManagement = null,
      dependencies: immutable.Seq[Dependency] = Nil,
      description: String = null,
      developers: immutable.Seq[Developer] = Nil,
      distributionManagement: DistributionManagement = null,
      inceptionYear: String = null,
      issueManagement: IssueManagement = null,
      licenses: immutable.Seq[License] = Nil,
      mailingLists: immutable.Seq[MailingList] = Nil,
      modelEncoding: String = "UTF-8",
      modelVersion: String = "4.0.0",
      modules: immutable.Seq[String] = Nil,
      name: String = null,
      organization: Organization = null,
      packaging: String = "jar",
      parent: Parent = null,
      pluginRepositories: immutable.Seq[Repository] = Nil,
      pomFile: File = null,
      prerequisites: Prerequisites = null,
      profiles: immutable.Seq[Profile] = Nil,
      properties: Map[String, String] = Map.empty,
      reporting: Reporting = null,
      repositories: immutable.Seq[Repository] = Nil,
      scm: Scm = null,
      url: String = null
  ) =
    new Model(
      gav,
      Option(build),
      Option(ciManagement),
      contributors,
      Option(dependencyManagement),
      dependencies,
      Option(description),
      developers,
      Option(distributionManagement),
      Option(inceptionYear),
      Option(issueManagement),
      licenses,
      mailingLists,
      modelEncoding,
      Option(modelVersion),
      modules,
      Option(name),
      Option(organization),
      packaging,
      Option(parent),
      pluginRepositories,
      Option(pomFile),
      Option(prerequisites),
      profiles,
      properties,
      reporting = Option(reporting),
      repositories,
      Option(scm),
      Option(url)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedModel(m: Model) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer(m.gav.asDoc)
    Some(m.packaging).filterNot(_ == "jar").foreach(args += assignString("packaging", _))
    m.name.foreach(args += assignString("name", _))
    m.description.foreach(args += assignString("description", _))
    m.url.foreach(args += assignString("url", _))
    m.prerequisites.foreach(ps => args += assign("prerequisites", ps.asDoc))
    m.issueManagement.foreach(im => args += assign("issueManagement", im.asDoc))
    m.ciManagement.foreach(ci => args += assign("ciManagement", ci.asDoc))
    m.inceptionYear.foreach(args += assignString("inceptionYear", _))
    Some(m.mailingLists).filterNot(_.isEmpty).foreach(ds =>
      args += assign("mailingLists", seq(ds.map(_.asDoc)))
    )
    Some(m.developers).filterNot(_.isEmpty).foreach(ds =>
      args += assign("developers", seq(ds.map(_.asDoc)))
    )
    Some(m.contributors).filterNot(_.isEmpty).foreach(cs =>
      args += assign("contributors", seq(cs.map(_.asDoc)))
    )
    Some(m.licenses).filterNot(_.isEmpty).foreach(ls =>
      args += assign("licenses", seq(ls.map(_.asDoc)))
    )
    m.scm.foreach(ps => args += assign("scm", ps.asDoc))
    m.organization.foreach(o => args += assign("organization", o.asDoc))
    m.parent.foreach(p => args += assign("parent", p.asDoc))
    args ++= m.asDocArgs
    Some(m.properties).filterNot(_.isEmpty).foreach(ps => args += assign("properties", ps.asDoc))
    m.build.foreach(b => args += assign("build", b.asDoc))
    m.reporting.foreach(r => args += assign("reporting", r.asDoc))
    Some(m.profiles).filterNot(_.isEmpty).foreach(ps =>
      args += assign("profiles", seq(ps.map(_.asDoc)))
    )
    Some(m.modelEncoding).filterNot(_ == "UTF-8").foreach(args += assignString("modelEncoding", _))
    m.modelVersion.foreach(args += assignString("modelVersion", _))
    `object`("Model", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.jdk.CollectionConverters._
import org.apache.maven.model.{Model => MavenModel}

class ConvertibleMavenModel(mm: MavenModel) {
  def asScala: Model = {
    Model(
      (mm.getGroupId, mm.getArtifactId, mm.getVersion).asScala,
      Option(mm.getBuild).map(_.asScala).orNull,
      Option(mm.getCiManagement).map(_.asScala).orNull,
      mm.getContributors.asScala.map(_.asScala).toList,
      Option(mm.getDependencyManagement).map(_.asScala).orNull,
      mm.getDependencies.asScala.map(_.asScala).toList,
      mm.getDescription,
      mm.getDevelopers.asScala.map(_.asScala).toList,
      Option(mm.getDistributionManagement).map(_.asScala).orNull,
      mm.getInceptionYear,
      Option(mm.getIssueManagement).map(_.asScala).orNull,
      mm.getLicenses.asScala.map(_.asScala).toList,
      mm.getMailingLists.asScala.map(_.asScala).toList,
      mm.getModelEncoding,
      mm.getModelVersion,
      mm.getModules.asScala.toList,
      mm.getName,
      Option(mm.getOrganization).map(_.asScala).orNull,
      mm.getPackaging,
      Option(mm.getParent).map(_.asScala).orNull,
      mm.getPluginRepositories.asScala.map(_.asScala).toList,
      mm.getPomFile,
      Option(mm.getPrerequisites).map(_.asScala).orNull,
      mm.getProfiles.asScala.map(_.asScala).toList,
      mm.getProperties.asScala.toMap,
      reporting = Option(mm.getReporting()).map(_.asScala).orNull,
      mm.getRepositories.asScala.map(_.asScala).toList,
      Option(mm.getScm).map(_.asScala).orNull,
      mm.getUrl
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._
import java.util.Properties

class ConvertibleScalaModel(m: Model) {
  def asJava: MavenModel = {
    val mm = new MavenModel
    mm.setArtifactId(m.gav.artifactId)
    mm.setBuild(m.build.map(_.asJava).orNull)
    mm.setCiManagement(m.ciManagement.map(_.asJava).orNull)
    mm.setContributors(m.contributors.map(_.asJava).asJava)
    mm.setDescription(m.description.orNull)
    mm.setDevelopers(m.developers.map(_.asJava).asJava)
    mm.setDependencies(m.dependencies.map(_.asJava).asJava)
    mm.setDependencyManagement(m.dependencyManagement.map(_.asJava).orNull)
    mm.setDistributionManagement(m.distributionManagement.map(_.asJava).orNull)
    mm.setGroupId(m.gav.groupId.orNull)
    mm.setInceptionYear(m.inceptionYear.orNull)
    mm.setIssueManagement(m.issueManagement.map(_.asJava).orNull)
    mm.setLicenses(m.licenses.map(_.asJava).asJava)
    mm.setMailingLists(m.mailingLists.map(_.asJava).asJava)
    mm.setModules(m.modules.asJava)
    mm.setModelEncoding(m.modelEncoding)
    mm.setModelVersion(m.modelVersion.orNull)
    mm.setName(m.name.orNull)
    mm.setOrganization(m.organization.map(_.asJava).orNull)
    mm.setPackaging(m.packaging)
    mm.setParent(m.parent.map(_.asJava).orNull)
    mm.setPluginRepositories(m.pluginRepositories.map(_.asJava).asJava)
    mm.setPomFile(m.pomFile.orNull)
    mm.setPrerequisites(m.prerequisites.map(_.asJava).orNull)
    mm.setProfiles(m.profiles.map(_.asJava).asJava)
    mm.setProperties(Option(m.properties).map { m =>
      val p = new Properties
      m.foreach { case (k, v) => p.setProperty(k, v) }
      p
    }.orNull)
    mm.setReporting(m.reporting.map(_.asJava).orNull)
    mm.setRepositories(m.repositories.map(_.asJava).asJava)
    mm.setScm(m.scm.map(_.asJava).orNull)
    mm.setUrl(m.url.orNull)
    mm.setVersion(m.gav.version.orNull)
    mm
  }
}
