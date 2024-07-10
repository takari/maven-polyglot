/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.sonatype.maven.polyglot.scala.model._
import org.codehaus.plexus.util.xml.Xpp3Dom

import scala.collection.immutable

@RunWith(classOf[JUnitRunner])
class ConfigSpec extends Specification {

  "The config" should {
    "should convert from an xml doc to a config" in {
      val xml = new Xpp3Dom("configuration")
      val child1 = new Xpp3Dom("key1")
      xml.addChild(child1)
      val child2 = new Xpp3Dom("key2")
      child2.setValue("value2")
      child1.addChild(child2)
      val child3 = new Xpp3Dom("key3")
      child1.addChild(child3)
      val child4 = new Xpp3Dom("key4")
      child4.setAttribute("attr4", "attrValue4")
      child1.addChild(child4)

      val c = new ConvertibleMavenConfig(xml)
      val es = c.asScala.elements

      es.size must_== 1
      es.head._1 must_== "key1"
      val e = es.head._2.get.asInstanceOf[Config].elements
      e.size must_== 3
      e.head._1 must_== "key2"
      e.head._2.get.asInstanceOf[String] must_== "value2"
      e(1)._1 must_== "key3"
      e(1)._2 must beNone
      e(2)._1 must_== "key4"
      val ea = e(2)._2.get.asInstanceOf[Config].elements
      ea.size must_== 1
      ea.head._1 must_== "@attr4"
      ea.head._2.get.asInstanceOf[String] must_== "attrValue4"
    }
    "should convert from a config to an xml doc" in {
      val config = new Config(
        immutable.Seq(
          "key1" -> Some(new Config(
            immutable.Seq(
              "key2" -> Some("value2"),
              "key3" -> None,
              "key4" -> Some(new Config(
                immutable.Seq(
                  "@attr4" -> Some("attrValue4")
                )
              ))
            )
          ))
        )
      )

      val c = new ConvertibleScalaConfig(config)
      val xml = c.asJava

      xml.getName must_== "configuration"
      xml.getChildCount must_== 1
      val child1 = xml.getChild(0)
      child1.getName must_== "key1"
      child1.getChildCount must_== 3
      val child2 = child1.getChild(0)
      child2.getName must_== "key2"
      child2.getValue must_== "value2"
      val child3 = child1.getChild(1)
      child3.getName must_== "key3"
      child3.getValue must beNull
      val child4 = child1.getChild(2)
      child4.getName must_== "key4"
      child4.getAttributeNames.size must_== 1
      val child4Attr = child4.getAttribute("attr4")
      child4Attr must_== "attrValue4"
      child4.getChildCount must_== 0
    }
    "should permit elements to be assigned via dynamic apply" in {
      val config = Config(key1 = Config(key2 = "value2", key3 = None, `@key4` = "attrValue4"))

      val es = config.elements

      es.size must_== 1
      es.head._1 must_== "key1"
      val e = es.head._2.get.asInstanceOf[Config].elements
      e.size must_== 3
      e.head._1 must_== "key2"
      e.head._2.get.asInstanceOf[String] must_== "value2"
      e(1)._1 must_== "key3"
      e(1)._2 must beNone
      e(2)._1 must_== "@key4"
      e(2)._2.get.asInstanceOf[String] must_== "attrValue4"
    }

    // the attribute marker
    s"should permit `@` char in element name via dynamic apply" in {
      Config(`@Key@` = "value").elements.head._1 must_== "@Key$at"
    }

    // this one should always work, as _ is also valid in scala
    s"should permit `_` char in element name via dynamic apply" in {
      Config(`_Key_` = "value").elements.head._1 must_== "_Key_"
    }

    s"should permit `:` char in element name via dynamic apply" in {
      Config(`:Key:` = "value").elements.head._1 must_== ":Key:"
    }

    s"should permit `.` char in element name via dynamic apply" in {
      Config(`.Key.` = "value").elements.head._1 must_== "$u002EKey."
    }

    s"should permit `-` char in element name via dynamic apply" in {
      Config(`-Key-` = "value").elements.head._1 must_== "$minusKey-"
    }

    def checkConfig(config: Config) = {
      val c = new ConvertibleScalaConfig(config)
      val xml = c.asJava

      xml.getName must_== "configuration"
      xml.getChildCount must_== 1
      val child1 = xml.getChild(0)
      child1.getName must_== "key1"
      child1.getValue must beNull
      child1.getChildCount must_== 0
    }

    "convert a configuration to XML with an empty config block (Config ctr with None)" in {
      checkConfig(
        new Config(immutable.Seq(
          "key1" -> None
        ))
      )
    }

    "convert a configuration to XML with an empty config block (Config apply with Config.Emptry)" in {
      checkConfig(
        Config(
          key1 = None
        )
      )
    }
  }
}
