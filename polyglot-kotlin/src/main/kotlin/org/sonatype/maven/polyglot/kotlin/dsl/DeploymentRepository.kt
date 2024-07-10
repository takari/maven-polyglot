package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class DeploymentRepository : org.apache.maven.model.DeploymentRepository(), Cloneable {

  @PomDsl
  fun uniqueVersion(uniqueVersion: Boolean = true) {
    this.isUniqueVersion = uniqueVersion
  }

  @PomDsl
  fun id(id: String): DeploymentRepository {
    this.id = id
    return this
  }

  @PomDsl
  fun name(name: String): DeploymentRepository {
    this.name = name
    return this
  }

  @PomDsl
  fun url(url: String): DeploymentRepository {
    this.url = url
    return this
  }

  @PomDsl
  fun layout(layout: String): DeploymentRepository {
    this.layout = layout
    return this
  }

  override fun clone(): org.apache.maven.model.DeploymentRepository {
    return super<org.apache.maven.model.DeploymentRepository>.clone()
  }
}
