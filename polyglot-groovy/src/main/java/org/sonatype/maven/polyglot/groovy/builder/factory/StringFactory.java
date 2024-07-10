/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy.builder.factory;

import groovy.util.FactoryBuilderSupport;
import java.util.Map;

/**
 * Builds string nodes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public class StringFactory extends NamedFactory {
    public StringFactory(final String name) {
        super(name);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attrs) {
        return String.valueOf(value);
    }
}
