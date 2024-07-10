/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.execute;

import java.io.File;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Provides context for {@link ExecuteTask}s.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public interface ExecuteContext {

    /**
     * Returns the maven project.
     */
    MavenProject getProject();

    /**
     * Returns the maven session.
     */
    MavenSession getSession();

    /**
     * Returns the project base directory.
     *
     * @deprecated Use {@link #getBasedir()} instead
     */
    @Deprecated
    default File basedir() {
        return getBasedir();
    }

    /**
     * Returns the project base directory.
     */
    default File getBasedir() {
        return getProject().getBasedir();
    }

    /**
     * Returns the logger.
     *
     * @deprecated Use {@link #getLog()} instead
     */
    @Deprecated
    default Log log() {
        return getLog();
    }

    /**
     * Returns the log.
     */
    Log getLog();
}
