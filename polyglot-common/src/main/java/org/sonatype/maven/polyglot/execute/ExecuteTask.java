/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.execute;

/**
 * Represents a language specific task to be executed.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public interface ExecuteTask {
    String getId();

    String getPhase();

    /**
     * profile id of the execute task. can be <code>null</code> then the execute task belongs to
     * project.build
     */
    String getProfileId();

    void execute(ExecuteContext context) throws Exception;
}
