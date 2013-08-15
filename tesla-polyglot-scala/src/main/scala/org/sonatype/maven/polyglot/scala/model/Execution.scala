/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.xml.Elem

case class Execution(
                      id: String,
                      phase: Option[String],
                      goals: Seq[String],
                      override val inherited: Boolean,
                      override val configuration: Option[Elem]
                      ) extends ConfigurationContainer(inherited, configuration)

object Execution {
  def apply(
             id: String = "default",
             phase: String = null,
             goals: Seq[String] = Seq.empty,
             inherited: Boolean = true,
             configuration: Elem = null
             ) = {
    new Execution(
      id,
      Option(phase),
      goals,
      inherited,
      Option(configuration)
    )
  }
}


import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedExecution(e: Execution) {
  def asDoc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    Some(e.id).filterNot(_ == "default").foreach(args += assignString("id", _))
    e.phase.foreach(args += assignString("phase", _))
    Some(e.goals).filterNot(_.isEmpty).foreach(g => args += assign("goals", seqString(g)))
    args ++= e.asDocArgs
    `object`("Execution", args)
  }
}


import org.sonatype.maven.polyglot.scala.MavenConverters._
import scala.collection.JavaConverters._
import org.apache.maven.model.{PluginExecution => MavenExecution}

class ConvertibleMavenExecution(me: MavenExecution) {
  def asScala: Execution = {
    Execution(
      me.getId,
      me.getPhase,
      me.getGoals.asScala,
      me.isInherited,
      Option(me.getConfiguration).map(_.asScala).orNull
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaExecution(e: Execution) {
  def asJava: MavenExecution = {
    val me = new MavenExecution
    me.setGoals(e.goals.asJava)
    me.setId(e.id)
    me.setPhase(e.phase.orNull)
    me.setConfiguration(e.configuration.map(_.asJava).orNull)
    me.setInherited(e.inherited)
    me
  }
}