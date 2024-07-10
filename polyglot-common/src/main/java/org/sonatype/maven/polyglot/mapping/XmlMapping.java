/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.mapping;

import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.building.StringModelSource;

/**
 * Xml model mapping.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
@Singleton
@Named("xml")
public class XmlMapping extends MappingSupport {
    public XmlMapping() {
        super(null);
        setPomNames("pom.xml");
        setAcceptLocationExtensions(".xml", ".pom");
        setAcceptOptionKeys("xml:4.0.0");
    }

    @Override
    public boolean accept(Map<String, ?> options) {
        // assume StringModelSource is default maven, i.e. xml
        if (options != null && options.get(ModelProcessor.SOURCE) instanceof StringModelSource) {
            return true;
        }
        return super.accept(options);
    }
}
