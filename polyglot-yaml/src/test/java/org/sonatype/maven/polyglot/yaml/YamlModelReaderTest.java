/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Build;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.apache.maven.model.io.ModelWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Test;

public class YamlModelReaderTest {
    @Test
    public void testModelCloning() throws Exception {
        getModel().clone();
    }

    @Test
    public void testModelReader() throws Exception {
        Model model = getModel();
        assertNotNull(model);

        Parent parent = model.getParent();
        assertEquals("org.sonatype.maven", parent.getGroupId());
        assertEquals("maven-parent", parent.getArtifactId());
        assertEquals("1.0", parent.getVersion());

        assertEquals("org.sonatype.raven", model.getGroupId());
        assertEquals("raven", model.getArtifactId());
        assertEquals("1.0-SNAPSHOT", model.getVersion());
        assertEquals("Raven", model.getName());

        // Developers
        List<Developer> developers = model.getDevelopers();
        Developer dev0 = developers.get(0);
        assertEquals("jvanzyl", dev0.getId());
        assertEquals("Jason van Zyl", dev0.getName());

        // Contributors
        List<Contributor> contributors = model.getContributors();
        Contributor con0 = contributors.get(0);
        assertEquals("Will Price", con0.getName());

        // DependencyManagement
        List<Dependency> depManDependencies = model.getDependencyManagement().getDependencies();
        assertEquals(2, depManDependencies.size());

        Dependency dmd0 = depManDependencies.get(0);
        assertEquals("org.apache.maven", dmd0.getGroupId());
        assertEquals("maven-model-builder", dmd0.getArtifactId());
        assertEquals("3.0", dmd0.getVersion());

        Dependency dmd1 = depManDependencies.get(1);
        assertEquals("org.yaml", dmd1.getGroupId());
        assertEquals("snakeyaml", dmd1.getArtifactId());
        assertEquals("1.4", dmd1.getVersion());

        // Dependencies
        List<Dependency> dependencies = model.getDependencies();
        assertEquals(3, dependencies.size());

        Dependency d0 = dependencies.get(0);
        assertEquals("org.apache.maven", d0.getGroupId());
        assertEquals("maven-core", d0.getArtifactId());
        assertEquals("3.0", d0.getVersion());

        Dependency d1 = dependencies.get(1);
        assertEquals("org.yaml", d1.getGroupId());
        assertEquals("snakeyaml", d1.getArtifactId());
        assertEquals("1.4", d1.getVersion());

        // Modules
        List<String> modules = model.getModules();
        assertEquals("reader", modules.get(0));
        assertEquals("writer", modules.get(1));
        assertEquals("shell-integration", modules.get(2));

        // Build
        Build build = model.getBuild();
        List<Plugin> plugins = build.getPlugins();
        assertEquals(1, plugins.size());
        Plugin p0 = plugins.get(0);
        assertEquals("org.apache.maven.plugins", p0.getGroupId());
        assertEquals("maven-compiler-plugin", p0.getArtifactId());
        assertEquals("2.0.2", p0.getVersion());
        Xpp3Dom configuration = (Xpp3Dom) p0.getConfiguration();
        assertNotNull(configuration);
        assertEquals(2, configuration.getChildCount());
        assertEquals("1.6", configuration.getChild("source").getValue());
        assertEquals("1.5", configuration.getChild("target").getValue());

        // DistributionManagement
        DistributionManagement distMan = model.getDistributionManagement();
        Site site = distMan.getSite();
        assertEquals("site", site.getId());
        assertEquals("http://www.apache.org", site.getUrl());
        DeploymentRepository releases = distMan.getRepository();
        assertEquals("releases", releases.getId());
        assertEquals("releases", releases.getName());
        assertEquals("http://maven.sonatype.org/releases", releases.getUrl());
        DeploymentRepository snapshots = distMan.getSnapshotRepository();
        assertEquals("snapshots", snapshots.getId());
        assertEquals("snapshots", snapshots.getName());
        assertEquals("http://maven.sonatype.org/snapshots", snapshots.getUrl());

        // SCM
        Scm scm = model.getScm();
        assertEquals("connection", scm.getConnection());
        assertEquals("developerConnection", scm.getDeveloperConnection());
        assertEquals("http://maven.sonatype.org/scm", scm.getUrl());

        // IssueManagement
        IssueManagement issueManagement = model.getIssueManagement();
        assertEquals("jira", issueManagement.getSystem());
        assertEquals("http://jira.codehaus.org/browse/MNG", issueManagement.getUrl());

        // CiManagement
        CiManagement ciManagement = model.getCiManagement();
        assertEquals("hudson", ciManagement.getSystem());
        assertEquals("http://grid.sonatype.org/ci", ciManagement.getUrl());

        // Profiles
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
        assertFalse(output, output.contains("extensions: []"));
        assertFalse(output, output.contains("properties: {}"));
    }

    protected Model getModel() throws Exception {
        YamlModelReader modelReader = new YamlModelReader();
        URL url = getClass().getResource("test.yml");
        assertNotNull(url);
        InputStream reader = url.openStream();
        return modelReader.read(reader, null);
    }
}
