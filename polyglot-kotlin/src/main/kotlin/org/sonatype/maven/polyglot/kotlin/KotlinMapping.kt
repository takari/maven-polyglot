package org.sonatype.maven.polyglot.kotlin

import javax.inject.Named
import javax.inject.Singleton
import org.sonatype.maven.polyglot.mapping.MappingSupport

@Singleton
@Named("kotlin")
class KotlinMapping : MappingSupport("kotlin") {

  init {
    setPomNames("pom.kts")
    setAcceptLocationExtensions(".kts")
    setAcceptOptionKeys("kotlin:4.0.0", "kts:4.0.0")
    priority = 1f
  }
}
