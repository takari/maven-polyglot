package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Notifier : org.apache.maven.model.Notifier(), Cloneable {

  @PomDsl
  fun type(type: String): Notifier {
    this.type = type
    return this
  }

  @PomDsl
  fun address(address: String): Notifier {
    this.address = address
    return this
  }

  @PomDsl
  fun sendOnError(sendOnError: Boolean = true): Notifier {
    this.isSendOnError = sendOnError
    return this
  }

  @PomDsl
  fun sendOnFailure(sendOnFailure: Boolean = true): Notifier {
    this.isSendOnFailure = sendOnFailure
    return this
  }

  @PomDsl
  fun sendOnSuccess(sendOnSuccess: Boolean = true): Notifier {
    this.isSendOnSuccess = sendOnSuccess
    return this
  }

  @PomDsl
  fun sendOnWarning(sendOnWarning: Boolean = true): Notifier {
    this.isSendOnWarning = sendOnWarning
    return this
  }

  @PomDsl
  fun configuration(block: Properties.(Properties) -> Unit) {
    val properties = Properties()
    block.invoke(properties, properties)
    this.configuration = propertiesFactory().apply { putAll(properties.entries()) }
  }

  override fun clone(): org.apache.maven.model.Notifier {
    return super<org.apache.maven.model.Notifier>.clone()
  }
}
