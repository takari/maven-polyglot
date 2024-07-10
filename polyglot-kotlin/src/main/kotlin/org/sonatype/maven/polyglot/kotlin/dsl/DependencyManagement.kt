package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class DependencyManagement : org.apache.maven.model.DependencyManagement(), Cloneable {

  @PomDsl
  fun dependencies(block: ManagedDependencyList.(ManagedDependencyList) -> Unit) {
    val dependencies = ManagedDependencyList()
    block.invoke(dependencies, dependencies)
    this.dependencies = dependencies
  }

  override fun clone(): org.apache.maven.model.DependencyManagement {
    return super<org.apache.maven.model.DependencyManagement>.clone()
  }
}
