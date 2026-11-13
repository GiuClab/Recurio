// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.io.ByteArrayInputStream

class BackupTest {
 private val password="A long test password!".toCharArray()
 private val payment=Payment(id="first",name="Netflix 家族",cents=1599,currency="EUR",anchor=LocalDate.parse("2026-11-27"),cycle=Cycle.QUARTERLY,link="https://netflix.com/account?value=a%2Fb",reminder=true,daysBefore=3,hour=18,minute=36,active=false)
 private fun rejects(block:()->Unit) {try {block();fail("Expected rejection")}catch(_:Exception){}}
 @Test fun backupRoundTripPreservesEveryPaymentFieldAndMultipleCurrencies() {
  val records=listOf(payment,payment.copy(id="second",name="Second",currency="USD",cycle=Cycle.ONCE,reminder=false,active=true))
  assertEquals(records,Backup.decrypt(Backup.encrypt(records,password),password))
 }
 @Test fun repeatedBackupsUseDifferentSaltAndIv() {
  val one=Backup.encrypt(listOf(payment),password);val two=Backup.encrypt(listOf(payment),password)
  assertFalse(one.contentEquals(two));assertEquals(listOf(payment),Backup.decrypt(two,password))
 }
 @Test fun wrongPasswordTamperingAndTruncationAreRejected() {
  val bytes=Backup.encrypt(listOf(payment),password)
  rejects {Backup.decrypt(bytes,"Another password!".toCharArray())}
  val changed=bytes.copyOf();changed[changed.lastIndex]=(changed.last().toInt() xor 1).toByte()
  rejects {Backup.decrypt(changed,password)}
  val header=bytes.copyOf();header[12]=(header[12].toInt() xor 1).toByte()
  rejects {Backup.decrypt(header,password)}
  rejects {Backup.decrypt(bytes.copyOf(15),password)}
 }
 @Test fun emptyBackupRoundTrip() {assertEquals(emptyList<Payment>(),Backup.decrypt(Backup.encrypt(emptyList(),password),password))}
 @Test fun invalidRecordDataAndDuplicateIdsAreRejectedBeforeExport() {
  rejects {Backup.encrypt(listOf(payment,payment),password)}
  rejects {Backup.encrypt(listOf(payment.copy(hour=24)),password)}
  rejects {Backup.encrypt(listOf(payment.copy(link="javascript:alert(1)")),password)}
  rejects {Backup.encrypt(listOf(payment),"tiny".toCharArray())}
 }
 @Test fun backupReadSizeIsBounded() {
  rejects {Backup.readLimited(ByteArrayInputStream(ByteArray(Backup.MAX_BYTES+129)))}
  assertArrayEquals(byteArrayOf(1,2,3),Backup.readLimited(ByteArrayInputStream(byteArrayOf(1,2,3))))
 }
 @Test fun decryptsIndependentPythonCryptoFixture() {
  val bytes=javaClass.getResourceAsStream("/portable-backup.recurio")!!.use {it.readBytes()}
  val expected=Payment(id="fixture-id",name="Fixture Netflix",cents=1599,currency="EUR",anchor=LocalDate.parse("2026-11-27"),cycle=Cycle.MONTHLY,link="https://netflix.com/account",reminder=true,daysBefore=3,hour=18,minute=36,active=true)
  assertEquals(listOf(expected),Backup.decrypt(bytes,"public-fixture-password".toCharArray()))
 }
 @Test fun fiveCharacterPasswordIsAcceptedAndOlderBackupsStillWork() {
  val short="abc12".toCharArray();assertEquals(listOf(payment),Backup.decrypt(Backup.encrypt(listOf(payment),short),short))
 }
 @Test fun restoreErrorsNeverSuggestCreatingAnExport() {
  assertEquals("wrongPassword",restoreFailureMessage(javax.crypto.AEADBadTagException()))
  assertEquals("backupReadError",restoreFailureMessage(java.io.IOException()))
  for(lang in languages.keys) {assertNotEquals("wrongPassword",tr(lang,"wrongPassword"));assertNotEquals("restoreWriteError",tr(lang,"restoreWriteError"))}
 }
}
