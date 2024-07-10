package org.sonatype.maven.polyglot.kotlin

import java.io.File
import java.io.InputStream
import java.io.Reader
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import org.apache.maven.model.Model
import org.apache.maven.model.building.ModelProcessor
import org.apache.maven.model.io.ModelReader
import org.sonatype.maven.polyglot.execute.ExecuteManager
import org.sonatype.maven.polyglot.kotlin.dsl.Project
import org.sonatype.maven.polyglot.kotlin.engine.ScriptHost

@Singleton
@Named("kotlin")
class KotlinModelReader : ModelReader {

  @Inject private lateinit var executeManager: ExecuteManager

  override fun read(input: File, options: Map<String, *>): Model {
    val source = options[ModelProcessor.SOURCE].toString()
    val sourceFile = File(source)
    val basedir = sourceFile.parentFile.canonicalFile
    val model = Project(input)
    ScriptHost.eval(input, basedir, model)
    val tasks = ArrayList(model.tasks)
    executeManager.register(model, tasks)
    executeManager.install(model, options)
    model.tasks.clear() // Must be cleared or Maven goes into an infinitely repeatable introspection
    return model
  }

  override fun read(input: Reader, options: MutableMap<String, *>): Model {
    val temp = File.createTempFile("pom", ".kts")
    temp.deleteOnExit()
    temp.writer().use { input.copyTo(it) }
    val model = read(temp, options)
    temp.delete()
    return model
  }

  override fun read(input: InputStream, options: MutableMap<String, *>): Model {
    val temp = File.createTempFile("pom", ".kts")
    temp.deleteOnExit()
    temp.outputStream().use { input.copyTo(it) }
    val model = read(temp, options)
    temp.delete()
    return model
  }
}
