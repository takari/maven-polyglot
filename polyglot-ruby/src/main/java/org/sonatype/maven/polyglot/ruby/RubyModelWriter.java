/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.maven.polyglot.io.ModelWriterSupport;

@Singleton
@Named("ruby")
public class RubyModelWriter extends ModelWriterSupport {

    protected Logger log = LoggerFactory.getLogger(RubyModelWriter.class);

    public void write(final Writer output, final Map<String, Object> options, final Model model) throws IOException {
        assert output != null;
        assert model != null;

        ModelPrinter p = new ModelPrinter(output);
        p.print(model);
    }

    static class ModelPrinter {
        private final RubyPrintWriter p;

        ModelPrinter(Writer output) {
            this.p = new RubyPrintWriter(output);
        }

        void print(Model model) {

            project(model);

            p.flush();
            p.close();
        }

        void repositories(Repository... repositories) {
            printRepositories("repository", repositories);
            if (repositories.length > 0) {
                p.println();
            }
        }

        void pluginRepositories(Repository... repositories) {
            printRepositories("plugin_repository", repositories);
            if (repositories.length > 0) {
                p.println();
            }
        }

        void distribution(DistributionManagement distribution) {
            if (distribution != null) {
                p.printStartBlock("distribution");
                if (distribution.getRepository() != null) {
                    printRepositories("repository", distribution.getRepository());
                }
                if (distribution.getSnapshotRepository() != null) {
                    printRepositories("snapshot_repository", distribution.getSnapshotRepository());
                }
                if (distribution.getSite() != null) {
                    Site site = distribution.getSite();
                    p.printWithOptions(
                            "site",
                            options(
                                    "id", site.getId(),
                                    "name", site.getName(),
                                    "url", site.getUrl()));
                }
                p.printEndBlock();
                p.println();
            }
        }

        private Map<String, Object> options(Object... args) {
            Map<String, Object> options = new LinkedHashMap<String, Object>();
            String key = null;
            for (Object arg : args) {
                if (key == null) {
                    key = arg.toString();
                    continue;
                } else {
                    if (arg != null) {
                        options.put(key, arg);
                    }
                    key = null;
                }
            }
            return options;
        }

        void sourceControl(Scm scm) {
            if (scm != null) {
                p.printWithOptions(
                        "source_control",
                        options(
                                "url", scm.getUrl(),
                                "connection", scm.getConnection(),
                                "developer_connection", scm.getDeveloperConnection(),
                                "tag", scm.getTag().equals("HEAD") ? null : scm.getTag()));
                p.println();
            }
        }

        private void printRepositories(String name, Repository... repositories) {
            for (Repository r : repositories) {
                if (r.getReleases() == null && r.getSnapshots() == null) {
                    p.printWithOptions(
                            name,
                            options(
                                    "id", r.getId(),
                                    "name", r.getName(),
                                    "url", r.getUrl()));
                } else {
                    p.printStartBlock(name, options("id", r.getId(), "name", r.getName(), "url", r.getUrl()));
                    printRepositoryPolicy("releases", r.getReleases());
                    printRepositoryPolicy("snapshots", r.getSnapshots());
                    p.printEndBlock();
                }
            }
        }

        private void printRepositoryPolicy(String name, RepositoryPolicy policy) {
            if (policy != null) {
                if (policy.getChecksumPolicy() == null && policy.getUpdatePolicy() == null) {
                    p.println(name + " " + policy.isEnabled());
                } else {
                    p.println("TODO: " + policy);
                }
            }
        }

        void project(Model model) {
            String name = model.getName();
            if (name == null) {
                name = model.getArtifactId();
            }

            p.printStartBlock("project", name, model.getUrl());
            p.println();

            p.println("model_version", model.getModelVersion());
            p.println("inception_year", model.getInceptionYear());

            id(model);
            parent(model.getParent());

            p.println("packaging", model.getPackaging());
            p.println();

            description(model.getDescription());

            developers(model.getDevelopers());

            issueManagement(model.getIssueManagement());

            mailingLists(model.getMailingLists());

            repositories(toRepositoryArray(model.getRepositories()));

            pluginRepositories(toRepositoryArray(model.getPluginRepositories()));

            sourceControl(model.getScm());

            distribution(model.getDistributionManagement());

            properties(model.getProperties());

            dependencies(model.getDependencies());

            modules(model.getModules());

            managements(model.getDependencyManagement(), model.getBuild());

            build(model.getBuild(), model.getBuild());

            profiles(model.getProfiles());

            reporting(model.getReporting());

            p.printEndBlock();
        }

        private void mailingLists(List<MailingList> mailingLists) {
            for (MailingList list : mailingLists) {
                p.printStartBlock("mailing_list", list.getName());
                p.println("subscribe", list.getSubscribe());
                p.println("unsubscribe", list.getUnsubscribe());
                p.println("post", list.getPost());
                // not thread safe !!
                list.getOtherArchives().add(0, list.getArchive());
                p.println("archives", toArray(list.getOtherArchives()));
                // keep model as is - not thread safe !!
                list.getOtherArchives().remove(0);
                p.printEndBlock();
                p.println();
            }
        }

        private void issueManagement(IssueManagement issueManagement) {
            if (issueManagement != null) {
                p.println("issue_management", issueManagement.getUrl(), issueManagement.getSystem());
                p.println();
            }
        }

        private void developers(List<Developer> developers) {
            for (Developer dev : developers) {
                p.printStartBlock("developer", dev.getId());
                p.println("name", dev.getName());
                p.println("email", dev.getEmail());
                p.println("roles", toArray(dev.getRoles()));
                p.printEndBlock();
                p.println();
            }
        }

        void reporting(Reporting reporting) {
            if (reporting != null) {
                p.printStartBlock("reporting");
                plugins(reporting.getPlugins());
                p.printEndBlock();
                p.println();
            }
        }

        void profiles(List<Profile> profiles) {
            if (profiles != null) {
                for (Profile profile : profiles) {
                    p.print("profile");
                    if (profile.getId() != null) {
                        p.append(" '").append(profile.getId()).append("'");
                        p.printStartBlock();
                        p.println();

                        if (profile.getActivation() != null) {
                            Activation activation = profile.getActivation();
                            p.printStartBlock("activation");
                            {
                                ActivationProperty prop = activation.getProperty();
                                if (prop != null) {
                                    Map<String, Object> options = new LinkedHashMap<String, Object>();
                                    options.put("name", prop.getName());
                                    if (prop.getValue() != null) {
                                        options.put("value", prop.getValue());
                                    }
                                    p.printWithOptions("property", options);
                                }
                            }
                            {
                                ActivationFile file = activation.getFile();
                                if (file != null) {
                                    Map<String, Object> options = new LinkedHashMap<String, Object>();
                                    if (file.getExists() != null) {
                                        options.put("exists", file.getExists());
                                    }
                                    if (file.getMissing() != null) {
                                        options.put("missing", file.getMissing());
                                    }
                                    p.printWithOptions("file", options);
                                }
                            }
                            {
                                String jdk = activation.getJdk();
                                if (jdk != null) {
                                    p.print("jdk", jdk);
                                }
                            }
                            {
                                if (activation.isActiveByDefault()) {
                                    p.print("active_by_default", "true");
                                }
                            }
                            {
                                ActivationOS os = activation.getOs();
                                if (os != null) {
                                    Map<String, Object> options = new LinkedHashMap<String, Object>();
                                    if (os.getArch() != null) {
                                        options.put("arch", os.getArch());
                                    }
                                    if (os.getFamily() != null) {
                                        options.put("family", os.getFamily());
                                    }
                                    if (os.getName() != null) {
                                        options.put("name", os.getName());
                                    }
                                    if (os.getVersion() != null) {
                                        options.put("version", os.getVersion());
                                    }
                                    p.printWithOptions("os", options);
                                }
                            }
                            p.printEndBlock();
                            p.println();
                        }

                        repositories(toRepositoryArray(profile.getRepositories()));

                        pluginRepositories(toRepositoryArray(profile.getPluginRepositories()));

                        distribution(profile.getDistributionManagement());

                        properties(profile.getProperties());

                        dependencies(profile.getDependencies());

                        modules(profile.getModules());

                        managements(profile.getDependencyManagement(), profile.getBuild());

                        build(profile.getBuild());

                        reporting(profile.getReporting());

                        p.printEndBlock();
                        p.println();
                    }
                }
            }
        }

        void description(String description) {
            if (description != null) {
                p.println("description", description);
                p.println();
            }
        }

        void build(BuildBase build) {
            build(build, null);
        }

        void build(BuildBase build, Build b) {
            if (build != null) {
                plugins(build.getPlugins());
                if (build.getDefaultGoal() != null
                        || build.getDirectory() != null
                        || (b != null
                                && (b.getOutputDirectory() != null
                                        || b.getTestOutputDirectory() != null
                                        || b.getSourceDirectory() != null
                                        || b.getTestSourceDirectory() != null))
                        || build.getFinalName() != null
                        || build.getResources().size() > 0
                        || build.getTestResources().size() > 0) {
                    p.println();
                    p.printStartBlock("build");
                    if (build.getDefaultGoal() != null) {
                        p.println("default_goal", build.getDefaultGoal());
                    }
                    if (build.getDirectory() != null) {
                        p.println("directory", build.getDirectory());
                    }
                    if (b != null) {
                        if (b.getOutputDirectory() != null) {
                            p.println("output_directory", b.getOutputDirectory());
                        }
                        if (b.getTestOutputDirectory() != null) {
                            p.println("test_output_directory", b.getTestOutputDirectory());
                        }
                        if (b.getSourceDirectory() != null) {
                            p.println("source_directory", b.getSourceDirectory());
                        }
                        if (b.getTestSourceDirectory() != null) {
                            p.println("test_source_directory", b.getTestSourceDirectory());
                        }
                    }
                    if (build.getFinalName() != null) {
                        p.println("final_name", build.getFinalName());
                    }

                    resource("resource", build.getResources());
                    resource("test_resource", build.getTestResources());

                    p.printEndBlock();
                    p.println();
                }
            }
        }

        private void resource(String name, List<Resource> resources) {
            for (Resource resource : resources) {
                p.println();
                p.printStartBlock(name);
                p.println("directory", resource.getDirectory());
                p.println("includes", toArray(resource.getIncludes()));
                p.println("excludes", toArray(resource.getExcludes()));
                p.println("target_path", resource.getTargetPath());
                // default is false, i.e. null indicates default
                p.println("filtering", resource.isFiltering() ? "true" : null);
                p.printEndBlock();
            }
        }

        void managements(DependencyManagement dependencyManagement, BuildBase build) {
            if ((dependencyManagement != null
                            && !dependencyManagement.getDependencies().isEmpty())
                    || (build != null
                            && build.getPluginManagement() != null
                            && !build.getPluginManagement().getPlugins().isEmpty())) {
                p.printStartBlock("overrides");
                if (dependencyManagement != null) {
                    dependencies(dependencyManagement.getDependencies());
                }
                if (build != null && build.getPluginManagement() != null) {
                    plugins(build.getPluginManagement().getPlugins());
                }
                p.printEndBlock();
                p.println();
            }
        }

        void id(Model model) {
            String groupId = model.getGroupId();
            if (groupId == null & model.getParent() != null) {
                groupId = model.getParent().getGroupId();
            }

            String version = model.getVersion();
            if (version == null && model.getParent() != null) {
                version = model.getParent().getVersion();
            }
            p.println("id", groupId + ":" + model.getArtifactId() + ":" + version);
        }

        void parent(Parent parent) {
            if (parent != null) {
                p.print("inherit", parent.getGroupId() + ":" + parent.getArtifactId() + ":" + parent.getVersion());
                if (parent.getRelativePath() != null) {
                    if (parent.getRelativePath().equals("../pom.xml")) {
                        // p.append( ", '" ).append( "../pom.rb" ).append( "'" );
                        p.println();
                    } else {
                        p.append(", '" + parent.getRelativePath() + "'").println();
                    }
                } else {
                    p.println();
                }
            }
        }

        void properties(Properties properties) {
            if (!properties.isEmpty()) {
                List<Object> keys = new ArrayList<Object>(properties.keySet());
                String prefix = "properties( ";
                String indent = prefix.replaceAll(".", " ");
                p.print(prefix);
                for (int i = 0; i < keys.size(); i++) {
                    Object key = keys.get(i);
                    if (i != 0) {
                        p.print(indent);
                    }
                    Object value = properties.get(key);
                    if (value != null) {
                        if (value instanceof String) {
                            p.append("'" + key + "' => '" + value + "'");
                        } else {
                            p.append("'" + key + "' => " + value);
                        }
                        if (i + 1 != keys.size()) {
                            p.append(",");
                        } else {
                            p.append(" )");
                        }
                        p.println();
                    }
                }
                p.println();
            }
        }

        void modules(List<String> modules) {
            if (!modules.isEmpty()) {
                p.print("modules [ ");
                for (int i = 0; i < modules.size(); i++) {
                    String module = modules.get(i);
                    if (i != 0) {
                        p.append("            ");
                    }
                    p.append("'").append(module).append("'");
                    if (i + 1 != modules.size()) {
                        p.append(",").println();
                    }
                }
                p.append(" ]").println();
                p.println();
            }
        }

        void dependencies(List<Dependency> deps) {
            for (int i = 0; i < deps.size(); i++) {
                Dependency d = deps.get(i);
                Map<String, Object> options = new HashMap<String, Object>();
                String gav;
                if (d.getVersion() != null) {
                    gav = d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion();
                } else {
                    //
                    // We are assuming the model is well-formed and that the
                    // parent or dependencyManagement
                    // is providing a version for this particular dependency.
                    //
                    gav = d.getGroupId() + ":" + d.getArtifactId();
                }
                if (d.getClassifier() != null) {
                    if (d.getVersion() == null) {
                        options.put("classifier", d.getClassifier());
                    } else {
                        gav += ":" + d.getClassifier();
                    }
                }
                if (d.getScope() != null) {
                    options.put("scope", d.getScope());
                }
                if (d.getOptional() != null) {
                    options.put("optional", d.isOptional());
                }
                if (d.getExclusions().size() == 1) {
                    Exclusion e = d.getExclusions().get(0);
                    String ga = e.getGroupId() + ":" + e.getArtifactId();
                    options.put("exclusions", ga);
                } else if (d.getExclusions().size() > 1) {
                    List<String> exclusions =
                            new ArrayList<String>(d.getExclusions().size());
                    for (Exclusion e : d.getExclusions()) {
                        String ga = e.getGroupId() + ":" + e.getArtifactId();
                        exclusions.add(ga);
                    }
                    options.put("exclusions", exclusions);
                }
                final String prefix = options.size() == 0 ? d.getType() + " " : d.getType() + "( ";
                final String indent = prefix.replaceAll(".", " ");

                p.print(prefix);
                p.append("'").append(gav).append("'");
                if (options.size() > 0) {
                    for (Map.Entry<String, Object> item : options.entrySet()) {
                        p.append(",");
                        p.println();
                        p.print(indent);
                        p.append(":");
                        p.append(item.getKey());
                        p.append(" => ");
                        if (item.getValue() instanceof String) {
                            p.append("'");
                            p.append(item.getValue().toString());
                            p.append("'");
                        } else if (item.getValue() instanceof Boolean) {
                            p.append(item.getValue().toString());
                        } else {
                            @SuppressWarnings("unchecked")
                            List<String> list = (List<String>) item.getValue();
                            boolean first = true;
                            p.append("[ ");
                            for (String ex : list) {
                                if (first) {
                                    first = false;
                                } else {
                                    p.append(",");
                                    p.println();
                                    p.print(indent);
                                    p.append("                 ");
                                }
                                p.append("'");
                                p.append(ex);
                                p.append("'");
                            }
                            p.append(" ]");
                        }
                    }
                    p.append(" )");
                }
                p.println();
            }
            if (!deps.isEmpty()) {
                p.println();
            }
        }

        <T extends ConfigurationContainer> void plugins(List<T> plugins) {
            for (int i = 0; i < plugins.size(); i++) {
                T container = plugins.get(i);
                String prefix = container.getConfiguration() == null ? "plugin " : "plugin( ";
                String indent = prefix.replaceAll(".", " ");
                p.print(prefix);
                Plugin plugin = null;
                ReportPlugin rplugin = null;
                if (container instanceof Plugin) {
                    plugin = (Plugin) container;
                    pluginProlog(plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion());
                } else {
                    rplugin = (ReportPlugin) container;
                    pluginProlog(rplugin.getGroupId(), rplugin.getArtifactId(), rplugin.getVersion());
                }
                if (!container.isInherited()) {
                    p.append(", ");
                    p.appendName("inherited").append(" => false");
                }
                if (plugin != null && plugin.isExtensions()) {
                    p.append(", ");
                    p.appendName("extensions").append(" => true");
                }
                p.printConfiguration(indent, container.getConfiguration());
                if (plugin != null
                        && (!plugin.getExecutions().isEmpty()
                                || !plugin.getDependencies().isEmpty())) {
                    p.printStartBlock();

                    dependencies(plugin.getDependencies());

                    for (PluginExecution exec : plugin.getExecutions()) {
                        p.printWithOptions(
                                "execute_goals",
                                options(
                                        "id", "default".equals(exec.getId()) ? null : exec.getId(),
                                        "inherited", exec.isInherited() ? null : "false",
                                        "phase", exec.getPhase()),
                                exec.getConfiguration(),
                                toArray(exec.getGoals()));
                    }

                    p.printEndBlock();
                }
                if (rplugin != null && !rplugin.getReportSets().isEmpty()) {
                    p.printStartBlock();
                    for (ReportSet set : rplugin.getReportSets()) {
                        p.printWithOptions(
                                "report_set",
                                options(
                                        "id", "default".equals(set.getId()) ? null : set.getId(),
                                        "inherited", set.isInherited() ? null : "false"),
                                set.getConfiguration(),
                                toArray(set.getReports()));
                    }
                    p.printEndBlock();
                }
                p.println();
            }
        }

        private String[] toArray(List<String> list) {
            return list.toArray(new String[list.size()]);
        }

        private Repository[] toRepositoryArray(List<Repository> list) {
            return list.toArray(new Repository[list.size()]);
        }

        private void pluginProlog(String groupId, String artifactId, String version) {
            if ("org.apache.maven.plugins".equals(groupId) && artifactId.startsWith("maven-")) {
                String name = artifactId.replaceAll("maven-|-plugin", "");
                if (name.contains("-")) {
                    p.append(":'").append(name).append("'");
                } else {
                    p.append(":").append(name);
                }
                if (version != null) {
                    p.append(", '").append(version).append("'");
                }
            } else {
                p.append("'").append(groupId).append(":").append(artifactId);
                if (version != null) {
                    p.append(":").append(version);
                }
                p.append("'");
            }
        }
    }
}
