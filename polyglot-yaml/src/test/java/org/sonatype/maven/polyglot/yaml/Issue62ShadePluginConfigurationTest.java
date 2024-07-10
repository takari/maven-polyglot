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
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Test;

/**
 * @see <a href="https://github.com/takari/polyglot-maven/issues/62">[polyglot-yaml] does not handle shade plugin</a>
 */
public class Issue62ShadePluginConfigurationTest {

    private static final String PLUGIN_ARTIFACT_ID = "maven-shade-plugin";
    private static final String PLUGIN_VERSION = "2.4.3";

    @Test
    public void readShadePluginConfig() throws Exception {

        // given
        InputStream input = getResourceAsStream("issue62-shade-plugin-config.yaml");
        assertNotNull(input);

        String expectedContents = Util.getLocalResource("issue62-shade-plugin-config-expected.xml");

        YamlModelReader modelReader = new YamlModelReader();

        // when
        Model model = modelReader.read(input, null);
        assertNotNull(model);
        Plugin plugin = model.getBuild().getPlugins().get(0);
        assertEquals(PLUGIN_ARTIFACT_ID, plugin.getArtifactId());
        assertEquals(PLUGIN_VERSION, plugin.getVersion());

        Xpp3Dom conf = (Xpp3Dom) plugin.getExecutions().get(0).getConfiguration();

        // then
        assertEquals(expectedContents.trim(), conf.toString().trim());
    }

    @Test
    public void readShadePluginConfigWithExplicitPairs() throws Exception {

        // given
        InputStream input = getResourceAsStream("issue62-shade-plugin-config-with-pairs.yaml");
        assertNotNull(input);

        String expectedContents = Util.getLocalResource("issue62-shade-plugin-config-with-pairs-expected.xml");

        YamlModelReader modelReader = new YamlModelReader();

        // when
        Model model = modelReader.read(input, null);
        assertNotNull(model);
        Plugin plugin = model.getBuild().getPlugins().get(0);
        assertEquals(PLUGIN_ARTIFACT_ID, plugin.getArtifactId());
        assertEquals(PLUGIN_VERSION, plugin.getVersion());

        Xpp3Dom conf = (Xpp3Dom) plugin.getExecutions().get(0).getConfiguration();

        // then
        assertEquals(expectedContents.trim(), conf.toString().trim());
    }

    private InputStream getResourceAsStream(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    }
}
