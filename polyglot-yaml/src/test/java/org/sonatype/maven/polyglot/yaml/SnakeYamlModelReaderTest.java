/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.*;
import org.apache.maven.model.io.ModelWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Test;

public class SnakeYamlModelReaderTest {
    @Test
    public void testModelCloning() throws Exception {
        getModel().clone();
    }

    @Test
    public void testModelReader() throws Exception {
        Model model = getModel();
        assertNotNull(model);

        Parent parent = model.getParent();
        assertNull(parent);

        assertEquals("org.yaml", model.getGroupId());
        assertEquals("snakeyaml", model.getArtifactId());
        assertEquals("1.17-SNAPSHOT", model.getVersion());
        assertEquals("SnakeYAML", model.getName());

        // Developers
        List<Developer> developers = model.getDevelopers();
        Developer dev0 = developers.get(0);
        assertEquals("asomov", dev0.getId());
        assertEquals("Andrey Somov", dev0.getName());

        // Contributors
        List<Contributor> contributors = model.getContributors();
        assertEquals(0, contributors.size());

        // Dependencies
        List<Dependency> dependencies = model.getDependencies();
        assertEquals(4, dependencies.size());

        Dependency d0 = dependencies.get(0);
        assertEquals("junit", d0.getGroupId());
        assertEquals("junit", d0.getArtifactId());
        assertEquals("4.12", d0.getVersion());

        Dependency d1 = dependencies.get(1);
        assertEquals("org.springframework", d1.getGroupId());
        assertEquals("spring", d1.getArtifactId());
        assertEquals("2.5.6", d1.getVersion());

        // Modules
        List<String> modules = model.getModules();
        assertEquals(0, modules.size());

        // Build
        Build build = model.getBuild();
        List<Plugin> plugins = build.getPlugins();
        assertEquals(10, plugins.size());
        Plugin p0 = plugins.get(0);
        assertEquals("org.apache.maven.plugins", p0.getGroupId());
        assertEquals("maven-compiler-plugin", p0.getArtifactId());
        assertEquals("3.3", p0.getVersion());
        Xpp3Dom configuration = (Xpp3Dom) p0.getConfiguration();
        assertNotNull(configuration);
        assertEquals(3, configuration.getChildCount());
        assertEquals("1.5", configuration.getChild("source").getValue());
        assertEquals("1.5", configuration.getChild("target").getValue());

        // DistributionManagement
        DistributionManagement distMan = model.getDistributionManagement();
        Site site = distMan.getSite();
        assertNull(site);

        // SCM
        Scm scm = model.getScm();
        assertEquals("scm:hg:http://bitbucket.org/asomov/snakeyaml", scm.getConnection());
        assertEquals("scm:hg:https://bitbucket.org/asomov/snakeyaml", scm.getDeveloperConnection());
        assertEquals("https://bitbucket.org/asomov/snakeyaml/src", scm.getUrl());

        // IssueManagement
        IssueManagement issueManagement = model.getIssueManagement();
        assertEquals("Bitbucket", issueManagement.getSystem());
        assertEquals("https://bitbucket.org/asomov/snakeyaml/issues", issueManagement.getUrl());

        // CiManagement
        CiManagement ciManagement = model.getCiManagement();
        assertNull(ciManagement);
    }

    @Test
    public void testModelWriter() throws Exception {
        StringWriter sw = new StringWriter();
        ModelWriter writer = new YamlModelWriter();
        Model model = getModel();
        Properties p = new Properties();
        p.setProperty("FOO", "BAR");
        model.setProperties(p);
        writer.write(sw, null, model);
        String output = sw.toString();
        // System.out.println(output);
        assertTrue(output, output.contains("\nproperties: {FOO: BAR}\n"));
        assertFalse("Null values should be printed.", output.contains("reporting: null"));
        assertFalse("Empty maps should be printed.", output.contains("properties: {}"));
        assertFalse("Empty lists should be printed.", output.contains("extensions: []"));
        assertFalse("AsMap should be printed.", output.contains("AsMap"));
        assertFalse("getModelEncoding should be printed.", output.contains("getModelEncoding"));

        String expected = Util.getLocalResource("snakeyaml/generated-pom.yaml");
        // assertEquals(expected.trim(), output.trim());

        YamlModelReader modelReader = new YamlModelReader();
        InputStream stream = new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));

        Model model2 = modelReader.read(stream, null);
        assertNotNull(model2);
    }

    protected Model getModel() throws Exception {
        YamlModelReader modelReader = new YamlModelReader();
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("snakeyaml/pom.yaml");
        assertNotNull(input);
        return modelReader.read(input, null);
    }
}
