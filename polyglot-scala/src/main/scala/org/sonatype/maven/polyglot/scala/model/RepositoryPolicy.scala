/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

class RepositoryPolicy(
    val enabled: Boolean,
    val updatePolicy: String,
    val checksumPolicy: String
)

object RepositoryPolicy {
  def apply(
      enabled: Boolean,
      updatePolicy: String = "daily",
      checksumPolicy: String = "warn"
  ) =
    new RepositoryPolicy(
      enabled,
      updatePolicy,
      checksumPolicy
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedRepositoryPolicy(rp: RepositoryPolicy) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    Option(rp.enabled).filterNot(_ == true).map(e => args += assign("enabled", e.toString))
    Option(rp.updatePolicy).filterNot(_ == "daily").map(args += assignString("updatePolicy", _))
    Option(rp.checksumPolicy).filterNot(_ == "warn").map(args += assignString("checksumPolicy", _))
    `object`("RepositoryPolicy", args.toList)
  }
}

import org.apache.maven.model.{RepositoryPolicy => MavenRepositoryPolicy}

class ConvertibleMavenRepositoryPolicy(mrp: MavenRepositoryPolicy) {
  def asScala: RepositoryPolicy = {
    RepositoryPolicy(
      mrp.isEnabled,
      mrp.getUpdatePolicy,
      mrp.getChecksumPolicy
    )
  }
}

class ConvertibleScalaRepositoryPolicy(rp: RepositoryPolicy) {
  def asJava: MavenRepositoryPolicy = {
    val mrp = new MavenRepositoryPolicy
    mrp.setEnabled(rp.enabled)
    mrp.setUpdatePolicy(rp.updatePolicy)
    mrp.setChecksumPolicy(rp.checksumPolicy)
    mrp
  }
}
