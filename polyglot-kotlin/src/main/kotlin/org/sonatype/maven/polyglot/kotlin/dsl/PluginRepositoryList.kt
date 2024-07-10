package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class PluginRepositoryList : ArrayList<org.apache.maven.model.Repository>(), Cloneable {

  @PomDsl
  fun pluginRepository(nameOrId: String? = null, block: Repository.(Repository) -> Unit) {
    val repository =
        Repository().apply {
          this.id = nameOrId
          this.name = nameOrId
        }
    block.invoke(repository, repository)
    add(repository)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
