package org.sonatype.maven.polyglot.kotlin.serialization

import java.io.Writer
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import org.apache.maven.execution.MavenSession
import org.apache.maven.model.*
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.util.xml.Xpp3Dom
import org.sonatype.maven.polyglot.execute.ExecuteContext
import org.sonatype.maven.polyglot.kotlin.dsl.addAll
import org.sonatype.maven.polyglot.kotlin.dsl.addAllNonNull
import org.sonatype.maven.polyglot.kotlin.dsl.addFirstNonNull
import org.sonatype.maven.polyglot.kotlin.dsl.cast

/**
 * A `ModelScriptWriter` converts a Maven project model into a Kotlin script capable of producing an
 * identical model. Instances of this class may be configured with one of two flavors of the Kotlin
 * Model DSL. The most basic flavor, "block", consists of simply writing callbacks on each model
 * element and using property setters to initialize its fields. All complex model elements can be
 * configured using this "block" flavor of the DSL. The "block" flavor, however, does little on its
 * own to reduce the code verbosity of the original XML.
 *
 * ```
 * project {
 *   groupId = "com.example"
 *   artifactId = "sample-app
 *   version = "1.0"
 *   packaging = "jar"
 *   name = "Sample Application"
 *   properties {
 *     "kotlin.version" to "1.3.11"
 *   }
 *   build {
 *     plugins {
 *       plugin {
 *         groupId = "org.jetbrains.kotlin"
 *         artifactId = "kotlin-maven-plugin"
 *         version = "\${kotlin.version}"
 *         executions {
 *           execution {
 *             id = "compile"
 *             phase = "compile"
 *             goals = listOf("compile")
 *           }
 *           execution {
 *             id = "test-compile"
 *             phase = "test-compile"
 *             goals = listOf("test-compile")
 *           }
 *         }
 *         dependencies {
 *           dependency {
 *             groupId = "org.jetbrains.kotlin"
 *             artifactId = "kotlin-maven-allopen"
 *             version = "\${kotlin.version}"
 *           }
 *           dependency {
 *             groupId = "org.jetbrains.kotlin"
 *             artifactId = "kotlin-maven-noarg"
 *             version = "\${kotlin.version}"
 *           }
 *         }
 *         configuration = xml("""
 *             <configuration>
 *               <jvmTarget>1.8</jvmTarget>
 *               <javaParameters>true</javaParameters>
 *               <compilerPlugins>
 *                 <plugin>spring</plugin>
 *                 <plugin>jpa</plugin>
 *               </compilerPlugins>
 *             </configuration>""")
 *       }
 *     }
 *   }
 * }
 * ```
 *
 * The "mixed" flavor leverages optional parameters on block functions as well as additional
 * extension functions and in order to minimize the verbosity of specific model elements.
 *
 * ```
 * project("Sample Application") {
 *   id("com.example:sample-app:1.0:jar")
 *   properties {
 *     "kotlin.version" to "1.3.11"
 *   }
 *   build {
 *     plugins {
 *       plugin("org.jetbrains.kotlin:kotlin-maven-plugin:\${kotlin.version}") {
 *         execution(id = "compile", phase = "compile", goals = listOf("compile"))
 *         execution(id = "test-compile", phase = "test-compile", goals = listOf("test-compile"))
 *         dependency("org.jetbrains.kotlin:kotlin-maven-allopen:\${kotlin.version}")
 *         dependency("org.jetbrains.kotlin:kotlin-maven-noarg:\${kotlin.version}")
 *         configuration {
 *           "jvmTarget" to 1.8
 *           "javaParameters" to true
 *           "compilerPlugins" {
 *             "plugin" to "spring"
 *             "plugin" to "jpa"
 *           }
 *         }
 *       }
 *     }
 *   }
 * }
 * ```
 *
 * Fields that require values of type [Xpp3Dom] can receive their values as strings or by using the
 * XML builder DSL shown in the sample plugin configuration above.
 */
internal class ModelScriptWriter(writer: Writer, private val options: Map<String, Any?>) :
    KotlinScriptWriter(writer) {

  private val fileComment: String =
      (options["file.comment"] as String?)
          ?: "Generated from pom.kts on ${LocalDate.now()} at ${LocalTime.now()}"

  // By default, Xpp3Dom values are scripted as a function that accepts a raw (triple-quoted)
  // string.
  // When using the XML builder DSL, we script the Xpp3Dom as hierarchy of gets on tag names using
  // an operator
  private val useXmlBuilder: Boolean = option("xml.dsl.enabled", false) { it == "true" }

  // The default flavor is to use consistent block semantics all the way down the model.
  // When the flavor is "mixed", we'll write the script using additional extensions that
  // have been designed to minimize verbosity.
  private val flavor: String =
      option("flavor", "block") {
        when (it) {
          "mixed" -> "mixed"
          else -> "block"
        }
      }

  private val mixedFlavor: Boolean = flavor == "mixed"

  private val blockFlavor: Boolean = flavor == "block"

  // For convenience in referencing methods inside certain lambdas
  private val out: ModelScriptWriter = this

  // -- Public API
  // --------------------------------------------------------------------------------------------------//

  fun write(model: Model) {

    blockComment(fileComment)

    val gavp =
        with(model) {
          "${groupId ?: ""}:${artifactId ?: ""}:${version ?: ""}:${packaging ?: "jar"}"
        }
    block("project", model, { if (mixedFlavor) it.addFirstNonNull(name, gavp) }) {
      set("modelVersion", modelVersion) { modelVersion != "4.0.0" }
      set("modelEncoding", modelEncoding) { modelEncoding != "UTF-8" }

      writeParent(parent)

      if (blockFlavor || name != null) {
        endLine()
        if (mixedFlavor) {
          set("id", gavp)
        } else {
          set("groupId", groupId)
          set("artifactId", artifactId)
          set("version", version)
          set("packaging", packaging) { packaging != "jar" }
        }
      }

      if (mixedFlavor && name != null) {
        if (listOfNotNull(description, url, inceptionYear).isNotEmpty()) {
          endLine()
        }
      } else {
        if (listOfNotNull(name, description, url, inceptionYear).isNotEmpty()) {
          endLine()
        }
      }

      set("name", name) { blockFlavor }
      set("description", description)
      set("url", url)
      set("inceptionYear", inceptionYear)

      writeOrganization(organization)

      block("licenses", licenses) { forEach(out::writeLicense) }
      block("developers", developers) { forEach(out::writeDeveloper) }
      block("contributors", contributors) { forEach(out::writeContributor) }
      block("mailingLists", mailingLists) { forEach(out::writeMailingList) }

      writePrerequisites(prerequisites)

      block("modules", modules, { it.addAll(modules) })

      writeScm(scm)
      writeIssueManagement(issueManagement)
      writeCiManagement(ciManagement)
      writeDistributionManagement(distributionManagement)
      writeProperties(properties)
      writeDependencyManagement(dependencyManagement)

      block("dependencies", dependencies) { forEach(out::writeDependency) }
      block("repositories", repositories) { forEach(out::writeRepository) }
      block("pluginRepositories", pluginRepositories) { forEach(::writePluginRepository) }

      writeBuild(build)

      set("reports", reports as Xpp3Dom?)

      writeReporting(reporting)

      block("profiles", profiles) { forEach(out::writeProfile) }
    }
  }

  // -- Protected API
  // -----------------------------------------------------------------------------------------------//

  private fun writeActivation(activation: Activation?) {
    block("activation", activation) {
      set("isActiveByDefault", "activeByDefault", isActiveByDefault) { isActiveByDefault }
      set("jdk", jdk)
      writeActivationOS(os)
      writeActivationProperty(property)
      writeActivationFile(file)
    }
  }

  private fun writeActivationFile(activationFile: ActivationFile?) {
    block("file", activationFile) {
      set("missing", missing)
      set("exists", exists)
    }
  }

  private fun writeActivationOS(activationOS: ActivationOS?) {
    block("os", activationOS, { if (mixedFlavor) it.add(name) }) {
      set("name", name) { blockFlavor }
      set("family", family)
      set("arch", arch)
      set("version", version)
    }
  }

  private fun writeActivationProperty(activationProperty: ActivationProperty?) {
    block("property", activationProperty, { if (mixedFlavor) it.add(name) }) {
      set("name", name) { blockFlavor }
      set("value", value)
    }
  }

  private fun writeBuild(build: Build?) {
    block("build", build) {
      set("finalName", finalName)
      set("defaultGoal", defaultGoal)
      set("sourceDirectory", sourceDirectory)
      set("testSourceDirectory", testSourceDirectory)
      set("scriptSourceDirectory", scriptSourceDirectory)
      set("directory", directory)
      set("outputDirectory", outputDirectory)
      set("testOutputDirectory", testOutputDirectory)
      set("filters", filters)
      block("resources", resources) { forEach(out::writeResource) }
      block("testResources", testResources) { forEach(out::writeTestResource) }
      block("extensions", extensions) { forEach(out::writeExtension) }
      writePluginManagement(pluginManagement)
      block("plugins", plugins) { forEach(out::writePlugin) }
    }
  }

  private fun writeBuildBase(buildBase: BuildBase?) {
    block("build", buildBase) {
      set("defaultGoal", defaultGoal)
      set("resources", cast<List<String?>>(resources))
      set("testResources", cast<List<String?>>(testResources))
      set("directory", directory)
      set("finalName", finalName)
      set("filters", filters)
      writePluginManagement(pluginManagement)
      block("plugins", plugins) { forEach(out::writePlugin) }
    }
  }

  private fun writeCiManagement(ciManagement: CiManagement?) {
    block("ciManagement", ciManagement) {
      set("system", system)
      set("url", url)
      block("notifiers", notifiers) { forEach(out::writeNotifier) }
    }
  }

  private fun writeContributor(contributor: Contributor) {
    block("contributor", contributor, { if (mixedFlavor) it.add(name) }) {
      set("name", name) { blockFlavor }
      set("email", email)
      set("url", url)
      set("organization", organization)
      set("organizationUrl", organizationUrl)
      set("roles", roles)
      set("timezone", timezone)
      writeProperties(properties)
    }
  }

  private fun writeDependency(dependency: Dependency) {
    val gav =
        with(dependency) {
          if (version == null) {
            "${groupId ?: ""}:${artifactId ?: ""}"
          } else {
            "${groupId ?: ""}:${artifactId ?: ""}:${version}"
          }
        }
    with(dependency) {
      val fields = HashSet<String>()
      if (type != null && type != "jar") fields.add("type")
      if (classifier != null) fields.add("classifier")
      if (scope != null) fields.add("scope")
      if (systemPath != null) fields.add("systemPath")
      if (!exclusions.isNullOrEmpty()) fields.add("exclusions")
      if (isOptional) fields.add("isOptional")

      if (mixedFlavor) {
        if (fields.isEmpty()) {
          block("dependency", dependency, { it.add(gav) })
        } else if (fields.contains("scope")) {
          if (scope == "import" && type == "pom") {
            if (fields.size == 2) {
              block(scope, dependency, { it.add(gav) })
            } else {
              block(scope, dependency, { it.add(gav) }) {
                set("classifier", classifier)
                writeExclusions(exclusions)
                set("isOptional", "optional", isOptional) { isOptional }
              }
            }
          } else if (scope == "system" && systemPath != null) {
            if (fields.size == 2) {
              block(scope, dependency, { it.addAll(gav, systemPath) })
            } else {
              block(scope, dependency, { it.addAll(gav, systemPath) }) {
                set("type", type) { type != "jar" }
                set("classifier", classifier)
                writeExclusions(exclusions)
                set("isOptional", "optional", isOptional) { isOptional }
              }
            }
          } else {
            if (fields.size == 1) {
              block(scope, dependency, { it.add(gav) })
            } else {
              block(scope, dependency, { it.add(gav) }) {
                set("type", type) { type != "jar" }
                set("classifier", classifier)
                writeExclusions(exclusions)
                set("isOptional", "optional", isOptional) { isOptional }
              }
            }
          }
        } else if (fields.contains("isOptional")) {
          if (fields.size == 1) {
            block("optional", dependency, { it.add(gav) })
          } else {
            block("optional", dependency, { it.add(gav) }) {
              set("type", type) { type != "jar" }
              set("classifier", classifier)
              writeExclusions(exclusions)
            }
          }
        } else {
          block("dependency", dependency, { it.add(gav) }) {
            set("type", type) { type != "jar" }
            set("classifier", classifier)
            writeExclusions(exclusions)
          }
        }
      } else {
        block("dependency", dependency, { if (mixedFlavor) it.add(gav) }) {
          if (blockFlavor) {
            set("groupId", groupId)
            set("artifactId", artifactId)
            set("version", version)
          }
          set("type", type) { type != "jar" }
          set("classifier", classifier)
          set("scope", scope)
          set("systemPath", systemPath)
          writeExclusions(exclusions)
          set("isOptional", "optional", isOptional) { isOptional }
        }
      }
    }
  }

  private fun writeDependencyManagement(dependencyManagement: DependencyManagement?) {
    block("dependencyManagement", dependencyManagement) {
      block("dependencies", dependencies) { forEach(out::writeDependency) }
    }
  }

  private fun writeDeploymentRepository(
      deploymentRepository: DeploymentRepository?,
      blockName: String
  ) {
    block(blockName, deploymentRepository, { if (mixedFlavor) it.addFirstNonNull(name, id) }) {
      set("id", id) { blockFlavor || (name != null && name != id) }
      set("name", name) { blockFlavor }
      set("url", url)
      set("layout", layout) { layout != "default" }
      set("isUniqueVersion", "uniqueVersion", isUniqueVersion) { !isUniqueVersion }
    }
  }

  private fun writeDeveloper(developer: Developer) {
    if (mixedFlavor && developer.email != null) {
      val address =
          if (developer.name != null) {
            "${developer.name} <${developer.email}>"
          } else {
            developer.email
          }
      val id = if (developer.id != developer.email.split('@')[0]) developer.id else null
      if (id != null ||
          developer.url != null ||
          developer.organization != null ||
          developer.organizationUrl != null ||
          !developer.roles.isNullOrEmpty() ||
          developer.timezone != null ||
          !developer.properties.isNullOrEmpty()) {
        block("developer", developer, { it.add(address) }) {
          set("id", id) { id != developer.email.split('@')[0] }
          set("url", url)
          set("organization", organization)
          set("organizationUrl", organizationUrl)
          set("roles", roles)
          set("timezone", timezone)
          writeProperties(properties)
        }
      } else {
        block("developer", developer, { it.add(address) })
      }
    } else {
      block("developer", developer, { if (mixedFlavor) it.addFirstNonNull(name, id) }) {
        set("id", id) { blockFlavor || (name != null && name != id) }
        set("name", name) { blockFlavor }
        set("email", email)
        set("url", url)
        set("organization", organization)
        set("organizationUrl", organizationUrl)
        set("roles", roles)
        set("timezone", timezone)
        writeProperties(properties)
      }
    }
  }

  private fun writeDistributionManagement(distributionManagement: DistributionManagement?) {
    block("distributionManagement", distributionManagement) {
      writeDeploymentRepository(repository)
      writeDeploymentSnapshotRepository(snapshotRepository)
      writeSite(site)
      set("downloadUrl", downloadUrl)
      writeRelocation(relocation)
      set("status", status)
    }
  }

  private fun writeExclusion(exclusion: Exclusion) {
    block("exclusion", exclusion) {
      set("groupId", groupId)
      set("artifactId", artifactId)
    }
  }

  private fun writeExclusions(exclusions: List<Exclusion>?) {
    if (mixedFlavor) {
      block(
          "exclusions",
          exclusions,
          { args -> args.addAll(map { "${it.groupId ?: ""}:${it.artifactId ?: ""}" }) })
    } else {
      block("exclusions", exclusions) { forEach(out::writeExclusion) }
    }
  }

  private fun writeExtension(extension: Extension) {
    if (mixedFlavor) {
      val gav =
          with(extension) {
            if (version == null) {
              "${groupId ?: ""}:${artifactId ?: ""}"
            } else {
              "${groupId ?: ""}:${artifactId ?: ""}:${version}"
            }
          }
      block("extension", extension, { it.add(gav) })
    } else {
      block("extension", extension) {
        set("groupId", groupId)
        set("artifactId", artifactId)
        set("version", version)
      }
    }
  }

  private fun writeIssueManagement(issueManagement: IssueManagement?) {
    block("issueManagement", issueManagement) {
      set("system", system)
      set("url", url)
    }
  }

  private fun writeLicense(license: License) {
    block("license", license, { if (mixedFlavor) it.add(name) }) {
      set("name", name) { blockFlavor }
      set("url", url)
      set("distribution", distribution)
      set("comments", comments)
    }
  }

  private fun writeMailingList(mailingList: MailingList) {
    block("mailingList", mailingList, { if (mixedFlavor) it.add(name) }) {
      set("name", name) { blockFlavor }
      set("subscribe", subscribe)
      set("unsubscribe", unsubscribe)
      set("post", post)
      set("archive", archive)
      set("otherArchives", otherArchives)
    }
  }

  private fun writeNotifier(notifier: Notifier) {
    block("notifier", notifier) {
      set("type", type) { type != "mail" }
      set("address", address)
      set("isSendOnError", "sendOnError", isSendOnError) { !isSendOnError }
      set("isSendOnFailure", "sendOnFailure", isSendOnFailure) { !isSendOnFailure }
      set("isSendOnSuccess", "sendOnSuccess", isSendOnSuccess) { !isSendOnFailure }
      set("isSendOnWarning", "sendOnWarning", isSendOnWarning) { !isSendOnFailure }
      writeProperties(configuration, "configuration")
    }
  }

  private fun writeOrganization(organization: Organization?) {
    if (mixedFlavor) {
      block("organization", organization, { it.addAllNonNull(name, url) })
    } else {
      block("organization", organization) {
        set("name", name)
        set("url", url)
      }
    }
  }

  private fun writeParent(parent: Parent?) {
    if (mixedFlavor) {
      block("parent", parent, { it.addAll(groupId, artifactId, version, relativePath) })
    } else {
      block("parent", parent) {
        set("groupId", groupId)
        set("artifactId", artifactId)
        set("version", version)
        set("relativePath", relativePath) { relativePath != "../pom.kts" }
      }
    }
  }

  private fun writePlugin(plugin: Plugin) {
    val gav =
        with(plugin) {
          val groupId = if (groupId == null) "" else groupId
          if (version == null) {
            "${groupId}:${artifactId ?: ""}"
          } else {
            "${groupId}:${artifactId ?: ""}:${version}"
          }
        }
    with(plugin) {
      if (mixedFlavor &&
          extensions.isNullOrEmpty() &&
          executions.isNullOrEmpty() &&
          dependencies.isNullOrEmpty() &&
          isInherited &&
          configuration == null) {
        block("plugin", plugin, { it.add(gav) })
      } else {
        block("plugin", plugin, { if (mixedFlavor) it.add(gav) }) {
          if (blockFlavor) {
            set("groupId", groupId)
            set("artifactId", artifactId)
            set("version", version)
          }
          set("extensions", extensions)
          block("executions", executions) { forEach(out::writePluginExecution) }
          block("dependencies", dependencies) { forEach(out::writeDependency) }
          set("isInherited", "inherited", isInherited) { !isInherited }
          set("configuration", configuration as Xpp3Dom?)
        }
      }
    }
  }

  private fun writePluginExecution(pluginExecution: PluginExecution) {
    block("execution", pluginExecution, { if (mixedFlavor && id != "default") it.add(id) }) {
      set("id", id) { id != "default" && blockFlavor }
      set("phase", phase)
      set("goals", goals)
      set("isInherited", "inherited", isInherited) { !isInherited }
      set("configuration", configuration as Xpp3Dom?)
    }
  }

  private fun writePluginManagement(pluginManagement: PluginManagement?) {
    block("pluginManagement", pluginManagement) {
      block("plugins", plugins) { forEach(out::writePlugin) }
    }
  }

  private fun writePrerequisites(prerequisites: Prerequisites?) {
    block("prerequisites", prerequisites) { set("maven", maven) { maven != "2.0" } }
  }

  private fun writeProfile(profile: Profile) {
    block("profile", profile, { if (mixedFlavor) it.add(id) }) {
      set("id", id) { id != "default" && blockFlavor }

      writeActivation(activation)

      block("modules", modules, { it.addAll(modules) })

      writeDistributionManagement(distributionManagement)
      writeProperties(properties)
      writeDependencyManagement(dependencyManagement)

      block("dependencies", dependencies) { forEach(out::writeDependency) }
      block("repositories", repositories) { forEach(out::writeRepository) }
      block("pluginRepositories", pluginRepositories) { forEach(out::writePluginRepository) }

      writeBuildBase(build)

      set("reports", reports as Xpp3Dom?)

      writeReporting(reporting)
    }
  }

  private fun writeProperties(properties: Properties?, blockName: String = "properties") {
    @Suppress("UNCHECKED_CAST")
    block(blockName, properties as Map<String, String>) {
      entries
          .sortedBy { entry -> entry.key }
          .forEach { entry ->
            writePair(entry.key to entry.value)
            endLine()
          }
    }
  }

  private fun writeRelocation(relocation: Relocation?) {
    block("relocation", relocation) {
      set("groupId", groupId)
      set("artifactId", artifactId)
      set("version", version)
      set("message", message)
    }
  }

  private fun writeReportPlugin(reportPlugin: ReportPlugin) {
    val gav =
        with(reportPlugin) {
          val groupId = if (groupId == null) "" else groupId
          if (version == null) {
            "${groupId}:${artifactId ?: ""}"
          } else {
            "${groupId}:${artifactId ?: ""}:${version}"
          }
        }
    with(reportPlugin) {
      if (mixedFlavor && reportSets.isNullOrEmpty() && isInherited && configuration == null) {
        block("plugin", reportPlugin, { it.add(gav) })
      } else {
        block("plugin", reportPlugin, { if (mixedFlavor) it.add(gav) }) {
          if (blockFlavor) {
            set("groupId", groupId)
            set("artifactId", artifactId)
            set("version", version)
          }
          block("reportSets", reportSets) { forEach(out::writeReportSet) }
          set("isInherited", "inherited", isInherited) { !isInherited }
          set("configuration", configuration as Xpp3Dom?)
        }
      }
    }
  }

  private fun writeReportSet(reportSet: ReportSet) {
    block("reportSet", reportSet, { if (mixedFlavor) it.add(id) }) {
      set("id", id) { id != "default" }
      set("reports", reports)
      set("isInherited", "inherited", isInherited) { !isInherited }
      set("configuration", configuration as Xpp3Dom?)
    }
  }

  private fun writeReporting(reporting: Reporting?) {
    block("reporting", reporting) {
      set("isExcludeDefaults", "excludeDefaults", isExcludeDefaults) { !isExcludeDefaults }
      set("outputDirectory", outputDirectory)
      block("plugins", plugins) { forEach(out::writeReportPlugin) }
    }
  }

  private fun writeRepository(repository: Repository, blockName: String) {
    block(blockName, repository, { if (mixedFlavor) it.addFirstNonNull(name, id) }) {
      set("id", id) { blockFlavor || (name != null && name != id) }
      set("name", name) { blockFlavor }
      set("url", url)
      set("layout", layout) { layout != "default" }
      writeReleaseRepositoryPolicy(releases)
      writeSnapshotRepositoryPolicy(snapshots)
    }
  }

  private fun writeRepositoryPolicy(repositoryPolicy: RepositoryPolicy?, blockName: String) {
    block(blockName, repositoryPolicy) {
      set("isEnabled", "enabled", isEnabled) { !isEnabled }
      set("updatePolicy", updatePolicy)
      set("checksumPolicy", checksumPolicy)
    }
  }

  private fun writeResource(resource: Resource, blockName: String) {
    block(blockName, resource) {
      set("targetPath", targetPath)
      set("isFiltering", "filtering", isFiltering) { !isFiltering }
      set("directory", directory)
      set("includes", includes)
      set("excludes", excludes)
    }
  }

  private fun writeScm(scm: Scm?) {
    block("scm", scm) {
      set("url", url)
      set("connection", connection)
      set("developerConnection", developerConnection)
      set("tag", tag) { tag != "HEAD" }
    }
  }

  private fun writeSite(site: Site?) {
    block("site", site, { if (mixedFlavor) it.addFirstNonNull(name, id) }) {
      set("id", id) { blockFlavor || (name != null && name != id) }
      set("name", name) { blockFlavor }
      set("url", url)
    }
  }

  private fun set(fieldName: String, value: String?, test: (() -> Boolean) = { true }) {
    if (value != null && test()) {
      val content: () -> Unit = { singleQuotedString(value) }
      if (mixedFlavor) {
        function(fieldName, content)
      } else {
        field(fieldName, content)
      }
      endLine()
    }
  }

  private fun set(
      fieldName: String,
      functionName: String,
      value: Boolean,
      test: (() -> Boolean) = { true }
  ) {
    if (test()) {
      val content: () -> Unit = { write(value.toString()) }
      if (mixedFlavor) {
        function(functionName, content)
      } else {
        field(fieldName, content)
      }
      endLine()
    }
  }

  private fun set(fieldName: String, values: List<String?>?) {
    if (!values.isNullOrEmpty()) {
      if (mixedFlavor) {
        function(fieldName) { arguments(values) }
      } else {
        field(fieldName) { function("listOf") { arguments(values) } }
      }
      endLine()
    }
  }

  private fun set(fieldName: String, xml: Xpp3Dom?) {
    when {
      xml == null -> return
      useXmlBuilder -> {
        block(xml.name, xml) { children.forEach { writeXpp3Dom(it) } }
      }
      else -> {
        if (indentLevel == 1) {
          endLine()
        }
        field(fieldName) {
          multiLineString {
            val str =
                xml.toString()
                    .replace(Regex("""(?s)\Q<?\E.*?\Q?>\E\s*"""), "")
                    .replace("$", "\${\"$\"}")
            str.lines().forEach {
              if (it.isNotEmpty()) {
                write(it)
                endLine()
              }
            }
          }
        }
        endLine()
      }
    }
  }

  private fun writeXpp3Dom(xml: Xpp3Dom) {
    if (xml.value == null) {
      block("\"${xml.name}\"", xml) { children.forEach { writeXpp3Dom(it) } }
    } else {
      writePair(Pair(xml.name, xml.value))
      endLine()
    }
  }

  private val executeContext: ExecuteContext = ExecuteContextStub()

  private class ExecuteContextStub : ExecuteContext {
    override fun getProject(): MavenProject {
      throw NotImplementedError("not implemented")
    }

    override fun getSession(): MavenSession {
      throw NotImplementedError("not implemented")
    }

    override fun getLog(): Log {
      throw NotImplementedError("not implemented")
    }
  }

  private fun writeDeploymentRepository(deploymentRepository: DeploymentRepository?) {
    writeDeploymentRepository(deploymentRepository, "repository")
  }

  private fun writeDeploymentSnapshotRepository(deploymentRepository: DeploymentRepository?) {
    writeDeploymentRepository(deploymentRepository, "snapshotRepository")
  }

  private fun writeRepository(repository: Repository) {
    writeRepository(repository, "repository")
  }

  private fun writePluginRepository(repository: Repository) {
    writeRepository(repository, "pluginRepository")
  }

  private fun writeReleaseRepositoryPolicy(repositoryPolicy: RepositoryPolicy?) {
    writeRepositoryPolicy(repositoryPolicy, "releases")
  }

  private fun writeSnapshotRepositoryPolicy(repositoryPolicy: RepositoryPolicy?) {
    writeRepositoryPolicy(repositoryPolicy, "snapshots")
  }

  private fun writeResource(resource: Resource) {
    writeResource(resource, "resource")
  }

  private fun writeTestResource(resource: Resource) {
    writeResource(resource, "testResource")
  }

  private fun <T> option(key: String, default: T, converter: (String) -> T): T {
    val value = options[key] ?: return default
    return converter(value.toString())
  }
}
