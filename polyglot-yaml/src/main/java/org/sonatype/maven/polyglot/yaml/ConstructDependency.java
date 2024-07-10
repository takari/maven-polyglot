/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import java.util.regex.Matcher;
import org.apache.maven.model.Dependency;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class ConstructDependency extends AbstractConstruct {
    @Override
    public Object construct(Node node) {
        ScalarNode scalar = (ScalarNode) node;
        return createDependency(scalar.getValue(), new Dependency());
    }

    public static Dependency createDependency(String scalar, Dependency dependency) {
        Matcher matcher = ModelResolver.DEPENDENCY_PATTERN.matcher(scalar);
        if (matcher.matches()) {
            // scope and version are present
            String version = matcher.group("version");
            dependency.setVersion(version);
            String scope = matcher.group("scope");
            dependency.setScope(scope);
        } else {
            matcher = ModelResolver.COORDINATE_PATTERN.matcher(scalar);
            // version is present
            if (matcher.matches()) {
                String version = matcher.group("version");
                dependency.setVersion(version);
            } else {
                matcher = ModelResolver.GROUP_NAME_PATTERN.matcher(scalar);
                if (!matcher.matches()) {
                    throw new IllegalArgumentException("Unexpected node: " + scalar);
                }
            }
        }
        // groupId and artifactId are always present
        String groupId = matcher.group("groupId");
        String artifactId = matcher.group("artifactId");
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        return dependency;
    }
}
