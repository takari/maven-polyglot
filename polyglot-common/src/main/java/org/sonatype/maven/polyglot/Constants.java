/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.codehaus.plexus.util.IOUtil;

public class Constants {

    private static Properties polyglotProperties;

    static {
        polyglotProperties = getPolyglotMavenProperties();
    }

    public static String getGroupId() {
        return polyglotProperties.getProperty("groupId");
    }

    public static String getArtifactId(String postfix) {
        return polyglotProperties.getProperty("prefix") + postfix;
    }

    public static String getVersion() {
        return polyglotProperties.getProperty("version");
    }

    public static String getGAV(String postfix) {
        return getGroupId() + ":" + getArtifactId(postfix) + ":" + getVersion();
    }

    public static Properties getPolyglotMavenProperties() {
        Properties properties = new Properties();
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = Constants.class.getClassLoader().getResourceAsStream("maven-polyglot.properties");
            if (resourceAsStream != null) {
                properties.load(resourceAsStream);
            }
        } catch (IOException e) {
            System.err.println("Unable determine version from JAR file: " + e.getMessage());
        } finally {
            IOUtil.close(resourceAsStream);
        }
        return properties;
    }
}
