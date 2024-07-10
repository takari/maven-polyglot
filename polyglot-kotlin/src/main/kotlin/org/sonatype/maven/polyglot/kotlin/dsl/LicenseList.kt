package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class LicenseList : ArrayList<org.apache.maven.model.License>(), Cloneable {

  @PomDsl
  fun license(name: String? = null, block: License.(License) -> Unit) {
    val license = License().apply { this.name = name }
    block.invoke(license, license)
    add(license)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
