/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy.builder.factory;

import groovy.util.FactoryBuilderSupport;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Model;
import org.sonatype.maven.polyglot.execute.ExecuteManager;
import org.sonatype.maven.polyglot.execute.ExecuteTask;
import org.sonatype.maven.polyglot.groovy.builder.ModelBuilder;

/**
 * Builds {@link Model} elements and handles registration of any {@link ExecuteTask}s.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public class ModelFactory extends NamedFactory {
    public ModelFactory() {
        super("project");
    }

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attrs)
            throws InstantiationException, IllegalAccessException {
        return new Model();
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        Model model = (Model) node;
        ExecuteManager manager = ((ModelBuilder) builder).getExecuteManager();
        List<ExecuteTask> tasks = ((ModelBuilder) builder).getTasks();
        manager.register(model, tasks);

        // Reset the tasks list for sanity
        tasks.clear();
    }
}
