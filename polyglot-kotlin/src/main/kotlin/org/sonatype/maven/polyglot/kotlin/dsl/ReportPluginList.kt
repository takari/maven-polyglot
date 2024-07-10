package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ReportPluginList : ArrayList<org.apache.maven.model.ReportPlugin>(), Cloneable {

  /** Provides a callback for defining a new report plugin. */
  @PomDsl
  fun plugin(
      gav: String? = null,
      block: (ReportPlugin.(ReportPlugin) -> Unit)? = null
  ): ReportPlugin {
    val (groupId, artifactId, version) = splitCoordinates(gav, 3)
    return ReportPlugin().apply {
      this.groupId = groupId
      this.artifactId = artifactId
      this.version = version
      this@ReportPluginList.add(this)
      block?.invoke(this, this)
    }
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
