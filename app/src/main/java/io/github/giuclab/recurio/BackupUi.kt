// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import kotlinx.coroutines.*
import java.io.File
import java.time.LocalDate

@Composable fun BackupScreen(ctx: Context, lang: String, payments: List<Payment>, canExport: Boolean, onRestored: (List<Payment>, Boolean) -> Unit, onClose: () -> Unit) {
 val scope=rememberCoroutineScope(); val t:(String)->String={tr(lang,it)}
 var password by remember { mutableStateOf("") };var confirm by remember { mutableStateOf("") }
 var busy by remember { mutableStateOf(false) }
 var importUri by rememberSaveable { mutableStateOf<String?>(null) }
 var incoming by remember { mutableStateOf<List<Payment>?>(null) }
 var message by remember { mutableStateOf<String?>(null) }
 val pending=remember { File(ctx.cacheDir,"recurio-pending-encrypted-backup") }
 val exportPicker=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
  scope.launch {
   if(uri!=null) {
    val ok=withContext(Dispatchers.IO) { runCatching {
     require(pending.exists()); ctx.contentResolver.openOutputStream(uri,"w")!!.use { output -> pending.inputStream().use { it.copyTo(output) } }
    }.isSuccess }
    message=t(if(ok) "exportSuccess" else "backupWriteError")
   }
   withContext(Dispatchers.IO) { pending.delete() };busy=false
  }
 }
 val importPicker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
  busy=false
  if(uri!=null) { importUri=uri.toString();password="";confirm="";incoming=null }
 }
 Dialog(onDismissRequest={if(!busy) onClose()},properties=DialogProperties(usePlatformDefaultWidth=false)) {
  Surface(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),color=Background) {
   Column(Modifier.fillMaxSize().imePadding()) {
    Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
     Text(t("backupTitle"),Modifier.weight(1f),fontSize=20.sp,fontWeight=FontWeight.Bold)
     TextButton(onClick=onClose,enabled=!busy) {Text(t("close"))}
    }
    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
     Text(t("backupHelp"))
     if(importUri==null) {
      Text(t("exportTitle"),fontWeight=FontWeight.Bold)
      Text(t("backupPasswordHelp"),fontSize=13.sp,color=Muted)
     } else { Text(t("restoreTitle"),fontWeight=FontWeight.Bold);Text(t("restorePasswordHelp"),fontSize=13.sp,color=Muted) }
     OutlinedTextField(password,{password=it.take(1024)},label={Text(t("password"))},singleLine=true,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),enabled=!busy,modifier=Modifier.fillMaxWidth())
     if(importUri==null) OutlinedTextField(confirm,{confirm=it.take(1024)},label={Text(t("confirmPassword"))},singleLine=true,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),enabled=!busy,modifier=Modifier.fillMaxWidth())
     if(busy) LinearProgressIndicator(Modifier.fillMaxWidth())
     if(importUri==null) {
      Button(onClick={
       if(password.length<5 || password!=confirm) message=t("passwordInvalid")
       else {
        val secret=password.toCharArray();password="";confirm="";busy=true
        scope.launch {
         val ok=withContext(Dispatchers.IO) {
          try { pending.writeBytes(Backup.encrypt(payments,secret));true } catch(_:Exception) {pending.delete();false} finally {secret.fill('\u0000')}
         }
         if(ok) try { exportPicker.launch("Recurio-${LocalDate.now()}.recurio") } catch(_:Exception) {pending.delete();busy=false;message=t("backupWriteError")}
         else {busy=false;message=t("backupWriteError")}
        }
       }
      },enabled=!busy && canExport,modifier=Modifier.fillMaxWidth()) {Text(t("exportTitle"))}
      HorizontalDivider()
      Text(t("restoreWarning"),fontSize=13.sp)
      OutlinedButton(onClick={password="";confirm="";busy=true;try {importPicker.launch(arrayOf("*/*"))}catch(_:Exception){busy=false;message=t("backupReadError")}},enabled=!busy,modifier=Modifier.fillMaxWidth()) {Text(t("chooseBackup"))}
     } else {
      Button(onClick={
       if(password.length<5) message=t("wrongPassword")
       else {
        val secret=password.toCharArray();val uri=importUri!!.toUri();password="";busy=true
        scope.launch {
         val read=withContext(Dispatchers.IO) {
          try {Result.success(ctx.contentResolver.openInputStream(uri)!!.use {Backup.readLimited(it)}.let {Backup.decrypt(it,secret)})}catch(e:Exception){Result.failure<List<Payment>>(e)}finally{secret.fill('\u0000')}
         }
         incoming=read.getOrNull();busy=false
         if(read.isFailure)message=t(restoreFailureMessage(read.exceptionOrNull() as Exception))
        }
       }
      },enabled=!busy,modifier=Modifier.fillMaxWidth()) {Text(t("previewRestore"))}
      TextButton(onClick={importUri=null;password="";confirm=""},enabled=!busy) {Text(t("cancel"))}
     }
    }
   }
  }
 }
 if(incoming!=null) AlertDialog(onDismissRequest={if(!busy)incoming=null},title={Text(t("restoreTitle"))},text={Text("${t("restoreCount")} ${incoming!!.size}\n${t("currentCount")} ${payments.size}\n\n${t("restoreWarning")}")},confirmButton={TextButton(enabled=!busy,onClick={
  val next=incoming!!;busy=true
  scope.launch {
   val result=withContext(Dispatchers.IO) {
    runCatching {
     val store=Storage(ctx);val old=runCatching {store.read()}.getOrDefault(emptyList())
     store.write(next)
     val scheduled=runCatching {old.forEach {Reminders.cancel(ctx,it.id)};Reminders.all(ctx)}.isSuccess
     scheduled
    }
   }
   busy=false;incoming=null
   if(result.isSuccess){importUri=null;onRestored(next,result.getOrDefault(false))}
   else message=t("restoreWriteError")
  }
 }){Text(t("replaceRestore"))}},dismissButton={TextButton(onClick={incoming=null},enabled=!busy){Text(t("cancel"))}})
 if(message!=null) AlertDialog(onDismissRequest={message=null},text={Text(message!!)},confirmButton={TextButton(onClick={message=null}){Text("OK")}})
}
