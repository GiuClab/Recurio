// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import android.app.*
import android.content.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.time.ZonedDateTime

object Reminders {
 const val CHANNEL = "payments"
 fun channel(ctx: Context) {
  val lang = Storage(ctx).prefs.getString("language", "en") ?: "en"
  ctx.getSystemService(NotificationManager::class.java).createNotificationChannel(
   NotificationChannel(CHANNEL, tr(lang, "reminder"), NotificationManager.IMPORTANCE_DEFAULT))
 }
 private fun pending(ctx: Context, id: String): PendingIntent = PendingIntent.getBroadcast(ctx, 0,
  Intent(ctx, ReminderReceiver::class.java).setAction(id), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
 fun cancel(ctx: Context, id: String) {
  ctx.getSystemService(AlarmManager::class.java).cancel(pending(ctx, id))
  Storage(ctx).prefs.edit().remove("scheduled_$id").apply()
 }
 fun schedule(ctx: Context, p: Payment) {
  val alarms = ctx.getSystemService(AlarmManager::class.java)
  alarms.cancel(pending(ctx, p.id))
  val prefs = Storage(ctx).prefs
  if (!p.active || !p.reminder) { prefs.edit().remove("scheduled_${p.id}").apply(); return }
  val now = ZonedDateTime.now()
  val previous = prefs.getLong("scheduled_${p.id}", 0)
  val millis = now.toInstant().toEpochMilli()
  // Reopening or rebooting must not discard a reminder delayed by Android.
  val at = if (previous in (millis - 86400000)..millis && previous > 0) millis + 5000
   else p.nextReminder(now)?.toInstant()?.toEpochMilli() ?: return
  prefs.edit().putLong("scheduled_${p.id}", at).apply()
  alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending(ctx, p.id))
 }
 fun all(ctx: Context) { Storage(ctx).read().forEach { schedule(ctx, it) } }
 fun notify(ctx: Context, id: String, title: String, body: String, icon: Bitmap? = null) {
  channel(ctx)
  if (!NotificationManagerCompat.from(ctx).areNotificationsEnabled()) return
  val launch = PendingIntent.getActivity(ctx, 0, Intent(ctx, MainActivity::class.java).setAction(id).putExtra("payment_id", id), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  val n = NotificationCompat.Builder(ctx, CHANNEL).setSmallIcon(R.drawable.ic_notification)
   .setContentTitle(title).setContentText(body).setStyle(NotificationCompat.BigTextStyle().bigText(body))
   .setLargeIcon(icon).setContentIntent(launch).setAutoCancel(true)
   .setVisibility(NotificationCompat.VISIBILITY_PRIVATE).setPublicVersion(
    NotificationCompat.Builder(ctx, CHANNEL).setSmallIcon(R.drawable.ic_notification).setContentTitle("Recurio").build()).build()
  try { NotificationManagerCompat.from(ctx).notify(id, 0, n) } catch (_: SecurityException) { }
 }
}
class ReminderReceiver: BroadcastReceiver() {
 override fun onReceive(ctx: Context, intent: Intent) {
  val p = runCatching { Storage(ctx).read().firstOrNull { it.id == intent.action } }.getOrNull() ?: return
  if (!p.active || !p.reminder) return
  val lang = Storage(ctx).prefs.getString("language", "en") ?: "en"
  // Use only bundled or cached icons: receiving a reminder never starts a network request.
  Storage(ctx).prefs.edit().remove("scheduled_${p.id}").apply()
  Reminders.notify(ctx, p.id, p.name, reminderBody(p, lang), ReminderIcons.load(ctx, p))
  Reminders.schedule(ctx, p)
 }
}
class RescheduleReceiver: BroadcastReceiver() {
 override fun onReceive(ctx: Context, intent: Intent) {
  if (intent.action !in setOf(Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED)) return
  runCatching { Reminders.all(ctx) }
 }
}

/** The same bundled logos, cached website icons and initials used by the payment list. */
object ReminderIcons {
 fun load(ctx: Context, payment: Payment): Bitmap {
  val resource = when (Catalog.recognize(payment.link)?.id) {
   "github" -> R.drawable.ic_service_github
   "github-copilot" -> R.drawable.ic_service_copilot
   else -> null
  }
  val bundled = resource?.let { ContextCompat.getDrawable(ctx, it)?.toBitmap(128, 128) }
  if (bundled != null) return bundled
  val prefs = Storage(ctx).prefs
  if (prefs.getBoolean("icons", true) && prefs.getBoolean("icon_disclosure_seen", false)) {
   val cached = Catalog.host(payment.link)?.let { SiteIcons.cached(ctx, it) }
   if (cached != null) return Bitmap.createScaledBitmap(cached, 128, 128, true)
  }
  return Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888).also { bitmap ->
   val canvas = Canvas(bitmap)
   canvas.drawColor(Color.rgb(237, 241, 253))
   val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.rgb(56, 90, 208); textSize = 64f; textAlign = Paint.Align.CENTER
    typeface = android.graphics.Typeface.DEFAULT_BOLD
   }
   val initial = payment.name.trim().take(1).uppercase().ifEmpty { "R" }
   canvas.drawText(initial, 64f, 64f - (paint.ascent() + paint.descent()) / 2, paint)
  }
 }
}
