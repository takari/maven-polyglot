package org.sonatype.maven.polyglot.kotlin.engine

import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.templates.ScriptTemplateDefinition
import org.sonatype.maven.polyglot.execute.ExecuteContext

@KotlinScript(
    displayName = "External kotlin task script",
    fileExtension = "task.kts",
    compilationConfiguration = ScriptDefinition::class)
@ScriptTemplateDefinition(scriptFilePattern = ".*\\.task\\.kts")
abstract class TaskKtsScript(val script: File, private val delegate: ExecuteContext) :
    ExecuteContext by delegate
