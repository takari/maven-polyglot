/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import java.util.regex.Matcher;
import org.apache.maven.model.*;

public class Coordinate {
    private String groupId;
    private String artifactId;
    private String version;

    public Coordinate(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public static Coordinate createCoordinate(String scalar) {
        Matcher matcher = ModelResolver.COORDINATE_PATTERN.matcher(scalar);
        if (matcher.matches()) {
            return new Coordinate(matcher.group("groupId"), matcher.group("artifactId"), matcher.group("version"));
        } else {
            matcher = ModelResolver.GROUP_NAME_PATTERN.matcher(scalar);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Unexpected node: " + scalar);
            }
            return new Coordinate(matcher.group("groupId"), matcher.group("artifactId"), null);
        }
    }

    public Model mergeModel(Model model) {
        model.setGroupId(groupId);
        model.setArtifactId(artifactId);
        if (version != null) model.setVersion(version);
        return model;
    }

    public Plugin mergePlugin(Plugin plugin) {
        plugin.setGroupId(groupId);
        plugin.setArtifactId(artifactId);
        if (version != null) plugin.setVersion(version);
        return plugin;
    }

    public ReportPlugin mergeReportPlugin(ReportPlugin plugin) {
        plugin.setGroupId(groupId);
        plugin.setArtifactId(artifactId);
        if (version != null) plugin.setVersion(version);
        return plugin;
    }

    public Extension mergeExtension(Extension extension) {
        extension.setGroupId(groupId);
        extension.setArtifactId(artifactId);
        if (version != null) extension.setVersion(version);
        return extension;
    }

    public Parent mergeParent(Parent parent) {
        parent.setGroupId(groupId);
        parent.setArtifactId(artifactId);
        if (version != null) parent.setVersion(version);
        return parent;
    }
}
