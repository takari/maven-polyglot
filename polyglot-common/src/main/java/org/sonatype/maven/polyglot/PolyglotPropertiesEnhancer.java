/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.sonatype.maven.polyglot;

import static org.sonatype.maven.polyglot.mapping.Mapping.ARTIFACT_ID_PROPERTY;
import static org.sonatype.maven.polyglot.mapping.Mapping.DESCRIPTION_PROPERTY;
import static org.sonatype.maven.polyglot.mapping.Mapping.GROUP_ID_PROPERTY;
import static org.sonatype.maven.polyglot.mapping.Mapping.NAME_PROPERTY;
import static org.sonatype.maven.polyglot.mapping.Mapping.PACKAGING_PROPERTY;
import static org.sonatype.maven.polyglot.mapping.Mapping.PROPERTY_PREFIX;
import static org.sonatype.maven.polyglot.mapping.Mapping.URL_PROPERTY;
import static org.sonatype.maven.polyglot.mapping.Mapping.VERSION_PROPERTY;

import java.util.Properties;
import java.util.function.Consumer;
import org.apache.maven.model.Model;

public class PolyglotPropertiesEnhancer {

    /**
     * Enhances the model by overriding properties
     *
     * @param properties
     *            the overriding properties
     * @param model
     *            the model to enhance
     */
    public static void enhanceModel(Properties properties, Model model) {
        if (properties == null || model == null) {
            // nothing to do then...
            return;
        }
        setModelProperty(model::setArtifactId, ARTIFACT_ID_PROPERTY, properties);
        setModelProperty(model::setGroupId, GROUP_ID_PROPERTY, properties);
        setModelProperty(model::setVersion, VERSION_PROPERTY, properties);
        setModelProperty(model::setPackaging, PACKAGING_PROPERTY, properties);
        setModelProperty(model::setUrl, URL_PROPERTY, properties);
        setModelProperty(model::setDescription, DESCRIPTION_PROPERTY, properties);
        setModelProperty(model::setName, NAME_PROPERTY, properties);
        Properties modelProperties = new Properties(model.getProperties());
        for (String property : properties.stringPropertyNames()) {
            if (property.startsWith(PROPERTY_PREFIX)) {
                modelProperties.setProperty(
                        property.substring(PROPERTY_PREFIX.length()), properties.getProperty(property));
            }
        }
        model.setProperties(modelProperties);
    }

    private static void setModelProperty(Consumer<String> setter, String key, Properties properties) {
        String property = properties.getProperty(key, "");
        if (!property.isEmpty()) {
            setter.accept(property);
        }
    }
}
