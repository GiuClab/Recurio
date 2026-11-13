// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio
import org.junit.Test
import org.junit.Assert.*
import java.time.*
class ModelTest {
 private fun p(anchor: String = "2026-01-31", cycle: Cycle = Cycle.MONTHLY) = Payment(name = "Test", cents = 1200, anchor = LocalDate.parse(anchor), cycle = cycle)
 @Test fun monthEndKeepsAnchor() { val p = p(); assertEquals(LocalDate.parse("2026-02-28"), p.occurrence(1)); assertEquals(LocalDate.parse("2026-03-31"), p.occurrence(2)) }
 @Test fun leapYearKeepsAnchor() { val p = p("2024-02-29", Cycle.YEARLY); assertEquals(LocalDate.parse("2025-02-28"), p.occurrence(1)); assertEquals(LocalDate.parse("2028-02-29"), p.occurrence(4)) }
 @Test fun totalsCountActualOccurrences() { val p = p("2026-01-02", Cycle.WEEKLY); assertEquals(5, p.dueBetween(LocalDate.parse("2026-01-01"), LocalDate.parse("2026-02-01")).size) }
 @Test fun oneOffDoesNotRepeat() { assertNull(p("2026-01-01", Cycle.ONCE).nextOnOrAfter(LocalDate.parse("2026-01-02"))) }
 @Test fun recognizesOnlyRealDomain() { assertNull(Catalog.recognize("https://netflix.com.attacker.org/account")); assertEquals("Netflix", Catalog.recognize("https://www.netflix.com/account")?.name); assertEquals("YouTube Music", Catalog.recognize("https://music.youtube.com/")?.name) }
 @Test fun unsafeSchemesAndCredentialsRejected() { assertNull(Catalog.host("javascript:alert(1)")); assertNull(Catalog.host("https://name:secret@netflix.com")) }
 @Test fun extractsBrowserShare() { assertEquals("https://netflix.com/account", Catalog.sharedLink("Netflix https://netflix.com/account")) }
 @Test fun exactCents() { assertEquals(1234L, parseCents("12.34")); assertNull(parseCents("12,34")); assertNull(parseCents("1.001")); assertNull(parseCents("-2")); assertNull(parseCents("NaN")) }
 @Test fun remindersSkipPastButNotFuture() { val p = p("2026-01-31"); val now = ZonedDateTime.parse("2026-01-30T10:00:00+01:00[Europe/Rome]"); assertEquals(LocalDate.parse("2026-02-27"), p.nextReminder(now)?.toLocalDate()) }
 @Test fun suspendedHasNoReminder() { assertNull(p().copy(active=false).nextReminder(ZonedDateTime.now())) }
 @Test fun allLanguagesCoverKeys() { for (lang in languages.keys) { assertNotEquals("save", tr(lang, "save")); assertNotEquals("privacy", tr(lang, "privacy")) } }
 @Test fun bareNetflixLinkIsAccepted() { assertEquals("https://netflix.com/account/membership", Catalog.normalizeLink("netflix.com/account/membership")); assertEquals("Netflix", Catalog.recognize("netflix.com/account/membership")?.name); assertEquals("https://netflix.com/account/membership", Catalog.sharedLink("netflix.com/account/membership")) }
 @Test fun normalizedLinkPreservesSecretsAndEscapesWithoutAlteration() { assertEquals("https://example.com/a%20b?token=a%2Fb&x=1#section", Catalog.normalizeLink("  example.com/a%20b?token=a%2Fb&x=1#section  ")) }
 @Test fun explicitUnsafeSchemesNeverBecomeHttps() { for (url in listOf("javascript:alert(1)", "file:///tmp/test", "ftp://example.com", "https://user:pass@example.com", "name@example.com", "not a url")) assertNull(url, Catalog.normalizeLink(url)) }
 @Test fun uppercaseAndProtocolRelativeLinks() { assertEquals("https://NETFLIX.COM/account", Catalog.normalizeLink("HTTPS://NETFLIX.COM/account")); assertEquals("https://example.com/path", Catalog.normalizeLink("//example.com/path")) }
 @Test fun barePortAndMalformedDomains() { assertEquals("https://example.com:8443/path", Catalog.normalizeLink("example.com:8443/path")); assertNull(Catalog.normalizeLink("example..com")); assertNull(Catalog.normalizeLink("")) }
 @Test fun amountsRejectCommaScientificAndGrouping() { for (v in listOf("15,99", "1e2", "1,000.00", ".99", "15.999")) assertNull(v, parseCents(v)); assertEquals(1599L, parseCents("15.99")) }
 @Test fun totalsStaySeparateForEachCurrency() {
  val payments = listOf(p("2026-11-15").copy(currency="EUR", cents=1599), p("2026-11-20").copy(currency="USD", cents=1000), p("2026-11-21").copy(currency="GBP", cents=500), p("2026-11-22").copy(currency="EUR", cents=100), p("2026-11-23").copy(active=false, cents=999))
  assertEquals(mapOf("EUR" to 1699L, "USD" to 1000L, "GBP" to 500L), totalsByCurrency(payments, LocalDate.parse("2026-11-01"), LocalDate.parse("2026-12-01")))
 }
 @Test fun wholeCurrencyAmountsDoNotGetRoundedSilently() { assertTrue(acceptsCurrencyAmount(1500,"JPY")); assertFalse(acceptsCurrencyAmount(1599,"JPY")); assertTrue(acceptsCurrencyAmount(1599,"EUR")); assertEquals(30, commonCurrencies.distinct().size) }
 @Test fun dueSortUsesNearestFutureOccurrenceNotOriginalAnchor() {
  val today=LocalDate.parse("2026-11-13")
  val later=p("2026-01-20").copy(id="later")
  val sooner=p("2026-11-15").copy(id="sooner")
  assertEquals(listOf("sooner","later"), orderedPayments(listOf(later,sooner),PaymentOrder.DUE,today,"it").map { it.id })
 }
 @Test fun serviceSortGroupsMultipleNetflixPaymentsInDateOrder() {
  val today=LocalDate.parse("2026-11-13")
  val n1=p("2026-11-25").copy(id="n1", name="Account casa", link="https://netflix.com/account")
  val n2=p("2026-11-15").copy(id="n2", name="Account famiglia", link="https://netflix.com/account")
  val amazon=p("2026-11-20").copy(id="a",name="Amazon",link="https://amazon.it/")
  assertEquals(listOf("a","n2","n1"), orderedPayments(listOf(n1,amazon,n2),PaymentOrder.SERVICE,today,"it").map { it.id })
 }
 @Test fun sortingKeepsAllDistinctRecordsAndHandlesMissingService() {
  val today=LocalDate.parse("2026-11-13")
  val a=p("2026-11-16").copy(id="a",name="zeta")
  val b=p("2026-11-17").copy(id="b",name="Alpha")
  val paused=p("2026-11-14").copy(id="c",active=false)
  assertEquals(listOf("a","b","c"),orderedPayments(listOf(paused,b,a),PaymentOrder.DUE,today,"en").map { it.id })
  assertEquals(listOf("b","a"),orderedPayments(listOf(a,b),PaymentOrder.SERVICE,today,"en").map { it.id })
 }
 @Test fun everyCatalogEntryHasDistinctIdNameAndWorkingPublicLink() {
  assertEquals(145, Catalog.services.size)
  assertEquals(Catalog.services.size, Catalog.services.map { it.id }.toSet().size)
  assertEquals(Catalog.services.size, Catalog.services.map { it.name }.toSet().size)
  for (service in Catalog.services) for (rule in service.domains) {
   assertEquals(rule, service.id, Catalog.recognize("https://$rule")?.id)
  }
 }
 @Test fun relatedAppleProductsRemainDistinct() {
  assertEquals("Apple Music", Catalog.recognize("https://music.apple.com/it/account")?.name)
  assertEquals("Apple TV+", Catalog.recognize("https://tv.apple.com/")?.name)
  assertEquals("Apple Fitness+", Catalog.recognize("https://apple.com/it/apple-fitness-plus/")?.name)
  assertEquals("Apple", Catalog.recognize("https://apple.com/account")?.name)
 }
 @Test fun amazonProductsStayDistinctOnRegionalDomains() {
  assertEquals("Amazon Music",Catalog.recognize("https://music.amazon.it/settings")?.name)
  assertEquals("Amazon Prime",Catalog.recognize("https://amazon.it/gp/prime/account")?.name)
  assertEquals("Kindle Unlimited",Catalog.recognize("https://amazon.it/kindleunlimited")?.name)
  assertEquals("Prime Video",Catalog.recognize("https://primevideo.com/settings")?.name)
  assertEquals("Amazon",Catalog.recognize("https://amazon.it/account")?.name)
 }
 @Test fun pathBoundariesAndQueriesNeverInventAPlan() {
  assertEquals("YouTube Premium", Catalog.recognize("https://youtube.com/premium/")?.name)
  assertEquals("YouTube", Catalog.recognize("https://youtube.com/premium-fake")?.name)
  assertEquals("YouTube", Catalog.recognize("https://youtube.com/watch?next=/premium")?.name)
  assertEquals("Google", Catalog.recognize("https://google.com/account")?.name)
  assertNull(Catalog.recognize("https://music.youtube.com.attacker.org/"))
 }
 @Test fun localizedServiceLinksAndSpecificSubdomainsTakePriority() {
  assertEquals("PlayStation Plus", Catalog.recognize("https://playstation.com/en-us/ps-plus/")?.name)
  assertEquals("Microsoft 365", Catalog.recognize("https://microsoft.com/it-it/microsoft-365/")?.name)
  assertEquals("GitHub Copilot", Catalog.recognize("https://github.com/features/copilot/")?.name)
  assertEquals("GitHub", Catalog.recognize("https://github.com/settings")?.name)
  assertEquals("Google One", Catalog.recognize("https://one.google.com/storage")?.name)
 }
 @Test fun legacyDomainsAndFormerNamesRemainSearchable() {
  assertEquals("HBO Max", Catalog.recognize("https://max.com/")?.name)
  assertEquals("HBO Max", Catalog.recognize("https://hbomax.com/")?.name)
  assertEquals("Super Duolingo",Catalog.recognize("https://join.duolingo.com/plus")?.name)
  assertEquals("OnlyFans",Catalog.recognize("https://onlyfans.com/")?.name)
  assertTrue(Catalog.search("OpenAI").any { it.name == "ChatGPT" })
  assertTrue(Catalog.search("Gemini Advanced").any { it.id == "google-ai-gemini" })
  assertTrue(Catalog.search("not-a-service-38493").isEmpty())
 }
 @Test fun allCatalogCategoriesHaveTranslations() {
  for (category in Catalog.services.map { it.category }.distinct()) for (lang in languages.keys) assertNotEquals("category_$category",tr(lang,"category_$category"))
 }
}
