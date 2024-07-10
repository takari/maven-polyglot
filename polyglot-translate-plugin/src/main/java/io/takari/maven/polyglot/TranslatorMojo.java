/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.takari.maven.polyglot;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
 * @author Jason van Zyl
 */
@Mojo(name = "translate", requiresProject = false, aggregator = true)
public class TranslatorMojo extends AbstractMojo {

    @Component
    private TeslaModelTranslator translator;

    @Parameter(required = true, property = "input")
    private String input;

    @Parameter(required = true, property = "output")
    private String output;

    @Parameter(readonly = true, required = true, defaultValue = "${reactorProjects}")
    protected List<MavenProject> reactorProjects;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            for (MavenProject project : reactorProjects) {
                File inputFile = new File(project.getBasedir(), input);
                File outputFile = new File(project.getBasedir(), output);
                getLog().info(String.format("Translating %s -> %s", inputFile, outputFile));
                translator.translate(inputFile, outputFile);
            }
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Error translating %s -> %s", input, output), e);
        }
    }
}
