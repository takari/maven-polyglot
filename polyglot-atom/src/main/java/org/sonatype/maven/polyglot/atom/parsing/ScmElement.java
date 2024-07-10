/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.atom.parsing;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
class ScmElement {
    private final String connection;
    private final String developerConnection;
    private final String url;

    ScmElement(String connection, String developerConnection, String url) {
        this.connection = connection;
        this.developerConnection = developerConnection;
        this.url = url;
    }

    public String getConnection() {
        return connection;
    }

    public String getDeveloperConnection() {
        return developerConnection;
    }

    public String getUrl() {
        return url;
    }
}
