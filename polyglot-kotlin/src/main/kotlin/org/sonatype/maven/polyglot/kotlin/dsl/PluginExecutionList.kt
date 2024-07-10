package org.sonatype.maven.polyglot.kotlin.dsl

import org.codehaus.plexus.util.xml.Xpp3Dom

@PomDsl
class PluginExecutionList : ArrayList<org.apache.maven.model.PluginExecution>(), Cloneable {

  /** Defines a new plugin execution. */
  @PomDsl
  fun execution(
      id: String? = "default",
      phase: String? = null,
      goals: List<String>? = null,
      priority: Int = 0,
      inherited: Boolean? = null,
      configuration: Xpp3Dom? = null,
      block: (PluginExecution.(PluginExecution) -> Unit)? = null
  ): PluginExecution {
    return PluginExecution().apply {
      this.id = id
      this.phase = phase
      this.priority = priority
      if (inherited != null) this.isInherited = inherited
      this.goals = goals
      this.configuration = configuration
      this@PluginExecutionList.add(this)
      block?.invoke(this, this)
    }
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
