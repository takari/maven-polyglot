/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

public class RubyReaderWithGemfileTest extends AbstractInjectedTestCase {

    public void testGemfile() throws Exception {
        assertModels("gemfile/Mavenfile", false);
    }

    public void testGemfileWithAccessToModel() throws Exception {
        assertModels("gemfile_with_access_to_model/Mavenfile", false);
    }

    public void testGemfileWithExtras() throws Exception {
        assertModels("gemfile_with_extras/Mavenfile", false);
    }

    public void testGemfileIncludeJars() throws Exception {
        assertModels("gemfile_include_jars/Mavenfile", false);
    }

    public void testGemfileWithSource() throws Exception {
        assertModels("gemfile_with_source/Mavenfile", false);
    }

    public void testGemfileWithSourceAndNoJar() throws Exception {
        assertModels("gemfile_with_source_and_no_jar/Mavenfile", false);
    }

    public void testGemfileWithCustomSource() throws Exception {
        assertModels("gemfile_with_custom_source/Mavenfile", false);
    }

    public void testGemfileWithSourceAndCustomJarname() throws Exception {
        assertModels("gemfile_with_source_and_custom_jarname/Mavenfile", false);
    }

    public void testGemfileWithCustomSourceAndCustomJarname() throws Exception {
        assertModels("gemfile_with_custom_source_and_custom_jarname/Mavenfile", false);
    }
}
