package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class ContributorList : ArrayList<org.apache.maven.model.Contributor>(), Cloneable {

  private val nameAndEmailRegex: Regex = Regex("""([^<]+?)\s*<\s*(([^>@]+)@[^>]+)\s*>""")
  private val emailRegex: Regex = Regex("""([^@]+)@.+""")

  /**
   * Provides a callback for defining a new contributor entry. If formatted as `name <email>`, the
   * name will be extracted from the first part of the string, and the email from the second.
   *
   * @param name the name of the contributor
   */
  @PomDsl
  fun contributor(name: String? = null, block: (Contributor.(Contributor) -> Unit)? = null) {
    val contributor =
        Contributor().apply {
          if (name != null) {
            val nameAndEmailMatch = this@ContributorList.nameAndEmailRegex.matchEntire(name)
            if (nameAndEmailMatch != null) {
              this.name = nameAndEmailMatch.groupValues[1]
              this.email = nameAndEmailMatch.groupValues[2]
            } else {
              val emailMatch = this@ContributorList.emailRegex.matchEntire(name)
              if (emailMatch != null) {
                this.name = emailMatch.groupValues[1]
                this.email = emailMatch.groupValues[0]
              } else {
                this.name = name
              }
            }
          }
        }
    block?.invoke(contributor, contributor)
    add(contributor)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
