/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.*;
import org.junit.Test;

public class CompactFormatTest {

    @Test
    public void testModelReader() throws Exception {
        Model model = getModel("different-dependencies.yaml");
        assertNotNull(model);

        Parent parent = model.getParent();
        assertNull(parent);

        assertEquals("org.yaml", model.getGroupId());
        assertEquals("snakeyaml", model.getArtifactId());
        assertEquals("1.17-SNAPSHOT", model.getVersion());
        assertEquals("SnakeYAML", model.getName());

        // Dependencies
        List<Dependency> dependencies = model.getDependencies();
        assertEquals(10, dependencies.size());

        Dependency d0 = dependencies.get(0);
        assertEquals("junit", d0.getGroupId());
        assertEquals("junit", d0.getArtifactId());
        assertEquals("4.12", d0.getVersion());

        Dependency d1 = dependencies.get(1);
        assertEquals("org.springframework", d1.getGroupId());
        assertEquals("spring", d1.getArtifactId());
        assertEquals("2.5.6", d1.getVersion());

        Dependency mega = dependencies.get(3);
        assertEquals("org.mega", mega.getGroupId());
        assertEquals("package1", mega.getArtifactId());
        assertEquals("1.0b", mega.getVersion());
        assertEquals(2, mega.getExclusions().size());

        Dependency joda = dependencies.get(4);
        assertEquals("joda-time", joda.getGroupId());
        assertEquals("joda-time", joda.getArtifactId());
        assertEquals("test", joda.getScope());
        assertEquals("1.6", joda.getVersion());

        Dependency commons = dependencies.get(5);
        assertEquals("commons-io", commons.getGroupId());
        assertEquals("commons-io", commons.getArtifactId());
        assertNull(commons.getScope());
        assertEquals("2.4-SNAPSHOT", commons.getVersion());

        Dependency httpclient = dependencies.get(6);
        assertEquals("org.apache.httpcomponents", httpclient.getGroupId());
        assertEquals("httpclient", httpclient.getArtifactId());
        assertEquals("runtime", httpclient.getScope());
        assertEquals("4.2.5", httpclient.getVersion());
        assertEquals(2, httpclient.getExclusions().size());

        Dependency jumbo = dependencies.get(7);
        assertEquals("jumbo", jumbo.getGroupId());
        assertEquals("humbo", jumbo.getArtifactId());
        assertEquals("system", jumbo.getScope());
        assertEquals("0.1", jumbo.getVersion());
        assertEquals(1, jumbo.getExclusions().size());

        Dependency redundantId = dependencies.get(8);
        assertEquals("org.jumbo", redundantId.getGroupId());
        assertEquals("humo", redundantId.getArtifactId());
        assertNull(redundantId.getScope());
        assertEquals("0.1alpha", redundantId.getVersion());
        assertEquals(0, redundantId.getExclusions().size());

        // log:log4me
        Dependency noVersion = dependencies.get(9);
        assertEquals("log", noVersion.getGroupId());
        assertEquals("log4me", noVersion.getArtifactId());
        assertNull(noVersion.getScope());
        assertNull(noVersion.getVersion());
    }

    @Test
    public void testColonInFlowContext() throws Exception {
        // support for colon in the flow context is improved
        // in SnakeYAML 2.0
        try {
            getModel("dependencies-colon-issue.yaml");
        } catch (Exception e) {
            String message = e.getMessage();
            assertTrue(
                    message,
                    message.contains(
                            "Unable to find property 'groupId:log4j' on class: org.apache.maven.model.Exclusion"));
        }
    }

    @Test
    public void testCompactParent() throws Exception {
        Model model = getModel("compact-parent.yaml");
        assertNotNull(model);
        Parent parent = model.getParent();
        // org.apache:father:1.0.0.0
        assertEquals("org.apache", parent.getGroupId());
        assertEquals("father", parent.getArtifactId());
        assertEquals("1.0.0.0", parent.getVersion());
    }

    @Test
    public void testCompactPomId() throws Exception {
        Model model = getModel("compact-pom-id.yaml");
        assertNotNull(model);
        assertEquals("org.yaml", model.getGroupId());
        assertEquals("snakeyaml", model.getArtifactId());
        assertEquals("1.17", model.getVersion());
    }

    @Test
    public void testCompactBuildExtensions() throws Exception {
        Model model = getModel("compact-build-extensions.yaml");
        assertNotNull(model);
        Build build = model.getBuild();
        List<Extension> extensions = build.getExtensions();
        assertEquals(1, extensions.size());
        Extension extension = extensions.get(0);
        // org.apache.maven.wagon:wagon-ssh-external:1.0-beta-2
        assertEquals("org.apache.maven.wagon", extension.getGroupId());
        assertEquals("wagon-ssh-external", extension.getArtifactId());
        assertEquals("1.0-beta-2", extension.getVersion());
    }

    @Test
    public void testCompactProperties() throws Exception {
        Model model = getModel("compact-properties.yaml");
        assertNotNull(model);
        Properties properties = model.getProperties();
        assertEquals("UTF-8", properties.get("project.build.sourceEncoding"));
        assertEquals("${project.build.directory}/surefire-reports", properties.get("sonar.junit.reportsPath"));
    }

    @Test
    public void testCompactPrerequisites() throws Exception {
        Model model = getModel("compact-prerequisites.yaml");
        assertNotNull(model);
        Prerequisites prerequisites = model.getPrerequisites();
        assertEquals("3.3.1", prerequisites.getMaven());
    }

    @Test
    public void testCompactExample() throws Exception {
        Model model = getModel("compact-example.yaml");
        assertNotNull(model);
    }

    @Test
    public void testCompactBuildPlugins() throws Exception {
        Model model = getModel("compact-build-plugins.yaml");
        assertNotNull(model);
        Build build = model.getBuild();
        List<Plugin> plugins = build.getPlugins();
        assertEquals(5, plugins.size());
        Plugin extension = plugins.get(0);
        // org.apache.maven.plugins:maven-compiler-plugin:2.3
        assertEquals("org.apache.maven.plugins", extension.getGroupId());
        assertEquals("maven-compiler-plugin", extension.getArtifactId());
        assertEquals("2.3", extension.getVersion());

        Plugin site = plugins.get(2);
        // org.apache.maven.plugins:maven-site-plugin:2.3.2
        assertEquals("org.apache.maven.plugins", site.getGroupId());
        assertEquals("maven-site-plugin", site.getArtifactId());
        assertEquals("2.3.2", site.getVersion());
        assertNotNull(site.getConfiguration());
    }

    @Test
    public void testCompactBuildPluginManagement() throws Exception {
        Model model = getModel("compact-build-plugin-management.yaml");
        assertNotNull(model);
        Build build = model.getBuild();
        List<Plugin> plugins = build.getPluginManagement().getPlugins();
        assertEquals(5, plugins.size());
        Plugin extension = plugins.get(0);
        // org.apache.maven.plugins:maven-compiler-plugin:2.3
        assertEquals("org.apache.maven.plugins", extension.getGroupId());
        assertEquals("maven-compiler-plugin", extension.getArtifactId());
        assertEquals("2.3", extension.getVersion());

        Plugin site = plugins.get(2);
        // org.apache.maven.plugins:maven-site-plugin:2.3.2
        assertEquals("org.apache.maven.plugins", site.getGroupId());
        assertEquals("maven-site-plugin", site.getArtifactId());
        assertEquals("2.3.2", site.getVersion());
        assertNotNull(site.getConfiguration());
    }

    @Test
    public void testCompactReportPlugins() throws Exception {
        Model model = getModel("compact-report-plugins.yaml");
        assertNotNull(model);
        Reporting reporting = model.getReporting();
        List<ReportPlugin> plugins = reporting.getPlugins();
        assertEquals(1, plugins.size());
        ReportPlugin extension = plugins.get(0);
        // org.apache.maven.plugins:maven-compiler-plugin:2.3
        assertEquals("org.apache.maven.plugins", extension.getGroupId());
        assertEquals("maven-surefire-report-plugin", extension.getArtifactId());
        assertEquals("2.18.1", extension.getVersion());
    }

    protected Model getModel(String name) throws Exception {
        YamlModelReader modelReader = new YamlModelReader();
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        assertNotNull(input);
        return modelReader.read(input, null);
    }
}
