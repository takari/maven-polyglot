/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.atom.parsing;

import java.util.List;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public class Repositories extends Element {
    private final List<String> repositories;

    public Repositories(List<String> repositories) {
        this.repositories = repositories;
    }

    public List<String> getRepositories() {
        return repositories;
    }

    @Override
    public String toString() {
        return repositories.toString();
    }
}
