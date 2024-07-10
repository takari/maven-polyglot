package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class NotifierList : ArrayList<org.apache.maven.model.Notifier>(), Cloneable {

  /** Provides a callback for defining a new notifier entry. */
  @PomDsl
  fun notifier(block: Notifier.(Notifier) -> Unit) {
    val notifier = Notifier()
    block(notifier, notifier)
    add(notifier)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
