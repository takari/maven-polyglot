/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

import java.io.File;
import java.io.FilenameFilter;
import javax.inject.Named;
import javax.inject.Singleton;
import org.sonatype.maven.polyglot.mapping.MappingSupport;

/**
 * Ruby model mapping.
 *
 * @author m.kristian
 */
@Singleton
@Named("ruby")
public class RubyMapping extends MappingSupport {
    public RubyMapping() {
        super("ruby");
        setPomNames("pom.rb", "Mavenfile", "Jarfile", "Gemfile");
        setAcceptLocationExtensions(".rb", "Mavenfile", "Jarfile", "Gemfile", ".gemspec");
        setAcceptOptionKeys("ruby:4.0.0");
        setPriority(1);
    }

    @Override
    public File locatePom(File dir) {
        File result = super.locatePom(dir);
        if (result == null) {
            String[] list = dir.list(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".gemspec");
                }
            });
            if (list.length == 1) {
                result = new File(dir, list[0]);
            }
        }
        return result;
    }
}
