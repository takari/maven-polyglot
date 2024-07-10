package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class CiManagement : org.apache.maven.model.CiManagement(), Cloneable {

  @PomDsl
  fun system(system: String): CiManagement {
    this.system = system
    return this
  }

  @PomDsl
  fun url(url: String): CiManagement {
    this.url = url
    return this
  }

  /** Provides a callback for defining a list of notifiers. */
  @PomDsl
  fun notifiers(block: NotifierList.(NotifierList) -> Unit) {
    val notifiers = NotifierList()
    block.invoke(notifiers, notifiers)
    this.notifiers = notifiers
  }

  override fun clone(): org.apache.maven.model.CiManagement {
    return super<org.apache.maven.model.CiManagement>.clone()
  }
}
