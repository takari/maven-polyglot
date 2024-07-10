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

import java.io.InputStream;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Test;

/**
 * @see <a href="https://github.com/takari/polyglot-maven/issues/44">polyglot-yaml does not handle nested lists</a>
 */
public class Issue44NestedListsTest {

    /**
     * It converts nested lists from YAML into Maven's {@link ConfigurationContainer} XML DOM nodes and automagically
     * inserts XML child nodes with names derived from the parent's plural node name.
     */
    @Test
    public void readNestedLists() throws Exception {

        // given
        InputStream input =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("issue44-nested-lists.yaml");
        assertNotNull(input);

        String expectedContents = Util.getLocalResource("issue44-nested-lists-expected.xml");

        YamlModelReader modelReader = new YamlModelReader();

        // when
        Model model = modelReader.read(input, null);
        assertNotNull(model);
        Plugin plugin = model.getBuild().getPlugins().get(0);
        assertEquals("proguard-maven-plugin", plugin.getArtifactId());
        assertEquals("2.0.11", plugin.getVersion());

        Xpp3Dom conf = (Xpp3Dom) plugin.getConfiguration();

        // then
        assertEquals(expectedContents.trim(), conf.toString().trim());
    }

    /**
     * It converts explicitly specified YAML pairs into Maven's {@link ConfigurationContainer} XML DOM nodes. This method
     * can be used if the list container node name is not a plural form of the list items node names.
     */
    @Test
    public void readExplicitPairs() throws Exception {

        // given
        InputStream input =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("issue44-explicit-pairs.yaml");
        assertNotNull(input);

        String expectedContents = Util.getLocalResource("issue44-explicit-pairs-expected.xml");

        YamlModelReader modelReader = new YamlModelReader();

        // when
        Model model = modelReader.read(input, null);
        assertNotNull(model);
        Plugin plugin = model.getBuild().getPlugins().get(0);
        assertEquals("test-plugin", plugin.getArtifactId());
        assertEquals("3.4", plugin.getVersion());

        Xpp3Dom conf = (Xpp3Dom) plugin.getConfiguration();

        // then
        assertEquals(expectedContents.trim(), conf.toString().trim());
    }

    /**
     * It converts the given sample Maven resources configuration containing nested lists from YAML into Maven's
     * {@link ConfigurationContainer} XML DOM nodes.
     */
    @Test
    public void readMavenResources() throws Exception {

        // given
        InputStream input =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("issue44-maven-resources.yaml");
        assertNotNull(input);

        String expectedContents = Util.getLocalResource("issue44-maven-resources-expected.xml");

        YamlModelReader modelReader = new YamlModelReader();

        // when
        Model model = modelReader.read(input, null);
        assertNotNull(model);
        Plugin plugin = model.getBuild().getPlugins().get(0);
        assertEquals("maven-resources-plugin", plugin.getArtifactId());
        assertEquals("2.7", plugin.getVersion());

        Xpp3Dom conf = (Xpp3Dom) plugin.getExecutions().get(0).getConfiguration();

        // then
        assertEquals(expectedContents.trim(), conf.toString().trim());
    }

    /**
     * It converts the given sample Maven legacy reportPlugins configuration containing nested lists from YAML into
     * Maven's {@link ConfigurationContainer} XML DOM nodes.
     */
    @Test
    public void readLegacyReportPlugins() throws Exception {

        // given
        InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("issue44-legacy-report-plugins.yaml");
        assertNotNull(input);

        String expectedContents = Util.getLocalResource("issue44-legacy-report-plugins-expected.xml");

        YamlModelReader modelReader = new YamlModelReader();

        // when
        Model model = modelReader.read(input, null);
        assertNotNull(model);
        Plugin plugin = model.getBuild().getPlugins().get(0);
        assertEquals("maven-site-plugin", plugin.getArtifactId());
        assertEquals("3.4", plugin.getVersion());

        Xpp3Dom conf = (Xpp3Dom) plugin.getConfiguration();

        // then
        assertEquals(expectedContents.trim(), conf.toString().trim());
    }
}
