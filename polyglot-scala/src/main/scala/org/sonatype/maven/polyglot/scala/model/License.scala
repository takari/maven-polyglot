/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class License(
    val name: Option[String],
    val url: Option[String],
    val distribution: Option[String],
    val comments: Option[String]
)

object License {
  def apply(
      name: String = null,
      url: String = null,
      distribution: String = null,
      comments: String = null
  ): License =
    new License(
      Option(name),
      Option(url),
      Option(distribution),
      Option(comments)
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedLicense(im: License) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    im.name.foreach(args += assignString("name", _))
    im.url.foreach(args += assignString("url", _))
    im.distribution.foreach(args += assignString("distribution", _))
    im.comments.foreach(args += assignString("comments", _))
    `object`("License", args.toList)
  }
}

import org.apache.maven.model.{License => MavenLicense}

class ConvertibleMavenLicense(mim: MavenLicense) {
  def asScala: License = {
    License(
      mim.getName,
      mim.getUrl,
      mim.getDistribution,
      mim.getComments
    )
  }
}

class ConvertibleScalaLicense(im: License) {
  def asJava: MavenLicense = {
    val mim = new MavenLicense
    mim.setName(im.name.orNull)
    mim.setUrl(im.url.orNull)
    mim.setDistribution(im.distribution.orNull)
    mim.setComments(im.comments.orNull)
    mim
  }
}
