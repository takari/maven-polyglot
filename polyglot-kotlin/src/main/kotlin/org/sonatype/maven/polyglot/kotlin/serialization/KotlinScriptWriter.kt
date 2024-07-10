package org.sonatype.maven.polyglot.kotlin.serialization

import java.io.Writer
import java.util.*
import kotlin.collections.ArrayList
import org.sonatype.maven.polyglot.kotlin.dsl.escape
import org.sonatype.maven.polyglot.kotlin.dsl.escapeRaw

internal open class KotlinScriptWriter(writer: Writer) {

  private enum class SerializerState {
    BLOCK,
    FUNCTION,
    FIELD,
    RAW_STRING
  }

  private val state: Deque<SerializerState> = LinkedList<SerializerState>()
  private val indentation = "    "
  protected var indentLevel = 0
  private var lineStart = true
  private val argumentWrapThreshold = 50
  private val out = OUT(writer)
  private val expressionText = Regex("""[$][{][^}]+[}]""")
  private val booleanLiteral = Regex("true|false")

  class OUT(private val out: Writer) : Writer() {

    private var position: Long = 0

    override fun write(cbuf: CharArray, off: Int, len: Int) {
      out.write(cbuf, off, len)
      position += len
    }

    override fun flush() {
      out.flush()
    }

    override fun close() {
      out.close()
    }
  }

  protected fun write(str: String): KotlinScriptWriter {
    if (lineStart) {
      for (i in 1..indentLevel) {
        out.write(indentation)
      }
      lineStart = false
    }
    out.write(str)
    return this
  }

  private fun writeString(value: String): KotlinScriptWriter {
    val escapedValue = escape(value)
    when {
      expressionText.find(value) != null -> {
        write("\"${escapedValue}\"")
      }
      value != escapedValue -> {
        val escapedRawValue = escapeRaw(value)
        tripleQuotedString(escapedRawValue)
      }
      else -> write("\"${value}\"")
    }
    return this
  }

  protected fun writePair(pair: Pair<String, String?>): KotlinScriptWriter {
    writeString(pair.first)
    write(" to ")
    val value = pair.second
    when {
      value == null -> write("null")
      value.toLongOrNull().toString() == value -> write(value)
      value.toDoubleOrNull().toString() == value -> write(value)
      value.matches(booleanLiteral) -> write(value)
      else -> writeString(value)
    }
    return this
  }

  protected fun endLine(): KotlinScriptWriter {
    out.write("\n")
    lineStart = true
    return this
  }

  protected fun blockComment(comment: String): KotlinScriptWriter {
    write("/*").endLine()
    comment.prependIndent(" * ").lines().forEach { write(it).endLine() }
    write(" */").endLine()
    return this
  }

  protected fun lineComment(comment: String): KotlinScriptWriter {
    comment.prependIndent("// ").lines().forEach { write(it).endLine() }
    return this
  }

  protected fun <T : Any> block(
      name: String,
      modelObject: T?,
      args: (T.(MutableList<Any>) -> Unit)? = null,
      contents: (T.() -> Unit)? = null
  ) {
    block(name, modelObject, "it", args, contents)
  }

  protected fun <T : Any> block(
      name: String,
      modelObject: T?,
      paramName: String,
      args: (T.(MutableList<Any>) -> Unit)? = null,
      contents: (T.() -> Unit)? = null
  ) {
    if (modelObject == null) {
      return
    } else if (modelObject is Collection<*> && modelObject.isEmpty()) {
      return
    } else if (modelObject is Map<*, *> && modelObject.isEmpty()) {
      return
    } else if (indentLevel == 1) {
      // Add an extra space before project-level blocks
      endLine()
    }
    if (args != null) {
      val params: MutableList<Any> = ArrayList()
      args(modelObject, params)
      if (params.isEmpty()) {
        if (contents != null) {
          startBlock(name, paramName)
        }
      } else {
        function(name) {
          if (params.size == 1 || params.joinToString().length < argumentWrapThreshold) {
            @Suppress("UNCHECKED_CAST")
            when (params[0]) {
              is Pair<*, *> -> namedArguments(params as List<Pair<String, String>>)
              is String -> arguments(params as List<String>)
              else ->
                  throw IllegalArgumentException(
                      "The argument type \"${params[0]::class}\" is unsupported. Must be Pair or String")
            }
          } else {
            @Suppress("UNCHECKED_CAST")
            when (params[0]) {
              is Pair<*, *> -> namedMultiLineArguments(params as List<Pair<String, String>>)
              is String -> multiLineArguments(params as List<String>)
              else ->
                  throw IllegalArgumentException(
                      "The argument type \"${params[0]::class}\" is unsupported. Must be Pair or String")
            }
          }
        }
        if (contents != null) {
          write(" ")
          startBlock(null, paramName)
        }
      }
    } else if (contents != null) {
      startBlock(name, paramName)
    }
    if (contents != null) {
      contents(modelObject)
      endBlock()
    }
    endLine()
  }

  protected fun singleQuotedString(text: String?) {
    if (text == null) {
      write("null")
    } else {
      write("\"").write(escape(text)).write("\"")
    }
  }

  private fun tripleQuotedString(text: String?) {
    if (text == null) {
      write("null")
    } else {
      write("\"\"\"")
      state.push(SerializerState.RAW_STRING)
      incrementIndent()
      text.lines().forEach { write(it) }
      decrementIndent()
      state.pop()
      write("\"\"\"")
    }
  }

  private fun incrementIndent(): KotlinScriptWriter {
    indentLevel++
    return this
  }

  private fun decrementIndent(): KotlinScriptWriter {
    indentLevel--
    return this
  }

  private fun startBlock(name: String? = null, paramName: String? = "it"): KotlinScriptWriter {
    write(if (name == null) "{" else "${name} {")
    if (paramName != "it") {
      write(" ${paramName} ->")
    }
    state.push(SerializerState.BLOCK)
    indentLevel++
    endLine()
    return this
  }

  private fun endBlock(): KotlinScriptWriter {
    indentLevel--
    write("}")
    state.pop()
    return this
  }

  private fun startFunction(name: String): KotlinScriptWriter {
    write("${name}(")
    state.push(SerializerState.FUNCTION)
    return this
  }

  private fun endFunction(): KotlinScriptWriter {
    write(")")
    state.pop()
    return this
  }

  private fun startField(name: String) {
    write("${name} = ")
    state.push(SerializerState.FIELD)
  }

  private fun endField() {
    state.pop()
  }

  private fun startMultiLineString() {
    write("\"\"\"")
    state.push(SerializerState.RAW_STRING)
    incrementIndent()
    endLine()
  }

  private fun endMultiLineString() {
    write("\"\"\"")
    state.pop()
    decrementIndent()
  }

  protected fun arguments(values: List<String?>) {
    values.forEachIndexed { index, value ->
      if (index > 0) write(", ")
      singleQuotedString(value)
    }
  }

  private fun namedArguments(pairs: List<Pair<String, String?>>) {
    pairs.forEachIndexed { index, pair ->
      if (index > 0) write(", ")
      write("${pair.first} = ")
      singleQuotedString(pair.second)
    }
  }

  private fun multiLineArguments(values: List<String?>) {
    incrementIndent()
    endLine()
    values.forEachIndexed { index, value ->
      if (index > 0) {
        write(",")
        endLine()
      }
      singleQuotedString(value)
    }
    endLine()
    decrementIndent()
  }

  private fun namedMultiLineArguments(pairs: List<Pair<String, String?>>) {
    incrementIndent()
    endLine()
    pairs.forEachIndexed { index, pair ->
      if (index > 0) {
        write(",")
        endLine()
      }
      write("${pair.first} = ")
      singleQuotedString(pair.second)
    }
    endLine()
    decrementIndent()
  }

  protected fun field(fieldName: String, contents: () -> Unit) {
    startField(fieldName)
    contents()
    endField()
  }

  protected fun function(functionName: String, contents: () -> Unit) {
    startFunction(functionName)
    contents()
    endFunction()
  }

  protected fun multiLineString(contents: () -> Unit) {
    startMultiLineString()
    contents()
    endMultiLineString()
  }
}
