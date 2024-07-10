/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;

/**
 * Support for {@link ModelReader} implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public abstract class ModelReaderSupport implements ModelReader {
    public Model read(final File input, final Map<String, ?> options) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(input)) {
            Model model = read(inputStream, options);
            model.setPomFile(input);
            return model;
        }
    }

    public Model read(final InputStream input, final Map<String, ?> options) throws IOException {
        return read(new InputStreamReader(input, getCharset(options)), options);
    }

    protected Charset getCharset(Map<String, ?> options) {
        return Charset.defaultCharset();
    }
}
