package org.sonatype.maven.polyglot.java.dsl;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.sonatype.maven.polyglot.java.namedval.NamedValue;
import org.sonatype.maven.polyglot.java.namedval.NamedValueProcessor;

public class ModelFactory implements DependencyFactory, PropertyFactory, BuildFactory {

    protected Model model = new Model();

    protected String modelVersion;
    protected String groupId;
    protected String artifactId;
    protected String version;
    protected String packaging;
    protected String name;
    protected String description;
    protected String url;
    protected String inceptionYear;
    protected String modelEncoding;

    // Field descriptor #98 Lorg/apache/maven/model/Organization;
    private org.apache.maven.model.Organization organization;

    // Field descriptor #100 Ljava/util/List;
    // Signature: Ljava/util/List<Lorg/apache/maven/model/License;>;
    private java.util.List licenses;

    // Field descriptor #100 Ljava/util/List;
    // Signature: Ljava/util/List<Lorg/apache/maven/model/Developer;>;
    private java.util.List developers;

    // Field descriptor #100 Ljava/util/List;
    // Signature: Ljava/util/List<Lorg/apache/maven/model/Contributor;>;
    private java.util.List contributors;

    // Field descriptor #100 Ljava/util/List;
    // Signature: Ljava/util/List<Lorg/apache/maven/model/MailingList;>;
    private java.util.List mailingLists;

    // Field descriptor #110 Lorg/apache/maven/model/Prerequisites;
    private org.apache.maven.model.Prerequisites prerequisites;

    // Field descriptor #112 Lorg/apache/maven/model/Scm;
    private org.apache.maven.model.Scm scm;

    // Field descriptor #114 Lorg/apache/maven/model/IssueManagement;
    private org.apache.maven.model.IssueManagement issueManagement;

    // Field descriptor #116 Lorg/apache/maven/model/CiManagement;
    private org.apache.maven.model.CiManagement ciManagement;

    // Field descriptor #68 Lorg/apache/maven/model/DistributionManagement;
    private org.apache.maven.model.DistributionManagement distributionManagement;

    // Field descriptor #79 Ljava/lang/Object;
    private java.lang.Object reports;

    // Field descriptor #81 Lorg/apache/maven/model/Reporting;
    private org.apache.maven.model.Reporting reporting;

    public void parent(NamedValue... keyValuePairs) {
        Parent parent = new Parent();
        parent = NamedValueProcessor.namedToObject(parent, keyValuePairs);
        model.setParent(parent);
    }

    public void repositories(Repository... repositories) {
        asList(repositories).forEach(repo -> {
            model.addRepository(repo);
        });
    }

    public void pluginRepositories(Repository... repositories) {
        asList(repositories).forEach(repo -> {
            model.addPluginRepository(repo);
        });
    }

    public void dependencies(Dependency... dependencies) {
        asList(dependencies).forEach(dep -> {
            model.addDependency(dep);
        });
    }

    public void dependencies(Consumer<DependencyDTO>... dependencies) {
        if (dependencies != null) {
            for (Consumer<DependencyDTO> consumer : Arrays.asList(dependencies)) {
                DependencyDTO dto = new DependencyDTO();
                consumer.accept(dto);
                model.addDependency(dto.getDependency());
            }
        }
    }

    public void dependencyManagement(Dependency... dependencies) {
        if (model.getDependencyManagement() == null) {
            DependencyManagement dependencyManagement = new DependencyManagement();
            dependencyManagement.setDependencies(new ArrayList<Dependency>());
            model.setDependencyManagement(dependencyManagement);
        }
        asList(dependencies).forEach(dep -> {
            model.getDependencyManagement().addDependency(dep);
        });
    }

    public void dependencyManagement(Consumer<DependencyDTO>... dependencies) {
        if (model.getDependencyManagement() == null) {
            DependencyManagement dependencyManagement = new DependencyManagement();
            dependencyManagement.setDependencies(new ArrayList<Dependency>());
            model.setDependencyManagement(dependencyManagement);
        }
        if (dependencies != null) {
            for (Consumer<DependencyDTO> consumer : Arrays.asList(dependencies)) {
                DependencyDTO dto = new DependencyDTO();
                consumer.accept(dto);
                model.getDependencyManagement().addDependency(dto.getDependency());
            }
        }
    }

    public BuildBuilder build() {
        return new BuildBuilder(model);
    }

    public void modules(String... modules) {
        if (modules != null) {
            Arrays.asList(modules).forEach(module -> model.addModule(module));
        }
    }

    public void build(BuildNamedValue... namedValues) {
        Build build = new Build();

        Map<String, String> map = new HashMap<>();
        asList(namedValues).stream()
                .filter(kvp -> kvp != null)
                .filter(kvp -> !(kvp instanceof BuildComplexTypeNamedValue))
                .forEach(kvp -> map.put(kvp.name(), kvp.value()));
        NamedValueProcessor.mapToObject(build, map);

        for (BuildNamedValue namedvalue : asList(namedValues)) {
            if (namedvalue instanceof BuildPluginsNamedValue) {
                build.setPlugins(((BuildPluginsNamedValue) namedvalue).getPlugins());
            }

            if (namedvalue instanceof PluginManagementNamedValue) {
                build.setPluginManagement(((PluginManagementNamedValue) namedvalue).getPluginManagement());
            }

            if (namedvalue instanceof BuildExtensionNamedValue) {
                build.setExtensions(((BuildExtensionNamedValue) namedvalue).getExtensions());
            }

            if (namedvalue instanceof BuildFiltersNamedValue) {
                build.setFilters(((BuildFiltersNamedValue) namedvalue).getFilters());
            }

            if (namedvalue instanceof BuildResourcesNamedValue) {
                build.setResources(((BuildResourcesNamedValue) namedvalue).getResources());
            }

            if (namedvalue instanceof BuildTestResourcesNamedValue) {
                build.setTestResources(((BuildTestResourcesNamedValue) namedvalue).getResources());
            }
        }

        model.setBuild(build);
    }

    public void properties(PropertyFactory.Property... properties) {
        asList(properties).forEach(prop -> {
            model.addProperty(prop.getName(), prop.getValue());
        });
    }

    public ProfileBuilder profile(String id) {
        ProfileBuilder builder = new ProfileBuilder(model, id);
        return builder;
    }

    public void project() {}

    public Model getModel() {

        project();

        if (modelVersion != null) {
            model.setModelVersion(modelVersion);
        }
        if (groupId != null) {
            model.setGroupId(groupId);
        }
        if (artifactId != null) {
            model.setArtifactId(artifactId);
        }
        if (version != null) {
            model.setVersion(version);
        }
        if (packaging != null) {
            model.setPackaging(packaging);
        }
        if (name != null) {
            model.setName(name);
        }
        if (description != null) {
            model.setDescription(description);
        }
        if (url != null) {
            model.setUrl(url);
        }
        if (inceptionYear != null) {
            model.setInceptionYear(inceptionYear);
        }
        if (modelEncoding != null) {
            model.setModelEncoding(modelEncoding);
        }
        return model;
    }
}
