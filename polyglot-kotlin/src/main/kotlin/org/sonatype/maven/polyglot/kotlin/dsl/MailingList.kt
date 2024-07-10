package org.sonatype.maven.polyglot.kotlin.dsl

@PomDsl
class MailingList : org.apache.maven.model.MailingList(), Cloneable {

  @PomDsl
  fun name(name: String): MailingList {
    this.name = name
    return this
  }

  @PomDsl
  fun subscribe(subscribe: String): MailingList {
    this.subscribe = subscribe
    return this
  }

  @PomDsl
  fun unsubscribe(unsubscribe: String): MailingList {
    this.unsubscribe = unsubscribe
    return this
  }

  @PomDsl
  fun post(post: String): MailingList {
    this.post = post
    return this
  }

  @PomDsl
  fun archive(name: String): MailingList {
    this.archive = name
    return this
  }

  @PomDsl
  fun otherArchives(vararg otherArchives: String): MailingList {
    this.otherArchives = otherArchives.asList()
    return this
  }

  override fun clone(): org.apache.maven.model.MailingList {
    return super<org.apache.maven.model.MailingList>.clone()
  }
}
