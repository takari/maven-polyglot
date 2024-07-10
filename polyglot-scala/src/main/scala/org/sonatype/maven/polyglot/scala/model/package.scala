/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala

import scala.language.implicitConversions

/**
 * Conversion functions for use within pom declarations.
 */
package object model {

  /**
   * Create an alias for File to reduce the imports required.
   */
  type File = java.io.File

  /**
   * Produce a groupid from a string where a group id is required.
   */
  implicit def toGroupId(groupId: String): GroupId = GroupId(groupId)

  /**
   * Produce a dependency from a Gav when required.
   */
  implicit def toDependency(gav: Gav): Dependency = Dependency(gav)

  /**
   * Produce a dependency from a GroupArtifactId when required.
   */
  implicit def toDependency(groupArtifactId: GroupArtifactId): Dependency =
    Dependency(Gav(groupArtifactId, None))

  /**
   * Produce a Gav where we just have a group and artifact.
   */
  implicit def toGav(ga: GroupArtifactId): Gav = Gav(ga, None)
}
