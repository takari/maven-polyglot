package org.sonatype.maven.polyglot.kotlin.dsl

import org.codehaus.plexus.util.xml.Xpp3Dom

@PomDsl
open class XmlNode(val name: String, val parent: XmlNode? = null) {

  var xpp3Dom: Xpp3Dom = Xpp3Dom(name)

  init {
    if (parent != null) {
      parent.xpp3Dom.addChild(xpp3Dom)
    }
  }

  infix fun String.to(value: Any) {
    val node = XmlNode(this, this@XmlNode)
    node.xpp3Dom.value = value.toString()
  }

  infix fun String.to(value: Int) {
    val node = XmlNode(this, this@XmlNode)
    node.xpp3Dom.value = value.toString()
  }

  infix fun String.to(value: Long) {
    val node = XmlNode(this, this@XmlNode)
    node.xpp3Dom.value = value.toString()
  }

  infix fun String.to(value: Double) {
    val node = XmlNode(this, this@XmlNode)
    node.xpp3Dom.value = value.toString()
  }

  infix fun String.to(value: Boolean) {
    val node = XmlNode(this, this@XmlNode)
    node.xpp3Dom.value = value.toString()
  }

  @PomDsl
  operator fun String.invoke(block: XmlNode.(XmlNode) -> Unit): XmlNode {
    val node = XmlNode(this, this@XmlNode)
    block.invoke(node, node)
    return node
  }
}
