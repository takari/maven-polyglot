/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class Contributor(
    val email: Option[String],
    val name: Option[String],
    val organization: Option[String],
    val organizationUrl: Option[String],
    val roles: immutable.Seq[String],
    val timezone: Option[String],
    val url: Option[String]
)

object Contributor {
  def apply(
      email: String = null,
      name: String = null,
      organization: String = null,
      organizationUrl: String = null,
      roles: immutable.Seq[String] = Nil,
      timezone: String = null,
      url: String = null
  ) =
    new Contributor(
      Option(email),
      Option(name),
      Option(organization),
      Option(organizationUrl),
      roles,
      Option(timezone),
      Option(url)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedContributor(c: Contributor) {
  def asDoc: Doc = `object`("Contributor", this.asDocArgs)

  def asDocArgs: immutable.Seq[Doc] = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    c.email.foreach(args += assignString("email", _))
    c.name.foreach(args += assignString("name", _))
    c.organization.foreach(args += assignString("organization", _))
    c.organizationUrl.foreach(args += assignString("organizationUrl", _))
    Some(c.roles).filterNot(_.isEmpty).foreach(rs => args += assign("roles", seqString(rs)))
    c.timezone.foreach(args += assignString("timezone", _))
    c.url.foreach(args += assignString("url", _))
    args.toList
  }
}

import scala.jdk.CollectionConverters._
import org.apache.maven.model.{Contributor => MavenContributor}

class ConvertibleMavenContributor(mc: MavenContributor) {
  def asScala: Contributor = {
    Contributor(
      mc.getEmail,
      mc.getName,
      mc.getOrganization,
      mc.getOrganizationUrl,
      mc.getRoles.asScala.toList,
      mc.getTimezone,
      mc.getUrl
    )
  }
}

class ConvertibleScalaContributor(c: Contributor) {
  def asJava: MavenContributor = {
    val mc = new MavenContributor
    mc.setEmail(c.email.orNull)
    mc.setName(c.name.orNull)
    mc.setOrganization(c.organization.orNull)
    mc.setOrganizationUrl(c.organizationUrl.orNull)
    mc.setRoles(c.roles.asJava)
    mc.setTimezone(c.timezone.orNull)
    mc.setUrl(c.url.orNull)
    mc
  }
}
