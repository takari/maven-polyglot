/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy.execute;

import groovy.lang.Closure;
import java.util.Map;
import org.codehaus.groovy.runtime.StackTraceUtils;
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
public class GroovyExecuteTask extends ExecuteTaskSupport {
    private final Object value;

    private final Map attrs;

    private Closure closure;

    public GroovyExecuteTask(final Object value, final Map attrs) {
        this.value = value;
        this.attrs = attrs;
    }

    public Object getValue() {
        return value;
    }

    public Map getAttributes() {
        return attrs;
    }

    public Closure getClosure() {
        return closure;
    }

    public void setClosure(final Closure closure) {
        this.closure = closure;
    }

    public void execute(final ExecuteContext context) throws Exception {
        try {
            getClosure().call(context);
        } catch (Throwable t) {
            t = StackTraceUtils.sanitize(t);

            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Exception) {
                throw (Exception) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }

            throw new RuntimeException(t);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id='" + getId() + '\'' + ", phase='" + getPhase() + '\'' + ", value="
                + value + ", attrs=" + attrs + ", closure=" + closure + '}';
    }
}
