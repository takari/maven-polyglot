package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
open class DependencyList : ArrayList<org.apache.maven.model.Dependency>(), Cloneable {

  /**
   * Adds a dependency identified by the supplied coordinates.
   *
   * @param gavtc an artifact identifier in the form
   *   `groupId:artifactId[:version][:type][:classifier]`
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun dependency(
      gavtc: String? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    val (groupId, artifactId, version, type, classifier) = splitCoordinates(gavtc)
    return Dependency().apply {
      this.groupId = groupId
      this.artifactId = artifactId
      this.version = version
      if (type != null) this.type = type
      this.classifier = classifier
      this@DependencyList.add(this)
      block?.invoke(this, this)
    }
  }

  /**
   * Adds a "compile" scope dependency identified by the supplied coordinates.
   *
   * @param gavtc an artifact identifier in the form
   *   `groupId:artifactId[:version][:type][:classifier]`
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun compile(gavtc: String? = null, block: (Dependency.(Dependency) -> Unit)? = null): Dependency {
    return dependency(gavtc, block).apply { this.scope = "compile" }
  }

  /**
   * Adds a "test" scope dependency identified by the supplied coordinates.
   *
   * @param gavtc an artifact identifier in the form
   *   `groupId:artifactId[:version][:type][:classifier]`
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun test(gavtc: String? = null, block: (Dependency.(Dependency) -> Unit)? = null): Dependency {
    return dependency(gavtc, block).apply { this.scope = "test" }
  }

  /**
   * Adds a "provided" scope dependency identified by the supplied coordinates.
   *
   * @param gavtc an artifact identifier in the form
   *   `groupId:artifactId[:version][:type][:classifier]`
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun provided(
      gavtc: String? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return dependency(gavtc, block).apply { this.scope = "provided" }
  }

  /**
   * Adds a "runtime" scope dependency identified by the supplied coordinates.
   *
   * @param gavtc an artifact identifier in the form
   *   `groupId:artifactId[:version][:type][:classifier]`
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun runtime(gavtc: String? = null, block: (Dependency.(Dependency) -> Unit)? = null): Dependency {
    return dependency(gavtc, block).apply { this.scope = "runtime" }
  }

  /**
   * Adds a "system" scope dependency identified by the supplied coordinates.
   *
   * @param gavtc an artifact identifier in the form
   *   `groupId:artifactId[:version][:type][:classifier]`
   * @param systemPath the location of the system dependency, relative to `${basedir}`
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun system(
      gavtc: String? = null,
      systemPath: String,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return dependency(gavtc, block).apply {
      this.scope = "system"
      this.systemPath = systemPath
    }
  }

  /**
   * Adds an optional (non-transitive) dependency identified by the supplied coordinates.
   *
   * @param gavtc an artifact identifier in the form
   *   `groupId:artifactId[:version][:type][:classifier]`
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun optional(
      gavtc: String? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return dependency(gavtc, block).apply { this.isOptional = true }
  }

  /**
   * Adds a dependency identified by the supplied coordinate parameters.
   *
   * @param groupId the dependency groupId
   * @param artifactId the dependency artifactId
   * @param version the dependency version
   * @param type the dependency packaging type
   * @param classifier the dependency classifier
   * @param optional a boolean flag indicating whether the dependency is optional (non-transitive)
   * @param scope the dependency scope ("compile", "runtime", "test", "provided", "system")
   * @param systemPath the dependency system path when the scope is "system"
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun dependency(
      groupId: String,
      artifactId: String,
      version: String? = null,
      type: String = "jar",
      classifier: String? = null,
      optional: Boolean? = null,
      scope: String? = null,
      systemPath: String? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return Dependency().apply {
      this.groupId = groupId
      this.artifactId = artifactId
      this.version = version
      this.type = type
      this.classifier = classifier
      if (optional != null) this.isOptional = optional
      this.scope = scope
      this.systemPath = systemPath
      this@DependencyList.add(this)
      block?.invoke(this, this)
    }
  }

  /**
   * Adds a "compile" scope dependency identified by the supplied coordinate parameters.
   *
   * @param groupId the dependency groupId
   * @param artifactId the dependency artifactId
   * @param version the dependency version
   * @param type the dependency packaging type
   * @param classifier the dependency classifier
   * @param optional a boolean flag indicating whether the dependency is optional (non-transitive)
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun compile(
      groupId: String,
      artifactId: String,
      version: String? = null,
      type: String = "jar",
      classifier: String? = null,
      optional: Boolean? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return dependency(
            groupId = groupId,
            artifactId = artifactId,
            version = version,
            type = type,
            classifier = classifier,
            optional = optional,
            scope = "compile",
            block = block)
        .apply { scope = "compile" }
  }

  /**
   * Adds a "test" scope dependency identified by the supplied coordinate parameters.
   *
   * @param groupId the dependency groupId
   * @param artifactId the dependency artifactId
   * @param version the dependency version
   * @param type the dependency packaging type
   * @param classifier the dependency classifier
   * @param optional a boolean flag indicating whether the dependency is optional (non-transitive)
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun test(
      groupId: String,
      artifactId: String,
      version: String? = null,
      type: String = "jar",
      classifier: String? = null,
      optional: Boolean? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return dependency(
            groupId = groupId,
            artifactId = artifactId,
            version = version,
            type = type,
            classifier = classifier,
            optional = optional,
            scope = "test",
            block = block)
        .apply { scope = "test" }
  }

  /**
   * Adds a "provided" scope dependency identified by the supplied coordinate parameters.
   *
   * @param groupId the dependency groupId
   * @param artifactId the dependency artifactId
   * @param version the dependency version
   * @param type the dependency packaging type
   * @param classifier the dependency classifier
   * @param optional a boolean flag indicating whether the dependency is optional (non-transitive)
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun provided(
      groupId: String,
      artifactId: String,
      version: String? = null,
      type: String = "jar",
      classifier: String? = null,
      optional: Boolean? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return dependency(
            groupId = groupId,
            artifactId = artifactId,
            version = version,
            type = type,
            classifier = classifier,
            optional = optional,
            scope = "provided",
            block = block)
        .apply { this.scope = "provided" }
  }

  /**
   * Adds a "runtime" scope dependency identified by the supplied coordinate parameters.
   *
   * @param groupId the dependency groupId
   * @param artifactId the dependency artifactId
   * @param version the dependency version
   * @param type the dependency packaging type
   * @param classifier the dependency classifier
   * @param optional a boolean flag indicating whether the dependency is optional (non-transitive)
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun runtime(
      groupId: String,
      artifactId: String,
      version: String? = null,
      type: String = "jar",
      classifier: String? = null,
      optional: Boolean? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return dependency(
            groupId = groupId,
            artifactId = artifactId,
            version = version,
            type = type,
            classifier = classifier,
            optional = optional,
            scope = "runtime",
            block = block)
        .apply { this.scope = "runtime" }
  }

  /**
   * Adds a "system" scope dependency identified by the supplied coordinate parameters.
   *
   * @param groupId the dependency groupId
   * @param artifactId the dependency artifactId
   * @param version the dependency version
   * @param type the dependency packaging type
   * @param classifier the dependency classifier
   * @param optional a boolean flag indicating whether the dependency is optional (non-transitive)
   * @param systemPath the location of the system dependency relative to `${basedir}`
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun system(
      groupId: String,
      artifactId: String,
      version: String? = null,
      type: String = "jar",
      classifier: String? = null,
      optional: Boolean? = null,
      systemPath: String,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return dependency(
            groupId = groupId,
            artifactId = artifactId,
            version = version,
            type = type,
            classifier = classifier,
            optional = optional,
            scope = "system",
            systemPath = systemPath,
            block = block)
        .apply {
          this.scope = "system"
          this.systemPath = systemPath
        }
  }

  /**
   * Adds an optional dependency identified by the supplied coordinate parameters.
   *
   * @param groupId the dependency groupId
   * @param artifactId the dependency artifactId
   * @param version the dependency version
   * @param type the dependency packaging type
   * @param classifier the dependency classifier
   * @param block a callback for performing additional initialization
   */
  @PomDsl
  fun optional(
      groupId: String,
      artifactId: String,
      version: String? = null,
      type: String = "jar",
      classifier: String? = null,
      scope: String? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return dependency(
        groupId = groupId,
        artifactId = artifactId,
        version = version,
        scope = scope,
        type = type,
        classifier = classifier,
        optional = true,
        block = block)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
