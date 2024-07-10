/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class CiManagement(
    val notifiers: immutable.Seq[Notifier],
    val system: Option[String],
    val url: Option[String]
)

object CiManagement {
  def apply(
      notifiers: immutable.Seq[Notifier] = Nil,
      system: String = null,
      url: String = null
  ) =
    new CiManagement(
      notifiers,
      Option(system),
      Option(url)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedCiManagement(c: CiManagement) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    Some(c.notifiers).filterNot(_.isEmpty).foreach(n =>
      args += assign("notifiers", seq(n.map(_.asDoc)))
    )
    c.system.foreach(args += assignString("system", _))
    c.url.foreach(args += assignString("url", _))
    `object`("CiManagement", args.toList)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.jdk.CollectionConverters._
import org.apache.maven.model.{CiManagement => MavenCiManagement}

class ConvertibleMavenCiManagement(mcm: MavenCiManagement) {
  def asScala: CiManagement = {
    CiManagement(
      mcm.getNotifiers.asScala.map(_.asScala).toList,
      mcm.getSystem,
      mcm.getUrl
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaCiManagement(cm: CiManagement) {
  def asJava: MavenCiManagement = {
    val mcm = new MavenCiManagement
    mcm.setNotifiers(cm.notifiers.map(_.asJava).asJava)
    mcm.setSystem(cm.system.orNull)
    mcm.setUrl(cm.url.orNull)
    mcm
  }
}
