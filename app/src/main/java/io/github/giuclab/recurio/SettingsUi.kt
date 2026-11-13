// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

val SuccessGreen=Color(0xFF16875C)
@Composable fun GiuSwitch(checked:Boolean,onChange:(Boolean)->Unit) {
 Switch(checked=checked,onCheckedChange=onChange,modifier=Modifier.width(56.dp),colors=SwitchDefaults.colors(checkedThumbColor=Color.White,checkedTrackColor=SuccessGreen,checkedBorderColor=SuccessGreen,uncheckedThumbColor=Color.White,uncheckedTrackColor=Blue,uncheckedBorderColor=Blue))
}
@Composable private fun SettingsGroup(title:String,content:@Composable ColumnScope.()->Unit) {
 Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
  Text(title,color=Muted,fontSize=12.sp,fontWeight=FontWeight.SemiBold,modifier=Modifier.padding(start=4.dp))
  OutlinedCard(shape=RoundedCornerShape(14.dp),colors=CardDefaults.outlinedCardColors(containerColor=Color.White),border=BorderStroke(1.dp,Border)) {
   Column(Modifier.fillMaxWidth(),content=content)
  }
 }
}
@Composable private fun SettingAction(label:String,subtitle:String?=null,onClick:()->Unit) {
 Row(Modifier.fillMaxWidth().clickable(onClick=onClick).padding(horizontal=16.dp,vertical=12.dp).heightIn(min=28.dp),verticalAlignment=Alignment.CenterVertically) {
  Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {Text(label,fontSize=15.sp);if(subtitle!=null)Text(subtitle,color=Muted,fontSize=12.sp)}
  Text("›",color=Muted,fontSize=24.sp,modifier=Modifier.padding(start=12.dp))
 }
}
@Composable fun SettingsScreen(lang:String,icons:Boolean,notifications:Boolean,onLanguage:(String)->Unit,onIcons:(Boolean)->Unit,onGuide:()->Unit,onBackup:()->Unit,onNotifications:()->Unit,onTest:()->Unit,onSupport:()->Unit,onLicenses:()->Unit,onFeedback:()->Unit,onRepository:()->Unit,onShare:()->Unit,onClose:()->Unit) {
 val t:(String)->String={tr(lang,it)}
 var languageMenu by remember{mutableStateOf(false)};var detail by remember{mutableStateOf<String?>(null)};var about by remember{mutableStateOf(false)}
 Dialog(onDismissRequest=onClose,properties=DialogProperties(usePlatformDefaultWidth=false)) {
  Surface(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),color=Background) {
   Column {
    Row(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=12.dp),verticalAlignment=Alignment.CenterVertically) {
     Text(t("settings"),fontSize=24.sp,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));TextButton(onClick=onClose){Text(t("close"))}
    }
    LazyColumn(contentPadding=PaddingValues(start=20.dp,end=20.dp,bottom=20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
     item {SettingsGroup(t("sectionGeneral")) {
      Box {
       SettingAction(t("language"),languages[lang]){languageMenu=true}
       DropdownMenu(languageMenu,{languageMenu=false}) {languages.forEach{(code,label)->DropdownMenuItem(text={Text(if(code==lang)"✓ $label" else label)},onClick={onLanguage(code);languageMenu=false})}}
      }
     }}
     item {SettingsGroup(t("sectionData")) {SettingAction(t("backupTitle"),t("backupShort"),onBackup)}}
     item {SettingsGroup(t("sectionPrivacy")) {
      Row(Modifier.fillMaxWidth().clickable {onIcons(!icons)}.padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
       Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
        Text(t("icons"),fontSize=15.sp,fontWeight=FontWeight.SemiBold)
        Text(t(if(icons)"enabled" else "disabledShort"),fontSize=12.sp,color=if(icons)SuccessGreen else Muted)
       }
       GiuSwitch(icons,onIcons)
      }
      Text(t("iconsShort"),fontSize=12.sp,color=Muted,modifier=Modifier.padding(start=16.dp,end=16.dp,bottom=12.dp))
      HorizontalDivider(color=Border,modifier=Modifier.padding(horizontal=16.dp))
      SettingAction(t("privacyDetails")){detail="privacy"}
     }}
     item {SettingsGroup(t("reminder")) {
      SettingAction(t("notifications"),t(if(notifications)"enabled" else "disabledShort"),onNotifications)
      HorizontalDivider(color=Border,modifier=Modifier.padding(horizontal=16.dp))
      Row(Modifier.fillMaxWidth().padding(horizontal=8.dp),horizontalArrangement=Arrangement.SpaceBetween) {
       TextButton(onClick=onTest){Text(t("test"),fontSize=13.sp)}
       TextButton(onClick={detail="timing"}){Text(t("details"),fontSize=13.sp)}
      }
     }}
     item {SettingsGroup(t("sectionCommunity")) {
      SettingAction(t("feedback"),onClick=onFeedback)
      HorizontalDivider(color=Border,modifier=Modifier.padding(horizontal=16.dp))
      SettingAction(t("shareApp"),onClick=onShare)
      HorizontalDivider(color=Border,modifier=Modifier.padding(horizontal=16.dp))
      SettingAction(t("support"),"Ko-fi",onSupport)
     }}
     item {SettingsGroup(t("sectionHelp")) {
      SettingAction(t("guide"),onClick=onGuide)
      HorizontalDivider(color=Border,modifier=Modifier.padding(horizontal=16.dp))
      SettingAction(t("about"),onClick={about=true})
     }}
     item {Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(4.dp)) {
      Text("Recurio v0.1.0-alpha09 — © 2026 GiuClab",fontSize=11.sp,color=Muted)
      Text("Made with ❤️ by GiuClab",fontSize=11.sp,color=Muted)
     }}
    }
   }
  }
 }
 if(detail!=null) AlertDialog(onDismissRequest={detail=null},title={Text(t(if(detail=="privacy")"privacyDetails" else "reminder"))},text={Text(if(detail=="privacy")t("privacy")+"\n\n"+t("iconsHelp") else t("timing"),modifier=Modifier.verticalScroll(rememberScrollState()),fontSize=14.sp)},confirmButton={TextButton(onClick={detail=null}){Text(t("close"))}})
 if(about) AlertDialog(onDismissRequest={about=false},title={Text(t("about"))},text={Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)) {
  Image(painterResource(R.drawable.ic_launcher),null,Modifier.size(48.dp));Text("Recurio v0.1.0-alpha09",fontWeight=FontWeight.Bold);Text(t("tagline"));Text(t("alpha"),fontSize=12.sp,color=Muted)
  TextButton(onClick=onRepository){Text("GitHub")};TextButton(onClick=onFeedback){Text(t("feedback"))};TextButton(onClick=onSupport){Text(t("support"))};TextButton(onClick=onLicenses){Text(t("iconLicense"))}
  Text("© 2026 GiuClab · Made with ❤️ by GiuClab",fontSize=11.sp,color=Muted)
 }},confirmButton={TextButton(onClick={about=false}){Text(t("close"))}})
}
