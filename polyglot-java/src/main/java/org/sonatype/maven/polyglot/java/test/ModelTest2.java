package org.sonatype.maven.polyglot.java.test;

import org.sonatype.maven.polyglot.java.dsl.ModelFactory;

public class ModelTest2 extends ModelFactory {

    public void project() {

        modelVersion = "4.0.0";
        groupId = "org.sample1";
        artifactId = "project1";
        packaging = "jar";
        version = "1.0-SNAPSHOT";

        dependencies(dependency(groupId -> "junit", artifactId -> "junit", version -> "3.8.1", scope -> "test"));
    }
}
