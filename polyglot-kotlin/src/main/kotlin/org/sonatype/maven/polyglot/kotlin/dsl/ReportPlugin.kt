package org.sonatype.maven.polyglot.kotlin.dsl

import org.codehaus.plexus.util.xml.Xpp3Dom
import org.codehaus.plexus.util.xml.Xpp3DomBuilder

@PomDsl
class ReportPlugin : org.apache.maven.model.ReportPlugin(), Cloneable {

  @PomDsl
  fun groupId(groupId: String): ReportPlugin {
    this.groupId = groupId
    return this
  }

  @PomDsl
  fun artifactId(artifactId: String): ReportPlugin {
    this.artifactId = artifactId
    return this
  }

  @PomDsl
  fun version(version: String): ReportPlugin {
    this.version = version
    return this
  }

  @PomDsl
  fun inherited(inherited: Boolean = true): ReportPlugin {
    this.isInherited = inherited
    return this
  }

  /** Provides a callback for defining a list of report sets. */
  @PomDsl
  fun reportSets(block: ReportSetList.(ReportSetList) -> Unit) {
    val reportSets = ReportSetList()
    block(reportSets, reportSets)
    this.reportSets = reportSets
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

  override fun clone(): org.apache.maven.model.ReportPlugin {
    return super<org.apache.maven.model.ReportPlugin>.clone()
  }
}
