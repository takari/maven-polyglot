/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.maven.polyglot.mapping.MappingSupport;

/**
 * YAML model mapping.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
@Singleton
@Named("yaml")
public class YamlMapping extends MappingSupport {
  public YamlMapping() {
    super("yaml");
    setPomNames("pom.yaml", "pom.yml");
    setAcceptLocationExtensions(".yaml", ".yml");
    setAcceptOptionKeys("yaml:4.0.0");
    setPriority(1);
  }
}