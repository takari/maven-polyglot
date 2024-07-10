package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ReportSetList : ArrayList<org.apache.maven.model.ReportSet>(), Cloneable {

  /** Provides a callback for defining a new report set. */
  @PomDsl
  fun reportSet(id: String? = null, block: ReportSet.(ReportSet) -> Unit) {
    val reportSet = ReportSet().apply { this.id = id }
    block(reportSet, reportSet)
    add(reportSet)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
