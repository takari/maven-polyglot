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
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelWriter;
import org.junit.Test;

public class CloneTest {
    @Test
    public void testModelCloning() throws Exception {
        getModel().clone();
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
        // System.out.println(sw.toString());
    }

    protected Model getModel() throws Exception {
        YamlModelReader modelReader = new YamlModelReader();
        URL url = getClass().getResource("test2.yml");
        assertNotNull(url);
        InputStream reader = url.openStream();
        return modelReader.read(reader, null);
    }
}
