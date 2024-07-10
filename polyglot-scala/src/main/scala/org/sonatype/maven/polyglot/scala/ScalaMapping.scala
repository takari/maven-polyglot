/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala

import org.sonatype.maven.polyglot.mapping.MappingSupport
import javax.inject.Named

/**
 * Scala model mapping.
 */
@Named("scala")
class ScalaMapping extends MappingSupport("scala") {
  setPomNames("pom.scala")
  setAcceptLocationExtensions(".scala")
  setAcceptOptionKeys("scala:4.0.0")
  setPriority(1)
}
