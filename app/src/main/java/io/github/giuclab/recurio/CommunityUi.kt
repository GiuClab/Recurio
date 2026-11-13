// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable fun CommunityActions(lang: String, onOpen:(String)->Unit) {
 Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
  OutlinedButton(onClick={onOpen(ProjectLinks.feedback)},modifier=Modifier.fillMaxWidth()) {
   Image(painterResource(R.drawable.ic_service_github),null,Modifier.size(24.dp));Spacer(Modifier.width(12.dp));Text(tr(lang,"feedback"))
  }
  OutlinedButton(onClick={ProjectLinks.fdroid?.let(onOpen)},enabled=ProjectLinks.fdroid!=null,modifier=Modifier.fillMaxWidth()) {
   Image(painterResource(R.drawable.ic_fdroid),null,Modifier.size(24.dp));Spacer(Modifier.width(12.dp));Text("F-Droid · ${tr(lang,"comingSoon")}")
  }
  Button(onClick={onOpen(ProjectLinks.support)},modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=SuccessGreen)) {
   Text("♥  ${tr(lang,"support")} · Ko-fi")
  }
 }
}
