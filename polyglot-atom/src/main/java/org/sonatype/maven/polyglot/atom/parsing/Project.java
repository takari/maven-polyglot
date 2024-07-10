/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.atom.parsing;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.model.*;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public class Project extends Element {
    private final Id projectId;
    private final Parent parent;
    private String packaging = "jar";
    private List<Property> properties;
    private final Repositories repositories;
    private final String description;
    private final String url;
    private List<Id> deps;
    private List<Id> overrides;
    private List<String> modules;
    private List<Plugin> pluginOverrides;
    private List<Plugin> plugins;
    private Map<String, String> dirs;
    private static final String MAVEN_CENTRAL_URL = "http://repo1.maven.org/maven2";
    private final ScmElement scm;

    public Project(
            Id projectId,
            Parent parent,
            String packaging,
            List<Property> properties,
            Repositories repositories,
            String description,
            String url,
            List<Id> overrides,
            List<Id> deps,
            List<String> modules,
            List<Plugin> pluginOverrides,
            List<Plugin> plugins,
            Map<String, String> dirs,
            ScmElement scm) {
        this.projectId = projectId;
        this.parent = parent;
        this.packaging = packaging;
        this.properties = properties;
        this.repositories = repositories;
        this.description = description;
        this.url = url;
        this.overrides = overrides;
        this.deps = deps;
        this.modules = modules;
        this.pluginOverrides = pluginOverrides;
        this.plugins = plugins;
        this.dirs = dirs;
        this.scm = scm;
    }

    public Id getProjectId() {
        return projectId;
    }

    public Parent getParent() {
        return parent;
    }

    public String getPackaging() {
        return packaging;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public List<Plugin> getPluginOverrides() {
        return pluginOverrides;
    }

    public List<String> getModules() {
        return modules;
    }

    public List<Id> getOverrides() {
        return overrides;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public Repositories getRepositories() {
        return repositories;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public List<Id> getDeps() {
        return deps;
    }

    public Map<String, String> getDirs() {
        return dirs;
    }

    public Model toMavenModel() {
        Model model = new Model();
        model.setBuild(new Build());
        model.setDescription(description);
        model.setUrl(url);
        model.setName(projectId.getArtifact());
        model.setGroupId(projectId.getGroup());
        model.setVersion(projectId.getVersion());
        model.setArtifactId(projectId.getArtifact());
        model.setModelVersion("4.0.0");

        // parent
        if (parent != null) {
            model.setParent(parent);
        }

        model.setPackaging(packaging);

        if (properties != null) {
            Properties modelProperties = new Properties();
            for (Property p : properties) {
                modelProperties.setProperty(p.getKey(), p.getValue());
            }
            model.setProperties(modelProperties);
        }

        // Add jar repository urls.
        if (null != repositories) {
            for (String repoUrl : repositories.getRepositories()) {
                Repository repository = new Repository();
                repository.setId(Integer.toString(repoUrl.hashCode()));
                repository.setUrl(repoUrl);
                model.addRepository(repository);
            }
        }

        // Add dependency management
        if (overrides != null) {
            DependencyManagement depMan = new DependencyManagement();
            for (Id dep : overrides) {
                Dependency dependency = new Dependency();
                dependency.setGroupId(dep.getGroup());
                dependency.setArtifactId(dep.getArtifact());
                dependency.setVersion(dep.getVersion());
                // JVZ: We need to parse these
                dependency.setType("jar");

                if (null != dep.getClassifier()) {
                    dependency.setClassifier(dep.getClassifier());
                }
                depMan.addDependency(dependency);
            }
            model.setDependencyManagement(depMan);
        }

        // Add project dependencies.
        if (deps != null) {
            for (Id dep : deps) {
                Dependency dependency = new Dependency();
                dependency.setGroupId(dep.getGroup());
                dependency.setArtifactId(dep.getArtifact());
                dependency.setVersion(dep.getVersion());
                // JVZ: We need to parse these
                dependency.setType("jar");

                if (null != dep.getClassifier()) {
                    dependency.setClassifier(dep.getClassifier());
                }
                model.addDependency(dependency);
            }
        }

        if (modules != null) {
            model.setModules(modules);
        }

        if (pluginOverrides != null) {
            PluginManagement management = new PluginManagement();
            management.setPlugins(pluginOverrides);
            model.getBuild().setPluginManagement(management);
        }

        if (plugins != null) {
            model.getBuild().setPlugins(plugins);
        }

        // Optional source dirs customization.
        if (dirs != null) {
            Build build = new Build();
            String srcDir = dirs.get("src");
            String testDir = dirs.get("test");

            if (null != srcDir) build.setSourceDirectory(srcDir);

            if (null != testDir) build.setTestSourceDirectory(testDir);

            model.setBuild(build);
        }

        if (null != scm) {
            Scm scm = new Scm();
            scm.setConnection(this.scm.getConnection());
            scm.setDeveloperConnection(this.scm.getDeveloperConnection());
            scm.setUrl(this.scm.getUrl());

            model.setScm(scm);
        }

        return model;
    }

    public ScmElement getScm() {
        return scm;
    }
}
