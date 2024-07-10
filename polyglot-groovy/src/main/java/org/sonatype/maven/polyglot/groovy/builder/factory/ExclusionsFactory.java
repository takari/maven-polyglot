/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy.builder.factory;

import groovy.util.FactoryBuilderSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Exclusion;

/**
 * Builds exclusions nodes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public class ExclusionsFactory extends ListFactory {
    public ExclusionsFactory() {
        super("exclusions");
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attrs)
            throws InstantiationException, IllegalAccessException {
        List node;

        if (value != null) {
            node = parse(value);

            if (node == null) {
                throw new NodeValueParseException(this, value);
            }
        } else {
            node = new ArrayList();
        }

        return node;
    }

    public static List parse(final Object value) {
        assert value != null;

        if (value instanceof String) {
            Exclusion child = ExclusionFactory.parse(value);
            if (child != null) {
                List node = new ArrayList();
                node.add(child);
                return node;
            }
        } else if (value instanceof List) {
            List node = new ArrayList();
            for (Object item : (List) value) {
                Exclusion child = ExclusionFactory.parse(item);
                if (child == null) {
                    return null;
                }
                node.add(child);
            }
            return node;
        }

        return null;
    }
}
