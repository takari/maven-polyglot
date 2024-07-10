package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class RepositoryPolicy : org.apache.maven.model.RepositoryPolicy(), Cloneable {

  @PomDsl
  fun enabled(enabled: Boolean = true): RepositoryPolicy {
    this.isEnabled = enabled
    return this
  }

  @PomDsl
  fun updatePolicy(updatePolicy: String): RepositoryPolicy {
    this.updatePolicy = updatePolicy
    return this
  }

  @PomDsl
  fun checksumPolicy(checksumPolicy: String): RepositoryPolicy {
    this.checksumPolicy = checksumPolicy
    return this
  }

  override fun clone(): org.apache.maven.model.RepositoryPolicy {
    return super<org.apache.maven.model.RepositoryPolicy>.clone()
  }
}
