package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Extension : org.apache.maven.model.Extension(), Cloneable {

  @PomDsl
  fun groupId(groupId: String): Extension {
    this.groupId = groupId
    return this
  }

  @PomDsl
  fun artifactId(artifactId: String): Extension {
    this.artifactId = artifactId
    return this
  }

  @PomDsl
  fun version(version: String): Extension {
    this.version = version
    return this
  }

  override fun clone(): org.apache.maven.model.Extension {
    return super<org.apache.maven.model.Extension>.clone()
  }
}
