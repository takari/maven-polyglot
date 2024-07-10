/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import org.sonatype.maven.polyglot.execute.ExecuteContext

class Task(
    val id: String,
    val phase: String,
    val profileId: Option[String],
    val block: ExecuteContext => Unit
)

object Task {
  def apply(
      id: String,
      phase: String,
      profileId: String = null
  )(block: ExecuteContext => Unit): Task =
    new Task(id, phase, Option(profileId), block)
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedTask(t: Task) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    args += assignString("id", t.id)
    args += assignString("phase", t.phase)
    t.profileId.foreach(args += assignString("profileId", _))
    `object`("Task", args.toList) <+> "{compiled code}"
  }
}
