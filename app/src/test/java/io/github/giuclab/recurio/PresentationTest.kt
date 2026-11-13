// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio
import org.junit.Assert.*
import org.junit.Test
class PresentationTest {
 @Test fun singularAdvanceInAllFourLanguages() {
  assertEquals("1 day before",reminderAdvanceText("en",1));assertEquals("1 giorno prima",reminderAdvanceText("it",1));assertEquals("1 Tag vorher",reminderAdvanceText("de",1));assertEquals("1 jour avant",reminderAdvanceText("fr",1))
 }
 @Test fun pluralAdvanceInAllFourLanguagesAndZero() {
  assertEquals("3 days before",reminderAdvanceText("en",3));assertEquals("3 giorni prima",reminderAdvanceText("it",3));assertEquals("3 Tage vorher",reminderAdvanceText("de",3));assertEquals("3 jours avant",reminderAdvanceText("fr",3));assertEquals("0 days before",reminderAdvanceText("en",0))
 }
 @Test fun nineNewPlatformsAreRecognized() {
  val expected=mapOf("spliiit.com" to "Spliiit","sharesub.com" to "Sharesub","diveedi.com" to "Diveedi","gosplit.com" to "GoSplit","sharingful.com" to "Sharingful","gamsgo.com" to "GamsGo","coosub.com" to "Coosub","goingbus.com" to "GoingBus","togetherprice.com" to "Togetherprice")
  for((domain,name) in expected){assertEquals(name,Catalog.recognize("https://www.$domain/it/account")?.name);assertEquals(name,Catalog.suggestedName(domain));assertNull(Catalog.recognize("https://$domain.fake.invalid/"))}
 }
 @Test fun unknownDomainGetsCapitalizedNameWithoutSuffixOrSubdomain() {
  assertEquals("Example",Catalog.suggestedName("https://www.example.com/account"));assertEquals("Example",Catalog.suggestedName("https://billing.example.co.uk/pay"));assertEquals("Example",Catalog.suggestedName("https://example.net/account"));assertNull(Catalog.suggestedName("javascript:alert(1)"))
 }
 @Test fun manuallyTypedLinkUsesTheSameCatalogAsSharedLinks() {
  assertEquals("Netflix",nameAfterLinkEdit("","netflix.com/account",false));assertEquals("Sharesub",nameAfterLinkEdit("","https://www.sharesub.com/it/joined-subscriptions/123",false));assertEquals("YouTube Music",nameAfterLinkEdit("YouTube","music.youtube.com",false))
 }
 @Test fun personalizedNamesArePreservedButAutomaticNamesCanChange() {
  assertEquals("Netflix famiglia",nameAfterLinkEdit("Netflix famiglia","spotify.com",true));assertEquals("Spotify",nameAfterLinkEdit("Netflix","spotify.com",false));assertEquals("Netflix",nameAfterLinkEdit("Netflix","https://",false))
  assertTrue(isSuggestedServiceName("sharesub.com","https://sharesub.com/account"));assertFalse(isSuggestedServiceName("Netflix famiglia","https://netflix.com/account"))
 }
}
