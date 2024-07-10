package org.sonatype.maven.polyglot.kotlin.testing

import java.io.File
import org.apache.maven.model.Model
import org.apache.maven.model.building.ModelProcessor
import org.apache.maven.model.io.ModelReader
import org.apache.maven.model.io.ModelWriter
import org.assertj.core.api.Assertions.assertThat
import org.codehaus.plexus.ContainerConfiguration
import org.codehaus.plexus.PlexusConstants
import org.codehaus.plexus.PlexusTestCase
import org.junit.Assert
import org.sonatype.maven.polyglot.kotlin.dsl.propertiesFactory

abstract class AbstractModelTestCase(testName: String) : PlexusTestCase() {

  init {
    name = testName
    propertiesFactory = { org.sonatype.maven.polyglot.kotlin.util.Properties() }
  }

  override fun customizeContainerConfiguration(configuration: ContainerConfiguration?) {
    configuration?.autoWiring = true
    configuration?.classPathScanning = PlexusConstants.SCANNING_CACHE
  }

  protected val testBasePath: String = testName.removePrefix("test#").replace('#', '/')
  private val testResources: File = File("target/test-classes")
  private val testOutput: File = File("target/test-output")

  private val kotlinModelWriter: ModelWriter = lookup(ModelWriter::class.java, "kotlin")

  private val kotlinModelReader: ModelReader = lookup(ModelReader::class.java, "kotlin")

  private val xmlModelReader: ModelReader = lookup(ModelReader::class.java, "default")

  private val xmlModelWriter: ModelWriter = lookup(ModelWriter::class.java, "default")

  protected val pomXml: File =
      testResources.resolve(testBasePath).resolve("pom.xml").also { assertThat(it).exists() }

  protected val pomKts: File =
      testResources.resolve(testBasePath).resolve("pom.kts").also { assertThat(it).exists() }

  protected fun File.xml2model(vararg options: Pair<String, Any?>): Model =
      xmlModelReader.read(this, mapOf(*options) + mapOf(ModelProcessor.SOURCE to this))

  protected fun File.kts2model(vararg options: Pair<String, Any?>): Model =
      kotlinModelReader.read(this, mapOf(*options) + mapOf(ModelProcessor.SOURCE to this))

  protected fun Model.model2xml(vararg options: Pair<String, Any?>): File {
    val basedir = testOutput.resolve(testBasePath)
    basedir.mkdirs()
    val file = basedir.resolve("pom.xml")
    xmlModelWriter.write(file, mapOf(*options), this)
    return file
  }

  protected fun Model.model2kts(vararg options: Pair<String, Any?>): File {
    val basedir = testOutput.resolve(testBasePath)
    basedir.mkdirs()
    val file = basedir.resolve("pom.kts")
    kotlinModelWriter.write(file, mapOf(*options), this)
    return file
  }

  protected fun assertEquals(expected: File, actual: File) {
    Assert.assertEquals(expected.readText(), actual.readText())
  }
}
