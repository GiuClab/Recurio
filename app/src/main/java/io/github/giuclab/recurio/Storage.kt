// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import android.content.Context
import android.util.AtomicFile
import org.json.*
import java.io.File
import java.time.LocalDate

class Storage(private val ctx: Context) {
 private val file get() = AtomicFile(File(ctx.filesDir, "payments.json"))
 val prefs = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
 fun read(): List<Payment> {
  if (!File(ctx.filesDir, "payments.json").exists()) return emptyList()
  val array = JSONArray(file.openRead().bufferedReader().use { it.readText() })
  return (0 until array.length()).map { i -> val p = array.getJSONObject(i)
   Payment(p.getString("id"), p.getString("name"), p.getLong("cents"), p.getString("currency"),
    LocalDate.parse(p.getString("anchor")), Cycle.valueOf(p.getString("cycle")), p.getString("link"),
    p.getBoolean("reminder"), p.getInt("days"), p.getInt("hour"), p.getInt("minute"), p.getBoolean("active"))
  }
 }
 fun write(payments: List<Payment>) {
  val a = JSONArray(); payments.forEach { p -> a.put(JSONObject().put("id", p.id).put("name", p.name)
   .put("cents", p.cents).put("currency", p.currency).put("anchor", p.anchor.toString())
   .put("cycle", p.cycle.name).put("link", p.link).put("reminder", p.reminder).put("days", p.daysBefore)
   .put("hour", p.hour).put("minute", p.minute).put("active", p.active)) }
  val stream = file.startWrite()
  try { stream.write(a.toString().toByteArray()); file.finishWrite(stream) }
  catch (e: Exception) { file.failWrite(stream); throw e }
 }
}
