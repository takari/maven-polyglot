/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy.builder.factory;

import groovy.util.FactoryBuilderSupport;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.PluginExecution;

/**
 * Builds {@link org.apache.maven.model.PluginExecution} nodes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 0.7
 */
public class ExecutionFactory extends NamedFactory {
    public ExecutionFactory() {
        super("execution");
    }

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attrs)
            throws InstantiationException, IllegalAccessException {
        return new PluginExecution();
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object current, Map attrs) {
        PluginExecution node = (PluginExecution) current;

        // Custom handling for 'goal' and 'goals' attributes
        if (attrs.containsKey("goal")) {
            Object value = attrs.get("goal");
            node.setGoals(Collections.singletonList(String.valueOf(value)));
            attrs.remove("goal");
        } else if (attrs.containsKey("goals")) {
            Object value = attrs.get("goals");
            if (value instanceof String) {
                node.setGoals(Collections.singletonList(String.valueOf(value)));
            } else if (value instanceof List) {
                node.setGoals((List) value);
            }
            attrs.remove("goals");
        }

        return true;
    }
}
