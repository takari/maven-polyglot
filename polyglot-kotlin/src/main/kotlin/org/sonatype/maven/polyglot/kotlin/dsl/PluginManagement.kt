package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class PluginManagement : org.apache.maven.model.PluginManagement(), Cloneable {

  @PomDsl
  fun plugins(block: PluginList.(PluginList) -> Unit) {
    val plugins = PluginList()
    block.invoke(plugins, plugins)
    this.plugins = plugins
  }

  override fun clone(): org.apache.maven.model.PluginManagement {
    return super<org.apache.maven.model.PluginManagement>.clone()
  }
}
