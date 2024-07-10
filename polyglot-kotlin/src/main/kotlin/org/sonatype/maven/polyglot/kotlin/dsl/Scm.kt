package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Scm : org.apache.maven.model.Scm(), Cloneable {

  @PomDsl
  fun url(url: String) {
    this.url = url
  }

  @PomDsl
  fun connection(connection: String) {
    this.connection = connection
  }

  @PomDsl
  fun developerConnection(developerConnection: String) {
    this.developerConnection = developerConnection
  }

  @PomDsl
  fun tag(tag: String) {
    this.tag = tag
  }

  override fun clone(): org.apache.maven.model.Scm {
    return super<org.apache.maven.model.Scm>.clone()
  }
}
