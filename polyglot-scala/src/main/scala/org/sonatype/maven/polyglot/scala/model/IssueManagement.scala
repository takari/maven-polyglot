/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class IssueManagement(
    val system: Option[String],
    val url: Option[String]
)

object IssueManagement {
  def apply(
      system: String = null,
      url: String = null
  ): IssueManagement =
    new IssueManagement(
      Option(system),
      Option(url)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedIssueManagement(im: IssueManagement) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    im.system.foreach(args += assignString("system", _))
    im.url.foreach(args += assignString("url", _))
    `object`("IssueManagement", args.toList)
  }
}

import org.apache.maven.model.{IssueManagement => MavenIssueManagement}

class ConvertibleMavenIssueManagement(mim: MavenIssueManagement) {
  def asScala: IssueManagement = {
    IssueManagement(
      mim.getSystem,
      mim.getUrl
    )
  }
}

class ConvertibleScalaIssueManagement(im: IssueManagement) {
  def asJava: MavenIssueManagement = {
    val mim = new MavenIssueManagement
    mim.setSystem(im.system.orNull)
    mim.setUrl(im.url.orNull)
    mim
  }
}
