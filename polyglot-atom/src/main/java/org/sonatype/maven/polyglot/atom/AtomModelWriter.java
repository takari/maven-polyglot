/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.atom;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.maven.polyglot.atom.parsing.Token;
import org.sonatype.maven.polyglot.io.ModelWriterSupport;

@Singleton
@Named("atom")
public class AtomModelWriter extends ModelWriterSupport {
    private static final Pattern ATOM_REGEX = Pattern.compile("\\d+|true|false");

    protected Logger log = LoggerFactory.getLogger(AtomModelWriter.class);
    String indent = "  ";

    @Override
    public void write(final Writer output, final Map<String, Object> options, final Model model) throws IOException {
        assert output != null;
        assert model != null;

        PrintWriter pw = new PrintWriter(output);

        repositories(pw, model);
        project(pw, model);
        id(pw, model);
        parent(pw, model);
        properties(pw, model);
        dependencyManagement(pw, model);
        dependencies(pw, model);
        modules(pw, model);
        pw.println();
        pluginManagement(pw, model);
        plugins(pw, "plugin", model);

        pw.flush();
        pw.close();
    }

    private void repositories(PrintWriter pw, Model model) {
        List<Repository> repositories = model.getRepositories();
        if (!repositories.isEmpty()) {
            pw.print("repositories << \"");
            for (int i = 0; i < repositories.size(); i++) {
                pw.print(repositories.get(i).getUrl());
                if (i + 1 != repositories.size()) {
                    pw.print(",");
                }
            }
            pw.println("\"");
            pw.println();
        }
    }

    private void project(PrintWriter pw, Model model) {
        String name = model.getName();
        if (name == null) {
            name = model.getArtifactId();
        }
        String url = model.getUrl() == null ? "" : model.getUrl();
        pw.print("project \"" + name + "\" @ \"" + url + "\"");
        packaging(pw, model);
    }

    private void id(PrintWriter pw, Model model) {
        String groupId = model.getGroupId();
        if (groupId == null & model.getParent() != null) {
            groupId = model.getParent().getGroupId();
        }

        String version = model.getVersion();
        if (version == null && model.getParent() != null) {
            version = model.getParent().getVersion();
        }
        pw.println(indent + "id: " + groupId + ":" + model.getArtifactId() + ":" + version);
    }

    private void parent(PrintWriter pw, Model model) {
        if (model.getParent() != null) {
            pw.print(indent + "inherits: " + model.getParent().getGroupId() + ":"
                    + model.getParent().getArtifactId() + ":"
                    + model.getParent().getVersion());
            if (model.getParent().getRelativePath() != null) {
                // pw.println(":" + model.getParent().getRelativePath());
                //        pw.println(":" + "../pom.atom");
            }
            pw.println();
        }
    }

    private void packaging(PrintWriter pw, Model model) {
        pw.println(" as " + model.getPackaging());
    }

    private void properties(PrintWriter pw, Model model) {
        if (!model.getProperties().isEmpty()) {
            List<Object> keys = new ArrayList<Object>(model.getProperties().keySet());
            pw.print(indent + "properties: [ ");
            for (int i = 0; i < keys.size(); i++) {
                Object key = keys.get(i);
                if (i != 0) {
                    pw.print("                ");
                }
                Object value = model.getProperties().get(key);
                if (value != null) {
                    pw.print(key + ": " + toAtom(value.toString()));
                    if (i + 1 != keys.size()) {
                        pw.println();
                    }
                }
            }
            pw.println(" ]");
        }
    }

    private void modules(PrintWriter pw, Model model) {
        List<String> modules = model.getModules();
        if (!modules.isEmpty()) {
            pw.print(indent + "modules: [ ");
            for (int i = 0; i < modules.size(); i++) {
                String module = modules.get(i);
                if (i != 0) {
                    pw.print(indent + "           ");
                }
                pw.print(module);
                if (i + 1 != modules.size()) {
                    pw.println();
                }
            }
            pw.println(" ]");
        }
    }

    private void dependencyManagement(PrintWriter pw, Model model) {
        if (model.getDependencyManagement() != null) {
            deps(pw, "overrides", model.getDependencyManagement().getDependencies());
        }
    }

    private void dependencies(PrintWriter pw, Model model) {
        deps(pw, "deps", model.getDependencies());
    }

    private void deps(PrintWriter pw, String elementName, List<Dependency> deps) {
        if (!deps.isEmpty()) {
            pw.print(indent + elementName + ": [ ");
            for (int i = 0; i < deps.size(); i++) {
                Dependency d = deps.get(i);
                if (i != 0) {
                    pw.print("               ");
                }
                if (d.getVersion() != null) {
                    pw.print(d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion());
                } else {
                    //
                    // We are assuming the model is well-formed and that the parent is providing a version
                    // for this particular dependency.
                    //
                    pw.print(d.getGroupId() + ":" + d.getArtifactId());
                }
                if (d.getClassifier() != null) {
                    pw.print("(" + d.getClassifier() + ")");
                }
                if (i + 1 != deps.size()) {
                    pw.println();
                }
            }
            pw.println(" ]");
        }
    }

    private void pluginManagement(PrintWriter pw, Model model) {
        if (model.getBuild() != null && model.getBuild().getPluginManagement() != null) {
            plugins(
                    pw,
                    Token.PLUGIN_OVERRIDE_KEYWORD,
                    model.getBuild().getPluginManagement().getPlugins());
        }
    }

    private void plugins(PrintWriter pw, String element, Model model) {
        if (model.getBuild() != null && !model.getBuild().getPlugins().isEmpty()) {
            plugins(pw, element, model.getBuild().getPlugins());
        }
    }

    // need to write nested objects
    private void plugins(PrintWriter pw, String element, List<Plugin> plugins) {
        if (!plugins.isEmpty()) {
            for (int i = 0; i < plugins.size(); i++) {
                Plugin plugin = plugins.get(i);
                pw.println("\n" + element);

                pw.print(indent + "id: " + plugin.getGroupId() + ":" + plugin.getArtifactId());
                if (plugin.getVersion() != null) {
                    pw.print(":" + plugin.getVersion());
                }
                if (plugin.getConfiguration() != null) {
                    pw.println();
                    Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
                    printChildren(pw, configuration);
                }
                if (i + 1 != plugins.size()) {
                    pw.println();
                }
            }
            pw.println();
        }
    }

    private boolean flipBrackets = false;

    private void printChildren(PrintWriter pw, Xpp3Dom configuration) {
        if (configuration.getChildCount() > 0) {
            int count = configuration.getChildCount();
            for (int j = 0; j < count; j++) {
                Xpp3Dom c = configuration.getChild(j);
                if (c.getValue() != null) {
                    pw.print(indent + c.getName() + ": " + toAtom(c.getValue()));
                    if (j + 1 != count) {
                        pw.println();
                    }
                } else {

                    String keyString = indent + c.getName() + ": " + lbraceket();

                    if (c.getChildCount() == 0) {
                        pw.print(keyString);
                    } else {
                        pw.println(keyString);
                    }
                    String oldIndent = indent;
                    indent += "  ";
                    flipBrackets = !flipBrackets;
                    printChildren(pw, c);
                    flipBrackets = !flipBrackets;
                    indent = oldIndent;
                    if (c.getChildCount() == 0) {
                        pw.print(rbraceket());
                    } else {
                        pw.print("\n" + indent + rbraceket());
                    }
                }
            }
        }
    }

    /**
     * Quotes the dom element as a string, but only if necessary.
     */
    private String toAtom(String value) {
        return '"' + value + '"';
    }

    private char lbraceket() {
        return (flipBrackets ? '{' : '[');
    }

    private char rbraceket() {
        return (flipBrackets ? '}' : ']');
    }
}
