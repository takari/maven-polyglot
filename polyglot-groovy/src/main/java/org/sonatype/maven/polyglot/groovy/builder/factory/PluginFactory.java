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
import org.apache.maven.model.Plugin;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Reporting;
import org.sonatype.maven.polyglot.groovy.builder.ModelBuilder;

/**
 * Builds {@link org.apache.maven.model.Plugin} nodes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public class PluginFactory extends NamedFactory {
    public PluginFactory() {
        super("plugin");
    }

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attrs)
            throws InstantiationException, IllegalAccessException {
        if (((ModelBuilder) builder).findInContext(Reporting.class.getName()) != null) {
            return new ReportPlugin();
        } else {
            Plugin node;

            if (value != null) {
                node = parse(value);

                if (node == null) {
                    throw new NodeValueParseException(this, value);
                }
            } else {
                node = new Plugin();
            }

            return node;
        }
    }

    public static Plugin parse(final Object value) {
        assert value != null;

        if (value instanceof String) {
            Plugin node = new Plugin();
            String[] items = ((String) value).split(":");
            switch (items.length) {
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
