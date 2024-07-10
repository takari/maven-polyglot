package org.sonatype.maven.polyglot.kotlin.dsl

import java.util.*

@PomDsl
open class Properties {

  private val properties: MutableMap<String, String> = LinkedHashMap()
  private val groups: MutableList<Group> = ArrayList()

  internal fun entries(): Map<String, String> {
    return this@Properties.properties
  }

  @Suppress("unused")
  internal fun groups(): List<Group> {
    return this@Properties.groups
  }

  @PomDsl
  fun group(comment: String? = null, block: Properties.Group.(Properties.Group) -> Unit) {
    val group = Group(comment)
    block(group, group)
    groups.add(group)
  }

  infix fun String.to(value: Any) = addEntry(this, value)

  private fun addEntry(key: String, value: Any) {
    properties[key] = value.toString()
  }

  @PomDsl
  inner class Group(@Suppress("unused") val comment: String? = null) {

    private val keys: MutableSet<String> = LinkedHashSet()

    @Suppress("unused")
    fun entries(): Map<String, String> {
      return this@Properties.properties.filterKeys { keys.contains(it) }
    }

    infix fun String.to(value: String) = addEntry(this, value)

    infix fun String.to(value: Number) = addEntry(this, value)

    infix fun String.to(value: Boolean) = addEntry(this, value)

    private fun addEntry(key: String, value: Any) {
      keys.add(key)
      this@Properties.addEntry(key, value)
    }
  }
}
