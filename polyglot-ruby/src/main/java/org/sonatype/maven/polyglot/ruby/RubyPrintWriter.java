/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
package org.sonatype.maven.polyglot.ruby;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.maven.polyglot.ruby.ConfigVisitor.Config;
import org.sonatype.maven.polyglot.ruby.ConfigVisitor.ListItem;
import org.sonatype.maven.polyglot.ruby.ConfigVisitor.Node;

class RubyPrintWriter extends PrintWriter {

    private static final String INDENT = "  ";

    private String current = "";

    RubyPrintWriter(Writer writer) {
        super(writer);
    }

    void inc() {
        current += INDENT;
    }

    void dec() {
        current = current.substring(INDENT.length());
    }

    public void print(String value) {
        append(current).append(value);
    }

    void print(String name, String... values) {
        if (values.length == 1 && values[0] == null) {
            return;
        }
        append(current).append(name).append(" ");
        boolean first = true;
        for (String value : values) {
            if (value != null) {
                if (first) {
                    first = false;
                } else {
                    append(", ");
                }
                if (value.startsWith(":")) {
                    append(value);
                } else {
                    append("'").append(escape(value)).append("'");
                }
            }
        }
    }

    private String escape(String value) {
        return value.replaceAll("([^\\\\])'", "$1\\\\'");
    }

    public void println(String name) {
        print(name);
        println();
    }

    void println(String name, String... values) {
        if (values.length == 1 && values[0] == null) {
            return;
        }
        print(name, values);
        println();
    }

    void printWithOptions(String prefix, Map<String, Object> options, String... args) {
        printWithOptions(prefix, options, null, args);
    }

    void printWithOptions(String prefix, Map<String, Object> options, boolean newLine, String... args) {
        printWithOptions(prefix, options, null, newLine, args);
    }

    void printWithOptions(String prefix, Map<String, Object> options, Object config, String... args) {
        printWithOptions(prefix, options, config, true, args);
    }

    void printWithOptions(String prefix, Map<String, Object> options, Object config, boolean newLine, String... args) {
        if (!options.isEmpty() || config != null) {
            prefix += "(";
            print(prefix, args);
            String indent = prefix.replaceAll(".", " ") + ' ';
            boolean first = args.length == 0;
            for (Map.Entry<String, Object> item : options.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    append(",").println();
                    print(indent);
                }
                appendName(item.getKey()).append(" => ");
                if (item.getValue() instanceof String) {
                    append("'").append(escape(item.getValue().toString())).append("'");
                } else {
                    append(item.getValue().toString());
                }
            }
            if (config != null) {
                printConfiguration(indent, config);
                if (newLine) {
                    println();
                }
            } else {
                append(" )");
                if (newLine) {
                    println();
                }
            }
        } else {
            println(prefix, args);
        }
    }

    void printConfiguration(String indent, Object config) {
        if (config != null) {
            Xpp3Dom configuration = (Xpp3Dom) config;
            if (configuration.getChildCount() != 0) {
                append(",");
                println();
                ConfigVisitor visitor = new ConfigVisitor();
                visitor.visit(new Node(configuration));
                printHashConfig(indent, visitor.config.map);
            }
            append(" )");
        }
    }

    void printHashConfig(String indent, Map<String, Config> base) {
        printHashConfig(indent, base, false);
    }

    RubyPrintWriter appendName(String name) {
        if (name.matches("^[a-zA-Z_]*$")) {
            append(":").append(name);
        } else {
            append("'").append(escape(name)).append("'");
        }
        return this;
    }

    void printHashConfig(String indent, Map<String, Config> base, boolean skipFirst) {
        boolean first = true;
        for (Map.Entry<String, Config> entry : base.entrySet()) {
            if (first) {
                first = false;
                if (!skipFirst) {
                    print(indent);
                }
            } else {
                append(",").println();
                print(indent);
            }
            Config config = entry.getValue();
            switch (config.type) {
                case SINGLE:
                    append("'").append(entry.getKey()).append("' => ");
                    if (config.value != null) {
                        append(" '").append(escape(config.value)).append("'");
                    } else {
                        append("nil");
                    }
                    break;
                case MULTI:
                    append("'").append(entry.getKey()).append("' => [");
                    String ind = indent + "       " + entry.getKey().replaceAll(".", " ");
                    int count = config.stringList.size();
                    for (int j = 0; j < count; ) {
                        String c = config.stringList.get(j);
                        if (c != null) {
                            append(" '").append(escape(c)).append("'");
                        } else {
                            append("nil");
                        }
                        if (++j < count) {
                            append(",");
                            println();
                            print(ind);
                        }
                    }
                    append(" ]");
                    break;
                case MIXED:
                    append("'").append(entry.getKey()).append("' => [");
                    ind = indent + "       " + entry.getKey().replaceAll(".", " ");
                    count = config.list.size();
                    for (int j = 0; j < count; ) {
                        ListItem c = config.list.get(j);
                        if (c.isXml()) {
                            append(" xml( '").append(escape(c.xml)).append("' )");
                        } else {
                            append(" '").append(escape(c.value)).append("'");
                        }
                        if (++j < count) {
                            append(",");
                            println();
                            print(ind);
                        }
                    }
                    append(" ]");
                    break;
                case MAPS:
                    append("'").append(entry.getKey()).append("' => [");
                    ind = indent + "       " + entry.getKey().replaceAll(".", " ");
                    count = config.mapList.size();
                    for (int j = 0; j < count; ) {
                        Map<String, Config> c = config.mapList.get(j);
                        append(" { ");
                        printHashConfig(" " + ind + INDENT, c, true);
                        append(" }");
                        if (++j < count) {
                            append(",");
                            println();
                            print(ind);
                        }
                    }
                    append(" ]");
                    break;
                case MAP:
                    append("'").append(entry.getKey()).append("' => {").println();
                    printHashConfig(indent + INDENT, entry.getValue().map, false);
                    println();
                    print(indent);
                    append("}");
                    break;
                default:
            }
        }
    }

    void printStartBlock(String name) {
        print(name);
        printStartBlock();
    }

    void printStartBlock(String name, String... values) {
        printStartBlock(name, null, values);
    }

    void printStartBlock(String name, Map<String, Object> options, String... values) {
        if (options != null) {
            printWithOptions(name, options, false, values);
        } else {
            print(name, values);
        }
        printStartBlock();
    }

    void printStartBlock() {
        append(" do");
        println();
        inc();
    }

    void printEndBlock() {
        dec();
        append(current).append("end");
        println();
    }
}
