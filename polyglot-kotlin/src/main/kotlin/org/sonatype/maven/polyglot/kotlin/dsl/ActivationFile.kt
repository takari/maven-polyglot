package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ActivationFile : org.apache.maven.model.ActivationFile(), Cloneable {

  @PomDsl
  fun missing(missing: String): ActivationFile {
    this.missing = missing
    return this
  }

  @PomDsl
  fun exists(exists: String): ActivationFile {
    this.exists = exists
    return this
  }

  override fun clone(): org.apache.maven.model.ActivationFile {
    return super<org.apache.maven.model.ActivationFile>.clone()
  }
}
