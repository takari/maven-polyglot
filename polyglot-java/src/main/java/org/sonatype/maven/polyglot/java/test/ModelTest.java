package org.sonatype.maven.polyglot.java.test;

import java.util.Arrays;
import org.apache.maven.model.Resource;
import org.sonatype.maven.polyglot.java.dsl.ModelFactory;

public class ModelTest extends ModelFactory {

    @SuppressWarnings({"unchecked"})
    public void project() {

        modelVersion = "4.0";
        groupId = "my-grp";
        artifactId = "my-art";
        version = "1.0";

        parent(artifactId -> "artf_id", version -> "v1", relativePath -> "../..", groupId -> "grp-id-1");

        dependencies(
                dep -> {
                    dep.groupId = "grpid1";
                    dep.artifactId = "art1";
                },
                dep -> {
                    dep.groupId = "grpid2";
                    dep.artifactId = "art2";
                    dep.version = "1.0";
                    dep.exclusions = Arrays.asList("exclgr1:artifact1", "exclgr2:art2");
                });

        dependencies(
                dependency(
                        groupId -> "dep1grp",
                        artifactId -> "art2",
                        exclusions(exclusion(groupId -> "grpToExclude", artifactId -> "artifactToExclue")),
                        version -> "v2"),
                dependency(groupId -> "gr2", artifactId -> "art3"),
                test("org.junit:junit"));

        repositories(repository(id -> "my-repo", url -> "http://myserver/repo"));

        build(
                artifactId -> "artf_id",
                version -> "v1",
                plugins(plugin(
                                artifactId -> "org.apache.maven.plugins",
                                groupId -> "maven-jar-plugin",
                                version -> "2.6",
                                configuration(startXML()
                                        .tag("classifier", tag -> tag.content("pre-process"))
                                        .endXML()),
                                executions(execution(
                                                id -> "pre-process-classes",
                                                phase -> "pre-process",
                                                configuration(startXML()
                                                        .tag("classifier", tag -> tag.content("pre-process"))
                                                        .endXML()))
                                        .get()),
                                pluginDependencies(dependency(
                                        artifactId -> "org.apache.maven.plugins",
                                        groupId -> "maven-jar-plugin",
                                        version -> "2.6")))
                        .get()),
                resources(resource(targetPath -> "d:/", filtering -> "true")),
                testResources((Resource[]) null));

        build().resources(
                        res -> {
                            res.directory = "c://foodir";
                            res.filtering = true;
                            res.targetPath = "c://bardir";
                            res.includes = "*.a";
                            res.excludes = "*.b";
                        },
                        res -> {
                            res.directory = "src/main/resources";
                            res.filtering = true;
                            res.targetPath = "target ";
                        })
                .resources(
                        resource(
                                directory -> "c://foodir",
                                filtering -> "true",
                                targetPath -> "c://bardir",
                                includes("*.a"),
                                excludes("*.b")),
                        resource()
                                .directory("c://foodir")
                                .filtering(true)
                                .targetPath("c://bardir")
                                .includes("*.a")
                                .excludes("*.b")
                                .endResource(),
                        resource(r -> {
                            r.directory = "c://foodir";
                            r.filtering = true;
                            r.targetPath = "c://bardir";
                            r.includes = "*.a";
                            r.excludes = "*.b";
                        }))
                .pluginManagement(
                        plugin("org.apache.rat:apache-rat-plugin")
                                .configuration(startXML()
                                        .tag("excludes", excludes -> {
                                            excludes.child(
                                                    "exclude", exclude -> exclude.content("src/test/resources*/**"));
                                            excludes.child(
                                                    "exclude", exclude -> exclude.content("src/test/projects/**"));
                                            excludes.child(
                                                    "exclude", exclude -> exclude.content("src/test/remote-repo/**"));
                                            excludes.child("exclude", exclude -> exclude.content("**/*.odg"));
                                        })
                                        .endXML()),
                        plugin(
                                groupId -> "org.apache.maven.plugins",
                                artifactId -> "maven-checkstyle-plugin",
                                version -> "2.14"))
                .plugins(plugin("org.codehaus.mojo", "animal-sniffer-maven-plugin", "1.14")
                        .configuration(startXML()
                                .tag("signature", signature -> {
                                    signature.child(
                                            "groupId", groupId -> groupId.content("org.codehaus.mojo.signature"));
                                    signature.child("artifactId", artifactId -> artifactId.content("java17"));
                                    signature.child("version", version -> version.content("1.0"));
                                })
                                .endXML())
                        .executions(
                                execution(id -> "check-java-1.6-compat", phase -> "process-classes", goals("check")),
                                execution("check-java-1.6-compat")
                                        .phase("process-classes")
                                        .goals("check")));

        profile("jboss")
                .activeByDefault(true)
                .activeForFile("exists", "missing")
                .dependencies(
                        dependency(groupId -> "gr1", artifactId -> "art1"),
                        dependency(groupId -> "gr2", artifactId -> "art2"))
                .modules("a", "ss")
                .properties(property(name1 -> "prop1"), property(name2 -> "prop2"))
                .build(profileBuild()
                        .plugins(plugin("")
                                .configuration(startXML()
                                        .tag("classifier", tag -> tag.content("pre-process"))
                                        .endXML()))
                        .resources(resource("directory", "targetPath", true, new String[] {"*"}, null)));

        properties(property(name1 -> "property_1"), property(name2 -> "property_2"));
    }
}
