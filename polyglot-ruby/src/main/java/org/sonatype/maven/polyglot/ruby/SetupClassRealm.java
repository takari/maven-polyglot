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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

@Singleton
@Named
public class SetupClassRealm {

    @Inject
    RepositorySystem system;

    @Inject
    protected LegacySupport legacySupport;

    private static final String JRUBY_HOME = "polyglot.jruby.home";

    public void setupArtifact(String gav, ClassRealm realm) throws MalformedURLException {
        try {
            // test if class is already there
            realm.loadClass("org.jruby.embed.ScriptingContainer");
        } catch (ClassNotFoundException e) {

            // add the provided jars for the given artifact
            setup(gav, realm);
        }
    }

    private void setup(String gav, ClassRealm realm) throws MalformedURLException {
        // looking into another JRUBY_HOME is for jruby itself only to allow
        // jruby build to use itself for bootstraping

        // see if we have shall use jruby from somewhere else
        String jrubyHome = System.getenv("POLYGLOT_JRUBY_HOME");
        if (jrubyHome == null) {
            jrubyHome = System.getProperty(JRUBY_HOME);
        }
        if (jrubyHome == null) {
            jrubyHome =
                    legacySupport.getSession().getRequest().getUserProperties().getProperty(JRUBY_HOME);
        }
        if (jrubyHome == null) {
            jrubyHome = legacySupport
                    .getSession()
                    .getRequest()
                    .getSystemProperties()
                    .getProperty(JRUBY_HOME);
        }
        if (jrubyHome == null && legacySupport.getSession().getCurrentProject() != null) {
            jrubyHome = legacySupport
                    .getSession()
                    .getCurrentProject()
                    .getProperties()
                    .getProperty(JRUBY_HOME);
        }

        if (jrubyHome != null) {

            setupFromJrubyHome(jrubyHome, realm);

        } else {

            // use jruby from an artifact
            doSetupArtifact(gav, realm);
        }
    }

    protected void setupFromJrubyHome(String jrubyHome, ClassRealm realm) throws MalformedURLException {
        System.setProperty(JRUBY_HOME, jrubyHome);

        File[] jars = new File(jrubyHome, "lib").listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".jar");
            }
        });
        if (jars == null || jars.length == 0) {
            throw new RuntimeException("found jruby-home variable but no jruby.jar: " + jrubyHome);
        }
        for (File jar : jars) {
            realm.addURL(jar.toURI().toURL());
        }
    }

    public void doSetupArtifact(String gav, ClassRealm realm) throws MalformedURLException {
        String[] parts = gav.split(":");
        Artifact root = system.createArtifact(parts[0], parts[1], parts[2], "pom");
        ArtifactResolutionResult result;
        result = system.resolve(new ArtifactResolutionRequest()
                .setArtifact(root)
                .setCollectionFilter(new ArtifactFilter() {

                    public boolean include(Artifact artifact) {
                        return !"polyglot-common".equals(artifact.getArtifactId())
                                && !"test".equals(artifact.getScope());
                    }
                })
                .setResolveRoot(true)
                .setForceUpdate(true)
                .setOffline(legacySupport.getSession().getRequest().isOffline())
                .setMirrors(legacySupport.getSession().getRequest().getMirrors())
                .setProxies(legacySupport.getSession().getRequest().getProxies())
                .setServers(legacySupport.getSession().getRequest().getServers())
                .setLocalRepository(legacySupport.getSession().getRequest().getLocalRepository())
                .setRemoteRepositories(legacySupport.getSession().getRequest().getRemoteRepositories())
                .setResolveTransitively(true));

        // get searchable list of existing urls
        Set<String> urls = new TreeSet<String>();
        for (URL url : realm.getURLs()) {
            urls.add(url.toString());
        }

        for (Artifact a : result.getArtifacts()) {
            if ("jar".equals(a.getType()) && a.getFile() != null) {
                URL url = a.getFile().toURI().toURL();
                // add only if not already exist
                if (!urls.contains(url.toString())) {
                    realm.addURL(a.getFile().toURI().toURL());
                }
            }
        }
    }
}
