package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ActivationOS : org.apache.maven.model.ActivationOS(), Cloneable {

  @PomDsl
  fun name(name: String): ActivationOS {
    this.name = name
    return this
  }

  @PomDsl
  fun family(family: String): ActivationOS {
    this.family = family
    return this
  }

  @PomDsl
  fun arch(arch: String): ActivationOS {
    this.arch = arch
    return this
  }

  @PomDsl
  fun version(version: String): ActivationOS {
    this.version = version
    return this
  }

  override fun clone(): org.apache.maven.model.ActivationOS {
    return super<org.apache.maven.model.ActivationOS>.clone()
  }
}
