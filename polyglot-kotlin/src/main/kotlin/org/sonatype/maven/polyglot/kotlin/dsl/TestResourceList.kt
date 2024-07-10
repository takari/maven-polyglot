package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class TestResourceList : ArrayList<org.apache.maven.model.Resource>(), Cloneable {

  @PomDsl
  fun testResource(block: Resource.(Resource) -> Unit) {
    val resource = Resource()
    block.invoke(resource, resource)
    add(resource)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
