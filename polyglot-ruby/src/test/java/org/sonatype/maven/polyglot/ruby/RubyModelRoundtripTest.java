/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

// that is just a random collection of real world pom.xml !!!

public class RubyModelRoundtripTest extends AbstractInjectedTestCase {

    public void testSitebricksPom() throws Exception {
        assertRoundtrip("sitebricks-pom.xml", false);
    }

    public void testSitebricksParentPom() throws Exception {
        assertRoundtrip("sitebricks-parent-pom.xml", false);
    }

    public void testJRubyCorePom() throws Exception {
        assertRoundtrip("jruby-core-pom.xml", false);
    }

    public void testJRubyParentPom() throws Exception {
        assertRoundtrip("jruby-parent-pom.xml", false);
    }

    public void testJRubyTestPom() throws Exception {
        assertRoundtrip("jruby-test-pom.xml", false);
    }

    public void testMavenParentPom() throws Exception {
        assertRoundtrip("maven-parent-pom.xml", false);
    }

    public void testJrubyOsgiTestPom() throws Exception {
        assertRoundtrip("jruby-osgi-test-pom.xml", false);
    }
}
