package org.sonatype.maven.polyglot.java.dsl;

import java.util.Arrays;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class PluginExecutionBuilder {

    private String id = "default";
    private String phase;
    private int priority = 0;
    private java.util.List<String> goals;
    private Xpp3Dom configuration;
    private boolean inherited = true;

    public PluginExecutionBuilder(String id) {
        super();
        this.id = id;
    }

    public PluginExecutionBuilder(String id, String phase) {
        super();
        this.id = id;
        this.phase = phase;
    }

    public PluginExecutionBuilder(String phase, int priority) {
        super();
        this.phase = phase;
        this.priority = priority;
    }

    public PluginExecutionBuilder(String id, String phase, int priority) {
        super();
        this.id = id;
        this.phase = phase;
        this.priority = priority;
    }

    public PluginExecutionBuilder phase(String phase) {
        this.phase = phase;
        return this;
    }

    public PluginExecutionBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    public PluginExecutionBuilder goals(String... goals) {
        if (goals != null) {
            this.goals = Arrays.asList(goals);
        }
        return this;
    }

    public PluginExecutionBuilder configuration(Xpp3Dom configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }
        return this;
    }

    public PluginExecutionBuilder inherited(boolean inherited) {
        this.inherited = inherited;
        return this;
    }

    public PluginExecution get() {
        PluginExecution execution = new PluginExecution();

        if (configuration != null) {
            execution.setConfiguration(configuration);
        }
        if (goals != null) {
            execution.setGoals(goals);
        }
        execution.setId(id);
        execution.setInherited(inherited);
        execution.setPhase(phase);
        execution.setPriority(priority);

        return execution;
    }

    public static class WrapperPluginExecutionBuilder extends PluginExecutionBuilder {

        private PluginExecution pluginExecution;

        public WrapperPluginExecutionBuilder(PluginExecution pluginExecution) {
            super(null);

            this.pluginExecution = pluginExecution;
        }

        @Override
        public PluginExecution get() {
            return pluginExecution;
        }
    }
}
