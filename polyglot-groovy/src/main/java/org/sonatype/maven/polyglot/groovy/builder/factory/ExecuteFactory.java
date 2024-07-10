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
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Build;
import org.sonatype.maven.polyglot.execute.ExecuteTask;
import org.sonatype.maven.polyglot.groovy.builder.ModelBuilder;
import org.sonatype.maven.polyglot.groovy.execute.GroovyExecuteTask;

/**
 * Builds {@link GroovyExecuteTask}s.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public class ExecuteFactory extends NamedFactory {
    public ExecuteFactory() {
        super("$execute");
    }

    @Override
    public boolean isHandlesNodeChildren() {
        return true;
    }

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attrs)
            throws InstantiationException, IllegalAccessException {
        return new GroovyExecuteTask(value, attrs);
    }

    @Override
    public void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        if (parent instanceof Build) {
            GroovyExecuteTask task = (GroovyExecuteTask) child;
            List<ExecuteTask> tasks = ((ModelBuilder) builder).getTasks();
            tasks.add(task);
        } else {
            throw new IllegalStateException("The " + getName() + " element must only be defined under 'build'");
        }
    }

    @Override
    public boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure content) {
        GroovyExecuteTask task = (GroovyExecuteTask) node;
        task.setClosure(content);
        return false;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        GroovyExecuteTask task = (GroovyExecuteTask) node;
        if (task.getId() == null) {
            throw new IllegalStateException("Execute task is missing attribute 'id'");
        }
        if (task.getPhase() == null) {
            throw new IllegalStateException("Execute task is missing attribute 'phase'");
        }
    }
}
