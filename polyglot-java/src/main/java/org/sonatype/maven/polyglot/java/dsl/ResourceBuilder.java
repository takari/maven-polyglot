package org.sonatype.maven.polyglot.java.dsl;

import java.util.Arrays;
import org.apache.maven.model.Resource;

public class ResourceBuilder {

    private Resource resource = new Resource();

    public ResourceBuilder directory(String directory) {
        if (directory != null) {
            resource.setDirectory(directory);
        }
        return this;
    }

    public ResourceBuilder targetPath(String targetPath) {
        if (targetPath != null) {
            resource.setTargetPath(targetPath);
        }
        return this;
    }

    public ResourceBuilder filtering(boolean filtering) {
        resource.setFiltering(filtering);
        return this;
    }

    public ResourceBuilder includes(String... includes) {
        if (includes != null) {
            resource.setIncludes(Arrays.asList(includes));
        }

        return this;
    }

    public ResourceBuilder excludes(String... excludes) {
        if (excludes != null) {
            resource.setExcludes(Arrays.asList(excludes));
        }

        return this;
    }

    public Resource endResource() {
        return resource;
    }
}
