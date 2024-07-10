package org.sonatype.maven.polyglot.kotlin.testing

import java.io.ByteArrayOutputStream
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import java.lang.reflect.Field
import org.apache.maven.model.Model
import org.apache.maven.model.io.DefaultModelReader
import org.apache.maven.model.io.DefaultModelWriter
import org.apache.maven.model.io.xpp3.MavenXpp3Writer
import org.assertj.core.api.Assertions.assertThat

/** Returns a [Reader] to the classpath resource named by this string. */
internal fun String.asResource(): Reader? {
  println("Reading $this")
  return Thread.currentThread().contextClassLoader.getResourceAsStream(this)?.reader()
}

internal fun println(model: Model) {
  println(toString(model))
}

internal fun toString(model: Model): String {
  val output = StringWriter()
  val writer = MavenXpp3Writer()
  //    writer.setFileComment("\nGenerated from pom.kts on ${java.util.Date()}\n")
  writer.write(output, model)
  return output.toString()
}

internal fun toByteArray(model: Model): ByteArray? {
  val output = ByteArrayOutputStream()
  val writer = MavenXpp3Writer()
  //    writer.setFileComment("\nGenerated from pom.kts on ${java.util.Date()}\n")
  writer.write(output, model)
  return output.toByteArray()
}

/**
 * Asserts that no data is lost during serialization by doing a field by field comparison between
 * the original model and the deserialized model.
 */
internal fun assertNoDataLossOnSerialization(project: Model) {
  // Serialize the project model
  val options = HashMap<String, Any>()
  val output = StringWriter()
  DefaultModelWriter().write(output, options, project)

  // Verify that the project model deserializes with all its original data
  val input = StringReader(output.toString())
  val actual = DefaultModelReader().read(input, options)
  compare("project", Model::class.java, project, actual)
}

// -- Private Implementation
// ------------------------------------------------------------------------------------------//

private const val comparingMessage = "Comparing %-50s: %s%n"

private fun compare(name: String, type: Class<*>, o1: Any?, o2: Any?) {
  System.out.printf(comparingMessage, name, type.name)
  System.out.printf("\texpected: %s%n", o1?.toString()?.replace("\n", "\n              "))
  System.out.printf("\tactual:   %s%n", o2?.toString()?.replace("\n", "\n              "))
  if (o1 !== o2) {
    if (String::class.java.isAssignableFrom(type)) {
      assertThat(o2).isEqualTo(o1).withFailMessage("'$name' values are not equal")
    } else if (List::class.java.isAssignableFrom(type)) {
      if (o1 is List<*> && o2 is List<*>) {
        assertThat(o2.size).isEqualTo(o1.size).withFailMessage("'$name' values are not equal")
        for (i in o1.indices) {
          compare("$name[$i]", o1[i]!!::class.java, o1[i], o2[i])
        }
      }
    } else if (Map::class.java.isAssignableFrom(type)) {
      if (o1 is Map<*, *> && o2 is Map<*, *>) {
        assertThat(o2.size).isEqualTo(o1.size).withFailMessage("'$name' values are not equal")
        val keys = setOf(*o1.keys.toTypedArray(), *o2.keys.toTypedArray())
        for (key in keys) {
          compare("$name['$key']", o1[key]!!::class.java, o1[key], o2[key])
        }
      }
    } else if (o1 != null && o2 != null) {
      if (o2 is Model) {
        // Calling getters on lazily populated fields
        o2.developers
        o2.contributors
        o2.mailingLists
        o2.build?.resources
        o2.build?.testResources
        o2.build?.filters
        o2.modules
      }
      doWithFields(type) { field -> compare("$name.${field.name}", field, o1, o2) }
    } else {
      assertThat(o2).isEqualTo(o1).withFailMessage("'$name' values are not equal")
    }
  }
}

private fun <T> compare(name: String, field: Field, o1: T, o2: T) {
  field.isAccessible = true
  val expected = field.get(o1)
  val actual = field.get(o2)
  compare(name, field.type, expected, actual)
}

private fun doWithFields(clazz: Class<*>, fieldCallback: (Field) -> Unit) {
  var targetClass: Class<*>? = clazz
  do {
    val tc = targetClass!!
    val fields = tc.declaredFields
    for (field in fields) {
      fieldCallback.invoke(field)
    }
    targetClass = tc.superclass
  } while (targetClass != null && targetClass != Any::class.java)
}
