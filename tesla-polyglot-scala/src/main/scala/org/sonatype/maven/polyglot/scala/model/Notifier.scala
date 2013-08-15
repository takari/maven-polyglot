/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

case class Notifier(
                     address: Option[String],
                     configuration: Map[String, String],
                     sendOnError: Boolean,
                     sendOnFailure: Boolean,
                     sendOnSuccess: Boolean,
                     sendOnWarning: Boolean,
                     `type`: String
                     )

object Notifier {
  def apply(
             address: String = null,
             configuration: Map[String, String] = Map.empty,
             sendOnError: Boolean = true,
             sendOnFailure: Boolean = true,
             sendOnSuccess: Boolean = true,
             sendOnWarning: Boolean = true,
             `type`: String = "mail"
             ) =
    new Notifier(
      Option(address),
      configuration,
      sendOnError,
      sendOnFailure,
      sendOnSuccess,
      sendOnWarning,
      `type`
    )
}


import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedNotifier(n: Notifier) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    n.address.foreach(args += assignString("address", _))
    Some(n.configuration).filterNot(_.isEmpty).foreach(c => args += assign("configuration", c.asDoc))
    Some(n.sendOnError).filterNot(_ == true).foreach(e => args += assign("sendOnError", e.toString))
    Some(n.sendOnFailure).filterNot(_ == true).foreach(e => args += assign("sendOnFailure", e.toString))
    Some(n.sendOnSuccess).filterNot(_ == true).foreach(e => args += assign("sendOnSuccess", e.toString))
    Some(n.sendOnWarning).filterNot(_ == true).foreach(e => args += assign("sendOnWarning", e.toString))
    Some(n.`type`).filterNot(_ == "mail").foreach(args += assignString("`type`", _))
    `object`("Notifier", args)
  }
}


import org.sonatype.maven.polyglot.scala.MavenConverters._
import org.apache.maven.model.{Notifier => MavenNotifier}

class ConvertibleMavenNotifier(mn: MavenNotifier) {
  def asScala: Notifier = {
    Notifier(
      mn.getAddress,
      mn.getConfiguration.asScala,
      mn.isSendOnError,
      mn.isSendOnFailure,
      mn.isSendOnSuccess,
      mn.isSendOnWarning,
      mn.getType
    )
  }
}

import org.sonatype.maven.polyglot.scala.ScalaConverters._

class ConvertibleScalaNotifier(n: Notifier) {
  def asJava: MavenNotifier = {
    val mn = new MavenNotifier
    mn.setAddress(n.address.orNull)
    mn.setConfiguration(Some(n.configuration).map(_.asJava).orNull)
    mn.setSendOnError(n.sendOnError)
    mn.setSendOnFailure(n.sendOnFailure)
    mn.setSendOnSuccess(n.sendOnSuccess)
    mn.setSendOnWarning(n.sendOnWarning)
    mn.setType(n.`type`)
    mn
  }
}