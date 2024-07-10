/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.mapping;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.io.ModelWriter;

/**
 * Provides a mapping to polyglot specific models.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
public interface Mapping {

    public static final String NAME_PROPERTY = "pom.model.name";
    public static final String DESCRIPTION_PROPERTY = "pom.model.description";
    public static final String URL_PROPERTY = "pom.model.url";
    public static final String PACKAGING_PROPERTY = "pom.model.packaging";
    public static final String VERSION_PROPERTY = "pom.model.version";
    public static final String GROUP_ID_PROPERTY = "pom.model.groupId";
    public static final String ARTIFACT_ID_PROPERTY = "pom.model.artifactId";
    public static final String PROPERTY_PREFIX = "pom.model.property.";

    /**
     * Locates the pom in the given directory
     * @param dir the directory to locate the pom for
     * @return the located pom or <code>null</code> if none was found by this mapping
     */
    File locatePom(File dir);

    /**
     * Tests weather this mapping accepts the given option
     * @param options the options to use
     * @return <code>true</code> if options are accepted, <code>false</code> otherwise
     */
    boolean accept(Map<String, ?> options);

    /**
     *
     * @return the {@link ModelReader} responsible for reading poms returned by the {@link #locatePom(File)} method
     */
    ModelReader getReader();

    /**
     *
     * @return the {@link ModelWriter} responsible for writing poms returned by the {@link #locatePom(File)} method
     */
    ModelWriter getWriter();

    /**
     * get the priority of this mapping, higher priorities are given precedence over those with lower priority, the default priority is 0
     * @return the priority
     */
    float getPriority();

    /**
     * @return the flavor used to identify this mapping (e.g. xml, json, yaml, ...)
     */
    String getFlavour();

    /**
     * A properties object can enhance the returned polyglot model by e.g. a user defined file read by the mapping, supported keys are
     * <ul>
     * <li>{@link #ARTIFACT_ID_PROPERTY}, empty property will be interpreted as not given</li>
     * <li>{@link #GROUP_ID_PROPERTY}, empty property will be interpreted as not given</li>
     * <li>{@link #VERSION_PROPERTY}, empty property will be interpreted as not given</li>
     * <li>{@link #PACKAGING_PROPERTY}, empty property will be interpreted as not given</li>
     * <li>{@link #NAME_PROPERTY}, empty property will be interpreted as not given</li>
     * <li>{@link #DESCRIPTION_PROPERTY}, empty property will be interpreted as not given</li>
     * <li>{@link #URL_PROPERTY}, empty property will be interpreted as not given</li>
     * </ul>
     * other keys are possible but will be silently ignored.
     * If a property starts with {@link #PROPERTY_PREFIX} it will be interpreted as an enhancement to the properties of the model by removing the prefix and using its value as-is.
     * @return a properties object that enhances the the generated model (e.g. by a user provided file) or <code>null</code> if no properties exits
     */
    default Properties getEnhancementProperties(Map<String, ?> options) {
        return null;
    }
}
