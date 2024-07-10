/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.atom;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.building.ModelSource;
import org.codehaus.plexus.util.IOUtil;
import org.sonatype.maven.polyglot.atom.parsing.AtomParser;
import org.sonatype.maven.polyglot.atom.parsing.Project;
import org.sonatype.maven.polyglot.atom.parsing.Tokenizer;
import org.sonatype.maven.polyglot.io.ModelReaderSupport;

/**
 * Reads a <tt>pom.atom</tt> and transforms into a Maven {@link Model}.
 *
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
@Singleton
@Named("atom")
public class AtomModelReader extends ModelReaderSupport {

    public Model read(final Reader input, final Map<String, ?> options) throws IOException {
        assert input != null;

        // Parse the token stream from our pom.atom configuration file.
        Project project = new AtomParser(
                        (ModelSource) options.get(ModelProcessor.SOURCE),
                        new Tokenizer(IOUtil.toString(input)).tokenize())
                .parse();
        return project.toMavenModel();
    }
}
