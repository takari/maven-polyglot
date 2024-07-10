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

public class AtomModelWithGuiceTest extends InjectedTestCase {

    @Inject
    @Named("${basedir}/src/test/poms")
    private File poms;

    public void testAtomModelWriter() throws Exception {
        File pom = new File(poms, "guice-pom.xml");
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

    void testMavenModelForCompleteness(Model model) {
        //
        // parent
        //
        assertEquals(
                "com.google.inject:guice-parent:3.0-SNAPSHOT",
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
        assertNull(model.getProperties().getProperty("org.testng.version"));
        assertEquals("1.3", model.getProperties().getProperty("guice.api.version"));
        assertEquals("true", model.getProperties().getProperty("guice.with.jarjar"));
        assertEquals("true", model.getProperties().getProperty("guice.with.no_aop"));
        assertEquals("UTF-8", model.getProperties().getProperty("project.build.sourceEncoding"));
        //
        // depMan
        //
        Dependency testNg = model.getDependencies().get(0);
        assertEquals("org.testng:testng:5.11", gav(testNg));
        assertEquals("jdk15", testNg.getClassifier());
        //
        // modules
        //
        assertEquals("core", model.getModules().get(0));
        assertEquals("extensions", model.getModules().get(1));
        //
        // plugins
        //
        Plugin p0 = model.getBuild().getPluginManagement().getPlugins().get(0);
        assertEquals("org.apache.maven.plugins:maven-remote-resources-plugin:1.1", gav(p0));

        p0 = model.getBuild().getPluginManagement().getPlugins().get(1);
        assertEquals("org.apache.maven.plugins:maven-compiler-plugin:2.3.2", gav(p0));
        assertNull(p0.getConfiguration());

        p0 = model.getBuild().getPluginManagement().getPlugins().get(2);
        assertEquals("org.codehaus.mojo:animal-sniffer-maven-plugin:1.6", gav(p0));
        assertEquals(3, ((Xpp3Dom) p0.getConfiguration()).getChild("signature").getChildCount());
    }

    String gav(Dependency d) {
        return d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion();
    }

    String gav(Plugin p) {
        return p.getGroupId() + ":" + p.getArtifactId() + ":" + p.getVersion();
    }
}
