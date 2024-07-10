/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.yaml;

import org.apache.maven.model.ReportPlugin;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class ConstructReportPlugin extends AbstractConstruct {
    @Override
    public Object construct(Node node) {
        ScalarNode scalar = (ScalarNode) node;
        Coordinate coord = Coordinate.createCoordinate(scalar.getValue());
        return coord.mergeReportPlugin(new ReportPlugin());
    }
}
