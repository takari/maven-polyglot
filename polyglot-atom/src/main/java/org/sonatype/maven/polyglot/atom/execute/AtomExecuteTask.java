/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.atom.execute;

import java.util.Map;
import org.sonatype.maven.polyglot.execute.ExecuteContext;
import org.sonatype.maven.polyglot.execute.ExecuteTask;
import org.sonatype.maven.polyglot.execute.ExecuteTaskSupport;

/**
 * Encapsulates a Groovy {@link ExecuteTask}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public class AtomExecuteTask extends ExecuteTaskSupport {
    private final Object value;
    private final Map attrs;

    public AtomExecuteTask(final Object value, final Map attrs) {
        this.value = value;
        this.attrs = attrs;
    }

    public Object getValue() {
        return value;
    }

    public Map getAttributes() {
        return attrs;
    }

    public void execute(final ExecuteContext context) throws Exception {
        System.out.println("Executing atom code...");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id='"
                + getId() + '\'' + ", phase='"
                + getPhase() + '\'' + ", value="
                + value + ", attrs="
                + attrs + '}';
    }
}
