package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Prerequisites : org.apache.maven.model.Prerequisites(), Cloneable {

  @PomDsl
  fun maven(mavenVersion: String) {
    this.maven = mavenVersion
  }

  override fun clone(): org.apache.maven.model.Prerequisites {
    return super<org.apache.maven.model.Prerequisites>.clone()
  }
}
