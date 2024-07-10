/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

public class RubyReaderWithPomTest extends AbstractInjectedTestCase {

    public void testPomMavenStyle() throws Exception {
        assertModels("pom_maven_style/pom.rb", false);
    }

    public void testPomMavenAlternativeStyle() throws Exception {
        assertModels("pom_maven_alternative_style/pom.rb", false);
    }

    public void testPomMavenHashStyle() throws Exception {
        assertModels("pom_maven_hash_style/pom.rb", false);
    }

    public void testMavenfile() throws Exception {
        assertModels("mavenfile/Mavenfile", false);
    }

    // cd target/rubygems-provided/gems/maven-tools-1.1.0
    // find spec/*/* -name pom.xml | sed -e s/spec.// -e s/.pom.xml// -e "s/\(.*\)/  public void test_\1() throws
    // Exception {\n      assertModels( \"\1\/Mavenfile\", false );\n  }/"
    public void test_gemfile() throws Exception {
        assertModels("gemfile/Mavenfile", false);
    }

    public void test_gemfile_include_jars() throws Exception {
        assertModels("gemfile_include_jars/Mavenfile", false);
    }

    public void test_gemfile_with_access_to_model() throws Exception {
        assertModels("gemfile_with_access_to_model/Mavenfile", false);
    }

    public void test_gemfile_with_custom_source() throws Exception {
        assertModels("gemfile_with_custom_source/Mavenfile", false);
    }

    public void test_gemfile_with_custom_source_and_custom_jarname() throws Exception {
        assertModels("gemfile_with_custom_source_and_custom_jarname/Mavenfile", false);
    }

    public void test_gemfile_with_extras() throws Exception {
        assertModels("gemfile_with_extras/Mavenfile", false);
    }

    public void test_gemfile_with_groups() throws Exception {
        assertModels("gemfile_with_groups/Mavenfile", false);
    }

    public void test_gemfile_with_groups_and_lockfile() throws Exception {
        assertModels("gemfile_with_groups_and_lockfile/Mavenfile", false);
    }

    public void test_gemfile_with_jars_lock() throws Exception {
        assertModels("gemfile_with_jars_lock/Mavenfile", false);
    }

    public void test_gemfile_with_lock() throws Exception {
        assertModels("gemfile_with_lock/Mavenfile", false);
    }

    public void test_gemfile_with_path() throws Exception {
        assertModels("gemfile_with_path/Mavenfile", false);
    }

    public void test_gemfile_with_platforms() throws Exception {
        assertModels("gemfile_with_platforms/Mavenfile", false);
    }

    public void test_gemfile_with_source() throws Exception {
        assertModels("gemfile_with_source/Mavenfile", false);
    }

    public void test_gemfile_with_source_and_custom_jarname() throws Exception {
        assertModels("gemfile_with_source_and_custom_jarname/Mavenfile", false);
    }

    public void test_gemfile_with_source_and_no_jar() throws Exception {
        assertModels("gemfile_with_source_and_no_jar/Mavenfile", false);
    }

    public void test_gemfile_with_test_group() throws Exception {
        assertModels("gemfile_with_test_group/Mavenfile", false);
    }

    public void test_gemfile_with_two_sources() throws Exception {
        assertModels("gemfile_with_two_sources/Mavenfile", false);
    }

    public void test_gemfile_without_gemspec() throws Exception {
        assertModels("gemfile_without_gemspec/Mavenfile", false);
    }

    public void test_gemspec() throws Exception {
        assertModels("gemspec/Mavenfile", false);
    }

    public void test_gemspec_in_profile() throws Exception {
        assertModels("gemspec_in_profile/Mavenfile", false);
    }

    public void test_gemspec_include_jars() throws Exception {
        assertModels("gemspec_include_jars/Mavenfile", false);
    }

    public void test_gemspec_no_rubygems_repo() throws Exception {
        assertModels("gemspec_no_rubygems_repo/Mavenfile", false);
    }

    public void test_gemspec_prerelease() throws Exception {
        assertModels("gemspec_prerelease/Mavenfile", false);
    }

    public void test_gemspec_prerelease_snapshot() throws Exception {
        assertModels("gemspec_prerelease_snapshot/Mavenfile", false);
    }

    public void test_gemspec_with_access_to_model() throws Exception {
        assertModels("gemspec_with_access_to_model/Mavenfile", false);
    }

    public void test_gemspec_with_custom_source() throws Exception {
        assertModels("gemspec_with_custom_source/Mavenfile", false);
    }

    public void test_gemspec_with_custom_source_and_custom_jarname() throws Exception {
        assertModels("gemspec_with_custom_source_and_custom_jarname/Mavenfile", false);
    }

    public void test_gemspec_with_extras() throws Exception {
        assertModels("gemspec_with_extras/Mavenfile", false);
    }

    public void test_gemspec_with_jar_dependencies() throws Exception {
        assertModels("gemspec_with_jar_dependencies/Mavenfile", false);
    }

    public void test_gemspec_with_jars_lock() throws Exception {
        assertModels("gemspec_with_jars_lock/Mavenfile", false);
    }

    public void test_gemspec_with_prereleased_dependency() throws Exception {
        assertModels("gemspec_with_prereleased_dependency/Mavenfile", false);
    }

    public void test_gemspec_with_prereleased_dependency_and_no_repo() throws Exception {
        assertModels("gemspec_with_prereleased_dependency_and_no_repo/Mavenfile", false);
    }

    public void test_gemspec_with_source() throws Exception {
        assertModels("gemspec_with_source/Mavenfile", false);
    }

    public void test_gemspec_with_source_and_custom_jarname() throws Exception {
        assertModels("gemspec_with_source_and_custom_jarname/Mavenfile", false);
    }

    public void test_gemspec_with_source_and_no_jar() throws Exception {
        assertModels("gemspec_with_source_and_no_jar/Mavenfile", false);
    }

    public void test_mavenfile_jrubyJar() throws Exception {
        assertModels("mavenfile_jrubyJar/Mavenfile", false);
    }

    public void test_mavenfile_jrubyWar() throws Exception {
        assertModels("mavenfile_jrubyWar/Mavenfile", false);
    }

    public void test_pom_from_jarfile() throws Exception {
        assertModels("pom_from_jarfile/pom.rb", false);
    }

    public void test_pom_from_jarfile_and_empty_lock() throws Exception {
        assertModels("pom_from_jarfile_and_empty_lock/pom.rb", false);
    }

    public void test_pom_from_jarfile_and_lock() throws Exception {
        assertModels("pom_from_jarfile_and_lock/pom.rb", false);
    }

    public void test_pom_from_jarfile_and_skip_lock() throws Exception {
        assertModels("pom_from_jarfile_and_skip_lock/pom.rb", false);
    }

    public void test_pom_from_jarfile_help_only() throws Exception {
        assertModels("pom_from_jarfile_help_only/pom.rb", false);
    }

    public void test_pom_from_jarfile_with_exclusions() throws Exception {
        assertModels("pom_from_jarfile_with_exclusions/pom.rb", false);
    }

    public void test_pom_from_jarfile_with_jruby() throws Exception {
        assertModels("pom_from_jarfile_with_jruby/pom.rb", false);
    }

    public void test_pom_from_jarfile_with_repos() throws Exception {
        assertModels("pom_from_jarfile_with_repos/pom.rb", false);
    }
    // the maven-tools has a hard-coded version of this
    // polyglot-ruby version. the test will remove the -SNAPSHOT
    // of this version
    public void test_pom_with_execute() throws Exception {
        // FIXME execute tasks are not inheritable
        //      assertModels( "pom_with_execute/pom.rb", false );
    }
}
