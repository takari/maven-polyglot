package org.sonatype.maven.polyglot.kotlin.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DSLSupportTests {

  @Test
  fun `splitGavtc with gav (1 of 1)`() {
    assertThat(splitCoordinates("com.example:sample-lib:1.0", 3))
        .containsExactly("com.example", "sample-lib", "1.0")
    val (groupId, artifactId, version) = splitCoordinates("com.example:sample-lib:1.0", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isEqualTo("com.example")
    assertThat(dep.artifactId).isEqualTo("sample-lib")
    assertThat(dep.version).isEqualTo("1.0")
  }

  @Test
  fun `splitGavtc with ga_ (1 of 2)`() {
    assertThat(splitCoordinates("com.example:sample-lib:", 3))
        .containsExactly("com.example", "sample-lib", null)
    val (groupId, artifactId, version) = splitCoordinates("com.example:sample-lib:", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isEqualTo("com.example")
    assertThat(dep.artifactId).isEqualTo("sample-lib")
    assertThat(dep.version).isNull()
  }

  @Test
  fun `splitGavtc with ga_ (2 of 2)`() {
    assertThat(splitCoordinates("com.example:sample-lib", 3))
        .containsExactly("com.example", "sample-lib", null)
    val (groupId, artifactId, version) = splitCoordinates("com.example:sample-lib", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isEqualTo("com.example")
    assertThat(dep.artifactId).isEqualTo("sample-lib")
    assertThat(dep.version).isNull()
  }

  @Test
  fun `splitGavtc with g_v (1 of 1)`() {
    assertThat(splitCoordinates("com.example::1.0", 3)).containsExactly("com.example", null, "1.0")
    val (groupId, artifactId, version) = splitCoordinates("com.example::1.0", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isEqualTo("com.example")
    assertThat(dep.artifactId).isNull()
    assertThat(dep.version).isEqualTo("1.0")
  }

  @Test
  fun `splitGavtc with g__ (1 of 3)`() {
    assertThat(splitCoordinates("com.example::", 3)).containsExactly("com.example", null, null)
    val (groupId, artifactId, version) = splitCoordinates("com.example::", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isEqualTo("com.example")
    assertThat(dep.artifactId).isNull()
    assertThat(dep.version).isNull()
  }

  @Test
  fun `splitGavtc with g__ (2 of 3)`() {
    assertThat(splitCoordinates("com.example:", 3)).containsExactly("com.example", null, null)
    val (groupId, artifactId, version) = splitCoordinates("com.example:", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isEqualTo("com.example")
    assertThat(dep.artifactId).isNull()
    assertThat(dep.version).isNull()
  }

  @Test
  fun `splitGavtc with g__ (3 of 3)`() {
    assertThat(splitCoordinates("com.example", 3)).containsExactly("com.example", null, null)
    val (groupId, artifactId, version) = splitCoordinates("com.example", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isEqualTo("com.example")
    assertThat(dep.artifactId).isNull()
    assertThat(dep.version).isNull()
  }

  @Test
  fun `splitGavtc with _av (1 of 1)`() {
    assertThat(splitCoordinates(":sample-lib:1.0", 3)).containsExactly(null, "sample-lib", "1.0")
    val (groupId, artifactId, version) = splitCoordinates(":sample-lib:1.0", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isNull()
    assertThat(dep.artifactId).isEqualTo("sample-lib")
    assertThat(dep.version).isEqualTo("1.0")
  }

  @Test
  fun `splitGavtc with _a_ (1 of 2)`() {
    assertThat(splitCoordinates(":sample-lib:", 3)).containsExactly(null, "sample-lib", null)
    val (groupId, artifactId, version) = splitCoordinates(":sample-lib", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isNull()
    assertThat(dep.artifactId).isEqualTo("sample-lib")
    assertThat(dep.version).isNull()
  }

  @Test
  fun `splitGavtc with _a_ (2 of 2)`() {
    assertThat(splitCoordinates(":sample-lib", 3)).containsExactly(null, "sample-lib", null)
    val (groupId, artifactId, version) = splitCoordinates(":sample-lib", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isNull()
    assertThat(dep.artifactId).isEqualTo("sample-lib")
    assertThat(dep.version).isNull()
  }

  @Test
  fun `splitGavtc with __v (1 of 1)`() {
    assertThat(splitCoordinates("::1.0", 3)).containsExactly(null, null, "1.0")
    val (groupId, artifactId, version) = splitCoordinates("::1.0", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isNull()
    assertThat(dep.artifactId).isNull()
    assertThat(dep.version).isEqualTo("1.0")
  }

  @Test
  fun `splitGavtc with ___ (1 of 3)`() {
    assertThat(splitCoordinates("::", 3)).containsExactly(null, null, null)
    val (groupId, artifactId, version) = splitCoordinates("::", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isNull()
    assertThat(dep.artifactId).isNull()
    assertThat(dep.version).isNull()
  }

  @Test
  fun `splitGavtc with ___ (2 of 3)`() {
    assertThat(splitCoordinates(":", 3)).containsExactly(null, null, null)
    val (groupId, artifactId, version) = splitCoordinates(":", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isNull()
    assertThat(dep.artifactId).isNull()
    assertThat(dep.version).isNull()
  }

  @Test
  fun `splitGavtc with ___ (3 of 3)`() {
    assertThat(splitCoordinates("", 3)).containsExactly(null, null, null)
    val (groupId, artifactId, version) = splitCoordinates("", 3)
    val dep =
        Dependency().apply {
          this.groupId = groupId
          this.artifactId = artifactId
          this.version = version
        }
    assertThat(dep.groupId).isNull()
    assertThat(dep.artifactId).isNull()
    assertThat(dep.version).isNull()
  }
}
