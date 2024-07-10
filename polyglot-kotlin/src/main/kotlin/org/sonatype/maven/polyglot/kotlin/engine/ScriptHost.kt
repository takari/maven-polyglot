package org.sonatype.maven.polyglot.kotlin.engine

import java.io.File
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import org.apache.maven.MavenExecutionException
import org.sonatype.maven.polyglot.execute.ExecuteContext
import org.sonatype.maven.polyglot.kotlin.dsl.Project

object ScriptHost {
  private val host = BasicJvmScriptingHost()
  private val pomCompilationConfig = createJvmCompilationConfigurationFromTemplate<PomKtsScript>()
  private val taskCompilationConfig = createJvmCompilationConfigurationFromTemplate<TaskKtsScript>()

  fun eval(script: File, basedir: File, model: Project) {
    eval(script, pomCompilationConfig) {
      constructorArgs(script, basedir, model)
      jvm { baseClassLoader(PomKtsScript::class.java.classLoader) }
    }
  }

  fun eval(script: File, executeContext: ExecuteContext) {
    eval(script, taskCompilationConfig) {
      constructorArgs(script, executeContext)
      jvm { baseClassLoader(TaskKtsScript::class.java.classLoader) }
    }
  }

  private fun eval(
      script: File,
      compilationConfig: ScriptCompilationConfiguration,
      evaluationConfigBuilder: ScriptEvaluationConfiguration.Builder.() -> Unit
  ) {
    val sourceCode = script.readText().toScriptSource()
    val evaluationConfig = ScriptEvaluationConfiguration(evaluationConfigBuilder)
    val result = host.eval(sourceCode, compilationConfig, evaluationConfig)
    result.reports
        .filter { it.exception != null }
        .forEach {
          System.err.println(it)
          it.exception?.printStackTrace()
        }
    if (result is ResultWithDiagnostics.Failure) {
      throw MavenExecutionException(
          "Failed to evaluate script\n" +
              result.reports
                  .filterNot { it.severity == ScriptDiagnostic.Severity.DEBUG }
                  .joinToString("\n"),
          script)
    }
  }
}
