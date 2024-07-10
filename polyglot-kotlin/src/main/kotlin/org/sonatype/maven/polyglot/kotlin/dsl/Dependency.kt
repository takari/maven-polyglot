package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Dependency : org.apache.maven.model.Dependency(), Cloneable {

  @PomDsl
  fun groupId(groupId: String): Dependency {
    this.groupId = groupId
    return this
  }

  @PomDsl
  fun artifactId(artifactId: String): Dependency {
    this.artifactId = artifactId
    return this
  }

  @PomDsl
  fun version(version: String): Dependency {
    this.version = version
    return this
  }

  @PomDsl
  fun type(type: String): Dependency {
    this.type = type
    return this
  }

  @PomDsl
  fun classifier(classifier: String): Dependency {
    this.classifier = classifier
    return this
  }

  @PomDsl
  fun scope(scope: String): Dependency {
    this.scope = scope
    return this
  }

  @PomDsl
  fun systemPath(systemPath: String): Dependency {
    this.systemPath = systemPath
    return this
  }

  @PomDsl
  fun optional(optional: Boolean = true): Dependency {
    this.isOptional = optional
    return this
  }

  /** Provides a callback for defining dependency exclusions. */
  @PomDsl
  fun exclusions(block: ExclusionList.(ExclusionList) -> Unit): Dependency {
    val exclusionList = ExclusionList()
    block(exclusionList, exclusionList)
    this.exclusions = exclusionList
    return this
  }

  /**
   * Excludes the artifacts having the specified `groupId:artifactId`. The wildcard `*` character
   * can be used to exclude matching artifacts.
   */
  @PomDsl
  fun exclusions(vararg ga: String): Dependency {
    this.exclusions =
        ga.map { splitCoordinates(it) }
            .map { (groupId, artifactId) ->
              Exclusion().apply {
                this.groupId = groupId
                this.artifactId = artifactId
              }
            }
    return this
  }

  override fun clone(): org.apache.maven.model.Dependency {
    return super<org.apache.maven.model.Dependency>.clone()
  }
}
