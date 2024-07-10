/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import org.apache.maven.model.Model;
import org.junit.Test;

public class Issue36Test {

    @Test
    public void testOriginalYaml() throws Exception {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("issue36.yaml");
        assertNotNull(input);
        YamlModelReader modelReader = new YamlModelReader();
        Model model = modelReader.read(input, null);
        assertNotNull(model);
    }

    @Test
    public void testShortYaml() throws Exception {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("issue36-short.yaml");
        assertNotNull(input);
        YamlModelReader modelReader = new YamlModelReader();
        Model model = modelReader.read(input, null);
        assertNotNull(model);
    }
}
