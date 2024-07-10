/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.clojure;

import clojure.lang.Atom;
import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Model;
import org.sonatype.maven.polyglot.PolyglotModelUtil;
import org.sonatype.maven.polyglot.io.ModelReaderSupport;

/**
 * Reads a <tt>pom.clj</tt> and transforms into a Maven {@link Model}.
 *
 * @author <a href="mailto:mark@derricutt.com">Mark Derricutt</a>
 * @author <a href="mailto:antony.blakey@linkuistics.com">Antony Blakey</a>
 *
 * @since 0.7
 */
@Singleton
@Named("clojure")
public class ClojureModelReader extends ModelReaderSupport {

    public Model read(final Reader input, final Map<String, ?> options) throws IOException {
        assert input != null;

        Thread currentThread = Thread.currentThread();
        ClassLoader originalCL = currentThread.getContextClassLoader();
        ClassLoader classloaderWithClojure = this.getClass().getClassLoader();
        try {
            currentThread.setContextClassLoader(classloaderWithClojure);

            String location = PolyglotModelUtil.getLocation(options);
            final Var USE = Var.intern(RT.CLOJURE_NS, Symbol.create("use"));
            final Symbol READER = Symbol.create("org.sonatype.maven.polyglot.clojure.dsl.reader");
            final Symbol LEININGEN = Symbol.create("org.sonatype.maven.polyglot.clojure.dsl.leiningen");
            USE.invoke(READER);
            USE.invoke(LEININGEN);
            clojure.lang.Compiler.load(input, location, location);
            final Var MODEL = Var.intern(Namespace.findOrCreate(READER), Symbol.create("*MODEL*"));
            return (Model) ((Atom) MODEL.get()).deref();
        } catch (Exception e) {
            // Don't use new IOException(e) because it doesn't exist in Java 5
            throw (IOException) new IOException(e.toString()).initCause(e);
        } finally {
            currentThread.setContextClassLoader(originalCL);
        }
    }
}
