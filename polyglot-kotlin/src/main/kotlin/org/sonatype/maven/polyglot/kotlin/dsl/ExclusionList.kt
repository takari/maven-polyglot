package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ExclusionList : ArrayList<org.apache.maven.model.Exclusion>(), Cloneable {

  /** Provides a callback for defining a new dependency exclusion. */
  @PomDsl
  fun exclusion(block: Exclusion.(Exclusion) -> Unit) {
    val exclusion = Exclusion()
    block(exclusion, exclusion)
    add(exclusion)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
