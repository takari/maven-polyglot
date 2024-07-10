/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot;

import java.io.File;
import java.net.URL;
import java.util.Map;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.building.ModelSource;

/**
 * Support for models.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 0.7
 */
public class PolyglotModelUtil {
    /**
     * Gets the location, as configured via the {@link ModelProcessor#SOURCE} key.
     *
     * Supports values of:
     * <ul>
     * <li>String
     * <li>File
     * <li>URL
     * <li>ModelSource
     * </ul>
     *
     * @return  The model location; or null.
     */
    public static String getLocation(final Map<?, ?> options) {
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

    public static File getLocationFile(final Map<?, ?> options) {
        if (options != null) {
            Object src = options.get(ModelProcessor.SOURCE);
            if (src instanceof URL) {
                return new File(((URL) src).getFile());
            } else if (src != null) {
                ModelSource sm = (ModelSource) src;
                return new File(sm.getLocation());
            }
        }
        return null;
    }
}
