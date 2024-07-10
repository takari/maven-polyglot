/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

public class RubyReaderWithGemspecsTest extends AbstractInjectedTestCase {

    public void testGemspec() throws Exception {
        assertModels("gemspec/Mavenfile", false);
    }

    public void testGemspecInProfile() throws Exception {
        assertModels("gemspec_in_profile/Mavenfile", false);
    }

    public void testGemspecWithAccessToModel() throws Exception {
        assertModels("gemspec_with_access_to_model/Mavenfile", false);
    }

    public void testGemspecWithExtras() throws Exception {
        assertModels("gemspec_with_extras/Mavenfile", false);
    }

    public void testGemspecIncludeJars() throws Exception {
        assertModels("gemspec_include_jars/Mavenfile", false);
    }

    public void testGemspecWithSource() throws Exception {
        assertModels("gemspec_with_source/Mavenfile", false);
    }

    public void testGemspecWithSourceAndNoJar() throws Exception {
        assertModels("gemspec_with_source_and_no_jar/pom.xml", "gemspec_with_source_and_no_jar/Mavenfile", false);
    }

    public void testGemspecWithCustomSource() throws Exception {
        assertModels("gemspec_with_custom_source/pom.xml", "gemspec_with_custom_source/Mavenfile", false);
    }

    public void testGemspecWithSourceAndCustomJarname() throws Exception {
        assertModels(
                "gemspec_with_source_and_custom_jarname/pom.xml",
                "gemspec_with_source_and_custom_jarname/Mavenfile",
                false);
    }

    public void testGemspecWithCustomSourceAndCustomJarname() throws Exception {
        assertModels(
                "gemspec_with_custom_source_and_custom_jarname/pom.xml",
                "gemspec_with_custom_source_and_custom_jarname/Mavenfile",
                false);
    }
}
