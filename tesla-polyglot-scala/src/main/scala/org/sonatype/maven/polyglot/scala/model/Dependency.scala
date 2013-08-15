/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

case class Dependency(
                       gav: Gav,
                       `type`: String,
                       classifier: Option[String],
                       scope: Option[String],
                       systemPath: Option[String],
                       exclusions: Seq[GroupArtifactId],
                       optional: Boolean
                       )

object Dependency {
  def apply(
             gav: Gav,
             `type`: String = "jar",
             classifier: String = null,
             scope: String = null,
             systemPath: String = null,
             exclusions: Seq[GroupArtifactId] = Seq.empty,
             optional: Boolean = false
             ) =
    new Dependency(
      gav,
      `type`,
      Option(classifier),
      Option(scope),
      Option(systemPath),
      exclusions,
      optional
    )
}


import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedDependency(d: Dependency) {
  def asDoc: Doc = {
    val typeAssigned = d.`type` != "jar"
    val classifierAssigned = d.classifier.isDefined
    val systemPathAssigned = d.systemPath.isDefined
    val exclusionsAssigned = !d.exclusions.isEmpty
    val optionalAssigned = d.optional
    if (typeAssigned || classifierAssigned || systemPathAssigned || exclusionsAssigned || optionalAssigned) {
      val args = scala.collection.mutable.ListBuffer(d.gav.asDoc)
      if (typeAssigned) args += assignString("`type`", d.`type`)
      d.classifier.foreach(args += assignString("classifier", _))
      d.scope.foreach(args += assignString("scope", _))
      d.systemPath.foreach(args += assignString("systemPath", _))
      if (exclusionsAssigned) args += assign("exclusions", seq(d.exclusions.map(_.asDoc)))
      if (optionalAssigned) args += assign("optional", d.optional.toString)
      `object`("Dependency", args)
    } else {
      val gav = d.gav.asDoc
      val version = if (d.gav.version.isEmpty) space <> percent <+> dquote <> dquote else empty
      gav <> version <> d.scope.map(empty <+> percent <+> dquotes(_)).getOrElse(empty)
    }
  }
}


import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.collection.JavaConverters._
import org.apache.maven.model.{Dependency => MavenDependency, Exclusion => MavenExclusion}

class ConvertibleMavenDependency(md: MavenDependency) {
  def asScala: Dependency = {
    Dependency(
      (md.getGroupId, md.getArtifactId, md.getVersion).asScala,
      md.getType,
      md.getClassifier,
      md.getScope,
      md.getSystemPath,
      md.getExclusions.asScala.map(e => (e.getGroupId, e.getArtifactId).asScala),
      md.isOptional
    )
  }
}

class ConvertibleScalaDependency(d: Dependency) {
  def asJava: MavenDependency = {
    val md = new MavenDependency
    md.setArtifactId(d.gav.artifactId)
    md.setClassifier(d.classifier.orNull)
    md.setExclusions(d.exclusions.map({
      gav =>
        val e = new MavenExclusion
        e.setArtifactId(gav.artifactId)
        e.setGroupId(gav.groupId.orNull)
        e
    }).asJava)
    md.setGroupId(d.gav.groupId.orNull)
    md.setOptional(d.optional)
    md.setScope(d.scope.orNull)
    md.setSystemPath(d.systemPath.orNull)
    md.setType(d.`type`)
    md.setVersion(d.gav.version.orNull)
    md
  }
}