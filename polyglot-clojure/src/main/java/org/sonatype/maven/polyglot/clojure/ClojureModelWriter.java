/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.clojure;

import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Model;
import org.sonatype.maven.polyglot.io.ModelWriterSupport;

/**
 * Writes a Maven {@link org.apache.maven.model.Model} to a <tt>pom.clj</tt>.
 *
 * @author <a href="mailto:mark@derricutt.com">Mark Derricutt</a>
 * @author <a href="mailto:antony.blakey@linkuistics.com">Antony Blakey</a>
 *
 * @since 0.7
 */
@Singleton
@Named("clojure")
public class ClojureModelWriter extends ModelWriterSupport {

    public void write(Writer writer, Map<String, Object> stringObjectMap, Model model) throws IOException {
        try {
            final Var REQUIRE = Var.intern(RT.CLOJURE_NS, Symbol.create("require"));
            final Symbol REFLECTOR = Symbol.create("org.sonatype.maven.polyglot.clojure.dsl.writer");
            REQUIRE.invoke(REFLECTOR);
            final Var WRITER = Var.intern(Namespace.findOrCreate(REFLECTOR), Symbol.create("write-model"));
            WRITER.invoke(model, writer);
        } catch (Exception e) {
            e.printStackTrace();
            // Don't use new IOException(e) because it doesn't exist in Java 5
            throw (IOException) new IOException(e.toString()).initCause(e);
        }
    }
}
