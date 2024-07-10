/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

class MailingList(
    val name: Option[String],
    val subscribe: Option[String],
    val unsubscribe: Option[String],
    val post: Option[String],
    val archive: Option[String],
    val otherArchives: immutable.Seq[String]
)

object MailingList {
  def apply(
      name: String = null,
      subscribe: String = null,
      unsubscribe: String = null,
      post: String = null,
      archive: String = null,
      otherArchives: immutable.Seq[String] = Nil
  ): MailingList =
    new MailingList(
      Option(name),
      Option(subscribe),
      Option(unsubscribe),
      Option(post),
      Option(archive),
      otherArchives
    )
}

import org.sonatype.maven.polyglot.scala.ScalaPrettyPrinter._

class PrettiedMailingList(ml: MailingList) {
  def asDoc: Doc = {
    val args = scala.collection.mutable.ListBuffer[Doc]()
    ml.name.foreach(args += assignString("name", _))
    ml.subscribe.foreach(args += assignString("subscribe", _))
    ml.unsubscribe.foreach(args += assignString("unsubscribe", _))
    ml.post.foreach(args += assignString("post", _))
    ml.archive.foreach(args += assignString("archive", _))
    Some(ml.otherArchives).filterNot(_.isEmpty).foreach(ds =>
      args += assign("otherArchives", seqString(ds))
    )
    `object`("MailingList", args.toList)
  }
}

import org.apache.maven.model.{MailingList => MavenMailingList}
import scala.jdk.CollectionConverters._

class ConvertibleMavenMailingList(mml: MavenMailingList) {
  def asScala: MailingList = {
    MailingList(
      mml.getName,
      mml.getSubscribe,
      mml.getUnsubscribe,
      mml.getPost,
      mml.getArchive,
      mml.getOtherArchives.asScala.toList
    )
  }
}

class ConvertibleScalaMailingList(ml: MailingList) {
  def asJava: MavenMailingList = {
    val mml = new MavenMailingList
    mml.setArchive(ml.archive.orNull)
    mml.setName(ml.name.orNull)
    mml.setOtherArchives(ml.otherArchives.asJava)
    mml.setPost(ml.post.orNull)
    mml.setSubscribe(ml.subscribe.orNull)
    mml.setUnsubscribe(ml.unsubscribe.orNull)
    mml
  }
}
