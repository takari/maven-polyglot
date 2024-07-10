/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala.model

import scala.collection.immutable

/**
 * A ScalaModel provides a convenient construction of a regular Model that is configured to support
 * Scala projects. Configuration includes setting up the source and test source directories (src/main/scala,
 * src/test/scala respectively), configuration of m2e's lifecycle, the maven compiler for Java is disabled, the
 * Scala compiler is enabled and surefire is configured to look for *Spec.* files. The scala-library is also
 * added as a dependency and requires that a ScalaVersion declaration is made implicitly. Thus the minumum
 * Scala project declaration becomes something like:
 * {{{
 *   import org.sonatype.maven.polyglot.scala.model._
 *
 *   implicit val scalaVersion = ScalaVersion("2.11.6")
 *   ScalaModel("somegroup" % "someartifact" % "somever")
 * }}}
 */
object ScalaModel {

  val sourceDirectory = "src/main/scala"
  val testSourceDirectory = "src/test/scala"

  val lifeCycleMapping = Plugin(
    "org.eclipse.m2e" % "lifecycle-mapping" % "1.0.0",
    configuration = Config(
      lifecycleMappingMetadata = Config(
        pluginExecutions = Config(
          pluginExecution = Config(
            pluginExecutionFilter = Config(
              groupId = "net.alchim31.maven",
              artifactId = "scala-maven-plugin",
              versionRange = "[3.2.0,)",
              goals = Config(
                goal = "add-source",
                goal = "compile",
                goal = "testCompile"
              )
            ),
            action = Config(
              ignore = None
            )
          )
        )
      )
    )
  )

  val mavenCompiler = Plugin(
    "org.apache.maven.plugins" % "maven-compiler-plugin" % "3.2",
    executions = immutable.Seq(
      Execution(
        id = "default-compile",
        phase = "none"
      )
    )
  )

  val scalaCompiler = Plugin(
    "net.alchim31.maven" % "scala-maven-plugin" % "3.2.0",
    executions = immutable.Seq(
      Execution(
        goals = immutable.Seq(
          "compile",
          "testCompile"
        ),
        configuration = Config(
          args = Config(
            arg = "-deprecation"
          ),
          checkMultipleScalaVersions = "false",
          recompileMode = "incremental"
        )
      )
    )
  )

  val surefire = Plugin(
    "org.apache.maven.plugins" % "maven-surefire-plugin" % "2.18.1",
    configuration = Config(
      includes = Config(
        include = "%regex[.*Spec.*]"
      )
    )
  )

  def apply(
      gav: Gav,
      build: Build = null,
      ciManagement: CiManagement = null,
      contributors: immutable.Seq[Contributor] = Nil,
      dependencyManagement: DependencyManagement = null,
      dependencies: immutable.Seq[Dependency] = Nil,
      description: String = null,
      developers: immutable.Seq[Developer] = Nil,
      distributionManagement: DistributionManagement = null,
      inceptionYear: String = null,
      issueManagement: IssueManagement = null,
      licenses: immutable.Seq[License] = Nil,
      mailingLists: immutable.Seq[MailingList] = Nil,
      modelEncoding: String = "UTF-8",
      modelVersion: String = "4.0.0",
      modules: immutable.Seq[String] = Nil,
      name: String = null,
      organization: Organization = null,
      packaging: String = "jar",
      parent: Parent = null,
      pluginRepositories: immutable.Seq[Repository] = Nil,
      pomFile: File = null,
      prerequisites: Prerequisites = null,
      profiles: immutable.Seq[Profile] = Nil,
      properties: Map[String, String] = Map.empty,
      reporting: Reporting = null,
      repositories: immutable.Seq[Repository] = Nil,
      scm: Scm = null,
      url: String = null
  )(implicit scalaVersion: ScalaVersion) = {

    val scalaLang = "org.scala-lang" % "scala-library" % scalaVersion.version
    val scalaLangIncluded = dependencies.exists {
      d => d.gav.groupId == scalaLang.groupId && d.gav.artifactId == scalaLang.artifactId
    }
    val targetDependencies =
      if (scalaLangIncluded) dependencies else dependencies :+ Dependency(scalaLang)

    val targetBuild = Option(build).map({
      b =>
        val targetSourceDirectory =
          if (b.sourceDirectory.isEmpty) Some(sourceDirectory)
          else b.sourceDirectory

        val targetTestSourceDirectory =
          if (b.testSourceDirectory.isEmpty) Some(testSourceDirectory)
          else b.testSourceDirectory

        val pluginManagement = b.pluginManagement.getOrElse(PluginManagement())
        val lifeCycleMappingIncluded = pluginManagement.plugins.exists {
          p =>
            p.gav.groupId == lifeCycleMapping.gav.groupId && p.gav.artifactId == lifeCycleMapping.gav.artifactId
        }
        val targetPluginManagementPlugins =
          if (lifeCycleMappingIncluded) pluginManagement.plugins
          else pluginManagement.plugins :+ lifeCycleMapping
        val targetPluginManagement = Some(new PluginManagement(targetPluginManagementPlugins))

        val mavenCompilerIncluded = b.plugins.exists {
          p =>
            p.gav.groupId == mavenCompiler.gav.groupId && p.gav.artifactId == mavenCompiler.gav.artifactId
        }
        val targetMavenCompiler =
          if (mavenCompilerIncluded) None
          else Some(mavenCompiler)
        val scalaCompilerIncluded = b.plugins.exists {
          p =>
            p.gav.groupId == scalaCompiler.gav.groupId && p.gav.artifactId == scalaCompiler.gav.artifactId
        }
        val targetScalaCompiler =
          if (scalaCompilerIncluded) None
          else Some(scalaCompiler)
        val surefireIncluded = b.plugins.exists {
          p => p.gav.groupId == surefire.gav.groupId && p.gav.artifactId == surefire.gav.artifactId
        }
        val targetSurefire =
          if (surefireIncluded) None
          else Some(surefire)
        val targetPlugins = b.plugins ++: immutable.Seq(
          targetMavenCompiler,
          targetScalaCompiler,
          targetSurefire
        ).flatten

        new Build(
          targetSourceDirectory,
          b.scriptSourceDirectory,
          targetTestSourceDirectory,
          b.outputDirectory,
          b.testOutputDirectory,
          b.extensions,
          b.defaultGoal,
          b.resources,
          b.testResources,
          b.directory,
          b.finalName,
          b.filters,
          targetPluginManagement,
          targetPlugins,
          b.tasks
        )
    }).getOrElse(
      Build(
        sourceDirectory = sourceDirectory,
        testSourceDirectory = testSourceDirectory,
        pluginManagement = PluginManagement(immutable.Seq(lifeCycleMapping)),
        plugins = immutable.Seq(mavenCompiler, scalaCompiler, surefire)
      )
    )

    new Model(
      gav,
      Some(targetBuild),
      Option(ciManagement),
      contributors,
      Option(dependencyManagement),
      targetDependencies,
      Option(description),
      developers,
      Option(distributionManagement),
      Option(inceptionYear),
      Option(issueManagement),
      licenses,
      mailingLists,
      modelEncoding,
      Option(modelVersion),
      modules,
      Option(name),
      Option(organization),
      packaging,
      Option(parent),
      pluginRepositories,
      Option(pomFile),
      Option(prerequisites),
      profiles,
      properties,
      Option(reporting),
      repositories,
      Option(scm),
      Option(url)
    )
  }
}
