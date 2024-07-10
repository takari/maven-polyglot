package org.sonatype.maven.polyglot.java.dsl;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.maven.polyglot.java.dsl.PluginBuilder.WrapperPluginBuilder;
import org.sonatype.maven.polyglot.java.dsl.PluginExecutionBuilder.WrapperPluginExecutionBuilder;
import org.sonatype.maven.polyglot.java.namedval.NamedValue;
import org.sonatype.maven.polyglot.java.namedval.NamedValueProcessor;
import org.sonatype.maven.polyglot.java.xml.ConfiugrationXmlBuilder;

public interface PluginFactory extends DependencyFactory {

    public default PluginExecutionBuilder execution(String id) {
        return new PluginExecutionBuilder(id);
    }

    public default PluginExecutionBuilder execution(String id, String phase) {
        return new PluginExecutionBuilder(id, phase);
    }

    public default PluginExecutionBuilder execution(String phase, int priority) {
        return new PluginExecutionBuilder(phase, priority);
    }

    public default PluginExecutionBuilder execution(String id, String phase, int priority) {
        return new PluginExecutionBuilder(id, phase, priority);
    }

    public default PluginBuilder plugin(String groupId, String artifactId, String version) {
        return new PluginBuilder(groupId, artifactId, version);
    }

    public default PluginBuilder plugin(String groupId, String artifactId) {
        return new PluginBuilder(groupId, artifactId);
    }

    public default PluginBuilder plugin(String classifier) {
        return new PluginBuilder(classifier);
    }

    public default PluginExecutionGoalsNamedValue goals(String... goals) {
        if (goals != null) {
            PluginExecutionGoalsNamedValue container = new PluginExecutionGoalsNamedValue();
            container.setGoals(Arrays.asList(goals));
            return container;
        }
        return null;
    }

    public default WrapperPluginBuilder plugin(PluginNamedValue... namedValues) {
        Plugin plugin = new Plugin();

        Map<String, String> map = new HashMap<>();
        for (PluginNamedValue kvp : namedValues) {
            if (kvp instanceof ConfigurationNamedValue) {
                plugin.setConfiguration(((ConfigurationNamedValue) kvp).getConfiguration());
            } else if (kvp instanceof ExecutionsNamedValue) {
                plugin.setExecutions(((ExecutionsNamedValue) kvp).getExecutions());
            } else if (kvp instanceof DependenciesNamedValue) {
                plugin.setDependencies(((DependenciesNamedValue) kvp).getDependencies());
            } else {
                map.put(kvp.name(), kvp.value());
                NamedValueProcessor.mapToObject(plugin, map);
            }
        }

        return new WrapperPluginBuilder(plugin);
    }

    public interface PluginNamedValue extends NamedValue {}

    public interface PluginExecutionNamedValue extends NamedValue {}

    public default PluginNamedValue pluginDependencies(Dependency... dependencies) {

        DependenciesNamedValue deps = new DependenciesNamedValue();
        deps.setDependencies(asList(dependencies));
        return deps;
    }

    public default PluginNamedValue executions(PluginExecution... executions) {

        ExecutionsNamedValue exec = new ExecutionsNamedValue();
        exec.setExecutions(asList(executions));
        return exec;
    }

    public default WrapperPluginExecutionBuilder execution(PluginExecutionNamedValue... namedValues) {
        PluginExecution execution = new PluginExecution();

        Map<String, String> map = new HashMap<>();
        for (PluginExecutionNamedValue kvp : namedValues) {
            if (kvp instanceof ConfigurationNamedValue) {
                execution.setConfiguration(((ConfigurationNamedValue) kvp).getConfiguration());
            } else if (kvp instanceof PluginExecutionGoalsNamedValue) {
                execution.setGoals(((PluginExecutionGoalsNamedValue) kvp).getGoals());
            } else {
                map.put(kvp.name(), kvp.value());
                NamedValueProcessor.mapToObject(execution, map);
            }
        }

        return new WrapperPluginExecutionBuilder(execution);
    }

    public default ConfiugrationXmlBuilder startXML() {
        return new ConfiugrationXmlBuilder().startXML();
    }

    public default ConfigurationNamedValue configuration(Xpp3Dom configuration) {

        ConfigurationNamedValue conf = new ConfigurationNamedValue();
        conf.setConfiguration(configuration);

        return conf;
    }

    public class PluginExecutionGoalsNamedValue implements PluginExecutionNamedValue {

        private List<String> goals;

        public List<String> getGoals() {
            return goals;
        }

        public void setGoals(List<String> goals) {
            this.goals = goals;
        }

        @Override
        public String apply(String str) {
            return str;
        }
    }

    public class ConfigurationNamedValue implements PluginNamedValue, PluginExecutionNamedValue {

        private Xpp3Dom configuration;

        public Xpp3Dom getConfiguration() {
            return configuration;
        }

        public void setConfiguration(Xpp3Dom configuration) {
            this.configuration = configuration;
        }

        @Override
        public String apply(String t) {
            return t;
        }
    }

    public class ExecutionsNamedValue implements PluginNamedValue {

        private List<PluginExecution> executions;

        public List<PluginExecution> getExecutions() {
            return executions;
        }

        public void setExecutions(List<PluginExecution> executions) {
            this.executions = executions;
        }

        @Override
        public String apply(String t) {
            return t;
        }
    }

    public class DependenciesNamedValue implements PluginNamedValue {

        private List<Dependency> dependencies;

        public List<Dependency> getDependencies() {
            return dependencies;
        }

        public void setDependencies(List<Dependency> dependencies) {
            this.dependencies = dependencies;
        }

        @Override
        public String apply(String t) {
            return t;
        }
    }
}
