/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.cli;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.sonatype.maven.polyglot.TeslaModelTranslator;

public class TeslaTranslatorCli {
    private final DefaultPlexusContainer container;

    private final TeslaModelTranslator translator;

    public TeslaTranslatorCli(ClassWorld classWorld) throws Exception {
        if (classWorld == null) {
            classWorld = new ClassWorld("plexus.core", Thread.currentThread().getContextClassLoader());
        }

        ContainerConfiguration cc =
                new DefaultContainerConfiguration().setClassWorld(classWorld).setName("translator");

        container = new DefaultPlexusContainer(cc);
        translator = container.lookup(TeslaModelTranslator.class);
    }

    public TeslaTranslatorCli() throws Exception {
        this(null);
    }

    public int run(final String[] args) throws Exception {
        if (args == null || args.length != 2) {
            System.out.println("usage: translate <input-file> <output-file>");
            return -1;
        }

        File input = new File(args[0]).getCanonicalFile();
        File output = new File(args[1]).getCanonicalFile();

        System.out.println("Translating " + input + " -> " + output);

        translate(input, output);

        return 0;
    }

    public void translate(final File input, final File output) throws IOException {
        assert input != null;
        assert output != null;

        translate(input.toURI().toURL(), output.toURI().toURL());
    }

    public void translate(final URL input, final URL output) throws IOException {
        assert input != null;
        assert output != null;

        translator.translate(input, output);
    }

    public static void main(final String[] args) throws Exception {
        int result = main(args, null);
        System.exit(result);
    }

    public static int main(final String[] args, final ClassWorld classWorld) throws Exception {
        assert classWorld != null;
        return new TeslaTranslatorCli(classWorld).run(args);
    }
}
