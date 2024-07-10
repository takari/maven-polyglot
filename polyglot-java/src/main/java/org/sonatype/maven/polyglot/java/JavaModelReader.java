/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.java;

import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelParseException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.maven.polyglot.io.ModelReaderSupport;
import org.sonatype.maven.polyglot.java.dsl.ModelFactory;

/**
 * Java model reader.
 *
 */
@Singleton
@Named("java")
public class JavaModelReader extends ModelReaderSupport {

    @Inject
    private PlexusContainer container;

    protected Logger log = LoggerFactory.getLogger(JavaModelReader.class);

    public JavaModelReader() {}

    private String getClassPath() {

        String pathSeparator = System.getProperty("path.separator", ":");

        StringBuilder sb = new StringBuilder();
        for (ClassRealm realm : container.getContainerRealm().getWorld().getRealms()) {
            if (realm.getId().contains("io.takari.polyglot:polyglot-java")) {
                for (URL jarURL : Arrays.asList(realm.getURLs())) {
                    if (sb.length() > 0) {
                        sb.append(pathSeparator);
                    }
                    sb.append(jarURL.getPath());
                }
            }
            if (realm.getId().contains("plexus.core")) {
                for (URL jarURL : Arrays.asList(realm.getURLs())) {
                    if (jarURL.getPath().contains("commons-lang3")
                            || jarURL.getPath().contains("plexus-utils")
                            || jarURL.getPath().contains("maven-model-3")) {
                        if (sb.length() > 0) {
                            sb.append(pathSeparator);
                        }
                        sb.append(jarURL.getPath().replaceAll("/bin/..", ""));
                    }
                }
            }
        }
        String classPath = sb.toString();
        log.debug("Calculated classpath for dynamic POM.java compilation as " + classPath);

        return classPath;
    }

    private String replaceClassNameInSrc(String src, String className) {

        int classWordEnd = src.indexOf("class") + "class".length();
        int extendsStart = src.indexOf("extends");
        String newSrc =
                src.substring(0, classWordEnd) + " " + className + " " + src.substring(extendsStart, src.length());
        log.debug("Replaced class name to be " + className + " full src =" + newSrc);

        return newSrc;
    }

    @SuppressWarnings("unchecked")
    private Model compileJavaCode(String src) {

        Model model = null;

        File tempDir = Files.createTempDir();
        String randomClassName = "POM" + UUID.randomUUID().toString().replaceAll("-", "");
        String filePath = tempDir.getAbsolutePath();
        if (!filePath.endsWith("/")) {
            filePath = filePath + "/";
        }
        filePath = filePath + randomClassName + ".java";

        try {
            Files.write(replaceClassNameInSrc(src, randomClassName), new File(filePath), Charset.defaultCharset());
            log.debug("Created temp file " + filePath + " to compile POM.java");
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("Error writing file " + filePath, e);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, "-parameters", "-classpath", getClassPath(), filePath);
        log.debug("Dynamically compiled class " + filePath);

        try {
            URL comiledClassFolderURL = new URL("file:" + tempDir.getAbsolutePath() + "/");

            ClassRealm systemClassLoader = (ClassRealm) getClass().getClassLoader();
            systemClassLoader.addURL(comiledClassFolderURL);

            log.debug("Added URL " + comiledClassFolderURL + " to Maven class loader to load class dynamically");

            Class<ModelFactory> pomClass =
                    (Class<ModelFactory>) Class.forName(randomClassName, false, systemClassLoader);

            model = pomClass.newInstance().getModel();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException e) {
            log.error("Failed to dynamically load class " + randomClassName, e);
        } finally {
            try {
                FileUtils.deleteDirectory(tempDir);
            } catch (IOException e) {
                log.error("Failed to delete temp folder " + tempDir, e);
            }
        }

        return model;
    }

    @Override
    public Model read(Reader reader, Map<String, ?> options) throws IOException, ModelParseException {
        return compileJavaCode(readFile(reader));
    }

    private String readFile(Reader fileReader) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
