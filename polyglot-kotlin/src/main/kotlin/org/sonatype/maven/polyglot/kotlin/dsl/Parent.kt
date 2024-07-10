package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Parent : org.apache.maven.model.Parent(), Cloneable {

  init {
    relativePath = "../pom.kts"
  }

  @PomDsl
  fun groupId(groupId: String): Parent {
    this.groupId = groupId
    return this
  }

  @PomDsl
  fun artifactId(artifactId: String): Parent {
    this.artifactId = artifactId
    return this
  }

  @PomDsl
  fun version(version: String): Parent {
    this.version = version
    return this
  }

  @PomDsl
  infix fun relativePath(relativePath: String): Parent {
    this.relativePath = relativePath
    return this
  }

  override fun clone(): org.apache.maven.model.Parent {
    return super<org.apache.maven.model.Parent>.clone()
  }
}
