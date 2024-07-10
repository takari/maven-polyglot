/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala

import java.io.Writer
import java.util
import org.sonatype.maven.polyglot.io.ModelWriterSupport
import org.bitbucket.inkytonik.kiama.output.PrettyPrinter
import org.apache.maven.model.{
  Activation => MavenActivation,
  ActivationFile => MavenActivationFile,
  ActivationOS => MavenActivationOS,
  ActivationProperty => MavenActivationProperty,
  Build => MavenBuild,
  BuildBase => MavenBuildBase,
  CiManagement => MavenCiManagement,
  Contributor => MavenContributor,
  DependencyManagement => MavenDependencyManagement,
  Dependency => MavenDependency,
  DeploymentRepository => MavenDeploymentRepository,
  Developer => MavenDeveloper,
  DistributionManagement => MavenDistributionManagement,
  PluginExecution => MavenExecution,
  Extension => MavenExtension,
  IssueManagement => MavenIssueManagement,
  License => MavenLicense,
  MailingList => MavenMailingList,
  Model => MavenModel,
  Notifier => MavenNotifier,
  Organization => MavenOrganization,
  Parent => MavenParent,
  Plugin => MavenPlugin,
  PluginManagement => MavenPluginManagement,
  Prerequisites => MavenPrerequisites,
  Profile => MavenProfile,
  Relocation => MavenRelocation,
  RepositoryPolicy => MavenRepositoryPolicy,
  Repository => MavenRepository,
  Resource => MavenResource,
  Scm => MavenScm,
  Site => MavenSite,
  Reporting => MavenReporting,
  ReportPlugin => MavenReportPlugin,
  ReportSet => MavenReportSet
}
import org.sonatype.maven.polyglot.scala.model._
import scala.collection.immutable
import scala.language.implicitConversions
import javax.inject.Named

/**
 * Responsible for printing Scala source.
 */
object ScalaPrettyPrinter extends PrettyPrinter {
  override val defaultIndent = 2

  // Various doc functions that enhance doc construction for our domain.

  /**
   * Surround `d` with a tripple-double-quote if it contains a '"' or '\n' or '\n', else with a double-quote.
   * This is named `dquotes` to shadow `quotes(d: Doc): Doc` from PrettyPrinterBase.
   */
  def dquotes(d: String): Doc =
    if (d.matches(".*[\"\\n\\r].*")) surround(d, dquote <> dquote <> dquote)
    else surround(d, dquote)

  def assignString(label: String, value: String): Doc = label <+> equal <+> dquotes(value)

  def assign(label: String, value: Doc): Doc = label <+> equal <+> value

  def `object`(name: String, args: immutable.Seq[Doc]): Doc = {
    name <> lparen <> nest(lsep(args, comma)) <@> rparen
  }

  def mapStrings(m: Map[String, String]): Doc = {
    "Map" <> lparen <> nest(lsep(
      m.map(me => dquotes(me._1) <+> "->" <+> dquotes(me._2)).toSeq,
      comma
    )) <@> rparen
  }

  def seqString(s: immutable.Seq[String]): Doc =
    "Seq" <> lparen <> nest(lsep(s.map(dquotes(_)), comma)) <@> rparen

  def seq(s: immutable.Seq[Doc]): Doc = "Seq" <> lparen <> nest(lsep(s, comma)) <@> rparen

  implicit def toDoc(f: File): Doc = {
    "new" <+> "File" <> lparen <> dquote <> f.getCanonicalPath <> dquote <> rparen
  }

  // Implicit conversions for prettifying

  implicit def enrichPrettiedActivation(v: Activation): PrettiedActivation =
    new PrettiedActivation(v)

  implicit def enrichPrettiedActivationFile(v: ActivationFile): PrettiedActivationFile =
    new PrettiedActivationFile(v)

  implicit def enrichPrettiedActivationOS(v: ActivationOS): PrettiedActivationOS =
    new PrettiedActivationOS(v)

  implicit def enrichPrettiedActivationProperty(v: ActivationProperty): PrettiedActivationProperty =
    new PrettiedActivationProperty(v)

  implicit def enrichPrettiedBuild(v: Build): PrettiedBuild =
    new PrettiedBuild(v)

  implicit def enrichPrettiedBuildBase(v: BuildBase): PrettiedBuildBase =
    new PrettiedBuildBase(v)

  implicit def enrichPrettiedCiManagement(v: CiManagement): PrettiedCiManagement =
    new PrettiedCiManagement(v)

  implicit def enrichPrettiedConfig(v: Config): PrettiedConfig =
    new PrettiedConfig(v)

  implicit def enrichPrettiedConfigurationContainer(v: ConfigurationContainer)
      : PrettiedConfigurationContainer =
    new PrettiedConfigurationContainer(v)

  implicit def enrichPrettiedContributor(v: Contributor): PrettiedContributor =
    new PrettiedContributor(v)

  implicit def enrichPrettiedDependency(v: Dependency): PrettiedDependency =
    new PrettiedDependency(v)

  implicit def enrichPrettiedDependencyManagement(v: DependencyManagement)
      : PrettiedDependencyManagement =
    new PrettiedDependencyManagement(v)

  implicit def enrichPrettiedDeploymentRepository(v: DeploymentRepository)
      : PrettiedDeploymentRepository =
    new PrettiedDeploymentRepository(v)

  implicit def enrichPrettiedDeveloper(v: Developer): PrettiedDeveloper =
    new PrettiedDeveloper(v)

  implicit def enrichPrettiedDistributionManagement(v: DistributionManagement)
      : PrettiedDistributionManagement =
    new PrettiedDistributionManagement(v)

  implicit def enrichPrettiedExecution(v: Execution): PrettiedExecution =
    new PrettiedExecution(v)

  implicit def enrichPrettiedExtension(v: Extension): PrettiedExtension =
    new PrettiedExtension(v)

  implicit def enrichPrettiedGav(v: Gav): PrettiedGav =
    new PrettiedGav(v)

  implicit def enrichPrettiedGroupArtifactId(v: GroupArtifactId): PrettiedGroupArtifactId =
    new PrettiedGroupArtifactId(v)

  implicit def enrichPrettiedIssueManagement(v: IssueManagement): PrettiedIssueManagement =
    new PrettiedIssueManagement(v)

  implicit def enrichPrettiedLicense(v: License): PrettiedLicense =
    new PrettiedLicense(v)

  implicit def enrichPrettiedMailingList(v: MailingList): PrettiedMailingList =
    new PrettiedMailingList(v)

  implicit def enrichPrettiedModel(v: Model): PrettiedModel =
    new PrettiedModel(v)

  implicit def enrichPrettiedModelBase(v: ModelBase): PrettiedModelBase =
    new PrettiedModelBase(v)

  implicit def enrichPrettiedNotifier(v: Notifier): PrettiedNotifier =
    new PrettiedNotifier(v)

  implicit def enrichPrettiedOrganization(v: Organization): PrettiedOrganization =
    new PrettiedOrganization(v)

  implicit def enrichPrettiedParent(v: Parent): PrettiedParent =
    new PrettiedParent(v)

  implicit def enrichPrettiedPlugin(v: Plugin): PrettiedPlugin =
    new PrettiedPlugin(v)

  implicit def enrichPrettiedPluginContainer(v: PluginContainer): PrettiedPluginContainer =
    new PrettiedPluginContainer(v)

  implicit def enrichPrettiedPluginManagement(v: PluginManagement): PrettiedPluginManagement =
    new PrettiedPluginManagement(v)

  implicit def enrichPrettiedPrerequisites(v: Prerequisites): PrettiedPrerequisites =
    new PrettiedPrerequisites(v)

  implicit def enrichPrettiedProfile(v: Profile): PrettiedProfile =
    new PrettiedProfile(v)

  implicit def enrichPrettiedProperties(v: Map[String, String]): PrettiedProperties =
    new PrettiedProperties(v)

  implicit def enrichPrettiedRelocation(v: Relocation): PrettiedRelocation =
    new PrettiedRelocation(v)

  implicit def enrichPrettiedRepositoryPolicy(v: RepositoryPolicy): PrettiedRepositoryPolicy =
    new PrettiedRepositoryPolicy(v)

  implicit def enrichPrettiedRepository(v: Repository): PrettiedRepository =
    new PrettiedRepository(v)

  implicit def enrichPrettiedResource(v: Resource): PrettiedResource =
    new PrettiedResource(v)

  implicit def enrichPrettiedScm(v: Scm): PrettiedScm =
    new PrettiedScm(v)

  implicit def enrichPrettiedSite(v: Site): PrettiedSite =
    new PrettiedSite(v)

  implicit def enrichPrettiedTask(v: Task): PrettiedTask =
    new PrettiedTask(v)

  implicit def enrichPrettiedReporting(v: Reporting): PrettiedReporting =
    new PrettiedReporting(v)

  implicit def enrichPrettiedReportPlugin(v: ReportPlugin): PrettiedReportPlugin =
    new PrettiedReportPlugin(v)

  implicit def enrichPrettiedReportSet(v: ReportSet): PrettiedReportSet =
    new PrettiedReportSet(v)

}

/**
 * implicit conversions around the "pimp my library" approach for converting Maven models to their Scala types.
 */
object MavenConverters {
  implicit def enrichConvertibleActivation(v: MavenActivation): ConvertibleMavenActivation =
    new ConvertibleMavenActivation(v)

  implicit def enrichConvertibleActivationFile(v: MavenActivationFile)
      : ConvertibleMavenActivationFile =
    new ConvertibleMavenActivationFile(v)

  implicit def enrichConvertibleActivationOS(v: MavenActivationOS): ConvertibleMavenActivationOS =
    new ConvertibleMavenActivationOS(v)

  implicit def enrichConvertibleActivationProperty(v: MavenActivationProperty)
      : ConvertibleMavenActivationProperty =
    new ConvertibleMavenActivationProperty(v)

  implicit def enrichConvertibleBuild(v: MavenBuild): ConvertibleMavenBuild =
    new ConvertibleMavenBuild(v)

  implicit def enrichConvertibleBuildBase(v: MavenBuildBase): ConvertibleMavenBuildBase =
    new ConvertibleMavenBuildBase(v)

  implicit def enrichConvertibleCiManagement(v: MavenCiManagement): ConvertibleMavenCiManagement =
    new ConvertibleMavenCiManagement(v)

  implicit def enrichConvertibleConfig(v: Object): ConvertibleMavenConfig =
    new ConvertibleMavenConfig(v)

  implicit def enrichConvertibleContributor(v: MavenContributor): ConvertibleMavenContributor =
    new ConvertibleMavenContributor(v)

  implicit def enrichConvertibleDependency(v: MavenDependency): ConvertibleMavenDependency =
    new ConvertibleMavenDependency(v)

  implicit def enrichConvertibleDependencyManagement(v: MavenDependencyManagement)
      : ConvertibleMavenDependencyManagement =
    new ConvertibleMavenDependencyManagement(v)

  implicit def enrichConvertibleDeploymentRepository(v: MavenDeploymentRepository)
      : ConvertibleMavenDeploymentRepository =
    new ConvertibleMavenDeploymentRepository(v)

  implicit def enrichConvertibleDeveloper(v: MavenDeveloper): ConvertibleMavenDeveloper =
    new ConvertibleMavenDeveloper(v)

  implicit def enrichConvertibleDistributionManagement(v: MavenDistributionManagement)
      : ConvertibleMavenDistributionManagement =
    new ConvertibleMavenDistributionManagement(v)

  implicit def enrichConvertibleExecution(v: MavenExecution): ConvertibleMavenExecution =
    new ConvertibleMavenExecution(v)

  implicit def enrichConvertibleExtension(v: MavenExtension): ConvertibleMavenExtension =
    new ConvertibleMavenExtension(v)

  implicit def enrichConvertibleGav(v: (String, String, String)): ConvertibleMavenGav =
    new ConvertibleMavenGav(v)

  implicit def enrichConvertibleGroupArtifactId(v: (String, String))
      : ConvertibleMavenGroupArtifactId =
    new ConvertibleMavenGroupArtifactId(v)

  implicit def enrichConvertibleIssueManagement(v: MavenIssueManagement)
      : ConvertibleMavenIssueManagement =
    new ConvertibleMavenIssueManagement(v)

  implicit def enrichConvertibleLicense(v: MavenLicense): ConvertibleMavenLicense =
    new ConvertibleMavenLicense(v)

  implicit def enrichConvertibleMailingList(v: MavenMailingList): ConvertibleMavenMailingList =
    new ConvertibleMavenMailingList(v)

  implicit def enrichConvertibleModel(v: MavenModel): ConvertibleMavenModel =
    new ConvertibleMavenModel(v)

  implicit def enrichConvertibleNotifier(v: MavenNotifier): ConvertibleMavenNotifier =
    new ConvertibleMavenNotifier(v)

  implicit def enrichConvertibleOrganization(v: MavenOrganization): ConvertibleMavenOrganization =
    new ConvertibleMavenOrganization(v)

  implicit def enrichConvertibleParent(v: MavenParent): ConvertibleMavenParent =
    new ConvertibleMavenParent(v)

  implicit def enrichConvertiblePlugin(v: MavenPlugin): ConvertibleMavenPlugin =
    new ConvertibleMavenPlugin(v)

  implicit def enrichConvertiblePluginManagement(v: MavenPluginManagement)
      : ConvertibleMavenPluginManagement =
    new ConvertibleMavenPluginManagement(v)

  implicit def enrichConvertiblePrerequisites(v: MavenPrerequisites)
      : ConvertibleMavenPrerequisites =
    new ConvertibleMavenPrerequisites(v)

  implicit def enrichConvertibleProfile(v: MavenProfile): ConvertibleMavenProfile =
    new ConvertibleMavenProfile(v)

  implicit def enrichConvertibleRelocation(v: MavenRelocation): ConvertibleMavenRelocation =
    new ConvertibleMavenRelocation(v)

  implicit def enrichConvertibleRepositoryPolicy(v: MavenRepositoryPolicy)
      : ConvertibleMavenRepositoryPolicy =
    new ConvertibleMavenRepositoryPolicy(v)

  implicit def enrichConvertibleRepository(v: MavenRepository): ConvertibleMavenRepository =
    new ConvertibleMavenRepository(v)

  implicit def enrichConvertibleResource(v: MavenResource): ConvertibleMavenResource =
    new ConvertibleMavenResource(v)

  implicit def enrichConvertibleScm(v: MavenScm): ConvertibleMavenScm =
    new ConvertibleMavenScm(v)

  implicit def enrichConvertibleSite(v: MavenSite): ConvertibleMavenSite =
    new ConvertibleMavenSite(v)

  implicit def enrichConvertibleReporting(v: MavenReporting): ConvertibleMavenReporting =
    new ConvertibleMavenReporting(v)

  implicit def enrichConvertibleReportPlugin(v: MavenReportPlugin): ConvertibleMavenReportPlugin =
    new ConvertibleMavenReportPlugin(v)

  implicit def enrichConvertibleReportSet(v: MavenReportSet): ConvertibleMavenReportSet =
    new ConvertibleMavenReportSet(v)
}

/**
 * Convert a Maven Model to a Scala Model in source code form.
 */
@Named("scala")
class ScalaModelWriter extends ModelWriterSupport {

  def write(writer: Writer, options: util.Map[String, AnyRef], mm: MavenModel): Unit = {
    import MavenConverters._
    import ScalaPrettyPrinter._

    if (mm.getModelEncoding() == null) {
      // A null-value might be the result of parsing the model from a file with a missing '<?xml version="1.0" encoding="UTF-8"?>' header
      // But in this case we will trigger a NPE inside of PrettyPrinter, which is really bad
      // Thus, we restore to non-null default value
      mm.setModelEncoding("UTF-8")
    }

    val d = "import" <+> "org.sonatype.maven.polyglot.scala.model._" <@>
      "import" <+> "scala.collection.immutable.Seq" <@>
      emptyDoc <@>
      mm.asScala.asDoc <@>
      emptyDoc
    val pp = ScalaPrettyPrinter.pretty(d)
    writer.append(pp.layout)
    writer.flush()
  }

}
