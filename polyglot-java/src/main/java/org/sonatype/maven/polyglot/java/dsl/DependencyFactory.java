package org.sonatype.maven.polyglot.java.dsl;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.sonatype.maven.polyglot.java.namedval.NamedValue;
import org.sonatype.maven.polyglot.java.namedval.NamedValueProcessor;

public interface DependencyFactory {

    /**
     * Factory method to create dependency.
     * <br>
     * Each key-value pair is a construct <br>
     * 	key -> "value" <br>
     * Where 'key' is the name of the property to set and "value" is it's string value. <br>
     *
     * All possible keys are:<br>
     * <ul>
     * <li>groupId - The project group that produced the dependency, e.g. <code>org.apache.maven</code>.</li>
     * <li>artifactId - The unique id for an artifact produced by the project group, e.g. <code>maven-artifact</code>.</li>
     * <li>version - The version of the dependency, e.g. <code>3.2.1</code>.</li>
     * <li>type - The type of dependency. While it
     *             usually represents the extension on the filename
     * of the dependency,
     *             that is not always the case. A type can be
     * mapped to a different
     *             extension and a classifier.
     *             The type often corresponds to the packaging
     * used, though this is also
     *             not always the case.
     *             Some examples are <code>jar</code>,
     * <code>war</code>, <code>ejb-client</code>
     *             and <code>test-jar</code>: see <a
     * href="../maven-core/artifact-handlers.html">default
     *             artifact handlers</a> for a list.
     *             New types can be defined by plugins that set
     *             <code>extensions</code> to <code>true</code>, so
     * this is not a complete list. Default value is "jar"</li>
     * <li>classifier - The classifier of the dependency. It is appended
     * to
     *             the filename after the version. This allows:
     *             <ul>
     *             <li>referring to attached artifact, for example
     * <code>sources</code> and <code>javadoc</code>:
     *             see <a
     * href="../maven-core/artifact-handlers.html">default artifact
     * handlers</a> for a list,</li>
     *             <li>distinguishing two artifacts
     *             that belong to the same POM but were built
     * differently.
     *             For example, <code>jdk14</code> and
     * <code>jdk15</code>.</li>
     *             </ul></li>
     * <li>scope - The scope of the dependency -
     * <code>compile</code>, <code>runtime</code>,
     *             <code>test</code>, <code>system</code>, and
     * <code>provided</code>. Used to
     *             calculate the various classpaths used for
     * compilation, testing, and so on.
     *             It also assists in determining which artifacts
     * to include in a distribution of
     *             this project. For more information, see
     *             <a
     * href="https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html">the
     *             dependency mechanism</a>. The default scope is
     * <code>compile</code>.</li>
     * <li>systemPath - FOR SYSTEM SCOPE ONLY. Note that use of this
     * property is <b>discouraged</b>
     *             and may be replaced in later versions. This
     * specifies the path on the filesystem
     *             for this dependency.
     *             Requires an absolute path for the value, not
     * relative.
     *             Use a property that gives the machine specific
     * absolute path,
     *             e.g. <code>${java.home}</code>.</li>
     * <li>optional - Indicates the dependency is optional for use of
     * this library. While the
     *             version of the dependency will be taken into
     * account for dependency calculation if the
     *             library is used elsewhere, it will not be passed
     * on transitively. Note: While the type
     *             of this field is <code>String</code> for
     * technical reasons, the semantic type is actually
     *             <code>Boolean</code>. Default value is
     * <code>false</code>.</li>
     * <li>exclusions - see exmplanation below</li>
     * </ul>
     * <p>
     * In addition to key-value pairs described above you can also you special method to define exclusions:
     * <ul>
     * <li>exclusions - List of dependency exclusions. Each individual exclusion can have two properties - groupId and artifactId(optional since you can exclude entire group).
     * <pre>
     * {@code
     * exclusions(
     * 	exclusion(groupId -> "grpToExclude", artifactId -> "artifactToExclue"),
     * 	exclusion(groupId -> "otherGrpToExclude", artifactId -> "otherArtifactToExclue")
     * )
     * }
     * </pre>
     * </li>
     * </ul>
     *
     * @param keyValuePairs - array of key -> "value" pairs. Example: <code>dependency(groupId -> "junit", artifactId -> "junit", version -> "3.8.1", scope -> "test")</code>
     * @return	defined Dependency
     */
    public default Dependency dependency(NamedValue... keyValuePairs) {

        Dependency dependency = new Dependency();

        Map<String, String> map = new HashMap<>();
        asList(keyValuePairs).stream()
                .filter(kvp -> kvp != null)
                .filter(kvp -> !(kvp instanceof DependencyExclusions))
                .forEach(kvp -> map.put(kvp.name(), kvp.value()));
        NamedValueProcessor.mapToObject(dependency, map);

        asList(keyValuePairs).stream()
                .filter(kvp -> kvp != null)
                .filter(kvp -> (kvp instanceof DependencyExclusions))
                .forEach(excl -> ((DependencyExclusions) excl)
                        .getExclusions()
                        .forEach(exclusion -> dependency.addExclusion(exclusion)));

        return dependency;
    }

    public default Dependency dependency(String definition) {
        return dependency(definition, null);
    }

    public default Dependency compile(String definition) {
        return dependency(definition, "compile");
    }

    public default Dependency provided(String definition) {
        return dependency(definition, "provided");
    }

    public default Dependency runtime(String definition) {
        return dependency(definition, "runtime");
    }

    public default Dependency test(String definition) {
        return dependency(definition, "test");
    }

    public default Dependency dependency(String definition, String scope) {

        Dependency dependency = new Dependency();

        String[] parts = definition.split(":");
        dependency.setGroupId(parts[0]);
        dependency.setArtifactId(parts[1]);

        if (parts.length > 2) {
            dependency.setVersion(parts[3]);
        }

        if (scope != null) {
            dependency.setScope(scope);
        }

        return dependency;
    }

    public default Repository repository(String id, String name, String url) {
        return repository(id, name, url, null, null, null);
    }

    /**
     * Factory method to create repository. Used for both - "repositories" and "pluginRepositories"
     * <br>
     * Each key-value pair is a construct <br>
     * 	key -> "value" <br>
     * Where 'key' is the name of the property to set and "value" is it's string value. <br>
     *
     * All possible keys are:<br>
     * <ul>
     * <li>id - A unique identifier for a repository. This is used to match the repository to configuration in the <code>settings.xml</code> file, for example. Furthermore, the identifier is used during POM inheritance and profile injection to detect repositories that should be merged.</li>
     * <li>name - Human readable name of the repository.</li>
     * <li>url - The url of the repository, in the form <code>protocol://hostname/path</code> </li>
     * <li>layout - The type of layout this repository uses for locating and storing artifacts - can be <code>legacy</code> or <code>default</code></li>
     * </ul>
     * @param keyValuePairs - array of key -> "value" pairs. Example: <code>repository(id -> "my-repo", url -> "http://myserver/repo") </code>
     * @return	defined Repository
     */
    public default Repository repository(NamedValue... keyValuePairs) {
        Repository repository = new Repository();
        NamedValueProcessor.namedToObject(repository, keyValuePairs);
        return repository;
    }

    public default Repository repository(
            String id, String name, String url, String layout, RepositoryPolicy releases, RepositoryPolicy snapshots) {
        Repository repository = new Repository();
        if (id != null) {
            repository.setId(id);
        }
        if (name != null) {
            repository.setName(name);
        }
        if (url != null) {
            repository.setUrl(url);
        }
        if (layout != null) {
            repository.setLayout(layout);
        }
        if (releases != null) {
            repository.setReleases(releases);
        }
        if (snapshots != null) {
            repository.setSnapshots(snapshots);
        }
        return repository;
    }

    public default RepositoryPolicy repositoryPolicy(boolean enabled, String updatePolicy, String checksumPolicy) {
        RepositoryPolicy policy = new RepositoryPolicy();
        policy.setEnabled(enabled);
        if (updatePolicy != null) {
            policy.setUpdatePolicy(updatePolicy);
        }
        if (checksumPolicy != null) {
            policy.setChecksumPolicy(checksumPolicy);
        }
        return policy;
    }

    public default NamedValue exclusions(Exclusion... exclusions) {

        DependencyExclusions depExclusions = new DependencyExclusions();
        depExclusions.setExclusions(exclusions);

        return depExclusions;
    }

    public default Exclusion exclusion(NamedValue... keyValuePairs) {
        Exclusion exclusion = new Exclusion();
        return NamedValueProcessor.namedToObject(exclusion, keyValuePairs);
    }

    public class DependencyExclusions implements NamedValue {

        private Exclusion[] exclusions;

        public Stream<Exclusion> getExclusions() {
            return asList(exclusions).stream();
        }

        public void setExclusions(Exclusion[] exclusions) {
            this.exclusions = exclusions;
        }

        @Override
        public String apply(String t) {
            return t;
        }
    }
}
