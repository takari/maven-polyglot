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
import org.apache.maven.model.Extension;

/**
 * Builds extensions nodes.
 *
 * @author <a href="mailto:tobrien@discursive.com">Tim O'Brien</a>
 *
 * @since 0.8
 */
public class ExtensionsFactory extends ListFactory {
    public ExtensionsFactory() {
        super("extensions");
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attrs)
            throws InstantiationException, IllegalAccessException {
        Object node;

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

    public static Object parse(final Object value) {
        assert value != null;

        // This first clause is a kludge.  Maybe there is a better way
        // but we're overloading "extensions" in POMs.
        if (value instanceof String && isBoolean((String) value)) {
            return value;
        } else if (value instanceof String) {
            Extension child = ExtensionFactory.parse(value);
            if (child != null) {
                List node = new ArrayList();
                node.add(child);
                return node;
            }
        } else if (value instanceof List) {
            List node = new ArrayList();
            for (Object item : (List) value) {
                Extension child = ExtensionFactory.parse(item);
                if (child == null) {
                    return null;
                }
                node.add(child);
            }
            return node;
        }

        return null;
    }

    //  TOB: Sure, some clever bastard is going to come along and tell me there
    //  is a simpler way to do this.  Please, if you have a better solution fix this.
    private static boolean isBoolean(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
    }
}
