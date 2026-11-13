// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun reminderAmount(payment: Payment, lang: String): String =
 NumberFormat.getCurrencyInstance(Locale.forLanguageTag(lang)).apply {
  currency = Currency.getInstance(payment.currency)
  minimumFractionDigits = currency.defaultFractionDigits
  maximumFractionDigits = currency.defaultFractionDigits
 }.format(BigDecimal.valueOf(payment.cents, 2))

fun reminderBody(payment: Payment, lang: String): String =
 "${reminderAmount(payment, lang)} · ${payment.currency} — ${tr(lang, "notice")}"
