/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import java.util.regex.Pattern;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;

/**
 * POM resolver does not need to resolve implicit scalar types as defined in
 * http://www.yaml.org/type/
 */
public class ModelResolver extends Resolver {
    public static final Pattern POM_NULL = Pattern.compile("^(?:null| )$");
    public static final Pattern COORDINATE_PATTERN =
            Pattern.compile("^(?:(?<groupId>[^:]+?):(?<artifactId>[^:]+?):(?<version>[0-9][^:]+?))$");
    public static final Pattern GROUP_NAME_PATTERN = Pattern.compile("^(?:(?<groupId>[^:]+?):(?<artifactId>[^:]+?))$");
    // TODO scopes: compile|provided|runtime|test|system|import
    public static final Pattern DEPENDENCY_PATTERN =
            Pattern.compile("^(?:(?<groupId>[^:]+?):(?<artifactId>[^:]+?):(?<scope>[^:]+?):(?<version>[0-9].+?))$");

    @Override
    protected void addImplicitResolvers() {
        addImplicitResolver(Tag.MERGE, MERGE, "<");
        addImplicitResolver(Tag.NULL, POM_NULL, "~n\0");
        addImplicitResolver(Tag.NULL, EMPTY, null);
    }
}
