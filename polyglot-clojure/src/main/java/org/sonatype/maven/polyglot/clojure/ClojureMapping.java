/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.clojure;

import javax.inject.Named;
import javax.inject.Singleton;
import org.sonatype.maven.polyglot.mapping.MappingSupport;

/**
 * Clojure model mapping.
 *
 * @author <a href="mailto:mark@derricutt.com">Mark Derricutt</a>
 * @author <a href="mailto:antony.blakey@linkuistics.com">Antony Blakey</a>
 *
 * @since 0.7
 */
@Singleton
@Named("clojure")
public class ClojureMapping extends MappingSupport {
    public ClojureMapping() {
        super("clojure");
        setPomNames("pom.clj", "projwct.clj");
        setAcceptLocationExtensions(".clj");
        setAcceptOptionKeys("clojure:4.0.0");
        setPriority(1);
    }
}
