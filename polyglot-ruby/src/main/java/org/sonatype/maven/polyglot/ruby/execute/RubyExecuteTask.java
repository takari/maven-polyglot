/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby.execute;

import org.jruby.embed.ScriptingContainer;
import org.sonatype.maven.polyglot.execute.ExecuteContext;
import org.sonatype.maven.polyglot.execute.ExecuteTask;
import org.sonatype.maven.polyglot.execute.ExecuteTaskSupport;

/**
 * Encapsulates a Ruby script {@link ExecuteTask}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public class RubyExecuteTask extends ExecuteTaskSupport {

    private ScriptingContainer jruby;
    private Object script;

    public RubyExecuteTask(ScriptingContainer jruby) {
        this.jruby = jruby;
    }

    public Object getScript() {
        return script;
    }

    public void setScript(Object script) {
        this.script = script;
    }

    public void execute(final ExecuteContext context) throws Exception {
        jruby.callMethod(script, "call", context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id='"
                + getId() + '\'' + ", phase='"
                + getPhase() + '\'' + (getProfileId() == null ? "" : ", profile='" + getProfileId() + '\'')
                + ","
                + script + "}";
    }
}
