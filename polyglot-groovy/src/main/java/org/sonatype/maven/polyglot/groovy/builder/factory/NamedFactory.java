/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy.builder.factory;

import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.util.List;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Support for named factories.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public abstract class NamedFactory extends AbstractFactory {
    private final String name;

    protected NamedFactory(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        if (parent instanceof List) {
            ((List) parent).add(child);
        } else {
            InvokerHelper.setProperty(parent, getName(), child);
        }
    }

    protected static class NodeValueParseException extends IllegalArgumentException {
        public NodeValueParseException(final NamedFactory factory, final Object value) {
            super("Unable to parse " + factory.getName() + " for: " + value + " (" + value.getClass() + ")");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "name='" + name + '\'' + '}';
    }
}
