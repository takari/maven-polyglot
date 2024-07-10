package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class MailingListList : ArrayList<org.apache.maven.model.MailingList>(), Cloneable {

  /** Provides a callback for defining a new mailing list. */
  @PomDsl
  fun mailingList(name: String? = null, block: MailingList.(MailingList) -> Unit) {
    val mailingList = MailingList().apply { this.name = name }
    block(mailingList, mailingList)
    add(mailingList)
  }

  override fun clone(): Any {
    return super<ArrayList>.clone()
  }
}
