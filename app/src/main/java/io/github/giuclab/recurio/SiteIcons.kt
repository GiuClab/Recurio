// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.*
import java.io.File
import java.security.MessageDigest

/** Optional network operation: only public HTTPS pages, no browser cookies, bounded downloads. */
object SiteIcons {
 private fun publicHost(host: String): Boolean = runCatching {
  !host.endsWith(".local") && host != "localhost" && InetAddress.getAllByName(host).all {
   !it.isAnyLocalAddress && !it.isLoopbackAddress && !it.isLinkLocalAddress && !it.isSiteLocalAddress && !it.isMulticastAddress &&
    !(it is Inet6Address && (it.address[0].toInt() and 0xfe) == 0xfc)
  }
 }.getOrDefault(false)
 private fun download(url: URL, host: String, max: Int, redirects: Int = 0): ByteArray? {
  if (url.protocol != "https" || url.host !in listOf(host, "www.$host") || url.userInfo != null || url.port !in listOf(-1, 443) || !publicHost(url.host) || redirects > 3) return null
  val c = url.openConnection() as HttpURLConnection
  try {
   c.connectTimeout = 5000; c.readTimeout = 5000; c.instanceFollowRedirects = false
   c.setRequestProperty("User-Agent", "Recurio/0.1 (website icon)")
   val status = c.responseCode
   if (status in 300..399) return c.getHeaderField("Location")?.let { download(URL(url, it), host, max, redirects + 1) }
   if (status != 200 || c.contentLength > max) return null
   return c.inputStream.use { input ->
    val output = java.io.ByteArrayOutputStream(); val buffer = ByteArray(8192)
    while (output.size() <= max) { val count = input.read(buffer, 0, minOf(buffer.size, max + 1 - output.size())); if (count < 0) break; output.write(buffer, 0, count) }
    output.toByteArray().takeIf { it.size <= max }
   }
  } finally { c.disconnect() }
 }
 fun load(ctx: Context, host: String): Bitmap? = runCatching {
  val cache = File(ctx.cacheDir, "icons").apply { mkdirs() }
  val key = MessageDigest.getInstance("SHA-256").digest(host.toByteArray()).joinToString("") { "%02x".format(it) }
  val file = File(cache, "$key.png")
  if (file.exists()) return@runCatching BitmapFactory.decodeFile(file.path)
  val base = URL("https://$host/")
  val html = download(base, host, 262144)?.toString(Charsets.UTF_8).orEmpty()
  val tags = Regex("<link\\b[^>]*>", RegexOption.IGNORE_CASE).findAll(html)
  fun attr(tag: String, name: String): String? = Regex("\\b$name\\s*=\\s*['\"]([^'\"]+)['\"]", RegexOption.IGNORE_CASE).find(tag)?.groupValues?.get(1)
  val candidates = tags.mapNotNull { m ->
   if (attr(m.value, "rel")?.lowercase()?.split(' ')?.any { it == "icon" || it == "apple-touch-icon" } == true)
    attr(m.value, "href")?.let { runCatching { URL(base, it) }.getOrNull() } else null
  }.take(3).toList() + URL(base, "/favicon.ico")
  for (url in candidates) {
   val bytes = download(url, host, 524288) ?: continue
   val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
   BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
   if (bounds.outWidth !in 1..4096 || bounds.outHeight !in 1..4096) continue
   val options = BitmapFactory.Options().apply { inSampleSize = (maxOf(bounds.outWidth, bounds.outHeight) / 128).coerceAtLeast(1) }
   val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options) ?: continue
   file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
   return@runCatching bitmap
  }
  null
 }.getOrNull()
}
