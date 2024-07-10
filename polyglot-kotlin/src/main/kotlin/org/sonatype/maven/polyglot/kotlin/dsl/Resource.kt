package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Resource : org.apache.maven.model.Resource(), Cloneable {

  @PomDsl
  fun targetPath(targetPath: String): Resource {
    this.targetPath = targetPath
    return this
  }

  @PomDsl
  fun filtering(filtering: Boolean = true): Resource {
    this.isFiltering = filtering
    return this
  }

  @PomDsl
  fun directory(directory: String): Resource {
    this.directory = directory
    return this
  }

  @PomDsl
  fun includes(vararg includes: String): Resource {
    this.includes = includes.asList()
    return this
  }

  @PomDsl
  fun excludes(vararg excludes: String): Resource {
    this.excludes = excludes.asList()
    return this
  }

  override fun clone(): org.apache.maven.model.Resource {
    return super<org.apache.maven.model.Resource>.clone()
  }
}
