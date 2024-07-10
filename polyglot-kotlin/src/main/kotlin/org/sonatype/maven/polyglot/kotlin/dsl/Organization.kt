package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Organization : org.apache.maven.model.Organization(), Cloneable {

  @PomDsl
  fun name(name: String): Organization {
    this.name = name
    return this
  }

  @PomDsl
  fun url(url: String): Organization {
    this.url = url
    return this
  }

  override fun clone(): org.apache.maven.model.Organization {
    return super<org.apache.maven.model.Organization>.clone()
  }
}
