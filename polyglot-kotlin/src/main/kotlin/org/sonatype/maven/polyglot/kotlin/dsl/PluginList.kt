package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class PluginList : ArrayList<org.apache.maven.model.Plugin>(), Cloneable {

  /** Provides a callback for defining a new build plugin. */
  @PomDsl
  fun plugin(gav: String? = null, block: (Plugin.(Plugin) -> Unit)? = null): Plugin {
    val (groupId, artifactId, version) = splitCoordinates(gav, 3)
    return Plugin().apply {
      this.groupId = groupId
      this.artifactId = artifactId
      this.version = version
      this@PluginList.add(this)
      block?.invoke(this, this)
    }
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
