package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class RepositoryList : ArrayList<org.apache.maven.model.Repository>(), Cloneable {

  @PomDsl
  fun repository(nameOrId: String? = null, block: Repository.(Repository) -> Unit) {
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
