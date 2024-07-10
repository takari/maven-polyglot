/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.mapping;

import java.io.File;
import java.net.URL;
import java.util.Map;
import javax.inject.Inject;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.io.ModelWriter;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * Support for {@link Mapping} implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public abstract class MappingSupport implements Mapping {
    private static final String[] EMPTY = {};

    private String roleHint;

    private String[] pomNames = EMPTY;

    private String[] acceptOptionKeys = EMPTY;

    private String[] acceptLocationExtensions = EMPTY;

    private float priority;

    @Inject
    private PlexusContainer container;

    private ModelReader reader;

    private ModelWriter writer;

    protected MappingSupport(final String roleHint) {
        this.roleHint = roleHint == null ? "default" : roleHint;
    }

    public String getFlavour() {
        return roleHint;
    }

    public ModelReader getReader() {
        if (reader == null) {
            try {
                assert container != null;
                reader = container.lookup(ModelReader.class, roleHint);
            } catch (ComponentLookupException e) {
                throw new RuntimeException(e);
            }
        }
        return reader;
    }

    public ModelWriter getWriter() {
        if (writer == null) {
            try {
                assert container != null;
                writer = container.lookup(ModelWriter.class, roleHint);
            } catch (ComponentLookupException e) {
                throw new RuntimeException(e);
            }
        }
        return writer;
    }

    public String[] getAcceptLocationExtensions() {
        return acceptLocationExtensions;
    }

    public void setAcceptLocationExtensions(final String... accept) {
        this.acceptLocationExtensions = accept;
    }

    public String[] getAcceptOptionKeys() {
        return acceptOptionKeys;
    }

    public void setAcceptOptionKeys(final String... accept) {
        this.acceptOptionKeys = accept;
    }

    public String[] getPomNames() {
        return pomNames;
    }

    public void setPomNames(final String... names) {
        this.pomNames = names;
    }

    public File locatePom(final File dir) {
        assert dir != null;

        for (String name : getPomNames()) {
            File file = new File(dir, name);
            if (file.exists()) {
                return file;
            }
        }

        return null;
    }

    public boolean accept(final Map<String, ?> options) {
        if (options != null) {
            for (String key : getAcceptOptionKeys()) {
                if (options.containsKey(key)) {
                    return true;
                }
            }

            String location = getLocation(options);
            if (location != null) {
                for (String ext : getAcceptLocationExtensions()) {
                    if (location.endsWith(ext)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public float getPriority() {
        return priority;
    }

    protected void setPriority(float priority) {
        this.priority = priority;
    }

    public String getLocation(final Map<?, ?> options) {
        if (options != null) {
            Object tmp = options.get(ModelProcessor.SOURCE);
            if (tmp instanceof String) {
                return (String) tmp;
            } else if (tmp instanceof URL) {
                return ((URL) tmp).toExternalForm();
            } else if (tmp instanceof File) {
                return ((File) tmp).getAbsolutePath();
            } else if (tmp instanceof ModelSource) {
                return ((ModelSource) tmp).getLocation();
            }
        }
        return null;
    }
}
