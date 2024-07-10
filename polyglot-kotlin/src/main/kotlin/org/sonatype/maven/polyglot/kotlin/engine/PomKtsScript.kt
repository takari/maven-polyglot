package org.sonatype.maven.polyglot.kotlin.engine

import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.templates.ScriptTemplateDefinition
import org.sonatype.maven.polyglot.execute.ExecuteContext
import org.sonatype.maven.polyglot.kotlin.dsl.PomDsl
import org.sonatype.maven.polyglot.kotlin.dsl.Project

@KotlinScript(
    displayName = "Maven kotlin POM script",
    fileExtension = "pom.kts",
    compilationConfiguration = ScriptDefinition::class)
@ScriptTemplateDefinition(scriptFilePattern = "pom\\.kts")
abstract class PomKtsScript(val script: File, val basedir: File, val model: Project) {

  /** Configures a Maven project model. */
  @PomDsl
  fun project(nameOrId: String? = null, block: Project.() -> Unit): Project {
    if (nameOrId != null) {
      model.name = nameOrId
      model.id = nameOrId
    }
    return model.apply(block)
  }

  /** Invokes the script at the supplied location */
  @Suppress("unused")
  fun eval(script: File) {
    ScriptHost.eval(script, basedir, model)
  }

  /** Invokes the script at the supplied location */
  @Suppress("unused")
  fun ExecuteContext.eval(taskScript: File) {
    ScriptHost.eval(taskScript, this)
  }
}
