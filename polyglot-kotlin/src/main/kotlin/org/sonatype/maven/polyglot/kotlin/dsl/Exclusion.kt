package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Exclusion : org.apache.maven.model.Exclusion(), Cloneable {

  @PomDsl
  fun groupId(groupId: String): Exclusion {
    this.groupId = groupId
    return this
  }

  @PomDsl
  fun artifactId(artifactId: String): Exclusion {
    this.artifactId = artifactId
    return this
  }

  override fun clone(): org.apache.maven.model.Exclusion {
    return super<org.apache.maven.model.Exclusion>.clone()
  }
}
