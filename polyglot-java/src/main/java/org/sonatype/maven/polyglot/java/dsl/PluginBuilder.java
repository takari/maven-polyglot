package org.sonatype.maven.polyglot.java.dsl;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class PluginBuilder {

    private Plugin plugin;

    public PluginBuilder(String groupId, String artifactId, String version) {
        plugin = new Plugin();
        plugin.setGroupId(groupId);
        plugin.setArtifactId(artifactId);
        plugin.setVersion(version);
    }

    public PluginBuilder(String groupId, String artifactId) {
        plugin = new Plugin();
        plugin.setGroupId(groupId);
        plugin.setArtifactId(artifactId);
    }

    public PluginBuilder(String classifier) {
        plugin = new Plugin();
        String[] parts = classifier.split(":");
        if (parts.length == 2) {
            plugin.setGroupId(parts[0]);
            plugin.setArtifactId(parts[1]);
        } else if (parts.length == 3) {
            plugin.setVersion(parts[2]);
        }
    }

    public PluginBuilder configuration(Xpp3Dom configuration) {
        if (configuration != null) {
            plugin.setConfiguration(configuration);
        }
        return this;
    }

    public PluginBuilder extensions(boolean extensions) {
        plugin.setExtensions(extensions);
        return this;
    }

    public PluginBuilder inherited(boolean inherited) {
        plugin.setInherited(inherited);
        return this;
    }

    public PluginBuilder dependencies(Dependency... dependencies) {
        if (dependencies != null) {
            plugin.setDependencies(Arrays.asList(dependencies));
        }
        return this;
    }

    public PluginBuilder executions(PluginExecutionBuilder... builders) {
        if (builders != null) {
            for (PluginExecutionBuilder executionBuilder : Arrays.asList(builders)) {
                if (plugin.getExecutions() == null) {
                    plugin.setExecutions(new ArrayList<>());
                }
                plugin.addExecution((executionBuilder.get()));
            }
        }
        return this;
    }

    public PluginBuilder endPlugin() {
        return this;
    }

    public Plugin get() {
        return plugin;
    }

    public static class WrapperPluginBuilder extends PluginBuilder {
        private Plugin plugin;

        public WrapperPluginBuilder(Plugin plugin) {
            super(null, null);
            this.plugin = plugin;
        }

        @Override
        public Plugin get() {
            return plugin;
        }
    }
}
