// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import java.net.URI
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.*
import java.util.UUID

data class Service(val name: String, val domains: List<String>, val category: String = "other", val id: String = "", val aliases: List<String> = emptyList()) {
 val publicLink: String get() = "https://${domains.first()}"
}
object Catalog {
 val services = catalogEntries
 /** Accept a bare web address without ever converting an explicit non-web scheme. */
 fun normalizeLink(link: String): String? = runCatching {
  val input = link.trim()
  require(input.isNotEmpty() && input.none { it.isWhitespace() })
  val candidate = when {
   input.startsWith("//") -> "https:$input"
   input.contains("://") -> input
   Regex("^[A-Za-z][A-Za-z0-9+.-]*:").containsMatchIn(input) -> {
    require(Regex("^[^/:]+\\.[^/:]+:[0-9]+(?:/.*)?$").matches(input))
    "https://$input"
   }
   else -> "https://$input"
  }
  val uri = URI(candidate)
  require(uri.scheme.lowercase() in listOf("http", "https"))
  require(uri.userInfo == null && uri.host != null && uri.host.contains('.') && uri.port in -1..65535)
  require(!uri.host.startsWith('.') && !uri.host.endsWith('.') && !uri.host.contains(".."))
  // Preserve escapes and query parameters exactly; only normalize the scheme.
  uri.scheme.lowercase() + candidate.substring(candidate.indexOf(':'))
 }.getOrNull()
 fun host(link: String): String? = normalizeLink(link)?.let {
  URI(it).host.lowercase().removePrefix("www.")
 }
 private val localePath = Regex("^/(en|it|de|fr|es|pt|ja|ko|zh|nl|pl|tr|ar|ru|sv|no|da|fi|cs|hu|id|th|vi)(?:[-_][a-z]{2})?(?=/)")
 fun recognize(link: String): Service? {
  val normalized = normalizeLink(link) ?: return null
  val host = host(normalized) ?: return null
  val rawPath = URI(normalized).rawPath.orEmpty().lowercase(java.util.Locale.ROOT).ifEmpty { "/" }
  val paths = listOf(rawPath, rawPath.replaceFirst(localePath, ""))
  return services.mapNotNull { service ->
   val score = service.domains.mapNotNull { rule ->
    val domain = rule.substringBefore('/').lowercase(java.util.Locale.ROOT)
    val prefix = rule.substringAfter('/', "").let { if (it.isEmpty()) "" else "/$it" }.trimEnd('/')
    if ((host == domain || host.endsWith(".$domain")) &&
      (prefix.isEmpty() || paths.any { it == prefix || it.startsWith("$prefix/") })) domain.length * 1000 + prefix.length else null
   }.maxOrNull()
   score?.let { service to it }
  }.maxByOrNull { it.second }?.first
 }
 fun search(query: String): List<Service> {
  val q = query.trim()
  return services.filter { q.isEmpty() || it.name.contains(q, true) || it.domains.any { domain -> domain.contains(q, true) } || it.aliases.any { alias -> alias.contains(q, true) } }
   .sortedBy { it.name.lowercase(java.util.Locale.ROOT) }
 }
 /** Catalog names keep their official spelling; unknown domains get an editable label. */
 fun suggestedName(link: String): String? {
  recognize(link)?.let { return it.name }
  val domain=host(link) ?: return null
  val parts=domain.split('.')
  if(parts.all { label -> label.toIntOrNull()?.let { it in 0..255 } == true }) return domain
  val compound=parts.size>=3 && parts.last().length==2 && parts[parts.lastIndex-1] in setOf("co","com","org","net","gov","edu","ac")
  val privateSuffix=parts.takeLast(2).joinToString(".") in setOf("github.io","netlify.app","vercel.app","pages.dev")
  val index=parts.lastIndex - if(compound || privateSuffix) 2 else 1
  val label=java.net.IDN.toUnicode(parts[index.coerceAtLeast(0)])
  return label.replaceFirstChar { it.titlecase(java.util.Locale.ROOT) }
 }
 fun sharedLink(text: String): String? = Regex("https?://[^\\s<>\"]+", RegexOption.IGNORE_CASE)
  .findAll(text).map { it.value.trimEnd('.', ',', ')', ']') }.firstOrNull { host(it) != null }?.let { normalizeLink(it) } ?: normalizeLink(text)
}
enum class Cycle { ONCE, WEEKLY, MONTHLY, QUARTERLY, YEARLY }
data class Payment(
 val id: String = UUID.randomUUID().toString(), val name: String, val cents: Long,
 val currency: String = "EUR", val anchor: LocalDate, val cycle: Cycle = Cycle.MONTHLY,
 val link: String = "", val reminder: Boolean = true, val daysBefore: Int = 1,
 val hour: Int = 9, val minute: Int = 0, val active: Boolean = true
) {
 fun occurrence(index: Long): LocalDate = when(cycle) {
  Cycle.ONCE -> anchor; Cycle.WEEKLY -> anchor.plusWeeks(index)
  Cycle.MONTHLY -> anchor.plusMonths(index); Cycle.QUARTERLY -> anchor.plusMonths(index * 3)
  Cycle.YEARLY -> anchor.plusYears(index)
 }
 fun nextOnOrAfter(day: LocalDate): LocalDate? {
  if (cycle == Cycle.ONCE) return anchor.takeIf { !it.isBefore(day) }
  var low = 0L; var high = 1L
  while (occurrence(high).isBefore(day)) high *= 2
  while (low < high) { val mid = (low + high) / 2; if (occurrence(mid).isBefore(day)) low = mid + 1 else high = mid }
  return occurrence(low)
 }
 fun dueBetween(start: LocalDate, endExclusive: LocalDate): List<LocalDate> {
  val dates = mutableListOf<LocalDate>(); var next = nextOnOrAfter(start)
  while (next != null && next < endExclusive) { dates.add(next); next = nextOnOrAfter(next.plusDays(1)) }
  return dates
 }
 fun nextReminder(now: ZonedDateTime): ZonedDateTime? {
  if (!active || !reminder) return null
  var due = nextOnOrAfter(now.toLocalDate()) ?: return null
  while (true) {
   val at = due.minusDays(daysBefore.toLong()).atTime(hour, minute).atZone(now.zone)
   if (at.isAfter(now)) return at
   due = nextOnOrAfter(due.plusDays(1)) ?: return null
  }
 }
}
fun parseCents(value: String): Long? = runCatching {
 val input = value.trim()
 require(Regex("[0-9]+(?:\\.[0-9]{1,2})?").matches(input))
 val n = BigDecimal(input); require(n.signum() > 0)
 n.setScale(2, RoundingMode.UNNECESSARY).movePointRight(2).longValueExact().also { require(it <= 99999999999L) }
}.getOrNull()

val commonCurrencies = listOf("EUR", "USD", "GBP", "CHF", "CAD", "AUD", "NZD", "JPY", "CNY", "HKD", "SGD", "INR", "BRL", "MXN", "KRW", "SEK", "NOK", "DKK", "PLN", "CZK", "HUF", "TRY", "ZAR", "AED", "SAR", "THB", "IDR", "MYR", "PHP", "ILS")
fun acceptsCurrencyAmount(cents: Long, code: String): Boolean = runCatching {
 val digits = java.util.Currency.getInstance(code).defaultFractionDigits
 digits in 0..2 && (digits != 0 || cents % 100L == 0L)
}.getOrDefault(false)
fun totalsByCurrency(payments: List<Payment>, start: LocalDate, end: LocalDate): Map<String, Long> =
 payments.filter { it.active }.groupBy { it.currency }.toSortedMap().mapValues { (_, list) ->
  list.sumOf { it.cents * it.dueBetween(start, end).size }
 }

enum class PaymentOrder { DUE, SERVICE }
fun orderedPayments(payments: List<Payment>, order: PaymentOrder, today: LocalDate, language: String): List<Payment> {
 val collator = java.text.Collator.getInstance(java.util.Locale.forLanguageTag(language)).apply { strength = java.text.Collator.PRIMARY }
 fun service(p: Payment) = Catalog.recognize(p.link)?.name ?: p.name.trim()
 val due = compareBy<Payment> { !it.active }.thenBy { it.nextOnOrAfter(today) ?: LocalDate.MAX }
 val byService = Comparator<Payment> { a, b -> collator.compare(service(a), service(b)) }
 return payments.sortedWith(when(order) {
  PaymentOrder.DUE -> due.then(byService).thenBy { it.id }
  PaymentOrder.SERVICE -> byService.then(due).thenBy { it.id }
 })
}

fun isSuggestedServiceName(name: String, link: String): Boolean = name.isBlank() || name == Catalog.suggestedName(link) || name.equals(Catalog.host(link), ignoreCase=true)
fun nameAfterLinkEdit(currentName: String, newLink: String, manuallyEdited: Boolean): String =
 if(manuallyEdited) currentName else Catalog.suggestedName(newLink) ?: currentName
