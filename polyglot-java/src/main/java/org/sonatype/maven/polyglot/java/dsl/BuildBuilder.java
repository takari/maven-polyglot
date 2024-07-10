package org.sonatype.maven.polyglot.java.dsl;

import java.util.Arrays;
import org.apache.maven.model.Build;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Model;

public class BuildBuilder extends BuildBaseBuilder {

    private Model model;
    private Build build;

    public BuildBuilder(Model model) {
        super();
        this.model = model;
    }

    @Override
    protected Build getBuild() {
        if (build == null) {
            build = new Build();
            model.setBuild(build);
        }
        return build;
    }

    public BuildBuilder sourceDirectory(String sourceDirectory) {
        if (sourceDirectory != null) {
            getBuild().setSourceDirectory(sourceDirectory);
        }
        return this;
    }

    public BuildBuilder scriptSourceDirectory(String scriptSourceDirectory) {
        if (scriptSourceDirectory != null) {
            getBuild().setScriptSourceDirectory(scriptSourceDirectory);
        }
        return this;
    }

    public BuildBuilder testSourceDirectory(String testSourceDirectory) {
        if (testSourceDirectory != null) {
            getBuild().setTestSourceDirectory(testSourceDirectory);
        }
        return this;
    }

    public BuildBuilder outputDirectory(String outputDirectory) {
        if (outputDirectory != null) {
            getBuild().setOutputDirectory(outputDirectory);
        }
        return this;
    }

    public BuildBuilder testOutputDirectory(String testOutputDirectory) {
        if (testOutputDirectory != null) {
            getBuild().setTestOutputDirectory(testOutputDirectory);
        }
        return this;
    }

    public BuildBuilder extensions(Extension... extensions) {
        if (extensions != null) {
            getBuild().setExtensions(Arrays.asList(extensions));
        }
        return this;
    }
}
