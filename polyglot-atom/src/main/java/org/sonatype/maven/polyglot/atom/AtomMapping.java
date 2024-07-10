/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.atom;

import javax.inject.Named;
import javax.inject.Singleton;
import org.sonatype.maven.polyglot.mapping.MappingSupport;

/**
 * Atom compact grammar model mapping.
 *
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
@Singleton
@Named("atom")
public class AtomMapping extends MappingSupport {
    public AtomMapping() {
        super("atom");
        setPomNames("pom.atom");
        setAcceptLocationExtensions(".atom");
        setAcceptOptionKeys("atom:4.0.0");
        setPriority(1);
    }
}
