package org.sonatype.maven.polyglot.kotlin.dsl

import org.codehaus.plexus.util.xml.Xpp3Dom
import org.codehaus.plexus.util.xml.Xpp3DomBuilder

@PomDsl
class Profile : org.apache.maven.model.Profile(), Cloneable {

  @PomDsl
  fun id(id: String): Profile {
    this.id = id
    return this
  }

  @PomDsl
  fun activation(block: Activation.(Activation) -> Unit) {
    val activation = Activation()
    block(activation, activation)
    this.activation = activation
  }

  @PomDsl
  fun build(block: Build.(Build) -> Unit) {
    val build = Build()
    block(build, build)
    this.build = build
  }

  @PomDsl
  fun dependencies(block: DependencyList.(DependencyList) -> Unit) {
    val dependencies = DependencyList()
    block(dependencies, dependencies)
    this.dependencies = dependencies
  }

  @PomDsl
  fun dependencyManagement(block: DependencyManagement.(DependencyManagement) -> Unit) {
    val dependencyManagement = DependencyManagement()
    block(dependencyManagement, dependencyManagement)
    this.dependencyManagement = dependencyManagement
  }

  @PomDsl
  fun distributionManagement(block: DistributionManagement.(DistributionManagement) -> Unit) {
    val distributionManagement = DistributionManagement()
    block.invoke(distributionManagement, distributionManagement)
    this.distributionManagement = distributionManagement
  }

  @PomDsl
  fun modules(vararg modules: String) {
    this.modules = modules.asList()
  }

  @PomDsl
  fun pluginRepositories(block: PluginRepositoryList.(PluginRepositoryList) -> Unit) {
    val pluginRepositories = PluginRepositoryList()
    block.invoke(pluginRepositories, pluginRepositories)
    this.pluginRepositories = pluginRepositories
  }

  @PomDsl
  fun properties(block: Properties.(Properties) -> Unit) {
    val properties = Properties()
    block.invoke(properties, properties)
    this.properties = propertiesFactory().apply { putAll(properties.entries()) }
  }

  @PomDsl
  fun repositories(block: RepositoryList.(RepositoryList) -> Unit) {
    val repositories = RepositoryList()
    block.invoke(repositories, repositories)
    this.repositories = repositories
  }

  @PomDsl
  fun reporting(block: Reporting.(Reporting) -> Unit) {
    val reporting = Reporting()
    block(reporting, reporting)
    this.reporting = reporting
  }

  // -- Configuration Helpers
  // ---------------------------------------------------------------------------------------//

  /**
   * Sets the reports content.
   *
   * @param source the reports source as a [String] of XML
   */
  @PomDsl
  fun reports(source: String): Xpp3Dom {
    this.reports = source
    return this.reports as Xpp3Dom
  }

  @PomDsl
  fun reports(block: XmlNode.(XmlNode) -> Unit) {
    val reports = XmlNode("reports")
    block(reports, reports)
    this.reports = reports.xpp3Dom
  }

  /** Sets the reports. */
  override fun setReports(source: Any?) {
    val name = "reports"
    val xpp3Dom =
        when (source) {
          null -> null
          is Xpp3Dom -> source
          else -> Xpp3DomBuilder.build(source.toString().reader())
        }
    if (xpp3Dom != null && xpp3Dom.name != name) {
      val configuration = Xpp3Dom(name)
      configuration.addChild(xpp3Dom)
      super.setReports(configuration)
    } else {
      super.setReports(xpp3Dom)
    }
  }

  override fun clone(): org.apache.maven.model.Profile {
    return super<org.apache.maven.model.Profile>.clone()
  }
}
