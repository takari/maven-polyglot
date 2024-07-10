package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ResourceList : ArrayList<org.apache.maven.model.Resource>(), Cloneable {

  @PomDsl
  fun resource(block: Resource.(Resource) -> Unit) {
    val resource = Resource()
    block.invoke(resource, resource)
    add(resource)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
