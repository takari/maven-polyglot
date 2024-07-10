/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.java;

import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.sonatype.maven.polyglot.mapping.MappingSupport;

/**
 * XML model mapping.
 *
 */
@Singleton
@Named("java")
public class JavaMapping extends MappingSupport {

    public JavaMapping() {
        super("java");
        setPomNames("pom.java");
        setAcceptLocationExtensions(".java");
        setAcceptOptionKeys("java:4.0.0");
        setPriority(-1);
    }

    @Override
    public boolean accept(Map<String, ?> options) {
        if (options != null) {

            String location = getLocation(options);
            if (location != null) {
                if (location.endsWith(".java")) {
                    return true;
                }
            }
        }

        return false;
    }
}
