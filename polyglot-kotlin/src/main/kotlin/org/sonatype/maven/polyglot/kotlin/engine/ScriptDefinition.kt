package org.sonatype.maven.polyglot.kotlin.engine

import kotlin.script.experimental.api.ScriptAcceptedLocation
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.acceptedLocations
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import org.apache.maven.artifact.Artifact
import org.apache.maven.model.Model
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import org.apache.maven.settings.Settings
import org.codehaus.plexus.util.xml.Xpp3DomBuilder
import org.sonatype.maven.polyglot.execute.ExecuteContext

object ScriptDefinition :
    ScriptCompilationConfiguration({
      jvm {
        dependenciesFromClassContext(PomKtsScript::class, "polyglot-kotlin") // needed for DSL
        dependenciesFromClassContext(
            ExecuteContext::class, "polyglot-common") // needed for executing tasks
        dependenciesFromClassContext(Model::class, "maven-model") // needed for maven model
        dependenciesFromClassContext(
            MavenProject::class, "maven-core") // needed for maven project/session
        dependenciesFromClassContext(
            Artifact::class, "maven-artifact") // needed for maven artifacts
        dependenciesFromClassContext(
            Settings::class, "maven-settings") // needed for accessing settings
        dependenciesFromClassContext(
            Xpp3DomBuilder::class, "plexus-utils") // needed for Xpp3DomBuilder
        dependenciesFromClassContext(
            Log::class, "maven-plugin-api") // Needed for writing to the Maven build log
      }
      ide { acceptedLocations(ScriptAcceptedLocation.Everywhere) }
    })
