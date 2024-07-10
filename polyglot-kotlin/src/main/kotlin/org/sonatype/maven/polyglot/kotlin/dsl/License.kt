package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class License : org.apache.maven.model.License(), Cloneable {

  @PomDsl
  fun name(name: String): License {
    this.name = name
    return this
  }

  @PomDsl
  fun url(url: String): License {
    this.url = url
    return this
  }

  @PomDsl
  fun distribution(distribution: String): License {
    this.distribution = distribution
    return this
  }

  @PomDsl
  fun comments(comments: String): License {
    this.comments = comments
    return this
  }

  override fun clone(): org.apache.maven.model.License {
    return super<org.apache.maven.model.License>.clone()
  }
}
