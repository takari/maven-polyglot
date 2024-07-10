/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.language.implicitConversions

case class GroupId(groupId: Option[String]) {
  def %(artifactId: String): GroupArtifactId = GroupArtifactId(this, artifactId)

  def %%(artifactId: String)(implicit scalaVersion: ScalaVersion): GroupArtifactId =
    GroupArtifactId(this, artifactId + "_" + scalaVersion.binaryVersion)
}

object GroupId {

  /**
   * Group ids are optional and when specified with just "" then they are to be treated as None.
   */
  def apply(groupId: String): GroupId = {
    GroupId(Option(groupId).flatMap({
      case g if g.isEmpty => None
      case g => Some(g)
    }))
  }
}

case class GroupArtifactId(private val groupIdObj: GroupId, artifactId: String) {
  def groupId: Option[String] = groupIdObj.groupId

  def %(version: String): Gav = Gav(this, version)
}

/**
 * A Gav provides the coordinates of a module in a repository.
 */
case class Gav(private val groupArtifactIdObj: GroupArtifactId, version: Option[String]) {
  def groupId: Option[String] = groupArtifactIdObj.groupId

  def artifactId: String = groupArtifactIdObj.artifactId

  def %(scope: String): Dependency = Dependency(gav = this, scope = scope)
}

object Gav {

  /**
   * Versions are optional and when specified with just "" then they are to be treated as None.
   */
  def apply(groupArtifactIdObj: GroupArtifactId, version: String): Gav = {
    Gav(
      groupArtifactIdObj,
      Option(version).flatMap({
        case v if v.isEmpty => None
        case v => Some(v)
      })
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedGroupArtifactId(ga: GroupArtifactId) {
  def asDoc: Doc = {
    dquotes(ga.groupId.getOrElse[String]("")) <+> percent <+> dquotes(ga.artifactId)
  }
}

class PrettiedGav(gav: Gav) {
  def asDoc: Doc = {
    dquotes(gav.groupId.getOrElse[String]("")) <+> percent <+> dquotes(
      gav.artifactId
    ) <> gav.version.map(space <> percent <+> dquotes(_)).getOrElse(emptyDoc)
  }
}

import org.sonatype.maven.polyglot.scala.MavenConverters._

class ConvertibleMavenGroupArtifactId(mga: (String, String)) {
  def asScala: GroupArtifactId = {
    GroupArtifactId(GroupId(mga._1), mga._2)
  }
}

class ConvertibleMavenGav(mgav: (String, String, String)) {
  def asScala: Gav = {
    val ga = (mgav._1, mgav._2).asScala
    Gav(ga, Option(mgav._3))
  }
}
