package org.sonatype.maven.polyglot.kotlin

import org.codehaus.plexus.ContainerConfiguration
import org.codehaus.plexus.PlexusConstants
import org.codehaus.plexus.PlexusTestCase
import org.junit.Assert
import org.junit.Test
import org.sonatype.maven.polyglot.mapping.Mapping

class KotlinMappingTest : PlexusTestCase() {

  override fun customizeContainerConfiguration(configuration: ContainerConfiguration?) {
    configuration?.autoWiring = true
    configuration?.classPathScanning = PlexusConstants.SCANNING_CACHE
  }

  @Test
  fun testMapping() {
    val mapping = lookup(Mapping::class.java, "kotlin")
    val options =
        mapOf("org.apache.maven.model.building.source" to "/polyglot-maven/polyglot-kotlin/pom.kts")
    Assert.assertTrue(mapping.accept(options))
  }
}
