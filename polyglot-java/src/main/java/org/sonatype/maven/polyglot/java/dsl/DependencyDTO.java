package org.sonatype.maven.polyglot.java.dsl;

import java.util.List;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;

public class DependencyDTO {
    public String groupId;
    public String artifactId;
    public String version;
    public String type = "jar";
    public String classifier;
    public String scope;
    public String systemPath;
    public List<String> exclusions;
    public boolean optional = false;

    public Dependency getDependency() {
        Dependency dependency = new Dependency();

        if (groupId != null) {
            dependency.setGroupId(groupId);
        }
        if (artifactId != null) {
            dependency.setArtifactId(artifactId);
        }
        if (version != null) {
            dependency.setArtifactId(artifactId);
        }
        if (type != null) {
            dependency.setType(type);
        }
        if (classifier != null) {
            dependency.setClassifier(classifier);
        }
        if (scope != null) {
            dependency.setScope(scope);
        }
        if (systemPath != null) {
            dependency.setSystemPath(systemPath);
        }
        if (exclusions != null) {
            for (String exclusionStr : exclusions) {
                String[] parts = exclusionStr.split(":");
                Exclusion exclusion = new Exclusion();
                exclusion.setGroupId(parts[0]);
                exclusion.setArtifactId(parts[1]);
                dependency.addExclusion(exclusion);
            }
        }
        dependency.setOptional(optional);

        return dependency;
    }
}
