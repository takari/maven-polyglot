/**
 * Copyright (c) 2012, 2016 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.takari.maven.polyglot;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.maven.polyglot.TeslaModelTranslator;

/**
 * Polgyglot model translator Mojo.
 *
 */
@Mojo(name = "translate-project")
public class TranslateProjectMojo extends AbstractMojo {

    @Component
    private TeslaModelTranslator translator;

    @Parameter(required = true, property = "input")
    private String input;

    @Parameter(required = true, property = "output")
    private String output;

    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            File inputFile = new File(mavenProject.getBasedir(), input);
            File outputFile = new File(mavenProject.getBasedir(), output);
            getLog().info(String.format("Translating %s -> %s", inputFile, outputFile));
            translator.translate(inputFile, outputFile);
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Error translating %s -> %s", input, output), e);
        }
    }
}
