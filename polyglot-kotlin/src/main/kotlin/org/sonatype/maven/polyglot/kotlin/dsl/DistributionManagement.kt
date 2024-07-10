package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class DistributionManagement : org.apache.maven.model.DistributionManagement(), Cloneable {

  @PomDsl
  fun downloadUrl(downloadUrl: String) {
    this.downloadUrl = downloadUrl
  }

  @PomDsl
  fun status(status: String) {
    this.status = status
  }

  @PomDsl
  fun repository(
      nameOrId: String? = null,
      block: DeploymentRepository.(DeploymentRepository) -> Unit
  ) {
    val repository =
        DeploymentRepository().apply {
          this.id = nameOrId
          this.name = nameOrId
        }
    block.invoke(repository, repository)
    this.repository = repository
  }

  @PomDsl
  fun snapshotRepository(
      nameOrId: String? = null,
      block: DeploymentRepository.(DeploymentRepository) -> Unit
  ) {
    val snapshotRepository =
        DeploymentRepository().apply {
          this.id = nameOrId
          this.name = nameOrId
        }
    block.invoke(snapshotRepository, snapshotRepository)
    this.snapshotRepository = snapshotRepository
  }

  @PomDsl
  fun site(nameOrId: String? = null, block: Site.(Site) -> Unit) {
    val site =
        Site().apply {
          this.id = nameOrId
          this.name = nameOrId
        }
    block.invoke(site, site)
    this.site = site
  }

  @PomDsl
  fun relocation(gav: String? = null, block: Relocation.(Relocation) -> Unit) {
    val (groupId, artifactId, version) = splitCoordinates(gav, 3)
    val relocation =
        Relocation().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    block.invoke(relocation, relocation)
    this.relocation = relocation
  }

  override fun clone(): org.apache.maven.model.DistributionManagement {
    return super<org.apache.maven.model.DistributionManagement>.clone()
  }
}
