package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Relocation : org.apache.maven.model.Relocation(), Cloneable {

  @PomDsl
  fun groupId(groupId: String): Relocation {
    this.groupId = groupId
    return this
  }

  @PomDsl
  fun artifactId(artifactId: String): Relocation {
    this.artifactId = artifactId
    return this
  }

  @PomDsl
  fun version(version: String): Relocation {
    this.version = version
    return this
  }

  @PomDsl
  fun message(message: String): Relocation {
    this.message = message
    return this
  }

  override fun clone(): org.apache.maven.model.Relocation {
    return super<org.apache.maven.model.Relocation>.clone()
  }
}
