/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy;

import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.lang.Singleton;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.DefaultModelWriter;
import org.codehaus.groovy.runtime.StackTraceUtils;
import org.codehaus.plexus.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.maven.polyglot.PolyglotModelUtil;
import org.sonatype.maven.polyglot.execute.ExecuteManager;
import org.sonatype.maven.polyglot.groovy.builder.ModelBuilder;
import org.sonatype.maven.polyglot.io.ModelReaderSupport;

/**
 * Reads a <tt>pom.groovy</tt> and transforms into a Maven {@link Model}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
@Singleton
@Named("groovy")
public class GroovyModelReader extends ModelReaderSupport {
    protected Logger log = LoggerFactory.getLogger(GroovyModelReader.class);

    @Inject
    private ModelBuilder builder;

    @Inject
    private ExecuteManager executeManager;

    @Override
    public Model read(final Reader input, final Map<String, ?> options) throws IOException {
        assert input != null;

        Model model;

        try {
            model = doRead(input, options);
        } catch (Throwable t) {
            t = StackTraceUtils.sanitize(t);

            if (t instanceof IOException) {
                throw (IOException) t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }

            throw new RuntimeException(t);
        }

        // FIXME: Looks like there are cases where the model is loaded more than once
        executeManager.install(model, options);

        if (log.isDebugEnabled()) {
            DefaultModelWriter writer = new DefaultModelWriter();
            StringWriter buff = new StringWriter();
            writer.write(buff, null, model);
            log.debug("Read groovy model: \n" + buff);
        }

        return model;
    }

    private Model doRead(final Reader input, final Map<String, ?> options) throws IOException {
        assert input != null;

        GroovyShell shell = new GroovyShell();
        String text = IOUtil.toString(input);
        String location = PolyglotModelUtil.getLocation(options);
        Script script = shell.parse(new GroovyCodeSource(text, location, location));

        /*
         * FIXME: Bring this back as pure java
         *
         * def include = {source -> assert source != null
         *
         * def include
         *
         * // TODO: Support String, support loading from resource
         *
         * if (source instanceof Class) { include = source.newInstance() } else if (source instanceof File) { include = shell.parse((File)source) } else if (source instanceof URL) { include =
         * shell.parse(((URL)source).openStream()) } else { throw new IllegalArgumentException("Invalid include source: $source") }
         *
         * include.run()
         *
         * // Include each closure variable which starts with '$' and curry in the builder include.binding.properties.variables.each { if (it.value instanceof Closure && it.key.startsWith('$')) {
         * binding.setVariable(it.key, it.value.curry(builder)) } } }
         *
         * include(Macros)
         *
         * binding.setProperty('$include', include)
         */

        assert builder != null;
        return (Model) builder.build(script);
    }
}
