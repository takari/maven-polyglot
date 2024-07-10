/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Model;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.util.IOUtil;
import org.sonatype.maven.polyglot.Constants;
import org.sonatype.maven.polyglot.PolyglotModelUtil;
import org.sonatype.maven.polyglot.execute.ExecuteManager;
import org.sonatype.maven.polyglot.io.ModelReaderSupport;

/**
 * Reads a <tt>pom.rb</tt> and transforms into a Maven {@link Model}.
 *
 * @author m.kristian
 */
@Singleton
@Named("ruby")
public class RubyModelReader extends ModelReaderSupport {

    @Inject
    ExecuteManager executeManager;

    @Inject
    SetupClassRealm setupManager;

    public Model read(final Reader input, final Map<String, ?> options) throws IOException {
        assert input != null;

        // for testing that classloader does not need to be a ClassRealm, i.e. the test setup needs
        // to take care that all classes are in place
        if (getClass().getClassLoader() instanceof ClassRealm) {
            setupManager.setupArtifact(
                    Constants.getGAV("ruby"), (ClassRealm) getClass().getClassLoader());
        }

        // read the stream from our pom.rb into a String
        StringWriter ruby = new StringWriter();
        IOUtil.copy(input, ruby);

        // parse the String and create a POM model
        return new RubyParser(executeManager)
                .parse(ruby.toString(), PolyglotModelUtil.getLocationFile(options), options);
    }
}
