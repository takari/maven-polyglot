/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import org.junit.Test;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

public class ModelResolverTest {

    @Test
    public void testModelNullResolver() {
        ModelResolver resolver = new ModelResolver();
        assertEquals(Tag.STR, resolver.resolve(NodeId.scalar, "Null", true));
        assertEquals(Tag.STR, resolver.resolve(NodeId.scalar, "NULL", true));
        assertEquals(Tag.NULL, resolver.resolve(NodeId.scalar, "null", true));
        assertEquals(Tag.NULL, resolver.resolve(NodeId.scalar, " ", true));
        assertEquals(Tag.NULL, resolver.resolve(NodeId.scalar, "", true));
    }

    @Test
    public void testModelResolver() {
        ModelResolver resolver = new ModelResolver();
        assertEquals(Tag.STR, resolver.resolve(NodeId.scalar, "8", true));
        assertEquals(Tag.STR, resolver.resolve(NodeId.scalar, "3.1", true));
        assertEquals(Tag.STR, resolver.resolve(NodeId.scalar, "true", true));
        assertEquals(Tag.STR, resolver.resolve(NodeId.scalar, "false", true));
    }

    @Test
    public void testCoordinateRE() {
        Matcher matcher = ModelResolver.COORDINATE_PATTERN.matcher("org.yaml:snakeyaml:1.17-SNAPSHOT");
        assertTrue(matcher.matches());
        assertEquals("org.yaml", matcher.group("groupId"));
        assertEquals("snakeyaml", matcher.group("artifactId"));
        assertEquals("1.17-SNAPSHOT", matcher.group("version"));
    }

    @Test
    public void testDependencyREwithScope() {
        Matcher matcher = ModelResolver.DEPENDENCY_PATTERN.matcher("org.yaml:snakeyaml:test:1.17-SNAPSHOT");
        assertTrue(matcher.matches());
        assertEquals("org.yaml", matcher.group("groupId"));
        assertEquals("snakeyaml", matcher.group("artifactId"));
        assertEquals("test", matcher.group("scope"));
        assertEquals("1.17-SNAPSHOT", matcher.group("version"));
    }
}
