package org.sonatype.maven.polyglot.java.dsl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Resource;

public class BuildBaseBuilder {

    private BuildBase build = new BuildBase();

    protected BuildBase getBuild() {
        return build;
    }

    public BuildBaseBuilder defaultGoal(String defaultGoal) {
        if (defaultGoal != null) {
            getBuild().setDefaultGoal(defaultGoal);
        }
        return this;
    }

    public BuildBaseBuilder directory(String directory) {
        if (directory != null) {
            getBuild().setDirectory(directory);
        }
        return this;
    }

    public BuildBaseBuilder finalName(String finalName) {
        if (finalName != null) {
            getBuild().setFinalName(finalName);
        }
        return this;
    }

    public BuildBaseBuilder filters(String... filters) {
        if (filters != null) {
            getBuild().setFilters(Arrays.asList(filters));
        }
        return this;
    }

    public BuildBaseBuilder resources(Resource... resources) {
        if (resources != null) {
            if (getBuild().getResources() == null) {
                getBuild().setResources(new ArrayList<>());
            }
            for (Resource resource : Arrays.asList(resources)) {
                getBuild().addResource(resource);
            }
        }
        return this;
    }

    public BuildBaseBuilder resources(Consumer<ResourceDTO>... resources) {
        if (resources != null) {
            if (getBuild().getResources() == null) {
                getBuild().setResources(new ArrayList<>());
            }
            for (Consumer<ResourceDTO> consumer : Arrays.asList(resources)) {
                ResourceDTO dto = new ResourceDTO();
                consumer.accept(dto);
                getBuild().addResource(dto.getResource());
            }
        }
        return this;
    }

    public BuildBaseBuilder resources(ResourceBuilder... resourceBuilders) {
        if (resourceBuilders != null) {
            if (getBuild().getResources() == null) {
                getBuild().setResources(new ArrayList<>());
            }
            for (ResourceBuilder resourceBuilder : Arrays.asList(resourceBuilders)) {
                getBuild().addResource(resourceBuilder.endResource());
            }
        }
        return this;
    }

    public BuildBaseBuilder testResources(ResourceBuilder... resourceBuilders) {
        if (resourceBuilders != null) {
            if (getBuild().getTestResources() == null) {
                getBuild().setTestResources(new ArrayList<>());
            }
            for (ResourceBuilder resourceBuilder : Arrays.asList(resourceBuilders)) {
                getBuild().addTestResource(resourceBuilder.endResource());
            }
        }
        return this;
    }

    public BuildBaseBuilder testResources(Resource... testResources) {
        if (testResources != null) {
            if (getBuild().getTestResources() == null) {
                getBuild().setTestResources(new ArrayList<>());
            }
            for (Resource testResource : Arrays.asList(testResources)) {
                getBuild().addTestResource(testResource);
            }
        }
        return this;
    }

    public BuildBaseBuilder plugins(PluginBuilder... builders) {
        if (builders != null) {
            if (getBuild().getPlugins() == null) {
                getBuild().setPlugins(new ArrayList<Plugin>());
            }

            Arrays.asList(builders).stream().forEach(builder -> getBuild().addPlugin(builder.get()));
        }
        return this;
    }

    public BuildBaseBuilder pluginManagement(PluginBuilder... builders) {
        if (builders != null) {
            if (getBuild().getPluginManagement() == null) {
                getBuild().setPluginManagement(new PluginManagement());
                getBuild().getPluginManagement().setPlugins(new ArrayList<Plugin>());
            }

            Arrays.asList(builders).stream()
                    .forEach(builder -> getBuild().getPluginManagement().addPlugin(builder.get()));
        }
        return this;
    }

    public BuildBaseBuilder endBuild() {
        return this;
    }

    public BuildBase get() {
        return getBuild();
    }
}
