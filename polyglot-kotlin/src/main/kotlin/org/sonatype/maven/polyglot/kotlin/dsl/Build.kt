package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
open class Build : org.apache.maven.model.Build(), Cloneable {

  @PomDsl
  fun sourceDirectory(sourceDirectory: String): Build {
    this.sourceDirectory = sourceDirectory
    return this
  }

  @PomDsl
  fun scriptSourceDirectory(scriptSourceDirectory: String): Build {
    this.scriptSourceDirectory = scriptSourceDirectory
    return this
  }

  @PomDsl
  fun testSourceDirectory(testSourceDirectory: String): Build {
    this.testSourceDirectory = testSourceDirectory
    return this
  }

  @PomDsl
  fun outputDirectory(outputDirectory: String): Build {
    this.outputDirectory = outputDirectory
    return this
  }

  @PomDsl
  fun testOutputDirectory(testOutputDirectory: String): Build {
    this.testOutputDirectory = testOutputDirectory
    return this
  }

  @PomDsl
  fun defaultGoal(defaultGoal: String): Build {
    this.defaultGoal = defaultGoal
    return this
  }

  @PomDsl
  fun directory(directory: String): Build {
    this.directory = directory
    return this
  }

  @PomDsl
  fun finalName(finalName: String): Build {
    this.finalName = finalName
    return this
  }

  @PomDsl
  fun resources(block: ResourceList.(ResourceList) -> Unit) {
    val resources = ResourceList()
    block(resources, resources)
    this.resources = resources
  }

  @PomDsl
  fun testResources(block: TestResourceList.(TestResourceList) -> Unit) {
    val testResources = TestResourceList()
    block(testResources, testResources)
    this.testResources = testResources
  }

  @PomDsl
  fun extensions(block: ExtensionList.(ExtensionList) -> Unit) {
    val extensions = ExtensionList()
    block(extensions, extensions)
    this.extensions = extensions
  }

  @PomDsl
  fun pluginManagement(block: PluginManagement.(PluginManagement) -> Unit) {
    val pluginManagement = PluginManagement()
    block.invoke(pluginManagement, pluginManagement)
    this.pluginManagement = pluginManagement
  }

  @PomDsl
  fun plugins(block: PluginList.(PluginList) -> Unit) {
    val plugins = PluginList()
    block.invoke(plugins, plugins)
    this.plugins = plugins
  }

  @PomDsl
  fun extensions(vararg gav: String): Build {
    this.extensions =
        gav.map { value ->
          val (groupId, artifactId, version) = splitCoordinates(value)
          Extension().apply {
            this.groupId = groupId
            this.artifactId = artifactId
            this.version = version
          }
        }
    return this
  }

  @PomDsl
  fun filters(vararg filters: String): Build {
    this.filters = filters.asList()
    return this
  }

  override fun clone(): org.apache.maven.model.Build {
    return super<org.apache.maven.model.Build>.clone()
  }
}
