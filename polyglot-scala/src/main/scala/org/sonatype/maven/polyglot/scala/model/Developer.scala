/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class Developer(
    val id: Option[String],
    override val email: Option[String],
    override val name: Option[String],
    override val organization: Option[String],
    override val organizationUrl: Option[String],
    override val roles: immutable.Seq[String],
    override val timezone: Option[String],
    override val url: Option[String]
) extends Contributor(
      email,
      name,
      organization,
      organizationUrl,
      roles,
      timezone,
      url
    )

object Developer {
  def apply(
      id: String = null,
      email: String = null,
      name: String = null,
      organization: String = null,
      organizationUrl: String = null,
      roles: immutable.Seq[String] = Nil,
      timezone: String = null,
      url: String = null
  ): Developer =
    new Developer(
      Option(id),
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

class PrettiedDeveloper(d: Developer) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    d.id.foreach(args += assignString("id", _))
    args ++= d.asInstanceOf[Contributor].asDocArgs
    `object`("Developer", args.toList)
  }
}

import scala.jdk.CollectionConverters._
import org.apache.maven.model.{Developer => MavenDeveloper}

class ConvertibleMavenDeveloper(mc: MavenDeveloper) {
  def asScala: Developer = {
    Developer(
      mc.getId,
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

class ConvertibleScalaDeveloper(d: Developer) {
  def asJava: MavenDeveloper = {
    val md = new MavenDeveloper
    md.setId(d.id.orNull)
    md.setEmail(d.email.orNull)
    md.setName(d.name.orNull)
    md.setOrganization(d.organization.orNull)
    md.setOrganizationUrl(d.organizationUrl.orNull)
    md.setRoles(d.roles.asJava)
    md.setTimezone(d.timezone.orNull)
    md.setUrl(d.url.orNull)
    md
  }
}
