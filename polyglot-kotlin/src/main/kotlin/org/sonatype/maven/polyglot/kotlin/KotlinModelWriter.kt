package org.sonatype.maven.polyglot.kotlin

import java.io.StringWriter
import java.io.Writer
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton
import org.apache.maven.model.Model
import org.apache.maven.project.MavenProject
import org.slf4j.LoggerFactory
import org.sonatype.maven.polyglot.io.ModelWriterSupport
import org.sonatype.maven.polyglot.kotlin.serialization.ModelScriptWriter

@Singleton
@Named("kotlin")
class KotlinModelWriter : ModelWriterSupport() {

  private var log = LoggerFactory.getLogger(KotlinModelWriter::class.java)

  @Inject private lateinit var projectProvider: Provider<MavenProject>

  override fun write(output: Writer, options: Map<String, Any>, model: Model) {
    output.write(
        with(StringWriter(1024)) {
          val config = HashMap<String, Any>(options)
          config.putIfAbsent(
              "xml.dsl.enabled",
              projectProvider
                  .get()
                  ?.properties
                  ?.getProperty("polyglot-kotlin.xml-dsl-enabled", "true") ?: "true")
          config.putIfAbsent(
              "flavor",
              projectProvider.get()?.properties?.getProperty("polyglot-kotlin.flavor", "mixed")
                  ?: "mixed")
          ModelScriptWriter(this, config).write(model)
          val kotlinScript = toString()
          if (log.isDebugEnabled) {
            log.debug(({ "POM model converted from XML: \n$kotlinScript\n" })())
          }
          kotlinScript
        })
    output.flush()
  }
}
