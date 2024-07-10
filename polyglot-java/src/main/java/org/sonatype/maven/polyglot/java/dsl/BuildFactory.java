package org.sonatype.maven.polyglot.java.dsl;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Resource;
import org.sonatype.maven.polyglot.java.namedval.NamedValue;
import org.sonatype.maven.polyglot.java.namedval.NamedValueProcessor;

public interface BuildFactory extends PluginFactory {

    public default BuildBaseBuilder profileBuild() {
        return new BuildBaseBuilder();
    }

    public default PluginManagementNamedValue pluginManagement(Plugin... plugins) {
        PluginManagementNamedValue namedValue = new PluginManagementNamedValue();
        PluginManagement pluginManagement = new PluginManagement();

        if (plugins != null) {
            pluginManagement.setPlugins(asList(plugins));
            namedValue.setPluginManagement(pluginManagement);
            return namedValue;
        }

        return null;
    }

    public default BuildFiltersNamedValue filters(String... filters) {
        if (filters != null) {
            BuildFiltersNamedValue namedValue = new BuildFiltersNamedValue();
            namedValue.setFilters(asList(filters));
            return namedValue;
        }
        return null;
    }

    public default BuildPluginsNamedValue plugins(Plugin... plugins) {
        BuildPluginsNamedValue namedValue = new BuildPluginsNamedValue();

        if (plugins != null) {
            namedValue.setPlugins(asList(plugins));
            return namedValue;
        }

        return null;
    }

    public default BuildExtensionNamedValue extensions(Extension... extensions) {
        if (extensions != null) {
            BuildExtensionNamedValue namedValue = new BuildExtensionNamedValue();
            namedValue.setExtensions(asList(extensions));
            return namedValue;
        }
        return null;
    }

    public default Extension extension(NamedValue... keyValuePairs) {
        return NamedValueProcessor.namedToObject(new Extension(), keyValuePairs);
    }

    public default Extension extension(String groupId, String artifactId, String version) {
        Extension extension = new Extension();
        extension.setGroupId(groupId);
        extension.setArtifactId(artifactId);
        extension.setVersion(version);
        return extension;
    }

    public default Extension extension(String classifier) {
        Extension extension = new Extension();

        String[] parts = classifier.split(":");
        if (parts.length == 2) {
            extension.setGroupId(parts[0]);
            extension.setArtifactId(parts[1]);
        } else if (parts.length == 3) {
            extension.setVersion(parts[2]);
        }

        return extension;
    }

    public default ResourceBuilder resource() {
        return new ResourceBuilder();
    }

    public default Resource resource(
            String directory, String targetPath, boolean filtering, String[] includes, String[] excludes) {
        return resource(directory, targetPath, filtering, Arrays.asList(includes), Arrays.asList(excludes));
    }

    public default Resource resource(
            String directory, String targetPath, boolean filtering, List<String> includes, List<String> excludes) {
        Resource resource = new Resource();
        resource.setDirectory(directory);
        if (targetPath != null) {
            resource.setTargetPath(targetPath);
        }
        resource.setFiltering(filtering);
        if (includes != null) {
            resource.setIncludes(includes);
        }
        if (excludes != null) {
            resource.setExcludes(excludes);
        }
        return resource;
    }

    public default BuildResourcesNamedValue resources(Resource... resources) {
        if (resources != null) {
            BuildResourcesNamedValue namedValue = new BuildResourcesNamedValue();
            namedValue.setResources(asList(resources));
            return namedValue;
        }
        return null;
    }

    public default Resource resource(Consumer<ResourceDTO> resourceConsumer) {
        ResourceDTO resource = new ResourceDTO();
        resourceConsumer.accept(resource);
        return resource.getResource();
    }

    public default Resource resource(ResourcesNamedValue... keyValuePairs) {
        Resource resource = new Resource();

        Map<String, String> map = new HashMap<>();
        asList(keyValuePairs).stream()
                .filter(kvp -> kvp != null)
                .filter(kvp ->
                        !(kvp instanceof ResourcesIncludesNamedValue) && !(kvp instanceof ResourcesExcludesNamedValue))
                .forEach(kvp -> map.put(kvp.name(), kvp.value()));
        NamedValueProcessor.mapToObject(resource, map);

        asList(keyValuePairs).stream()
                .filter(kvp -> kvp != null)
                .filter(kvp -> (kvp instanceof ResourcesIncludesNamedValue))
                .forEach(kvp -> resource.setIncludes(((ResourcesIncludesNamedValue) kvp).getIncludes()));

        asList(keyValuePairs).stream()
                .filter(kvp -> kvp != null)
                .filter(kvp -> (kvp instanceof ResourcesExcludesNamedValue))
                .forEach(kvp -> resource.setIncludes(((ResourcesExcludesNamedValue) kvp).getExcludes()));

        return resource;
    }

    public default BuildTestResourcesNamedValue testResources(Resource... resources) {
        if (resources != null) {
            BuildTestResourcesNamedValue namedValue = new BuildTestResourcesNamedValue();
            namedValue.setResources(asList(resources));
            return namedValue;
        }
        return null;
    }

    public default ResourcesIncludesNamedValue includes(String... includes) {
        ResourcesIncludesNamedValue namedValue = new ResourcesIncludesNamedValue();
        if (includes != null) {
            namedValue.setIncludes(asList(includes));
        }
        return namedValue;
    }

    public default ResourcesExcludesNamedValue excludes(String... excludes) {
        ResourcesExcludesNamedValue namedValue = new ResourcesExcludesNamedValue();
        if (excludes != null) {
            namedValue.setExcludes(asList(excludes));
        }
        return namedValue;
    }

    public interface ResourcesNamedValue extends NamedValue {}

    public class ResourcesIncludesNamedValue implements ResourcesNamedValue {

        private List<String> includes = new ArrayList<>();

        public List<String> getIncludes() {
            return includes;
        }

        public void setIncludes(List<String> includes) {
            this.includes = includes;
        }

        @Override
        public String apply(String str) {
            return str;
        }
    }

    public class ResourcesExcludesNamedValue implements ResourcesNamedValue {

        private List<String> excludes;

        public List<String> getExcludes() {
            return excludes;
        }

        public void setExcludes(List<String> excludes) {
            this.excludes = excludes;
        }

        @Override
        public String apply(String str) {
            return str;
        }
    }

    public interface BuildNamedValue extends NamedValue {}

    public interface BuildComplexTypeNamedValue extends BuildNamedValue {}

    public class BuildPluginsNamedValue implements BuildComplexTypeNamedValue {

        private List<Plugin> plugins;

        public List<Plugin> getPlugins() {
            return plugins;
        }

        public void setPlugins(List<Plugin> plugins) {
            this.plugins = plugins;
        }

        @Override
        public String apply(String str) {
            return str;
        }
    }

    public class PluginManagementNamedValue implements BuildComplexTypeNamedValue {

        private PluginManagement pluginManagement;

        public PluginManagement getPluginManagement() {
            return pluginManagement;
        }

        public void setPluginManagement(PluginManagement pluginManagement) {
            this.pluginManagement = pluginManagement;
        }

        @Override
        public String apply(String str) {
            return str;
        }
    }

    public class BuildExtensionNamedValue implements BuildComplexTypeNamedValue {

        private List<Extension> extensions;

        public List<Extension> getExtensions() {
            return extensions;
        }

        public void setExtensions(List<Extension> extensions) {
            this.extensions = extensions;
        }

        @Override
        public String apply(String str) {
            return str;
        }
    }

    public class BuildFiltersNamedValue implements BuildComplexTypeNamedValue {
        private List<String> filters;

        public List<String> getFilters() {
            return filters;
        }

        public void setFilters(List<String> filters) {
            this.filters = filters;
        }

        @Override
        public String apply(String str) {
            return str;
        }
    }

    public class BuildResourcesNamedValue implements BuildComplexTypeNamedValue {

        private List<Resource> resources;

        public List<Resource> getResources() {
            return resources;
        }

        public void setResources(List<Resource> resources) {
            this.resources = resources;
        }

        @Override
        public String apply(String str) {
            return str;
        }
    }

    public class BuildTestResourcesNamedValue implements BuildComplexTypeNamedValue {

        private List<Resource> resources;

        public List<Resource> getResources() {
            return resources;
        }

        public void setResources(List<Resource> resources) {
            this.resources = resources;
        }

        @Override
        public String apply(String str) {
            return str;
        }
    }
}
