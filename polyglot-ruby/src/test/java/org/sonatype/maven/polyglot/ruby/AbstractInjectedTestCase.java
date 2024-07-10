/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.io.ModelWriter;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.sisu.launch.InjectedTestCase;
import org.sonatype.maven.polyglot.Constants;
import org.sonatype.maven.polyglot.PolyglotModelManager;
import org.sonatype.maven.polyglot.execute.ExecuteManagerImpl;
import org.sonatype.maven.polyglot.mapping.Mapping;

public abstract class AbstractInjectedTestCase extends InjectedTestCase {

    private final String VERSION_PATTERN = Constants.getVersion().replaceAll("^.+\\.|-SNAPSHOT$", "");

    @Inject
    @Named("${basedir}/target/rubygems-provided/gems")
    protected File gems;

    @Inject
    @Named("${basedir}/src/test/poms")
    protected File poms;

    private File specs;

    private File specs() {
        if (specs == null) {
            File mavenTools = gems.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().startsWith("maven-tools-");
                }
            })[0];
            specs = new File(mavenTools, "spec");
        }
        return specs;
    }

    protected void assertModels(String pomRuby, boolean debug) throws Exception {

        File dir = new File(specs(), pomRuby).getParentFile();
        File pom = new File(dir, "pom.xml");
        if (!pom.exists()) {
            pom = new File(dir.getParentFile(), "pom.xml");
        }

        assertModels(pom, pomRuby, debug);
    }

    protected void assertModels(String pomXml, String pomRuby, boolean debug) throws Exception {
        assertModels(new File(specs(), pomXml), pomRuby, debug);
    }

    protected void assertModels(File pom, String pomRuby, boolean debug) throws Exception {
        MavenXpp3Reader xmlModelReader = new MavenXpp3Reader();
        Model xmlModel = xmlModelReader.read(new FileInputStream(pom));

        //
        // Read in the Ruby POM
        //
        RubyModelReader rubyModelReader = new RubyModelReader();
        final PolyglotModelManager modelManager = new PolyglotModelManager() {
            {
                mappings = new ArrayList<Mapping>();
            }
        };
        modelManager.addMapping(new RubyMapping());
        rubyModelReader.executeManager = new ExecuteManagerImpl() {
            {
                manager = modelManager;
            }
        };
        rubyModelReader.setupManager = new SetupClassRealm();

        File pomRubyFile = new File(specs(), pomRuby);
        Reader reader = new FileReader(pomRubyFile);
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(ModelProcessor.SOURCE, pomRubyFile.toURI().toURL());
        Model rubyModel = rubyModelReader.read(reader, options);

        assertModels(xmlModel, rubyModel, debug);
    }

    protected void assertRoundtrip(String pomName, boolean debug) throws Exception {
        File pom = new File(poms, pomName);
        MavenXpp3Reader xmlModelReader = new MavenXpp3Reader();
        Model xmlModel = xmlModelReader.read(new FileInputStream(pom));

        //
        // Write out the Ruby POM
        //
        ModelWriter writer = new RubyModelWriter();
        StringWriter w = new StringWriter();
        writer.write(w, new HashMap<String, Object>(), xmlModel);

        if (debug) {
            // Let's take a look at see what's there
            System.out.println(w.toString());
        }

        //
        // Read in the Ruby POM
        //
        RubyModelReader rubyModelReader = new RubyModelReader();
        final PolyglotModelManager modelManager = new PolyglotModelManager() {
            {
                mappings = new ArrayList<Mapping>();
            }
        };
        modelManager.addMapping(new RubyMapping());
        rubyModelReader.executeManager = new ExecuteManagerImpl() {
            {
                manager = modelManager;
            }
        };
        rubyModelReader.setupManager = new SetupClassRealm();
        if (debug) {
            System.out.println(w.toString());
        }
        StringReader reader = new StringReader(w.toString());
        Model rubyModel = rubyModelReader.read(reader, new HashMap<String, Object>());

        //
        // Test for fidelity
        //
        assertNotNull(rubyModel);

        assertModels(xmlModel, rubyModel, debug);
    }

    private void assertModels(Model xmlModel, Model rubyModel, boolean debug) throws IOException {
        MavenXpp3Writer xmlWriter = new MavenXpp3Writer();
        StringWriter ruby = new StringWriter();
        xmlWriter.write(ruby, rubyModel);
        StringWriter xml = new StringWriter();
        xmlWriter.write(xml, xmlModel);

        if (debug) {
            // Let's take a look at see what's there
            System.out.println(xml.toString());
            System.out.println(ruby.toString());
        }

        assertEquals(simplify(xml, debug), simplify(ruby, debug));
    }

    private String simplify(StringWriter xml, boolean debug) {
        String x = xml.toString()
                // no whitespace
                .replaceAll("\\s", "")
                // no process instructions
                .replaceFirst("<\\?.*\\?>", "")
                // properties have different ordering
                .replaceAll("<properties>.*?</properties>", "")
                // allow old style plugin definition to match new one
                .replaceAll("\\$\\{tesla.version\\}", Constants.getVersion())
                // the test cases still use the old groupIds and artifactIds
                .replaceAll("io.tesla.polyglot", "io.takari.polyglot")
                .replaceAll("tesla-polyglot", "polyglot")
                // for the pom_with_execute test
                // hardcoded version from maven-tools, could change more versions then
                // the one from this plugin
                .replaceAll("[0-9]+(-SNAPSHOT)?", VERSION_PATTERN)
                // fix absolute path for test_pom_from_jarfile
                .replaceAll("..basedir./myfirst.jar", "uri:classloader:/myfirst.jar")
                // some of the configuration tags are empty - unify them
                .replaceAll("></(arg|chmod)>", "/>");
        if (debug) {
            System.out.println(x);
        }
        return x;
    }
}
