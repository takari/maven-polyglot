/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.maven.model.Model;

public class Util {

    /**
     * Read resource form classpath
     * @param theName - the resource name
     */
    public static String getLocalResource(String theName) {
        try {
            InputStream input;
            input = Thread.currentThread().getContextClassLoader().getResourceAsStream(theName);
            if (input == null) {
                throw new RuntimeException("Can not find " + theName);
            }
            BufferedInputStream is = new BufferedInputStream(input);
            StringBuilder buf = new StringBuilder(3000);
            int i;
            try {
                while ((i = is.read()) != -1) {
                    buf.append((char) i);
                }
            } finally {
                is.close();
            }
            String resource = buf.toString();
            // convert EOLs
            String[] lines = resource.split("\\r?\\n");
            StringBuilder buffer = new StringBuilder();
            for (int j = 0; j < lines.length; j++) {
                buffer.append(lines[j]);
                buffer.append("\n");
            }
            return buffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Model getModel(String fileName) throws Exception {
        YamlModelReader modelReader = new YamlModelReader();
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        return modelReader.read(input, null);
    }
}
