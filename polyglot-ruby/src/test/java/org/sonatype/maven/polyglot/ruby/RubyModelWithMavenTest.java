/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.ModelWriter;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.sisu.launch.InjectedTestCase;
import org.sonatype.maven.polyglot.execute.ExecuteManagerImpl;

public class RubyModelWithMavenTest extends InjectedTestCase {

    @Inject
    @Named("${basedir}/src/test/poms")
    private File poms;

    public void testRubyModelReader() throws Exception {
        File pom = new File(poms, "pom.rb");
        assertRubyModel(IOUtil.toString(new FileInputStream(pom)));
    }

    public void testRubyModelReaderXmlOutput() throws Exception {
        File pom = new File(poms, "pom.rb");

        //
        // Read in the Ruby POM
        //
        RubyModelReader rubyModelReader = new RubyModelReader();
        rubyModelReader.executeManager = new ExecuteManagerImpl();
        rubyModelReader.setupManager = new SetupClassRealm();

        FileReader r = new FileReader(pom);
        Model rubyModel = rubyModelReader.read(r, new HashMap<String, Object>());

        //
        // Write out the xml POM
        //

        MavenXpp3Writer writer = new MavenXpp3Writer();
        StringWriter w = new StringWriter();
        writer.write(w, rubyModel);

        // Let's take a look at see what's there
        // System.out.println(w.toString());

        File pomXml = new File(poms, "pom.xml");
        assertEquals(w.toString(), IOUtil.toString(new FileInputStream(pomXml)));
    }

    public void testRubyModelWriter() throws Exception {
        File pom = new File(poms, "maven-parent-pom.xml");
        MavenXpp3Reader xmlModelReader = new MavenXpp3Reader();
        Model xmlModel = xmlModelReader.read(new FileInputStream(pom));

        //
        // Write out the Ruby POM
        //
        ModelWriter writer = new RubyModelWriter();
        StringWriter w = new StringWriter();
        writer.write(w, new HashMap<String, Object>(), xmlModel);

        // Let's take a look at see what's there
        // System.out.println(w.toString());

        assertRubyModel(w.toString());
    }

    private void assertRubyModel(String rubyPom) throws IOException {

        //
        // Read in the Ruby POM
        //
        RubyModelReader rubyModelReader = new RubyModelReader();
        rubyModelReader.executeManager = new ExecuteManagerImpl();
        rubyModelReader.setupManager = new SetupClassRealm();

        StringReader r = new StringReader(rubyPom);
        Model rubyModel = rubyModelReader.read(r, new HashMap<String, Object>());
        //
        // Test for fidelity
        //
        assertNotNull(rubyModel);
        testMavenModelForCompleteness(rubyModel);
    }

    void testMavenModelForCompleteness(Model model) {
        //
        // name, url
        //
        assertEquals("Apache Maven", model.getName());
        assertEquals("http://maven.apache.org/", model.getUrl());
        //
        // gav
        //
        assertEquals(
                "org.apache.maven:maven:3.0.4-SNAPSHOT",
                model.getGroupId() + ":" + model.getArtifactId() + ":" + model.getVersion());
        //
        // packaging
        //
        assertEquals("pom", model.getPackaging());
        //
        // description
        //
        assertTrue(model.getDescription().length() > 200);

        //
        // repos
        //
        assertEquals(0, model.getRepositories().size());
        assertEquals(0, model.getPluginRepositories().size());
        //
        // parent
        //
        assertEquals(
                "org.eclipse.tesla:tesla:3",
                model.getParent().getGroupId()
                        + ":"
                        + model.getParent().getArtifactId()
                        + ":"
                        + model.getParent().getVersion());
        //
        // properties
        //
        assertEquals("1.7", model.getProperties().getProperty("gossipVersion"));
        assertEquals("1.12", model.getProperties().getProperty("aetherVersion"));
        assertEquals("4.8.2", model.getProperties().getProperty("junitVersion"));
        assertEquals("2.0.6", model.getProperties().getProperty("plexusUtilsVersion"));
        assertEquals("2.4", model.getProperties().getProperty("classWorldsVersion"));
        assertEquals("UTF-8", model.getProperties().getProperty("project.build.sourceEncoding"));
        assertEquals("yyyyMMddHHmm", model.getProperties().getProperty("maven.build.timestamp.format"));
        assertEquals("2.2.1", model.getProperties().getProperty("sisuInjectVersion"));
        assertEquals("1.4.1", model.getProperties().getProperty("modelloVersion"));
        assertEquals("Eclipse Tesla", model.getProperties().getProperty("distributionName"));
        assertEquals("1.5.5", model.getProperties().getProperty("plexusVersion"));
        assertEquals("3.0.4-SNAPSHOT", model.getProperties().getProperty("mavenVersion"));
        assertEquals("1.2_Java1.3", model.getProperties().getProperty("easyMockVersion"));
        assertEquals("2.3", model.getProperties().getProperty("jlineVersion"));
        assertEquals("1.3", model.getProperties().getProperty("jxpathVersion"));
        assertEquals("true", model.getProperties().getProperty("maven.test.redirectTestOutputToFile"));
        assertEquals("1.2", model.getProperties().getProperty("commonsCliVersion"));
        assertEquals("UTF-8", model.getProperties().getProperty("project.reporting.outputEncoding"));
        assertEquals("1.14", model.getProperties().getProperty("plexusInterpolationVersion"));
        assertEquals("Tesla", model.getProperties().getProperty("distributionShortName"));
        assertEquals("1.0-beta-7", model.getProperties().getProperty("wagonVersion"));
        assertEquals("${maven.build.timestamp}", model.getProperties().getProperty("build.timestamp"));
        assertEquals("1.3", model.getProperties().getProperty("securityDispatcherVersion"));
        assertEquals("eclipse-tesla", model.getProperties().getProperty("distributionId"));
        assertEquals("1.6.1", model.getProperties().getProperty("slf4jVersion"));
        assertEquals("1.7", model.getProperties().getProperty("cipherVersion"));
        assertEquals("3.0.4-SNAPSHOT", model.getProperties().getProperty("gshellVersion"));
        //
        // dependencies
        //
        assertEquals("junit:junit:${junitVersion}", gav(model.getDependencies().get(0)));
        assertEquals("test", model.getDependencies().get(0).getScope());
        //
        // dependencyManager
        //
        assertEquals(
                "org.apache.maven:maven-model:${project.version}",
                gav(model.getDependencyManagement().getDependencies().get(0)));
        assertEquals(
                "org.apache.maven:maven-core:${project.version}",
                gav(model.getDependencyManagement().getDependencies().get(5)));
        assertEquals(
                "org.sonatype.sisu:sisu-inject-plexus:${sisuInjectVersion}",
                gav(model.getDependencyManagement().getDependencies().get(12)));
        assertEquals(
                "org.slf4j:slf4j-simple:${slf4jVersion}",
                gav(model.getDependencyManagement().getDependencies().get(18)));
        assertEquals(
                "runtime",
                model.getDependencyManagement().getDependencies().get(18).getScope());
        assertEquals(
                "commons-cli:commons-cli:${commonsCliVersion}",
                gav(model.getDependencyManagement().getDependencies().get(31)));
        assertEquals(
                2,
                model.getDependencyManagement()
                        .getDependencies()
                        .get(31)
                        .getExclusions()
                        .size());
        assertEquals(
                "commons-lang:commons-lang",
                gav(model.getDependencyManagement()
                        .getDependencies()
                        .get(31)
                        .getExclusions()
                        .get(0)));
        assertEquals(
                "commons-logging:commons-logging",
                gav(model.getDependencyManagement()
                        .getDependencies()
                        .get(31)
                        .getExclusions()
                        .get(1)));
        assertEquals(
                "org.sonatype.jline:jline:${jlineVersion}",
                gav(model.getDependencyManagement().getDependencies().get(61)));
        assertEquals(
                "tests",
                model.getDependencyManagement().getDependencies().get(61).getClassifier());
        //
        // pluginManager
        //
        Plugin p = model.getBuild().getPluginManagement().getPlugins().get(0);
        assertEquals("org.codehaus.plexus:plexus-component-metadata:${plexusVersion}", gav(p));
        assertNull(p.getConfiguration());
        p = model.getBuild().getPluginManagement().getPlugins().get(6);
        assertEquals("org.apache.maven.plugins:maven-assembly-plugin:2.2-beta-5", gav(p));
        assertNull(p.getConfiguration());
        p = model.getBuild().getPluginManagement().getPlugins().get(1);
        assertEquals("org.apache.maven.plugins:maven-compiler-plugin:2.3.2", gav(p));
        assertNotNull(p.getConfiguration());
        assertEquals("1.5", ((Xpp3Dom) p.getConfiguration()).getChild("source").getValue());
        p = model.getBuild().getPluginManagement().getPlugins().get(4);
        assertEquals("org.codehaus.modello:modello-maven-plugin:${modelloVersion}", gav(p));
        assertNotNull(p.getConfiguration());
        assertEquals(
                "true", ((Xpp3Dom) p.getConfiguration()).getChild("useJava5").getValue());
        //
        // modules
        //
        assertEquals("maven-core", model.getModules().get(0));
        assertEquals("apache-maven", model.getModules().get(1));
        assertEquals("maven-model", model.getModules().get(2));
        assertEquals("maven-settings", model.getModules().get(3));
        assertEquals("maven-settings-builder", model.getModules().get(4));
        assertEquals("maven-artifact", model.getModules().get(5));
        assertEquals("maven-aether-provider", model.getModules().get(6));
        assertEquals("maven-repository-metadata", model.getModules().get(7));
        assertEquals("maven-plugin-api", model.getModules().get(8));
        assertEquals("maven-model-builder", model.getModules().get(9));
        assertEquals("maven-embedder", model.getModules().get(10));
        assertEquals("maven-compat", model.getModules().get(11));
        assertEquals("tesla-shell", model.getModules().get(12));
        assertEquals("tesla-polyglot", model.getModules().get(13));
        //
        // plugins
        //
        p = model.getBuild().getPlugins().get(0);
        assertEquals("org.codehaus.mojo:animal-sniffer-maven-plugin:1.6", gav(p));
        assertNotNull(p.getConfiguration());
        Xpp3Dom config = ((Xpp3Dom) p.getConfiguration()).getChild("signature");
        assertNull(config.getValue());
        assertEquals("org.codehaus.mojo.signature", config.getChild("groupId").getValue());
        assertEquals("java15", config.getChild("artifactId").getValue());
        assertEquals("1.0", config.getChild("version").getValue());
        assertEquals(3, config.getChildCount());
        assertEquals(1, p.getExecutions().size());
        PluginExecution e = p.getExecutions().get(0);
        assertEquals("check-java-1.5-compat", e.getId());
        assertEquals("process-classes", e.getPhase());
        List<String> goals = e.getGoals();
        assertEquals(1, goals.size());
        assertEquals("check", goals.get(0));

        p = model.getBuild().getPlugins().get(1);
        assertEquals("org.sonatype.plugins:sisu-maven-plugin:null", gav(p));
        assertNull(p.getConfiguration());
        assertEquals(1, p.getExecutions().size());
        e = p.getExecutions().get(0);
        assertEquals("default", e.getId());
        goals = e.getGoals();
        assertEquals(2, goals.size());
        assertEquals("main-index", goals.get(0));
        assertEquals("test-index", goals.get(1));

        p = model.getBuild().getPlugins().get(2);
        assertEquals("com.mycila.maven-license-plugin:maven-license-plugin:1.9.0", gav(p));
        assertNotNull(p.getConfiguration());
        assertEquals(
                "true", ((Xpp3Dom) p.getConfiguration()).getChild("aggregate").getValue());
        assertEquals(
                "true", ((Xpp3Dom) p.getConfiguration()).getChild("strictCheck").getValue());
        assertEquals(
                "false",
                ((Xpp3Dom) p.getConfiguration()).getChild("useDefaultExcludes").getValue());
        assertEquals(
                "${project.basedir}/header.txt",
                ((Xpp3Dom) p.getConfiguration()).getChild("header").getValue());

        config = ((Xpp3Dom) p.getConfiguration()).getChild("excludes");
        assertNull(config.getValue());
        assertEquals(1, config.getChildCount());
        config = config.getChild("exclude");
        assertNotNull(config);
        assertEquals("**/target/**", config.getValue());

        config = ((Xpp3Dom) p.getConfiguration()).getChild("includes");
        assertNull(config.getValue());
        Xpp3Dom child = config.getChild(0);
        assertEquals("include", child.getName());
        assertEquals("**/pom.xml", child.getValue());
        child = config.getChild(11);
        assertEquals("include", child.getName());
        assertEquals("**/*.css", child.getValue());
        assertEquals(12, config.getChildCount());

        config = ((Xpp3Dom) p.getConfiguration()).getChild("mapping");
        assertNull(config.getValue());
        assertEquals(3, config.getChildCount());
    }

    String gav(Dependency d) {
        return d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion();
    }

    String gav(Plugin p) {
        return p.getGroupId() + ":" + p.getArtifactId() + ":" + p.getVersion();
    }

    String gav(Exclusion e) {
        return e.getGroupId() + ":" + e.getArtifactId();
    }
}
