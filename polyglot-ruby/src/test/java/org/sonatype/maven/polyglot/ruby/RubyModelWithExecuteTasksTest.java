/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.sisu.launch.InjectedTestCase;
import org.sonatype.maven.polyglot.PolyglotModelManager;
import org.sonatype.maven.polyglot.execute.ExecuteContext;
import org.sonatype.maven.polyglot.execute.ExecuteManagerImpl;
import org.sonatype.maven.polyglot.execute.ExecuteTask;
import org.sonatype.maven.polyglot.mapping.Mapping;

public class RubyModelWithExecuteTasksTest extends InjectedTestCase {

    @Inject
    @Named("${basedir}/src/test/poms")
    private File poms;

    public void testRubyModelWriter() throws Exception {
        File pom = new File(poms, "pom-with-execute-tasks.rb");

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

        PrintStream out = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));
        try {

            Map<String, Object> options = new HashMap<String, Object>();
            options.put("ruby:4.0.0", true);
            final Model rubyModel = rubyModelReader.read(new FileReader(pom), options);

            //
            // Test for fidelity
            //
            assertNotNull(rubyModel);

            ExecuteContext context = new ExecuteContext() {

                public MavenProject getProject() {
                    return new MavenProject(rubyModel);
                }

                public MavenSession getSession() {
                    return null;
                }

                public File getBasedir() {
                    return getProject().getBasedir();
                }

                public Log getLog() {
                    return null;
                }
            };
            List<ExecuteTask> tasks = rubyModelReader.executeManager.getTasks(rubyModel);

            tasks.get(0).execute(context);
            assertEquals("Execute Ruby Tasks", bytes.toString());

            bytes.reset();
            tasks.get(1).execute(context);
            assertEquals("com.example:ruby-pom:1.0-SNAPSHOT:pom", bytes.toString());

            bytes.reset();
            tasks.get(2).execute(context);
            assertEquals("#<Maven::Polyglot::Parser", bytes.toString().replaceFirst("Parser.*$", "Parser"));

        } finally {
            System.setOut(out);
        }
    }
}
