package org.sonatype.maven.polyglot.kotlin.dsl

import java.io.File
import org.codehaus.plexus.util.xml.Xpp3Dom
import org.codehaus.plexus.util.xml.Xpp3DomBuilder
import org.sonatype.maven.polyglot.execute.ExecuteTask

@PomDsl
class Project(pom: File) : org.apache.maven.model.Model(), Cloneable {

  init {
    this.pomFile = pom
    this.modelVersion = "4.0.0"
    this.modelEncoding = "UTF-8"
  }

  val tasks: MutableList<ExecuteTask>
    get() {
      val bld = build
      if (bld != null && bld is ProjectBuild) {
        return bld.tasks
      }
      return mutableListOf()
    }

  // -- Project Model Version
  // ---------------------------------------------------------------------------------------//

  @PomDsl
  fun modelVersion(modelVersion: String) {
    this.modelVersion = modelVersion
  }

  // -- Project Directory
  // -------------------------------------------------------------------------------------------//

  @PomDsl
  override fun getProjectDirectory(): File? {
    return super.getProjectDirectory()
  }

  // -- Project ID
  // --------------------------------------------------------------------------------------------------//

  /** @param gavp a string in the form groupId:artifactId:version[:packaging] */
  @PomDsl
  fun id(gavp: String) {
    val (groupId, artifactId, version, packaging) = splitCoordinates(gavp, 4)
    this.id(groupId, artifactId, version, packaging)
  }

  @PomDsl
  fun id(
      groupId: String? = null,
      artifactId: String? = null,
      version: String? = null,
      packaging: String? = null
  ) {
    this.groupId = groupId
    this.artifactId = artifactId
    this.version = version
    if (packaging != null) this.packaging = packaging
  }

  fun setId(gavp: String) {
    id(gavp)
  }

  // -- Parent POM
  // --------------------------------------------------------------------------------------------------//

  @PomDsl
  fun parent(block: Parent.(Parent) -> Unit) {
    val parent = Parent()
    block.invoke(parent, parent)
    this.parent = parent
  }

  @PomDsl
  fun parent(gav: String, relativePath: String? = null): Parent {
    val (groupId, artifactId, version) = splitCoordinates(gav, 3)
    return Parent().apply {
      this.groupId = groupId
      this.artifactId = artifactId
      this.version = version
      if (relativePath !== null) this.relativePath = relativePath
      this@Project.parent = this
    }
  }

  @PomDsl
  fun parent(
      groupId: String,
      artifactId: String,
      version: String,
      relativePath: String? = null
  ): Parent {
    return Parent().apply {
      this.groupId = groupId
      this.artifactId = artifactId
      this.version = version
      if (relativePath !== null) this.relativePath = relativePath
      this@Project.parent = this
    }
  }

  // -- Project Artifact ID
  // -----------------------------------------------------------------------------------------//

  @PomDsl
  fun artifactId(artifactId: String) {
    this.artifactId = artifactId
  }

  // -- Project Group ID
  // --------------------------------------------------------------------------------------------//

  @PomDsl
  fun groupId(groupId: String) {
    this.groupId = groupId
  }

  // -- Project Version
  // ---------------------------------------------------------------------------------------------//

  @PomDsl
  fun version(version: String) {
    this.version = version
  }

  // -- Project Packaging
  // -------------------------------------------------------------------------------------------//

  @PomDsl
  fun packaging(packaging: String) {
    this.packaging = packaging
  }

  // -- Project Name
  // ------------------------------------------------------------------------------------------------//

  @PomDsl
  fun name(name: String) {
    this.name = name
  }

  // -- Project Description
  // -----------------------------------------------------------------------------------------//

  @PomDsl
  fun description(description: String) {
    this.description = description
  }

  // -- Project URL
  // -------------------------------------------------------------------------------------------------//

  @PomDsl
  fun url(url: String) {
    this.url = url
  }

  // -- Project Inception Year
  // --------------------------------------------------------------------------------------//

  @PomDsl
  fun inceptionYear(inceptionYear: String) {
    this.inceptionYear = inceptionYear
  }

  @PomDsl
  fun inceptionYear(inceptionYear: Int) {
    this.inceptionYear = inceptionYear.toString()
  }

  // -- Organization
  // ------------------------------------------------------------------------------------------------//

  @PomDsl
  fun organization(block: Organization.(Organization) -> Unit) {
    val organization = Organization()
    block.invoke(organization, organization)
    this.organization = organization
  }

  @PomDsl
  fun organization(name: String?, url: String?): Organization {
    return Organization().apply {
      this.name = name
      this.url = url
      this@Project.organization = this
    }
  }

  // -- Licenses
  // ----------------------------------------------------------------------------------------------------//

  @PomDsl
  fun licenses(block: LicenseList.(LicenseList) -> Unit) {
    val licenses = LicenseList()
    block.invoke(licenses, licenses)
    this.licenses = licenses
  }

  @PomDsl
  fun license(
      name: String,
      url: String,
      distribution: String = "repo",
      comments: String? = null
  ): License {
    return License().apply {
      this.name = name
      this.url = url
      this.distribution = distribution
      this.comments = comments
      this@Project.addLicense(this)
    }
  }

  // -- Developers
  // --------------------------------------------------------------------------------------------------//

  @PomDsl
  fun developers(block: DeveloperList.(DeveloperList) -> Unit) {
    val developers = DeveloperList()
    block.invoke(developers, developers)
    this.developers = developers
  }

  // -- Contributors
  // ------------------------------------------------------------------------------------------------//

  @PomDsl
  fun contributors(block: ContributorList.(ContributorList) -> Unit) {
    val contributors = ContributorList()
    block.invoke(contributors, contributors)
    this.contributors = contributors
  }

  // -- Mailing Lists
  // -----------------------------------------------------------------------------------------------//

  @PomDsl
  fun mailingLists(block: MailingListList.(MailingListList) -> Unit) {
    val mailingLists = MailingListList()
    block.invoke(mailingLists, mailingLists)
    this.mailingLists = mailingLists
  }

  // -- Prerequisites
  // -----------------------------------------------------------------------------------------------//

  @PomDsl
  fun prerequisites(block: Prerequisites.(Prerequisites) -> Unit) {
    val prerequisites = Prerequisites()
    block.invoke(prerequisites, prerequisites)
    this.prerequisites = prerequisites
  }

  // -- Modules
  // -----------------------------------------------------------------------------------------------------//

  @PomDsl
  fun modules(vararg modules: String) {
    this.modules = modules.asList()
  }

  // -- SCM
  // ---------------------------------------------------------------------------------------------------------//

  @PomDsl
  fun scm(block: Scm.(Scm) -> Unit) {
    val scm = Scm()
    block.invoke(scm, scm)
    this.scm = scm
  }

  // -- Issue Management
  // --------------------------------------------------------------------------------------------//

  @PomDsl
  fun issueManagement(block: IssueManagement.(IssueManagement) -> Unit) {
    val issueManagement = IssueManagement()
    block.invoke(issueManagement, issueManagement)
    this.issueManagement = issueManagement
  }

  // -- CI Management
  // -----------------------------------------------------------------------------------------------//

  @PomDsl
  fun ciManagement(block: CiManagement.(CiManagement) -> Unit) {
    val ciManagement = CiManagement()
    block.invoke(ciManagement, ciManagement)
    this.ciManagement = ciManagement
  }

  // -- Distribution Management
  // -------------------------------------------------------------------------------------//

  @PomDsl
  fun distributionManagement(block: DistributionManagement.(DistributionManagement) -> Unit) {
    val distributionManagement = DistributionManagement()
    block.invoke(distributionManagement, distributionManagement)
    this.distributionManagement = distributionManagement
  }

  // -- Properties
  // --------------------------------------------------------------------------------------------------//

  @PomDsl
  fun properties(block: Properties.(Properties) -> Unit) {
    val properties = Properties()
    block.invoke(properties, properties)
    this.properties = propertiesFactory().apply { putAll(properties.entries()) }
  }

  // -- Dependency Management
  // ---------------------------------------------------------------------------------------//

  @PomDsl
  fun dependencyManagement(block: DependencyManagement.(DependencyManagement) -> Unit) {
    val dependencyManagement = DependencyManagement()
    block(dependencyManagement, dependencyManagement)
    this.dependencyManagement = dependencyManagement
  }

  // -- Dependencies
  // ------------------------------------------------------------------------------------------------//

  @PomDsl
  fun dependencies(block: DependencyList.(DependencyList) -> Unit) {
    val dependencies = DependencyList()
    block(dependencies, dependencies)
    this.dependencies = dependencies
  }

  // -- Repositories
  // ------------------------------------------------------------------------------------------------//

  @PomDsl
  fun repositories(block: RepositoryList.(RepositoryList) -> Unit) {
    val repositories = RepositoryList()
    block.invoke(repositories, repositories)
    this.repositories = repositories
  }

  // -- Plugin Repositories
  // -----------------------------------------------------------------------------------------//

  @PomDsl
  fun pluginRepositories(block: PluginRepositoryList.(PluginRepositoryList) -> Unit) {
    val pluginRepositories = PluginRepositoryList()
    block.invoke(pluginRepositories, pluginRepositories)
    this.pluginRepositories = pluginRepositories
  }

  // -- Build
  // -------------------------------------------------------------------------------------------------------//

  @PomDsl
  fun build(block: ProjectBuild.(ProjectBuild) -> Unit) {
    val build = ProjectBuild()
    block(build, build)
    setBuild(build)
  }

  // -- Reports
  // -----------------------------------------------------------------------------------------------------//

  /**
   * Sets the reports content.
   *
   * @param source the reports source as a [String] of XML
   */
  @PomDsl
  fun reports(source: String): Xpp3Dom {
    this.reports = source
    return this.reports as Xpp3Dom
  }

  @PomDsl
  fun reports(block: XmlNode.(XmlNode) -> Unit) {
    val reports = XmlNode("reports")
    block(reports, reports)
    this.reports = reports.xpp3Dom
  }

  /** Sets the reports. */
  override fun setReports(source: Any?) {
    val name = "reports"
    val xpp3Dom =
        when (source) {
          null -> null
          is Xpp3Dom -> source
          else -> Xpp3DomBuilder.build(source.toString().reader())
        }
    if (xpp3Dom != null && xpp3Dom.name != name) {
      val configuration = Xpp3Dom(name)
      configuration.addChild(xpp3Dom)
      super.setReports(configuration)
    } else {
      super.setReports(xpp3Dom)
    }
  }

  // -- Reporting
  // ---------------------------------------------------------------------------------------------------//

  @PomDsl
  fun reporting(block: Reporting.(Reporting) -> Unit) {
    val reporting = Reporting()
    block(reporting, reporting)
    this.reporting = reporting
  }

  // -- Profiles
  // ----------------------------------------------------------------------------------------------------//

  @PomDsl
  fun profiles(block: ProfileList.(ProfileList) -> Unit) {
    val profiles = ProfileList()
    block(profiles, profiles)
    this.profiles = profiles
  }

  override fun clone(): org.apache.maven.model.Model {
    return super<org.apache.maven.model.Model>.clone()
  }
}
