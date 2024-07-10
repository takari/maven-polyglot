package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class DeveloperList : ArrayList<org.apache.maven.model.Developer>(), Cloneable {

  private val nameAndEmailRegex: Regex = Regex("""([^<]+?)\s*<\s*(([^>@]+)@[^>]+)\s*>""")
  private val emailRegex: Regex = Regex("""([^@]+)@.+""")

  /**
   * Provides a callback for defining a new developer entry. If formatted as `name <email>`, the
   * name will be extracted from the first part of the string, and the email from the second. The ID
   * will be extracted from the email.
   *
   * @param nameOrId the name or id of the developer entry
   */
  @PomDsl
  fun developer(nameOrId: String? = null, block: (Developer.(Developer) -> Unit)? = null) {
    val developer =
        Developer().apply {
          if (nameOrId != null) {
            val nameAndEmailMatch = this@DeveloperList.nameAndEmailRegex.matchEntire(nameOrId)
            if (nameAndEmailMatch != null) {
              this.id = nameAndEmailMatch.groupValues[3]
              this.name = nameAndEmailMatch.groupValues[1]
              this.email = nameAndEmailMatch.groupValues[2]
            } else {
              val emailMatch = this@DeveloperList.emailRegex.matchEntire(nameOrId)
              if (emailMatch != null) {
                this.id = emailMatch.groupValues[1]
                this.name = emailMatch.groupValues[1]
                this.email = emailMatch.groupValues[0]
              } else {
                this.id = nameOrId
                this.name = nameOrId
              }
            }
          }
        }
    block?.invoke(developer, developer)
    add(developer)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
