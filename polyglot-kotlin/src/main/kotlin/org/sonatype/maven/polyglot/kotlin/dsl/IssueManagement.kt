package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class IssueManagement : org.apache.maven.model.IssueManagement(), Cloneable {

  @PomDsl
  fun system(system: String): IssueManagement {
    this.system = system
    return this
  }

  @PomDsl
  fun url(url: String): IssueManagement {
    this.url = url
    return this
  }

  override fun clone(): org.apache.maven.model.IssueManagement {
    return super<org.apache.maven.model.IssueManagement>.clone()
  }
}
