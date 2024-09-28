/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
package org.sonatype.maven.polyglot.ruby.execute;

import java.util.LinkedList;
import java.util.List;

import org.jruby.embed.ScriptingContainer;
import org.sonatype.maven.polyglot.execute.ExecuteTask;

public class RubyExecuteTaskFactory {
    private ScriptingContainer jruby;
    private List<ExecuteTask> tasks = new LinkedList<ExecuteTask>();

    public RubyExecuteTaskFactory( ScriptingContainer jruby, DualClassLoader loader ) {
        this.jruby = jruby;
    }

    public void addExecuteTask( String id, String phase, String profileId, Object script ){
        RubyExecuteTask task = new RubyExecuteTask( jruby, loader );
        task.setId( id );
        task.setPhase( phase );
        task.setProfileId( profileId );
        task.setScript( script );

        this.tasks.add( task );
    }

    public List<ExecuteTask> getExecuteTasks(){
        return tasks;
    }
}