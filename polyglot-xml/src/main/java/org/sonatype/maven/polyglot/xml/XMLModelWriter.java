/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Model;
import org.sonatype.maven.polyglot.io.ModelWriterSupport;
import org.sonatype.maven.polyglot.xml.xpp3.PolyglotMavenXpp3Writer;

/**
 * XML model writer.
 *
 */
@Singleton
@Named("xml41")
public class XMLModelWriter extends ModelWriterSupport {

    PolyglotMavenXpp3Writer writer;

    public XMLModelWriter() {
        writer = new PolyglotMavenXpp3Writer();
    }

    public void write(Writer output, Map<String, Object> o, Model model) throws IOException {

        writer.write(output, model);
    }
}
