package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ExtensionList : ArrayList<org.apache.maven.model.Extension>(), Cloneable {

  /** Provides a callback for defining a new build extension. */
  @PomDsl
  fun extension(gav: String? = null, block: (Extension.(Extension) -> Unit)? = null): Extension {
    val (groupId, artifactId, version) = splitCoordinates(gav, 3)
    return Extension().apply {
      this.groupId = groupId
      this.artifactId = artifactId
      this.version = version
      this@ExtensionList.add(this)
      block?.invoke(this, this)
    }
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
