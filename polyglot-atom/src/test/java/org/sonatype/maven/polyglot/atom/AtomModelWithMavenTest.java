/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.atom;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.io.ModelWriter;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.sisu.launch.InjectedTestCase;

public class AtomModelWithMavenTest extends InjectedTestCase {

    @Inject
    @Named("${basedir}/src/test/poms")
    private File poms;

    public void testAtomModelWriter() throws Exception {
        File pom = new File(poms, "maven-parent-pom.xml");
        MavenXpp3Reader xmlModelReader = new MavenXpp3Reader();
        Model xmlModel = xmlModelReader.read(new FileInputStream(pom));
        //
        // testMavenModelForCompleteness(xmlModel);
        //
        // Write out the Atom POM
        //
        ModelWriter writer = new AtomModelWriter();
        StringWriter w = new StringWriter();
        writer.write(w, new HashMap<String, Object>(), xmlModel);

        // Let's take a look at see what's there
        System.out.println(w.toString());

        //
        // Read in the Atom POM
        //
        ModelReader atomModelReader = new AtomModelReader();
        StringReader r = new StringReader(w.toString());
        Model atomModel = atomModelReader.read(r, new HashMap<String, Object>());
        //
        // Test for fidelity
        //
        assertNotNull(atomModel);
        testMavenModelForCompleteness(atomModel);
    }

    void testMavenModelForCompleteness(Model model) {
        //
        // repos
        //
        assertEquals(0, model.getRepositories().size());
        //
        // parent
        //
        assertEquals(
                "org.apache.maven:maven:3.0.4-SNAPSHOT",
                model.getGroupId() + ":" + model.getArtifactId() + ":" + model.getVersion());
        //
        // id
        //
        assertEquals(
                "org.eclipse.tesla:tesla:3",
                model.getParent().getGroupId() + ":" + model.getParent().getArtifactId() + ":"
                        + model.getParent().getVersion());
        //
        // properties
        //
        assertEquals("pom", model.getPackaging());
        assertNull(model.getProperties().getProperty("org.testng.version"));
        assertNull(model.getProperties().getProperty("org.mortbay.jetty.version"));
        assertEquals("1.7", model.getProperties().getProperty("gossipVersion"));
        assertEquals("1.12", model.getProperties().getProperty("aetherVersion"));
        assertEquals("4.8.2", model.getProperties().getProperty("junitVersion"));
        assertEquals("Eclipse Tesla", model.getProperties().getProperty("distributionName"));
        assertEquals("eclipse-tesla", model.getProperties().getProperty("distributionId"));
        //
        // depMan (overrides)
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
                "org.apache.maven.wagon:wagon-http-shared:${wagonVersion}",
                gav(model.getDependencyManagement().getDependencies().get(22)));
        assertEquals(
                "org.sonatype.gossip:gossip-core:${gossipVersion}",
                gav(model.getDependencyManagement().getDependencies().get(35)));
        //
        // modules
        //
        assertEquals("maven-core", model.getModules().get(0));
        assertEquals("maven-settings-builder", model.getModules().get(4));
        assertEquals("maven-aether-provider", model.getModules().get(6));
        //
        // plugins
        //
        Plugin p0 = model.getBuild().getPluginManagement().getPlugins().get(0);
        assertEquals("org.codehaus.plexus:plexus-component-metadata:${plexusVersion}", gav(p0));
        assertNull(p0.getConfiguration());

        p0 = model.getBuild().getPluginManagement().getPlugins().get(1);
        assertEquals("org.apache.maven.plugins:maven-compiler-plugin:2.3.2", gav(p0));
        assertEquals("1.5", ((Xpp3Dom) p0.getConfiguration()).getChild("source").getValue());
        assertEquals("1.5", ((Xpp3Dom) p0.getConfiguration()).getChild("target").getValue());
    }

    String gav(Dependency d) {
        return d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion();
    }

    String gav(Plugin p) {
        return p.getGroupId() + ":" + p.getArtifactId() + ":" + p.getVersion();
    }
}
