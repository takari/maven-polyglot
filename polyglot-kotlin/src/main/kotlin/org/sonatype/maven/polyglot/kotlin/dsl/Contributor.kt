package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Contributor : org.apache.maven.model.Contributor(), Cloneable {

  @PomDsl
  fun name(name: String): Contributor {
    this.name = name
    return this
  }

  @PomDsl
  fun email(email: String): Contributor {
    this.email = email
    return this
  }

  @PomDsl
  fun url(url: String): Contributor {
    this.url = url
    return this
  }

  @PomDsl
  fun organization(organization: String): Contributor {
    this.organization = organization
    return this
  }

  @PomDsl
  fun organizationUrl(organizationUrl: String): Contributor {
    this.organizationUrl = organizationUrl
    return this
  }

  @PomDsl
  fun roles(vararg roles: String): Contributor {
    this.roles = roles.asList()
    return this
  }

  @PomDsl
  fun timezone(timezone: String): Contributor {
    this.timezone = timezone
    return this
  }

  /** Provides a callback for defining additional contributor properties. */
  @PomDsl
  fun properties(block: Properties.(Properties) -> Unit) {
    val properties = Properties()
    block.invoke(properties, properties)
    this.properties = propertiesFactory().apply { putAll(properties.entries()) }
  }

  override fun clone(): org.apache.maven.model.Contributor {
    return super<org.apache.maven.model.Contributor>.clone()
  }
}
