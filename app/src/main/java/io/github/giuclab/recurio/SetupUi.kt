// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable fun IntroGuide(lang: String, onOpen: (String)->Unit, onDone: () -> Unit) {
 var page by rememberSaveable { mutableIntStateOf(0) }
 val scroll=rememberScrollState()
 LaunchedEffect(page) {scroll.scrollTo(0)}
 val pages = guidePages
 Dialog(onDismissRequest=onDone,properties=DialogProperties(usePlatformDefaultWidth=false)) {
  Surface(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),color=Background) {
   Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
     Text("Recurio",Modifier.weight(1f),fontSize=24.sp,fontWeight=FontWeight.Bold)
     Text("${page+1} / ${pages.size}",color=Muted)
    }
    Column(Modifier.weight(1f).verticalScroll(scroll),verticalArrangement=Arrangement.spacedBy(24.dp)) {
     Image(painterResource(R.drawable.ic_launcher),null,Modifier.size(96.dp))
     Text(tr(lang,"guide_${pages[page]}_title"),fontSize=27.sp,fontWeight=FontWeight.Bold)
     Text(tr(lang,"guide_${pages[page]}_body"),fontSize=17.sp)
     if(pages[page]=="community") CommunityActions(lang,onOpen)
    }
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
     TextButton(onClick=onDone) { Text(tr(lang,"skip")) }
     Spacer(Modifier.weight(1f))
     Button(onClick={ if(page<pages.lastIndex) page++ else onDone() },shape=RoundedCornerShape(10.dp)) { Text(tr(lang,if(page==pages.lastIndex) "finish" else "next")) }
    }
   }
  }
 }
}
