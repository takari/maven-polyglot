package org.sonatype.maven.polyglot.kotlin.execute

import org.sonatype.maven.polyglot.execute.ExecuteContext
import org.sonatype.maven.polyglot.execute.ExecuteTaskSupport

class KotlinExecuteTask(private val script: ExecuteContext.() -> Unit) : ExecuteTaskSupport() {

  override fun execute(context: ExecuteContext) {
    context.script()
  }
}
