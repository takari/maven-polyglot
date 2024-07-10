/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.clojure;

import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.io.ModelReader;
import org.junit.Test;

/**
 * Tests for {@link ClojureModelWriter}.
 *
 * @author <a href="mailto:mark@derricutt.com">Mark Derricutt</a>
 */
public class ClojureModelWriterTest extends TestCase {
    @Test
    public void testModelPrinting() throws Exception {
        Model model = readClojureModel("test1.clj");
        assertNotNull(model);

        ClojureModelWriter writer = new ClojureModelWriter();

        StringWriter sw = new StringWriter();

        writer.write(sw, new HashMap(), model);

        System.out.println(sw.getBuffer().toString());
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
