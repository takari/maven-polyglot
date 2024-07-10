/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.xml;

import java.io.FileInputStream;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.sonatype.maven.polyglot.mapping.MappingSupport;
import org.sonatype.maven.polyglot.xml.xpp3.PolyglotMavenXpp3Reader;

/**
 * XML model mapping.
 *
 */
@Singleton
@Named("xml41")
public class XMLMapping extends MappingSupport {

    public XMLMapping() {
        super("xml41");
        setPomNames("pom.xml41");
        setAcceptLocationExtensions(".xml41", ".xml");
        setAcceptOptionKeys("xml41:4.0.0");
        setPriority(-1);
    }

    @Override
    public boolean accept(Map<String, ?> options) {
        if (options != null) {

            String location = getLocation(options);
            if (location != null) {
                if (location.endsWith(".xml41")) {
                    return true;
                } else if (location.endsWith(".xml")) {
                    return canParse(options);
                }
            }
        }

        return false;
    }

    private boolean canParse(Map<String, ?> options) {
        boolean canParse = false;
        FileInputStream in = null;
        try {
            in = new FileInputStream(getLocation(options));
            PolyglotMavenXpp3Reader reader = new PolyglotMavenXpp3Reader();
            reader.read(in);
            canParse = true;
        } catch (Exception ex) {
            canParse = false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                canParse = false;
            }
        }
        return canParse;
    }
}
