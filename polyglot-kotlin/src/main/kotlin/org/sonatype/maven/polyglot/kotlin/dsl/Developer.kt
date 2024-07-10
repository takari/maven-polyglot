package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class Developer : org.apache.maven.model.Developer(), Cloneable {

  @PomDsl
  fun id(id: String): Developer {
    this.id = id
    return this
  }

  @PomDsl
  fun name(name: String): Developer {
    this.name = name
    return this
  }

  @PomDsl
  fun email(email: String): Developer {
    this.email = email
    return this
  }

  @PomDsl
  fun url(url: String): Developer {
    this.url = url
    return this
  }

  @PomDsl
  fun organization(organization: String): Developer {
    this.organization = organization
    return this
  }

  @PomDsl
  fun organizationUrl(organizationUrl: String): Developer {
    this.organizationUrl = organizationUrl
    return this
  }

  @PomDsl
  fun roles(vararg roles: String): Developer {
    this.roles = roles.asList()
    return this
  }

  @PomDsl
  fun timezone(timezone: String): Developer {
    this.timezone = timezone
    return this
  }

  /** Provides a callback for defining additional developer properties. */
  @PomDsl
  fun properties(block: Properties.(Properties) -> Unit) {
    val properties = Properties()
    block.invoke(properties, properties)
    this.properties = propertiesFactory().apply { putAll(properties.entries()) }
  }

  override fun clone(): org.apache.maven.model.Developer {
    return super<org.apache.maven.model.Developer>.clone()
  }
}
