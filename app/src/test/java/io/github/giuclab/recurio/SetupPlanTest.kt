// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio
import org.junit.Assert.*
import org.junit.Test
class SetupPlanTest {
 @Test fun italianPhoneStartsInItalianWithoutChooser() {val p=startupPlan("it",null,false,false,false);assertEquals("it",p.language);assertFalse(p.chooseLanguage);assertTrue(p.guide)}
 @Test fun chinesePhoneGetsEnglishChooserAndNoGuideUntilSelection() {val p=startupPlan("zh",null,false,false,false);assertEquals("en",p.language);assertTrue(p.chooseLanguage);assertFalse(p.guide);assertFalse(p.notificationIntro)}
 @Test fun eachSupportedDeviceLanguageIsAutomatic() {for(l in languages.keys){val p=startupPlan(l,null,false,false,false);assertEquals(l,p.language);assertFalse(p.chooseLanguage);assertTrue(p.guide)}}
 @Test fun explicitChoiceSurvivesDifferentPhoneLanguage() {val p=startupPlan("zh","fr",true,false,false);assertEquals("fr",p.language);assertFalse(p.chooseLanguage);assertTrue(p.guide)}
 @Test fun interruptedOnboardingResumesAtPermissionExplanation() {val p=startupPlan("it","it",true,true,false);assertFalse(p.guide);assertTrue(p.notificationIntro)}
 @Test fun existingPreviewUsersAreNotForcedThroughOnboarding() {val p=startupPlan("de","it",false,false,false);assertTrue(p.legacyUpgrade);assertFalse(p.guide);assertFalse(p.notificationIntro);assertEquals("it",p.language)}
 @Test fun completedSetupNeverShowsAgain() {val p=startupPlan("en","en",true,true,true);assertFalse(p.guide);assertFalse(p.notificationIntro)}
 @Test fun onboardingAndBackupLabelsExistInEverySupportedLanguage() {
  val keys=listOf("guide","guide_add_title","guide_add_body","guide_links_body","guide_reminders_body","guide_totals_body","guide_backup_body","notifyIntroTitle","notifyIntroBody","backupTitle","backupHelp","restoreWarning","backupReadError","replaceRestore","exportSuccess")
  for(lang in languages.keys)for(key in keys)assertNotEquals(key,tr(lang,key))
 }
}
