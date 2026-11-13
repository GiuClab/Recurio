// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class ReminderTextTest {
 private fun payment(currency: String, cents: Long) = Payment(name = "Service", cents = cents, currency = currency, anchor = LocalDate.of(2026, 11, 13))
 @Test fun reminderIncludesAmountCurrencyAndLocalizedNotice() {
  for (lang in listOf("en", "it", "de", "fr")) {
   val p = payment("CHF", 1290)
   val text = reminderBody(p, lang)
   assertTrue(text.contains(money(1290, "CHF", lang)))
   assertTrue(text.contains("CHF"))
   assertTrue(text.endsWith(tr(lang, "notice")))
  }
 }
 @Test fun currenciesWithNoFractionDigitsDoNotInventDecimals() {
  val text = reminderBody(payment("JPY", 120000), "en")
  assertTrue(text.contains("1,200"))
  assertTrue(text.contains("JPY"))
  assertFalse(text.contains(".00"))
 }
 @Test fun italianUsesCommaAndKeepsUsdUnambiguous() {
  val text = reminderBody(payment("USD", 1599), "it")
  assertTrue(text.contains("15,99"))
  assertTrue(text.contains("USD"))
 }
}
