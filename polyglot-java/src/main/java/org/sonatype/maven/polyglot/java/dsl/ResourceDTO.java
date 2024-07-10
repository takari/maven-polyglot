package org.sonatype.maven.polyglot.java.dsl;

import java.util.Arrays;
import org.apache.maven.model.Resource;

public class ResourceDTO {
    public String targetPath;
    public boolean filtering = false;
    public String directory;
    public String includes;
    public String excludes;

    public Resource getResource() {
        Resource resource = new Resource();

        if (targetPath != null) {
            resource.setTargetPath(targetPath);
        }
        resource.setFiltering(filtering);

        if (directory != null) {
            resource.setDirectory(directory);
        }
        if (includes != null) {
            resource.setIncludes(Arrays.asList(includes.split(",")));
        }
        if (excludes != null) {
            resource.setExcludes(Arrays.asList(excludes.split(",")));
        }

        return resource;
    }
}
