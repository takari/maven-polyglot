/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

import java.util.*;

public class SortedProperties extends Properties {

    public SortedProperties(Properties props) {
        super(props);
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<Object>(keySet()));
    }

    @Override
    public synchronized Set<Object> keySet() {
        return new TreeSet<Object>(super.keySet());
    }
}
