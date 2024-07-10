/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala

import scala.collection.immutable
import scala.language.implicitConversions

import com.twitter.io.StreamIO
import org.sonatype.maven.polyglot.scala.eval.Eval

import java.io._
import java.util

import javax.inject.{Named, Inject}

import org.apache.maven.model.io.ModelParseException
import org.apache.maven.model.io.ModelReader
import org.apache.maven.model.Model
import org.codehaus.plexus.util.FileUtils
import org.codehaus.plexus.util.IOUtil
import org.codehaus.plexus.util.io.RawInputStreamFacade
import org.sonatype.maven.polyglot.execute.{ExecuteContext, ExecuteTask, ExecuteManager}
import org.sonatype.maven.polyglot.PolyglotModelUtil
import org.sonatype.maven.polyglot.scala.model.{
  Activation => ScalaActivation,
  ActivationFile => ScalaActivationFile,
  ActivationOS => ScalaActivationOS,
  ActivationProperty => ScalaActivationProperty,
  Build => ScalaBuild,
  BuildBase => ScalaBuildBase,
  CiManagement => ScalaCiManagement,
  Config => ScalaConfig,
  Contributor => ScalaContributor,
  DependencyManagement => ScalaDependencyManagement,
  Dependency => ScalaDependency,
  DeploymentRepository => ScalaDeploymentRepository,
  Developer => ScalaDeveloper,
  DistributionManagement => ScalaDistributionManagement,
  Execution => ScalaExecution,
  Extension => ScalaExtension,
  IssueManagement => ScalaIssueManagement,
  License => ScalaLicense,
  MailingList => ScalaMailingList,
  Model => ScalaModel,
  Notifier => ScalaNotifier,
  Organization => ScalaOrganization,
  Parent => ScalaParent,
  Plugin => ScalaPlugin,
  PluginManagement => ScalaPluginManagement,
  Relocation => ScalaRelocation,
  RepositoryPolicy => ScalaRepositoryPolicy,
  Repository => ScalaRepository,
  Resource => ScalaResource,
  Scm => ScalaScm,
  Site => ScalaSite,
  _
}

/**
 * implicit conversions around the "pimp my library" approach for converting Scala models to their Maven types.
 */
object ScalaConverters {
  implicit def enrichScalaActivation(v: ScalaActivation): ConvertibleScalaActivation =
    new ConvertibleScalaActivation(v)

  implicit def enrichScalaActivationFile(v: ScalaActivationFile): ConvertibleScalaActivationFile =
    new ConvertibleScalaActivationFile(v)

  implicit def enrichScalaActivationOS(v: ScalaActivationOS): ConvertibleScalaActivationOS =
    new ConvertibleScalaActivationOS(v)

  implicit def enrichScalaActivationProperty(v: ScalaActivationProperty)
      : ConvertibleScalaActivationProperty =
    new ConvertibleScalaActivationProperty(v)

  implicit def enrichScalaBuild(v: ScalaBuild): ConvertibleScalaBuild =
    new ConvertibleScalaBuild(v)

  implicit def enrichScalaBuildBase(v: ScalaBuildBase): ConvertibleScalaBuildBase =
    new ConvertibleScalaBuildBase(v)

  implicit def enrichScalaCiManagement(v: ScalaCiManagement): ConvertibleScalaCiManagement =
    new ConvertibleScalaCiManagement(v)

  implicit def enrichScalaConfig(v: ScalaConfig): ConvertibleScalaConfig =
    new ConvertibleScalaConfig(v)

  implicit def enrichScalaContributor(v: ScalaContributor): ConvertibleScalaContributor =
    new ConvertibleScalaContributor(v)

  implicit def enrichScalaDependency(v: ScalaDependency): ConvertibleScalaDependency =
    new ConvertibleScalaDependency(v)

  implicit def enrichScalaDependencyManagement(v: ScalaDependencyManagement)
      : ConvertibleScalaDependencyManagement =
    new ConvertibleScalaDependencyManagement(v)

  implicit def enrichScalaDeploymentRepository(v: ScalaDeploymentRepository)
      : ConvertibleScalaDeploymentRepository =
    new ConvertibleScalaDeploymentRepository(v)

  implicit def enrichScalaDeveloper(v: ScalaDeveloper): ConvertibleScalaDeveloper =
    new ConvertibleScalaDeveloper(v)

  implicit def enrichScalaDistributionManagement(v: ScalaDistributionManagement)
      : ConvertibleScalaDistributionManagement =
    new ConvertibleScalaDistributionManagement(v)

  implicit def enrichScalaExecution(v: ScalaExecution): ConvertibleScalaExecution =
    new ConvertibleScalaExecution(v)

  implicit def enrichScalaExtension(v: ScalaExtension): ConvertibleScalaExtension =
    new ConvertibleScalaExtension(v)

  implicit def enrichScalaIssueManagement(v: ScalaIssueManagement)
      : ConvertibleScalaIssueManagement =
    new ConvertibleScalaIssueManagement(v)

  implicit def enrichScalaLicense(v: ScalaLicense): ConvertibleScalaLicense =
    new ConvertibleScalaLicense(v)

  implicit def enrichScalaMailingList(v: ScalaMailingList): ConvertibleScalaMailingList =
    new ConvertibleScalaMailingList(v)

  implicit def enrichScalaModel(v: ScalaModel): ConvertibleScalaModel =
    new ConvertibleScalaModel(v)

  implicit def enrichScalaNotifier(v: ScalaNotifier): ConvertibleScalaNotifier =
    new ConvertibleScalaNotifier(v)

  implicit def enrichScalaOrganization(v: ScalaOrganization): ConvertibleScalaOrganization =
    new ConvertibleScalaOrganization(v)

  implicit def enrichScalaParent(v: ScalaParent): ConvertibleScalaParent =
    new ConvertibleScalaParent(v)

  implicit def enrichScalaPlugin(v: ScalaPlugin): ConvertibleScalaPlugin =
    new ConvertibleScalaPlugin(v)

  implicit def enrichScalaPluginManagement(v: ScalaPluginManagement)
      : ConvertibleScalaPluginManagement =
    new ConvertibleScalaPluginManagement(v)

  implicit def enrichScalaPrerequisites(v: Prerequisites): ConvertibleScalaPrerequisites =
    new ConvertibleScalaPrerequisites(v)

  implicit def enrichScalaProfile(v: Profile): ConvertibleScalaProfile =
    new ConvertibleScalaProfile(v)

  implicit def enrichScalaReleasePolicy(v: ScalaRepositoryPolicy)
      : ConvertibleScalaRepositoryPolicy =
    new ConvertibleScalaRepositoryPolicy(v)

  implicit def enrichScalaRelocation(v: ScalaRelocation): ConvertibleScalaRelocation =
    new ConvertibleScalaRelocation(v)

  implicit def enrichScalaRepository(v: ScalaRepository): ConvertibleScalaRepository =
    new ConvertibleScalaRepository(v)

  implicit def enrichScalaResource(v: ScalaResource): ConvertibleScalaResource =
    new ConvertibleScalaResource(v)

  implicit def enrichScalaScm(v: ScalaScm): ConvertibleScalaScm =
    new ConvertibleScalaScm(v)

  implicit def enrichScalaSite(v: ScalaSite): ConvertibleScalaSite =
    new ConvertibleScalaSite(v)

  implicit def enrichScalaReporting(v: Reporting): ConvertibleScalaReporting =
    new ConvertibleScalaReporting(v)

  implicit def enrichScalaReportPlugin(v: ReportPlugin): ConvertibleScalaReportPlugin =
    new ConvertibleScalaReportPlugin(v)

  implicit def enrichScalaReportSet(v: ReportSet): ConvertibleScalaReportSet =
    new ConvertibleScalaReportSet(v)

}

/**
 * Reads a Scala model from a script and produces a Maven Model object.
 * All Scala evaluation is done with files so that checks can be made to determine whether compilation occurs.
 */
@Named("scala")
class ScalaModelReader @Inject() (executeManager: ExecuteManager) extends ModelReader {

  import org.sonatype.maven.polyglot.scala.ScalaConverters._

  override def read(reader: Reader, options: util.Map[String, _]): Model = {
    val evalPomFile = locateEvalPomFile(options)
    IOUtil.copy(reader, new FileOutputStream(evalPomFile))
    val sm = eval(evalPomFile, evalPomFile, options)
    val m = sm.asJava
    sm.build.map(b => registerExecutors(m, options, b.tasks))
    m
  }

  def read(input: InputStream, options: util.Map[String, _]): Model = {
    val evalPomFile = locateEvalPomFile(options)
    FileUtils.copyStreamToFile(new RawInputStreamFacade(input), evalPomFile)
    val sm = eval(evalPomFile, evalPomFile, options)
    val m = sm.asJava
    sm.build.map(b => registerExecutors(m, options, b.tasks))
    m
  }

  def read(input: File, options: util.Map[String, _]): Model = {
    val source = PolyglotModelUtil.getLocation(options)
    val evalPomFile = locateEvalPomFile(options)
    val sm = eval(evalPomFile, input, options).copy(pomFile = Some(input))
    val m = sm.asJava
    sm.build.map(b => registerExecutors(m, options, b.tasks))
    m
  }

  private def locateEvalPomFile(options: util.Map[String, _]): File = {
    val targetDir = Option(System.getProperty("polyglot.scala.outputdir")).getOrElse("target")
    val source = PolyglotModelUtil.getLocation(options)
    val binVersion =
      _root_.scala.util.Properties.versionNumberString.split("[.]").take(2).mkString(".")
    val evalTarget =
      new File(new File(source).getParent, targetDir + File.separator + "scalamodel_" + binVersion)
    evalTarget.mkdirs()
    new File(evalTarget, "pom.scala")
  }

  /**
   * We subclass the [[Eval]] class to customize the otherwise immutable prepocessors property.
   *
   * We provide an [[MvnIncludePreprocessor]] that resolves files and classes from an (externally) defined directory
   * and the the current classloader.
   */
  class MvnEval(target: Option[File], includeBaseDir: File) extends Eval(target) {

    /*
     * This is a preprocessor that can include files by requesting them from the given resolvers.
     *
     * This preprocessor support lines starting with: `//#include`.
     *
     * @example //#include file-name.scala
     *
     * Note that it is *not* recursive. Included files cannot have includes
     */
    class MvnIncludePreprocessor(resolvers: Seq[Resolver]) extends Preprocessor {
      def maximumRecursionDepth = 100

      def apply(code: String): String =
        apply(code, maximumRecursionDepth)

      def apply(code: String, maxDepth: Int): String = {
        val lines = code.linesIterator.map { line: String =>
          val tokens = line.trim.split(' ')
          if (tokens.length == 2 && tokens(0).equals("//#include")) {
            val path = tokens(1)
            resolvers find { resolver: Resolver =>
              resolver.resolvable(path)
            } match {
              case Some(r: Resolver) => {
                // recursively process includes
                if (maxDepth == 0) {
                  throw new IllegalStateException("Exceeded maximum recursion depth")
                } else {
                  apply(StreamIO.buffer(r.get(path)).toString, maxDepth - 1)
                }
              }
              case _ =>
                throw new IllegalStateException("No resolver could find '%s'".format(path))
            }
          } else {
            line
          }
        }
        lines.mkString("\n")
      }
    }

    /**
     * Preprocessors to run the code through before it is passed to the Scala compiler.
     */
    override protected lazy val preprocessors: Seq[Preprocessor] =
      Seq(
        new MvnIncludePreprocessor(
          Seq(
            new ClassScopedResolver(getClass),
            new FilesystemResolver(includeBaseDir)
          )
        )
      )
  }

  private def eval(
      evalPomFile: File,
      sourcePomFile: File,
      options: util.Map[String, _]
  ): ScalaModel = {
    val sourceFile = new File(PolyglotModelUtil.getLocation(options))
    // ensure, we always use the project base directory to resolve includes
    val includeBaseDir = sourceFile.getParentFile()
    val eval = new MvnEval(Some(evalPomFile.getParentFile), includeBaseDir)
    try {
      eval.apply[ScalaModel](sourcePomFile)
    } catch {
      case e: Eval.CompilerException =>
        // ModuleParseException is able to provide exact position (line nr., column nr.), so if later
        // versions of CompilerException make those information available, we should map them here (instead of zeros).
        // Currently, the information is only available as text in the exeception message.
        // Parsing it would be wasteful and possibly unstable.
        throw new ModelParseException(
          "Cannot compile pom file: " + sourceFile +
            "\nYou can run 'mvn -Deval.debug' to see the resolved scala file.",
          0,
          0,
          e
        )
      case e: Throwable =>
        throw new ModelParseException("Could not process pom file: " + sourceFile, 0, 0, e)
    }
  }

  private def registerExecutors(
      m: Model,
      options: util.Map[String, _],
      tasks: immutable.Seq[Task]
  ): Unit = {
    import scala.jdk.CollectionConverters._
    executeManager.register(m, tasks.map(new ScalaTask(_).asInstanceOf[ExecuteTask]).asJava)
    executeManager.install(m, options)
  }

}

/*
 * Contains all that is required to execute a block of Scala code.
 */
private class ScalaTask(t: Task) extends ExecuteTask {
  def getId: String = t.id

  def getPhase: String = t.phase

  def getProfileId: String = t.profileId.orNull

  def execute(ec: ExecuteContext): Unit = t.block(ec)
}
