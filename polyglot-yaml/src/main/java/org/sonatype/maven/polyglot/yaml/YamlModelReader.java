/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelParseException;
import org.sonatype.maven.polyglot.io.ModelReaderSupport;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

/**
 * YAML model reader.
 *
 * @author jvanzyl
 * @author bentmann
 *
 * @since 0.7
 */
@Singleton
@Named("yaml")
public class YamlModelReader extends ModelReaderSupport {
    private final Yaml yaml;

    public YamlModelReader() {
        ModelConstructor constructor = new ModelConstructor(new LoaderOptions());
        DumperOptions options = new DumperOptions();
        yaml = new Yaml(constructor, new Representer(options), options, new ModelResolver());
    }

    public Model read(Reader input, Map<String, ?> options) throws IOException, ModelParseException {
        if (input == null) {
            throw new IllegalArgumentException("YAML Reader is null.");
        }

        return (Model) yaml.load(input);
    }
}
