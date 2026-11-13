// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

data class SetupPlan(val language: String, val chooseLanguage: Boolean, val guide: Boolean, val notificationIntro: Boolean, val legacyUpgrade: Boolean)
fun startupPlan(deviceLanguage: String, savedLanguage: String?, started: Boolean, guideDone: Boolean, done: Boolean): SetupPlan {
 val saved=savedLanguage?.takeIf { it in languages }
 val device=supportedDeviceLanguage(deviceLanguage)
 val legacy=saved!=null && !started
 val complete=done || legacy
 val choose=saved==null && device==null
 return SetupPlan(saved ?: device ?: "en", choose, !complete && !choose && !guideDone, !complete && !choose && guideDone, legacy)
}
