package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Repository : org.apache.maven.model.Repository(), Cloneable {

  @PomDsl
  fun id(id: String): Repository {
    this.id = id
    return this
  }

  @PomDsl
  fun name(name: String): Repository {
    this.name = name
    return this
  }

  @PomDsl
  fun url(url: String): Repository {
    this.url = url
    return this
  }

  @PomDsl
  fun layout(layout: String): Repository {
    this.layout = layout
    return this
  }

  @PomDsl
  fun releases(block: RepositoryPolicy.(RepositoryPolicy) -> Unit) {
    val releases = RepositoryPolicy()
    block.invoke(releases, releases)
    this.releases = releases
  }

  @PomDsl
  fun snapshots(block: RepositoryPolicy.(RepositoryPolicy) -> Unit) {
    val snapshots = RepositoryPolicy()
    block.invoke(snapshots, snapshots)
    this.snapshots = snapshots
  }

  override fun clone(): org.apache.maven.model.Repository {
    return super<org.apache.maven.model.Repository>.clone()
  }
}
