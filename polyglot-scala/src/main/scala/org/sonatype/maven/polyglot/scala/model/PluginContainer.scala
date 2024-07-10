/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

abstract class PluginContainer(val plugins: immutable.Seq[Plugin])

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedPluginContainer(pc: PluginContainer) {
  def asDocArgs: immutable.Seq[Doc] = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    Some(pc.plugins).filterNot(_.isEmpty).foreach(ps =>
      args += assign("plugins", seq(ps.map(_.asDoc)))
    )
    args.toList
  }
}
