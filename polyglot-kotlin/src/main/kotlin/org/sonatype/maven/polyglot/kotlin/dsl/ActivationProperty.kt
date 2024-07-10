package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ActivationProperty : org.apache.maven.model.ActivationProperty(), Cloneable {

  @PomDsl
  fun name(name: String): ActivationProperty {
    this.name = name
    return this
  }

  @PomDsl
  fun value(value: String): ActivationProperty {
    this.value = value
    return this
  }

  override fun clone(): org.apache.maven.model.ActivationProperty {
    return super<org.apache.maven.model.ActivationProperty>.clone()
  }
}
