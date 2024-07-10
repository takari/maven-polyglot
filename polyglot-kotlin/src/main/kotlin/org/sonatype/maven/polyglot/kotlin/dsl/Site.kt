package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Site : org.apache.maven.model.Site(), Cloneable {

  @PomDsl
  fun id(id: String): Site {
    this.id = id
    return this
  }

  @PomDsl
  fun name(name: String): Site {
    this.name = name
    return this
  }

  @PomDsl
  fun url(url: String): Site {
    this.url = url
    return this
  }

  override fun clone(): org.apache.maven.model.Site {
    return super<org.apache.maven.model.Site>.clone()
  }
}
