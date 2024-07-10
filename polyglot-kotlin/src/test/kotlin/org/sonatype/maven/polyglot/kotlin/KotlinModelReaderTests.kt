package org.sonatype.maven.polyglot.kotlin

import org.junit.Test
import org.sonatype.maven.polyglot.kotlin.testing.AbstractModelTestCase

class KotlinModelReaderTests(testName: String) : AbstractModelTestCase(testName) {

  // @formatter:off

  @Test fun `test#example`() = doTest()

  @Test fun `test#convert#kotlin-to-xml#variation-0`() = doTest()

  @Test fun `test#convert#kotlin-to-xml#variation-1`() = doTest()

  @Test fun `test#convert#kotlin-to-xml#variation-2`() = doTest()

  @Test fun `test#convert#kotlin-to-xml#variation-3`() = doTest()

  @Test fun `test#convert#kotlin-to-xml#variation-4`() = doTest()

  @Test fun `test#convert#kotlin-to-xml#variation-5`() = doTest()

  // @formatter:on

  private fun doTest() {
    assertEquals(pomXml, pomKts.kts2model().model2xml())
  }
}
