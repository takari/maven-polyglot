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
import org.apache.maven.model.Dependency;

/**
 * Builds {@link org.apache.maven.model.Dependency} nodes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public class DependencyFactory extends NamedFactory {
    public DependencyFactory() {
        super("dependency");
    }

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attrs)
            throws InstantiationException, IllegalAccessException {
        Dependency node;

        if (value != null) {
            node = parse(value);

            if (node == null) {
                throw new NodeValueParseException(this, value);
            }
        } else {
            node = new Dependency();
        }

        return node;
    }

    public static Dependency parse(final Object value) {
        assert value != null;

        if (value instanceof String) {
            Dependency node = new Dependency();
            String[] items = ((String) value).split(":");
            switch (items.length) {
                case 4:
                    node.setGroupId(items[0]);
                    node.setArtifactId(items[1]);
                    node.setVersion(items[2]);
                    node.setScope(items[3]);
                    return node;

                case 3:
                    node.setGroupId(items[0]);
                    node.setArtifactId(items[1]);
                    node.setVersion(items[2]);
                    return node;

                case 2:
                    node.setGroupId(items[0]);
                    node.setArtifactId(items[1]);
                    return node;
            }
        }

        return null;
    }
}
