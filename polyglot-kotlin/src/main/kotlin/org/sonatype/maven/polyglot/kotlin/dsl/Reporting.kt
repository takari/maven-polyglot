package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Reporting : org.apache.maven.model.Reporting(), Cloneable {

  @PomDsl
  fun excludeDefaults(excludeDefaults: Boolean = true): Reporting {
    this.isExcludeDefaults = excludeDefaults
    return this
  }

  @PomDsl
  fun outputDirectory(outputDirectory: String): Reporting {
    this.outputDirectory = outputDirectory
    return this
  }

  @PomDsl
  fun plugins(block: ReportPluginList.(ReportPluginList) -> Unit) {
    val plugins = ReportPluginList()
    block.invoke(plugins, plugins)
    this.plugins = plugins
  }

  override fun clone(): org.apache.maven.model.Reporting {
    return super<org.apache.maven.model.Reporting>.clone()
  }
}
