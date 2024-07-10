/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy.builder.factory;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;
import groovy.util.Node;
import groovy.util.NodeBuilder;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Builds object nodes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public class ObjectFactory extends NamedFactory {
    public ObjectFactory(final String name) {
        super(name);
    }

    @Override
    public boolean isHandlesNodeChildren() {
        return true;
    }

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attrs)
            throws InstantiationException, IllegalAccessException {
        return new Xpp3Dom(getName());
    }

    @Override
    public boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure content) {
        Xpp3Dom dom = (Xpp3Dom) node;

        NodeBuilder nodes = new NodeBuilder() {
            @Override
            protected void setClosureDelegate(final Closure c, final Object o) {
                c.setDelegate(this);
                c.setResolveStrategy(Closure.DELEGATE_FIRST);
            }

            @Override
            public void setProperty(final String name, final Object value) {
                this.invokeMethod(name, value);
            }
        };

        content.setDelegate(nodes);
        content.setResolveStrategy(Closure.DELEGATE_FIRST);
        Node root = (Node) nodes.invokeMethod(getName(), content);

        for (Node child : (List<Node>) root.children()) {
            dom.addChild(nodeToXpp3(child));
        }

        return false;
    }

    private Xpp3Dom nodeToXpp3(final Node node) {
        Xpp3Dom dom = new Xpp3Dom((String) node.name());

        Object value = node.value();
        if (value instanceof String) {
            dom.setValue(String.valueOf(value));
        }

        Map attrs = node.attributes();
        for (Object key : attrs.keySet()) {
            dom.setAttribute(String.valueOf(key), String.valueOf(attrs.get(key)));
        }

        for (Object child : node.children()) {
            if (child instanceof Node) {
                dom.addChild(nodeToXpp3((Node) child));
            }
        }

        return dom;
    }
}
