// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import java.io.*
import java.security.SecureRandom
import java.time.LocalDate
import java.util.Currency
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/** Portable password-encrypted backup; deliberately independent of Android Keystore. */
object Backup {
 const val MAX_BYTES = 4 * 1024 * 1024
 const val MAX_RECORDS = 5000
 private val magic = "RECURIOBK1".toByteArray(Charsets.US_ASCII)
 private const val ITERATIONS = 600000
 private fun key(password: CharArray, salt: ByteArray): SecretKeySpec {
  val spec = PBEKeySpec(password, salt, ITERATIONS, 256)
  val raw = try { SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded } finally { spec.clearPassword() }
  return try { SecretKeySpec(raw, "AES") } finally { raw.fill(0) }
 }
 fun validate(payments: List<Payment>) {
  require(payments.size <= MAX_RECORDS && payments.map { it.id }.toSet().size == payments.size)
  payments.forEach { p ->
   require(p.id.length in 1..128 && p.name.isNotBlank() && p.name.length <= 120)
   require(p.cents in 1..99999999999L && p.currency.length == 3)
   require(Currency.getInstance(p.currency).defaultFractionDigits >= 0)
   require(p.anchor.year in 1900..2200 && p.daysBefore in 0..365 && p.hour in 0..23 && p.minute in 0..59)
   require(p.link.length <= 16384 && (p.link.isEmpty() || Catalog.normalizeLink(p.link) != null))
  }
 }
 private fun encode(payments: List<Payment>): ByteArray {
  validate(payments)
  val out = object: ByteArrayOutputStream() {
   override fun write(b: Int) {require(size()<MAX_BYTES);super.write(b)}
   override fun write(b: ByteArray, off: Int, len: Int) {require(size().toLong()+len<=MAX_BYTES);super.write(b,off,len)}
  }
  DataOutputStream(out).use { d ->
   d.writeInt(1); d.writeInt(payments.size)
   payments.forEach { p ->
    d.writeUTF(p.id); d.writeUTF(p.name); d.writeLong(p.cents); d.writeUTF(p.currency)
    d.writeLong(p.anchor.toEpochDay()); d.writeUTF(p.cycle.name); d.writeUTF(p.link)
    d.writeBoolean(p.reminder); d.writeInt(p.daysBefore); d.writeInt(p.hour); d.writeInt(p.minute); d.writeBoolean(p.active)
   }
  }
  return out.toByteArray().also { require(it.size <= MAX_BYTES) }
 }
 private fun decode(bytes: ByteArray): List<Payment> {
  require(bytes.size <= MAX_BYTES)
  return DataInputStream(ByteArrayInputStream(bytes)).use { d ->
   require(d.readInt() == 1)
   val count = d.readInt(); require(count in 0..MAX_RECORDS)
   fun bool(): Boolean { val value = d.readUnsignedByte(); require(value in 0..1); return value == 1 }
   val payments = List(count) {
    Payment(id=d.readUTF(), name=d.readUTF(), cents=d.readLong(), currency=d.readUTF(), anchor=LocalDate.ofEpochDay(d.readLong()), cycle=Cycle.valueOf(d.readUTF()), link=d.readUTF(), reminder=bool(), daysBefore=d.readInt(), hour=d.readInt(), minute=d.readInt(), active=bool())
   }
   require(d.read() == -1); validate(payments); payments
  }
 }
 fun encrypt(payments: List<Payment>, password: CharArray): ByteArray {
  require(password.size in 5..1024)
  val plain = encode(payments)
  try {
   val random = SecureRandom(); val salt=ByteArray(16).also { random.nextBytes(it) }; val iv=ByteArray(12).also { random.nextBytes(it) }
   val header=magic + salt + iv
   val cipher=Cipher.getInstance("AES/GCM/NoPadding")
   cipher.init(Cipher.ENCRYPT_MODE, key(password,salt), GCMParameterSpec(128,iv)); cipher.updateAAD(header)
   return header + cipher.doFinal(plain)
  } finally { plain.fill(0) }
 }
 fun decrypt(bytes: ByteArray, password: CharArray): List<Payment> {
  val start=magic.size
  require(bytes.size in (start+16+12+16)..(MAX_BYTES+128) && password.size in 5..1024)
  require(bytes.copyOfRange(0,start).contentEquals(magic))
  val salt=bytes.copyOfRange(start,start+16); val iv=bytes.copyOfRange(start+16,start+28)
  val header=bytes.copyOfRange(0,start+28)
  val cipher=Cipher.getInstance("AES/GCM/NoPadding")
  cipher.init(Cipher.DECRYPT_MODE,key(password,salt),GCMParameterSpec(128,iv)); cipher.updateAAD(header)
  val plain=cipher.doFinal(bytes,start+28,bytes.size-start-28)
  return try { decode(plain) } finally { plain.fill(0) }
 }
 fun readLimited(input: InputStream): ByteArray {
  val out=ByteArrayOutputStream(); val buffer=ByteArray(8192)
  while (true) { val n=input.read(buffer); if(n<0) break; require(out.size()+n <= MAX_BYTES+128); out.write(buffer,0,n) }
  return out.toByteArray()
 }
}
fun supportedDeviceLanguage(code: String): String? = code.lowercase(java.util.Locale.ROOT).takeIf { it in languages }

fun restoreFailureMessage(error: Exception): String = when(error) {
 is javax.crypto.BadPaddingException -> "wrongPassword"
 else -> "backupReadError"
}
