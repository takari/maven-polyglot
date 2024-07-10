package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ManagedDependencyList : DependencyList(), Cloneable {

  @PomDsl
  fun import(gav: String? = null, block: (Dependency.(Dependency) -> Unit)? = null): Dependency {
    return dependency(gav, block).apply {
      scope = "import"
      type = "pom"
    }
  }

  @PomDsl
  fun import(
      groupId: String,
      artifactId: String,
      version: String? = null,
      classifier: String? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return dependency(
            groupId = groupId,
            artifactId = artifactId,
            version = version,
            classifier = classifier,
            block = block)
        .apply {
          this.scope = "import"
          this.type = "pom"
        }
  }

  override fun clone(): Any {
    return super<DependencyList>.clone()
  }
}
