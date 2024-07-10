package org.sonatype.maven.polyglot.java.dsl;

import static java.util.Arrays.asList;

import java.util.Arrays;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;

public class ProfileBuilder {

    private Model model;

    private String id = "default";

    /**
     * not covered yet
     */
    private BuildBase build;

    private DistributionManagement distributionManagement;
    private ActivationOS os; // probably will not support this for activation

    public ProfileBuilder(Model model, String id) {
        super();
        this.model = model;
        this.id = id;
    }

    public ProfileBuilder modules(String... modules) {
        if (modules != null) {
            getProfile().setModules(Arrays.asList(modules));
        }
        return this;
    }

    public ProfileBuilder dependencies(Dependency... dependencies) {
        if (dependencies != null) {
            getProfile().setDependencies(Arrays.asList(dependencies));
        }
        return this;
    }

    public ProfileBuilder dependencyManagement(Dependency... dependencies) {
        if (dependencies != null) {
            DependencyManagement dependencyManagement = new DependencyManagement();
            dependencyManagement.setDependencies(Arrays.asList(dependencies));
            getProfile().setDependencyManagement(dependencyManagement);
        }
        return this;
    }

    public ProfileBuilder repositories(Repository... repositories) {
        if (repositories != null) {
            getProfile().setRepositories(Arrays.asList(repositories));
        }
        return this;
    }

    public ProfileBuilder pluginRepositories(Repository... pluginRepositories) {
        if (pluginRepositories != null) {
            getProfile().setPluginRepositories(Arrays.asList(pluginRepositories));
        }
        return this;
    }

    public ProfileBuilder properties(PropertyFactory.Property... properties) {
        if (properties != null) {
            asList(properties).forEach(prop -> {
                getProfile().addProperty(prop.getName(), prop.getValue());
            });
        }
        return this;
    }

    public ProfileBuilder activeByDefault(boolean activeByDefault) {
        getActivation().setActiveByDefault(activeByDefault);
        return this;
    }

    public ProfileBuilder activeForJDK(String jdk) {
        getActivation().setJdk(jdk);
        return this;
    }

    public ProfileBuilder activeForPropertyValue(String name, String value) {
        ActivationProperty activationProperty = new ActivationProperty();
        activationProperty.setName(name);
        activationProperty.setValue(value);
        getActivation().setProperty(activationProperty);
        return this;
    }

    public ProfileBuilder activeForFile(String exists, String missing) {
        ActivationFile activationFile = new ActivationFile();
        if (exists != null) {
            activationFile.setExists(exists);
        }
        if (missing != null) {
            activationFile.setMissing(missing);
        }
        getActivation().setFile(activationFile);
        return this;
    }

    public ProfileBuilder build(BuildBaseBuilder buildBaseBuilder) {
        if (buildBaseBuilder != null) {
            build = buildBaseBuilder.get();
        }
        return this;
    }

    private Activation getActivation() {
        if (getProfile().getActivation() == null) {
            getProfile().setActivation(new Activation());
        }
        return getProfile().getActivation();
    }

    public ProfileBuilder endProfile() {
        return this;
    }

    private Profile getProfile() {
        if (model.getProfiles() != null) {
            for (Profile profile : model.getProfiles()) {
                if (profile.getId().equals(id)) {
                    return profile;
                }
            }
        }

        Profile profile = new Profile();
        profile.setId(id);
        model.addProfile(profile);
        return profile;
    }
}
