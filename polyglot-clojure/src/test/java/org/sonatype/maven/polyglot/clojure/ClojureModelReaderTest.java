/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.clojure;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.io.ModelReader;
import org.junit.Test;

/**
 * Tests for {@link ClojureModelReader}.
 *
 * @author <a href="mailto:mark@derricutt.com">Mark Derricutt</a>
 */
public class ClojureModelReaderTest extends TestCase {
    @Test
    public void testReading() throws Exception {
        Model model = readClojureModel("test1.clj");
        assertNotNull(model);

        System.out.println(model.getModelVersion());

        assertEquals("UTF-8", model.getModelEncoding());
        assertEquals("jar", model.getPackaging());
        //        assertEquals("4.0.0", model.getModelVersion());
        assertEquals("a", model.getGroupId());
        assertEquals("b", model.getArtifactId());
        assertEquals("c", model.getVersion());

        assertNotNull(model.getDependencyManagement());
        assertEquals(model.getDependencyManagement().getDependencies().isEmpty(), false);

        assertNotNull(model.getDependencies());

        Dependency dependency = model.getDependencies().get(0);

        assertEquals("org.clojure", dependency.getGroupId());

        assertNotNull(model.getBuild());
        assertNotNull(model.getBuild().getPlugins());
        assertEquals(false, model.getBuild().getPlugins().isEmpty());

        assertEquals(2, model.getBuild().getPlugins().size());

        Plugin plugin = model.getBuild().getPlugins().get(1);

        assertNotNull(plugin);
        assertEquals("clojure-maven-plugin", plugin.getArtifactId());
        assertEquals("1.2-SNAPSHOT", plugin.getVersion());
    }

    @Test
    public void testScripted() throws Exception {
        Model model = readClojureModel("test2.clj");
        assertNotNull(model);

        boolean hasTestNg = false;
        for (Dependency dependency : model.getDependencies()) {
            if ("testng".equals(dependency.getArtifactId())) {
                hasTestNg = true;
            }
        }

        assertEquals(true, hasTestNg);
    }

    @Test
    public void testNoGroupOrArtifact() throws Exception {
        Model model = readClojureModel("test3.clj");
        assertNotNull(model);
    }

    @Test
    public void testReadsLeiningen() throws Exception {
        Model model = readClojureModel("test.leiningen.1.clj");
        assertNotNull(model);
    }

    private Model readClojureModel(final String sourceFile) throws Exception {
        ModelReader reader = new ClojureModelReader();

        URL input = getClass().getResource(sourceFile);
        assertNotNull(input);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put(ModelProcessor.SOURCE, input);
        return reader.read(input.openStream(), options);
    }
}
