package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ProfileList : ArrayList<org.apache.maven.model.Profile>(), Cloneable {

  /** Provides a callback for defining a new dependency exclusion. */
  @PomDsl
  fun profile(id: String? = null, block: Profile.(Profile) -> Unit) {
    val profile = Profile().apply { this.id = id }
    block.invoke(profile, profile)
    add(profile)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
