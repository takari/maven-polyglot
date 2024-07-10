package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Activation : org.apache.maven.model.Activation(), Cloneable {

  @PomDsl
  fun os(name: String? = null, block: ActivationOS.(ActivationOS) -> Unit) {
    val os = ActivationOS().apply { this.name = name }
    block(os, os)
    this.os = os
  }

  @PomDsl
  fun property(name: String? = null, block: ActivationProperty.(ActivationProperty) -> Unit) {
    val property = ActivationProperty().apply { this.name = name }
    block(property, property)
    this.property = property
  }

  @PomDsl
  fun file(block: ActivationFile.(ActivationFile) -> Unit) {
    val file = ActivationFile()
    block(file, file)
    this.file = file
  }

  @PomDsl
  fun jdk(jdk: String): Activation {
    this.jdk = jdk
    return this
  }

  @PomDsl
  fun activeByDefault(activeByDefault: Boolean = true): Activation {
    this.isActiveByDefault = activeByDefault
    return this
  }

  override fun clone(): org.apache.maven.model.Activation {
    return super<org.apache.maven.model.Activation>.clone()
  }
}
