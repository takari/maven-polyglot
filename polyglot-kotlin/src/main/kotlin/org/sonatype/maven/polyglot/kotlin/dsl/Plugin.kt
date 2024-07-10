package org.sonatype.maven.polyglot.kotlin.dsl

import org.codehaus.plexus.util.xml.Xpp3Dom
import org.codehaus.plexus.util.xml.Xpp3DomBuilder

@PomDsl
class Plugin : org.apache.maven.model.Plugin(), Cloneable {

  // -- Initialization
  // ----------------------------------------------------------------------------------------------//

  private val _executions: PluginExecutionList
    get() {
      return executions as PluginExecutionList
    }

  private val _dependencies: DependencyList
    get() {
      return dependencies as DependencyList
    }

  init {
    this.executions = PluginExecutionList()
    this.dependencies = DependencyList()
  }

  @PomDsl
  fun groupId(groupId: String): Plugin {
    this.groupId = groupId
    return this
  }

  @PomDsl
  fun artifactId(artifactId: String): Plugin {
    this.artifactId = artifactId
    return this
  }

  @PomDsl
  fun version(version: String): Plugin {
    this.version = version
    return this
  }

  @PomDsl
  fun extensions(extensions: Boolean = true): Plugin {
    this.extensions = extensions.toString()
    return this
  }

  @PomDsl
  fun inherited(inherited: Boolean = true): Plugin {
    this.isInherited = inherited
    return this
  }

  // -- Block Functions
  // ---------------------------------------------------------------------------------------------//

  @PomDsl
  fun executions(block: PluginExecutionList.(PluginExecutionList) -> Unit) {
    block.invoke(_executions, _executions)
  }

  @PomDsl
  fun dependencies(block: DependencyList.(DependencyList) -> Unit) {
    block(_dependencies, _dependencies)
  }

  // -- Plugin Dependency Helpers
  // -----------------------------------------------------------------------------------//

  @PomDsl
  fun dependency(
      gavtc: String? = null,
      block: (Dependency.(Dependency) -> Unit)? = null
  ): Dependency {
    return _dependencies.dependency(gavtc, block)
  }

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
    return _dependencies.dependency(
        groupId, artifactId, version, type, classifier, optional, scope, systemPath, block)
  }

  // -- Plugin Execution Helpers
  // ------------------------------------------------------------------------------------//

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
    return _executions.execution(id, phase, goals, priority, inherited, configuration, block)
  }

  // -- Configuration Helpers
  // ---------------------------------------------------------------------------------------//

  /**
   * Sets the plugin configuration.
   *
   * @param source the configuration source as a [String] of XML
   */
  @PomDsl
  fun configuration(source: String): Xpp3Dom {
    this.configuration = source
    return this.configuration as Xpp3Dom
  }

  @PomDsl
  fun configuration(block: XmlNode.(XmlNode) -> Unit) {
    val configuration = XmlNode("configuration")
    block(configuration, configuration)
    this.configuration = configuration.xpp3Dom
  }

  /** Sets the configuration. */
  override fun setConfiguration(source: Any?) {
    val name = "configuration"
    val xpp3Dom =
        when (source) {
          null -> null
          is Xpp3Dom -> source
          else -> Xpp3DomBuilder.build(source.toString().reader())
        }
    if (xpp3Dom != null && xpp3Dom.name != name) {
      val configuration = Xpp3Dom(name)
      configuration.addChild(xpp3Dom)
      super.setConfiguration(configuration)
    } else {
      super.setConfiguration(xpp3Dom)
    }
  }

  override fun clone(): org.apache.maven.model.Plugin {
    return super<org.apache.maven.model.Plugin>.clone()
  }
}
