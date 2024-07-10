/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy;

import javax.inject.Named;
import javax.inject.Singleton;
import org.sonatype.maven.polyglot.mapping.MappingSupport;

/**
 * Groovy model mapping.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
@Singleton
@Named("groovy")
public class GroovyMapping extends MappingSupport {
    public GroovyMapping() {
        super("groovy");
        setPomNames("pom.groovy", "pom.gy");
        setAcceptLocationExtensions(".groovy", ".gy");
        setAcceptOptionKeys("groovy:4.0.0");
        setPriority(1);
    }
}
