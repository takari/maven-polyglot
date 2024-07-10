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
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.sisu.launch.InjectedTestCase;

public class AtomModelWithSBTest extends InjectedTestCase {

    @Inject
    @Named("${basedir}/src/test/poms")
    private File poms;

    public void testAtomModelWriter() throws Exception {
        File pom = new File(poms, "sitebricks-parent-pom.xml");
        MavenXpp3Reader xmlModelReader = new MavenXpp3Reader();
        Model xmlModel = xmlModelReader.read(new FileInputStream(pom));
        //
        testMavenModelForCompleteness(xmlModel);
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

        w = new StringWriter();
        MavenXpp3Writer xmlWriter = new MavenXpp3Writer();
        xmlWriter.write(w, xmlModel);
        System.out.println(w.toString());

        assertEquals(xmlModel.getModules(), atomModel.getModules());
        assertEquals(
                xmlModel.getRepositories().size(), atomModel.getRepositories().size());
    }

    public void testAtomModelWriterWhereModelHasDependenciesWithNoVersions() throws Exception {
        File pom = new File(poms, "sitebricks-pom.xml");
        MavenXpp3Reader xmlModelReader = new MavenXpp3Reader();
        Model xmlModel = xmlModelReader.read(new FileInputStream(pom));

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
    }

    void testMavenModelForCompleteness(Model model) {
        //
        // repos
        //
        assertEquals(
                "http://scala-tools.org/repo-releases/",
                model.getRepositories().get(0).getUrl());
        //
        // parent
        //
        assertEquals(
                "com.google.sitebricks:sitebricks-parent:0.8.6-SNAPSHOT",
                model.getGroupId() + ":" + model.getArtifactId() + ":" + model.getVersion());
        //
        // id
        //
        //    assertEquals("org.sonatype.oss:oss-parent:6", model.getParent().getGroupId() + ":" +
        // model.getParent().getArtifactId() + ":" + model.getParent().getVersion());
        //
        // properties
        //
        //    assertEquals("pom", model.getPackaging());
        assertEquals("5.8", model.getProperties().getProperty("org.testng.version"));
        assertEquals("6.1.9", model.getProperties().getProperty("org.mortbay.jetty.version"));
        //
        // depMan
        //
        assertEquals(
                "com.google.sitebricks:sitebricks-converter:${project.version}",
                gav(model.getDependencyManagement().getDependencies().get(0)));
        assertEquals(
                "com.google.guava:guava:r09",
                gav(model.getDependencyManagement().getDependencies().get(5)));
        assertEquals(
                "org.freemarker:freemarker:2.3.10",
                gav(model.getDependencyManagement().getDependencies().get(12)));
        assertEquals(
                "saxpath:saxpath:1.0-FCS",
                gav(model.getDependencyManagement().getDependencies().get(22)));
        Dependency testNg = model.getDependencyManagement().getDependencies().get(35);
        assertEquals("org.testng:testng:${org.testng.version}", gav(testNg));
        assertEquals("jdk15", testNg.getClassifier());
        //
        // modules
        //
        assertEquals("sitebricks", model.getModules().get(0));
        assertEquals("sitebricks-acceptance-tests", model.getModules().get(4));
        assertEquals("slf4j", model.getModules().get(9));
        //
        // plugins
        //
        Plugin p0 = model.getBuild().getPlugins().get(0);
        assertEquals("org.apache.maven.plugins:maven-compiler-plugin:2.3.2", gav(p0));
        assertEquals("1.6", ((Xpp3Dom) p0.getConfiguration()).getChild("source").getValue());
        assertEquals("1.6", ((Xpp3Dom) p0.getConfiguration()).getChild("target").getValue());
    }

    String gav(Dependency d) {
        return d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion();
    }

    String gav(Plugin p) {
        return p.getGroupId() + ":" + p.getArtifactId() + ":" + p.getVersion();
    }
}
