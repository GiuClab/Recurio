// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.net.toUri
import androidx.core.content.edit
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

val Blue = Color(0xFF385AD0)
val Muted = Color(0xFF677082)
val Background = Color(0xFFF5F6FB)
val Border = Color(0xFFDFE3ED)

class MainActivity: ComponentActivity() {
 private var shared by mutableStateOf<String?>(null)
 private var focusedPayment by mutableStateOf<String?>(null)
 private var notificationEnabled by mutableStateOf(true)
 private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { notificationEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled() }
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  receive(intent)
  Reminders.channel(this)
  setContent { MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Background, surface = Color.White, onSurface = Color(0xFF242936), secondary = Blue, outline = Border, surfaceContainerHigh = Color.White, surfaceContainerHighest = Color.White, surfaceVariant = Color(0xFFF0F2F8), surfaceTint = Blue)) { App() } }
 }
 override fun onNewIntent(intent: Intent) { super.onNewIntent(intent); setIntent(intent); receive(intent) }
 override fun onResume() { super.onResume(); notificationEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled(); runCatching { Reminders.all(this) } }
 private fun receive(intent: Intent) { focusedPayment = intent.getStringExtra("payment_id"); if (intent.action == Intent.ACTION_SEND) shared = intent.getStringExtra(Intent.EXTRA_TEXT)?.take(16384) ?: "" }
 private fun requestNotifications() { if (Build.VERSION.SDK_INT >= 33) permission.launch(Manifest.permission.POST_NOTIFICATIONS) }
 private fun open(link: String, lang: String) {
  val normalized = Catalog.normalizeLink(link) ?: return
  try { startActivity(Intent(Intent.ACTION_VIEW, normalized.toUri()).addCategory(Intent.CATEGORY_BROWSABLE)) }
  catch (_: Exception) { Toast.makeText(this, tr(lang, "browserError"), Toast.LENGTH_LONG).show() }
 }
 @Composable private fun App() {
  val store = remember { Storage(this) }
  val initialSetup = remember {
   val plan=startupPlan(resources.configuration.locales[0].language,store.prefs.getString("language",null),store.prefs.getBoolean("setup_started",false),store.prefs.getBoolean("guide_done",false),store.prefs.getBoolean("setup_done",false))
   store.prefs.edit {putBoolean("setup_started",true);if(!plan.chooseLanguage)putString("language",plan.language);if(plan.legacyUpgrade)putBoolean("setup_done",true)}
   plan
  }
  var lang by rememberSaveable { mutableStateOf(initialSetup.language) }
  var chooseLanguage by rememberSaveable { mutableStateOf(initialSetup.chooseLanguage) }
  var showGuide by rememberSaveable { mutableStateOf(initialSetup.guide) }
  var notifyIntro by rememberSaveable { mutableStateOf(initialSetup.notificationIntro) }
  var guideReplay by rememberSaveable { mutableStateOf(false) }
  var showBackup by rememberSaveable { mutableStateOf(false) }
  LaunchedEffect(lang) { Reminders.channel(this@MainActivity) }
  var readError by remember { mutableStateOf(false) }
  var payments by remember { mutableStateOf(runCatching { store.read() }.getOrElse { readError = true; emptyList() }) }
  var editor by remember { mutableStateOf<Payment?>(null) }
  var settings by remember { mutableStateOf(false) }
  var icons by remember { mutableStateOf(store.prefs.getBoolean("icons", true)) }
  var order by remember { mutableStateOf(runCatching { PaymentOrder.valueOf(store.prefs.getString("order", "DUE") ?: "DUE") }.getOrDefault(PaymentOrder.DUE)) }
  var orderMenu by remember { mutableStateOf(false) }
  var restoreIconOffer by rememberSaveable {mutableStateOf(false)}
  var restoredAlarmsOk by rememberSaveable {mutableStateOf(true)}
  var iconNotice by rememberSaveable {mutableStateOf(!initialSetup.guide && !initialSetup.chooseLanguage && !initialSetup.notificationIntro && !store.prefs.getBoolean("icon_disclosure_seen",false))}
  val allowIconNetwork = icons && !iconNotice && !restoreIconOffer && !chooseLanguage && !showGuide && !notifyIntro
  var showIconLicense by remember { mutableStateOf(false) }
  var shareDialog by remember {mutableStateOf(false)}
  var message by remember { mutableStateOf<String?>(null) }
  val t: (String) -> String = { tr(lang, it) }
  LaunchedEffect(focusedPayment) { focusedPayment?.let { id -> editor = payments.firstOrNull { it.id == id }; focusedPayment = null } }
  LaunchedEffect(shared, chooseLanguage, showGuide, notifyIntro) {
   if (shared != null && !chooseLanguage && !showGuide && !notifyIntro) {
    val url = Catalog.sharedLink(shared!!)
    if (url != null && !readError) editor = Payment(name = Catalog.suggestedName(url).orEmpty(), cents = 0, anchor = LocalDate.now(), link = url)
    else message = t("linkError")
    shared = null
   }
  }
  fun persist(next: List<Payment>, removed: String? = null): Boolean {
   return try { store.write(next); payments = next; removed?.let { Reminders.cancel(this, it) }; runCatching { Reminders.all(this) }; true }
   catch (_: Exception) { message = t("writeError"); false }
  }
  Surface(modifier = Modifier.fillMaxSize(), color = Background) {
   Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
     Image(painterResource(R.drawable.ic_launcher), contentDescription = null, modifier = Modifier.size(44.dp))
     Column(Modifier.weight(1f).padding(start = 12.dp)) { Text("Recurio", fontSize = 25.sp, fontWeight = FontWeight.Bold); Text("by GiuClab", color = Muted, fontSize = 12.sp) }
     TextButton(onClick = { settings = true }) { Text(t("settings")) }
    }
    LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
     item { Text(t("tagline"), color = Muted) }
     if (readError) item { Text(t("readError"), color = MaterialTheme.colorScheme.error) }
     if (!notificationEnabled && payments.any { it.reminder && it.active }) item { Card { Column(Modifier.padding(16.dp)) { Text(t("disabled")); TextButton(onClick = { startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)) }) { Text(t("notifications")) } } } }
     item {
      Card(colors = CardDefaults.cardColors(containerColor = Blue), shape = RoundedCornerShape(16.dp)) {
       Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(t("month"), color = Color.White.copy(alpha = .85f))
        val today = LocalDate.now(); val start = today.withDayOfMonth(1); val end = start.plusMonths(1)
        val totals = totalsByCurrency(payments, start, end)
        (if (totals.isEmpty()) sortedMapOf("EUR" to 0L) else totals).forEach { (currency, value) ->
         Text("${t("monthlyTotal")} · $currency", color = Color.White.copy(alpha = .85f), fontSize = 13.sp)
         Text(money(value, currency, lang), fontSize = 30.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(color = Color.White.copy(alpha = .25f))
        Text(t("year"), color = Color.White.copy(alpha = .85f))
        val annual = totalsByCurrency(payments, today, today.plusYears(1))
        (if (annual.isEmpty()) sortedMapOf("EUR" to 0L) else annual).forEach { (currency, value) ->
         Text("$currency · ${money(value, currency, lang)}", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
       }
      }
     }
     item { Button(onClick = { editor = Payment(name = "", cents = 0, anchor = LocalDate.now()) }, enabled = !readError, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(16.dp)) { Text("+  ${t("add")}") } }
     item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
       Text(t("upcoming"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
       Box {
        OutlinedButton(onClick = { orderMenu = true }, shape = RoundedCornerShape(10.dp)) { Text("${t("sort")}: ${t(if (order == PaymentOrder.DUE) "sortDue" else "sortService")} ▾") }
        DropdownMenu(orderMenu, { orderMenu = false }) {
         PaymentOrder.entries.forEach { choice -> DropdownMenuItem(text = { Text(t(if (choice == PaymentOrder.DUE) "sortDue" else "sortService")) }, onClick = {
          order = choice; orderMenu = false; store.prefs.edit { putString("order", choice.name) }
         }) }
        }
       }
      }
     }
     if (payments.isEmpty()) item { OutlinedCard(shape = RoundedCornerShape(14.dp)) { Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(t("empty"), fontWeight = FontWeight.SemiBold); Text(t("emptyHelp"), color = Muted) } } }
     items(orderedPayments(payments, order, LocalDate.now(), lang), key = { it.id }) { p ->
      OutlinedCard(onClick = { if (!readError) editor = p }, shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = Color.White)) {
       Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
         SiteIcon(p.link, p.name, allowIconNetwork)
         Column(Modifier.weight(1f).padding(horizontal = 12.dp)) { Text(p.name, fontWeight = FontWeight.SemiBold); Text(t(p.cycle.name), color = Muted, fontSize = 12.sp) }
         Text(money(p.cents, p.currency, lang), fontWeight = FontWeight.Bold)
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
         if(p.reminder) {
          Text("${t("reminder")} ${"%02d:%02d".format(Locale.ROOT,p.hour,p.minute)}",color=Muted,fontSize=11.sp,maxLines=1,overflow=TextOverflow.Ellipsis)
          Text(reminderAdvanceText(lang,p.daysBefore),color=Muted,fontSize=11.sp,maxLines=1,overflow=TextOverflow.Ellipsis)
         } else Text(t("reminderOff"),color=Muted,fontSize=11.sp)
        }
        val due = p.nextOnOrAfter(LocalDate.now())
        Text(if (!p.active) t("paused") else due?.format(dateFormat(lang)) ?: t("past"), color = if (!p.active) Muted else Blue, fontSize = 13.sp)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
         if (p.link.isNotBlank()) TextButton(onClick = { open(p.link, lang) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.weight(1f)) { Text(t("open"), modifier = Modifier.fillMaxWidth()) }
         else Spacer(Modifier.weight(1f))
         TextButton(onClick = { editor = p }, enabled = !readError) { Text(t("editShort"), fontSize = 12.sp) }
        }
       }
      }
     }
     item { Text("Recurio v${BuildConfig.VERSION_NAME} — © 2026 GiuClab", color = Muted, fontSize = 11.sp) }
    }
   }
  }
  if (editor != null) PaymentEditor(editor!!, lang, allowIconNetwork, onDismiss = { editor = null }, onSave = { p ->
   val next = payments.filterNot { it.id == p.id } + p
   try {
    store.write(next); Reminders.cancel(this@MainActivity, p.id); payments = next
    runCatching { Reminders.all(this@MainActivity) }; editor = null; if (p.reminder) requestNotifications()
   } catch (_: Exception) { message = t("writeError") }
  }, onDelete = if (payments.any { it.id == editor!!.id }) ({ val id = editor!!.id; if (persist(payments.filterNot { it.id == id }, id)) editor = null }) else null)
  if (chooseLanguage) AlertDialog(onDismissRequest = {}, title = { Text("Choose your language") }, text = { Column { languages.forEach { (code, name) -> TextButton(onClick = { lang = code; store.prefs.edit { putString("language", code) }; chooseLanguage = false; showGuide = true }) { Text(name) } } } }, confirmButton = {})
  if (showGuide) IntroGuide(lang,onOpen={link -> open(link,lang)}) {
   showGuide = false
   if(!guideReplay) {store.prefs.edit {putBoolean("guide_done",true)};notifyIntro=true}
   guideReplay=false
  }
  if (notifyIntro) {
   fun completeIntro() {notifyIntro=false;iconNotice=false;store.prefs.edit {putBoolean("setup_done",true);putBoolean("icon_disclosure_seen",true);putBoolean("icons",icons)}}
   AlertDialog(onDismissRequest={completeIntro()},title={Text(t("notifyIntroTitle"))},text={Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)){Text(t("notifyIntroBody"));Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){Text(t("icons"),Modifier.weight(1f));GiuSwitch(icons){icons=it;store.prefs.edit{putBoolean("icons",it)}}}}},confirmButton={TextButton(onClick={completeIntro();requestNotifications()}){Text(t("enableNotifications"))}},dismissButton={TextButton(onClick={completeIntro()}){Text(t("notNow"))}})
  }
  if(showBackup) BackupScreen(this@MainActivity,lang,payments,!readError,onRestored={next,alarmOk -> restoreIconOffer=true;restoredAlarmsOk=alarmOk;payments=next;readError=false;showBackup=false},onClose={showBackup=false})
  if(settings) SettingsScreen(lang,icons,notificationEnabled,
   onLanguage={code -> lang=code;store.prefs.edit{putString("language",code)};Reminders.channel(this@MainActivity)},
   onIcons={value -> icons=value;store.prefs.edit{putBoolean("icons",value)}},
   onGuide={settings=false;guideReplay=true;showGuide=true},onBackup={settings=false;showBackup=true},
   onNotifications={startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,packageName))},
   onTest={if(notificationEnabled)Reminders.notify(this@MainActivity,"test","Recurio",t("testText"))else requestNotifications()},
   onSupport={open(ProjectLinks.support,lang)},onLicenses={showIconLicense=true},onFeedback={open(ProjectLinks.feedback,lang)},onRepository={open(ProjectLinks.repository,lang)},onShare={shareDialog=true},onClose={settings=false})
  if(restoreIconOffer) {
   fun chooseIcons(value:Boolean){icons=value;store.prefs.edit{putBoolean("icons",value);putBoolean("icon_disclosure_seen",true)};restoreIconOffer=false;iconNotice=false}
   AlertDialog(onDismissRequest={chooseIcons(false)},title={Text(t("restoreIconsTitle"))},text={Text(t(if(restoredAlarmsOk)"restoreSuccess" else "restoreCheckReminders")+"\n\n"+t("restoreIconsBody"))},confirmButton={TextButton(onClick={chooseIcons(true)},colors=ButtonDefaults.textButtonColors(contentColor=SuccessGreen)){Text(t("downloadIcons"))}},dismissButton={TextButton(onClick={chooseIcons(false)}){Text(t("notNow"))}})
  }
  if(iconNotice && !chooseLanguage && !showGuide && !notifyIntro && !restoreIconOffer) {
   fun acknowledgeIcons(value:Boolean){icons=value;store.prefs.edit{putBoolean("icons",value);putBoolean("icon_disclosure_seen",true)};iconNotice=false}
   AlertDialog(onDismissRequest={acknowledgeIcons(false)},title={Text(t("icons"))},text={Column(verticalArrangement=Arrangement.spacedBy(12.dp)){Text(t("iconNoticeBody"));Row(verticalAlignment=Alignment.CenterVertically){Text(t(if(icons)"enabled" else "disabledShort"),Modifier.weight(1f));GiuSwitch(icons){icons=it}}}},confirmButton={TextButton(onClick={acknowledgeIcons(icons)}){Text(t("savePreference"))}},dismissButton={TextButton(onClick={acknowledgeIcons(false)}){Text(t("notNow"))}})
  }
  if(shareDialog) AlertDialog(onDismissRequest={shareDialog=false},title={Text(t("shareApp"))},text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
   Text(t("shareAppHelp"));Text(ProjectLinks.downloads,color=Blue)
   OutlinedButton(onClick={
    val share=Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,"Recurio — ${t("tagline")}\n${ProjectLinks.downloads}")
    try{startActivity(Intent.createChooser(share,t("shareLink")));shareDialog=false}catch(_:Exception){message=t("shareError")}
   },modifier=Modifier.fillMaxWidth()){Text(t("shareLink"))}
   OutlinedButton(onClick={getSystemService(ClipboardManager::class.java).setPrimaryClip(ClipData.newPlainText("Recurio",ProjectLinks.downloads));Toast.makeText(this@MainActivity,t("linkCopied"),Toast.LENGTH_SHORT).show();shareDialog=false},modifier=Modifier.fillMaxWidth()){Text(t("copyLink"))}
  }},confirmButton={TextButton(onClick={shareDialog=false}){Text(t("close"))}})
  if (showIconLicense) AlertDialog(onDismissRequest = { showIconLicense = false }, title = { Text(t("iconLicense")) }, text = { Text(remember { assets.open("third-party-notices.txt").bufferedReader().use { it.readText() } }, modifier = Modifier.verticalScroll(rememberScrollState()), fontSize = 12.sp) }, confirmButton = { TextButton(onClick = { showIconLicense = false }) { Text("OK") } })
  if (message != null) AlertDialog(onDismissRequest = { message = null }, text = { Text(message!!) }, confirmButton = { TextButton(onClick = { message = null }) { Text("OK") } })
 }
 @Composable private fun SiteIcon(link: String, name: String, enabled: Boolean) {
  val host = Catalog.host(link)
  val localIcon = when (Catalog.recognize(link)?.id) {
   "github" -> R.drawable.ic_service_github
   "github-copilot" -> R.drawable.ic_service_copilot
   else -> null
  }
  val bitmap by produceState<android.graphics.Bitmap?>(null, host, enabled, localIcon) {
   value = null
   if (enabled && host != null && localIcon == null) value = withContext(Dispatchers.IO) { SiteIcons.load(this@MainActivity, host) }
  }
  Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFEDF1FD), modifier = Modifier.size(44.dp)) {
   Box(contentAlignment = Alignment.Center) {
    if (localIcon != null) Image(painterResource(localIcon), contentDescription = null, modifier = Modifier.padding(7.dp).fillMaxSize())
    else if (bitmap != null) Image(bitmap!!.asImageBitmap(), contentDescription = null, modifier = Modifier.padding(6.dp).fillMaxSize())
    else Text(name.take(1).uppercase(), color = Blue, fontSize = 21.sp, fontWeight = FontWeight.Bold)
   }
  }
 }
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable private fun PaymentEditor(initial: Payment, lang: String, icons: Boolean, onDismiss: () -> Unit, onSave: (Payment) -> Unit, onDelete: (() -> Unit)?) {
  var name by remember(initial.id) { mutableStateOf(if(initial.name.equals(Catalog.host(initial.link),ignoreCase=true)) Catalog.suggestedName(initial.link) ?: initial.name else initial.name) }
  var nameManuallyEdited by remember(initial.id) { mutableStateOf(!isSuggestedServiceName(initial.name,initial.link)) }; var amount by remember { mutableStateOf(if (initial.cents == 0L) "" else java.math.BigDecimal.valueOf(initial.cents, 2).toPlainString()) }
  var currency by remember { mutableStateOf(initial.currency) }; var date by remember { mutableStateOf(initial.anchor) }
  var cycle by remember { mutableStateOf(initial.cycle) }; var link by remember { mutableStateOf(initial.link) }
  var reminder by remember { mutableStateOf(initial.reminder) }; var days by remember { mutableStateOf(initial.daysBefore.toString()) }
  var hour by remember { mutableIntStateOf(initial.hour) }; var minute by remember { mutableIntStateOf(initial.minute) }
  var active by remember { mutableStateOf(initial.active) }; var error by remember { mutableStateOf<String?>(null) }; var delete by remember { mutableStateOf(false) }
  var catalogOpen by remember { mutableStateOf(false) }; var catalogQuery by remember { mutableStateOf("") }
  var cycles by remember { mutableStateOf(false) }; var currencyMenu by remember { mutableStateOf(false) }
  val t: (String) -> String = { tr(lang, it) }
  androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) {
   Surface(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing), color = Background) {
    Column(Modifier.fillMaxSize().imePadding()) {
     Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) { TextButton(onClick = onDismiss) { Text(t("cancel")) }; Text(t(if (onDelete == null) "add" else "edit"), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); TextButton(onClick = {
      val cents = parseCents(amount); val curr = currency.trim().uppercase(Locale.ROOT)
      val validCurrency = runCatching { Currency.getInstance(curr) }.isSuccess
      val advance = if (reminder) days.trim().toIntOrNull() else initial.daysBefore
      val normalizedLink = if (link.isBlank()) "" else Catalog.normalizeLink(link)
      error = when {
       name.isBlank() || name.length > 120 -> "invalidName"
       cents == null || !acceptsCurrencyAmount(cents, curr) -> "invalidAmount"
       !validCurrency -> "invalidCurrency"
       normalizedLink == null -> "invalidLink"
       advance == null || advance !in 0..365 -> "invalidDays"
       else -> null
      }
      if (error == null) onSave(initial.copy(name = name.trim(), cents = cents!!, currency = curr, anchor = date, cycle = cycle, link = normalizedLink!!, reminder = reminder, daysBefore = advance!!, hour = hour, minute = minute, active = active))
     }) { Text(t("save"), fontWeight = FontWeight.Bold) } }
     Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) { SiteIcon(link, name, icons); Text(name.ifBlank { "Recurio" }, Modifier.padding(start = 12.dp), fontSize = 23.sp, fontWeight = FontWeight.Bold) }
      if (link.isNotBlank()) Text(t("recognize"), color = Muted, fontSize = 12.sp)
      OutlinedButton(onClick = { catalogQuery = ""; catalogOpen = true }, modifier = Modifier.fillMaxWidth()) { Text(t("chooseService")) }
      OutlinedTextField(name, { name = it; nameManuallyEdited = it.isNotBlank() }, label = { Text(t("name")) }, singleLine = true, modifier = Modifier.fillMaxWidth())
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
       OutlinedTextField(amount, { amount = it }, label = { Text(t("amount")) }, supportingText = { Text(t("amountExample")) }, isError = amount.isNotEmpty() && parseCents(amount) == null, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii), modifier = Modifier.weight(1f))
       Box(Modifier.width(120.dp).padding(top = 8.dp)) {
        OutlinedButton(onClick = { currencyMenu = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)) { Text("$currency ▾") }
        DropdownMenu(currencyMenu, { currencyMenu = false }, modifier = Modifier.heightIn(max = 340.dp)) {
         (listOf(currency) + commonCurrencies).distinct().forEach { code ->
          DropdownMenuItem(text = { Text("$code — ${Currency.getInstance(code).getDisplayName(Locale.forLanguageTag(lang))}") }, onClick = { currency = code; currencyMenu = false })
         }
        }
       }
      }
      OutlinedButton(onClick = { DatePickerDialog(this@MainActivity, { _, y, m, d -> date = LocalDate.of(y, m + 1, d) }, date.year, date.monthValue - 1, date.dayOfMonth).show() }, modifier = Modifier.fillMaxWidth()) { Text("${t("date")}: ${date.format(dateFormat(lang))}") }
      Box { OutlinedButton(onClick = { cycles = true }, modifier = Modifier.fillMaxWidth()) { Text("${t("cycle")}: ${t(cycle.name)}") }; DropdownMenu(cycles, { cycles = false }) { Cycle.entries.forEach { c -> DropdownMenuItem(text = { Text(t(c.name)) }, onClick = { cycle = c; cycles = false }) } } }
      OutlinedTextField(link, { name = nameAfterLinkEdit(name,it,nameManuallyEdited); link = it }, label = { Text(t("link")) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri), modifier = Modifier.fillMaxWidth(), maxLines = 3)
      HorizontalDivider()
      Row(verticalAlignment = Alignment.CenterVertically) { Text(t("reminder"), Modifier.weight(1f), fontWeight = FontWeight.SemiBold); Switch(reminder, { reminder = it }) }
      if (reminder) {
       val reminderDate = date.minusDays((days.toLongOrNull() ?: 1L).coerceIn(0, 365))
       OutlinedButton(onClick = {
        val picker = DatePickerDialog(this@MainActivity, { _, y, m, d -> days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.of(y, m + 1, d), date).toString() }, reminderDate.year, reminderDate.monthValue - 1, reminderDate.dayOfMonth)
        picker.datePicker.minDate = date.minusDays(365).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        picker.datePicker.maxDate = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        picker.show()
       }, modifier = Modifier.fillMaxWidth()) { Text("${t("customDate")}: ${reminderDate.format(dateFormat(lang))}") }
       Text(t("orDays"), color = Muted)
       OutlinedTextField(days, { days = it }, label = { Text(t("days")) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
       Text(t("reminderDateHelp"), color = Muted, fontSize = 12.sp)
       OutlinedButton(onClick = { TimePickerDialog(this@MainActivity, { _, h, m -> hour = h; minute = m }, hour, minute, true).show() }, modifier = Modifier.fillMaxWidth()) { Text("${t("time")}: %02d:%02d".format(hour, minute)) }
       Text(t("timing"), color = Muted, fontSize = 12.sp)
      }
      Row(verticalAlignment = Alignment.CenterVertically) { Text(t("active"), Modifier.weight(1f)); Switch(active, { active = it }) }
      
      if (onDelete != null) TextButton(onClick = { delete = true }) { Text(t("delete"), color = Color(0xFFC93745)) }
      Spacer(Modifier.height(24.dp))
     }
    }
   }
  }
  if (catalogOpen) AlertDialog(onDismissRequest = { catalogOpen = false }, title = { Text(t("chooseService")) }, text = {
   Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Text(t("catalogHelp"), fontSize = 12.sp, color = Muted)
    OutlinedTextField(catalogQuery, { catalogQuery = it }, singleLine = true, label = { Text(t("searchService")) }, modifier = Modifier.fillMaxWidth())
    val found = Catalog.search(catalogQuery)
    if (found.isEmpty()) Text(t("noService"), color = Muted)
    LazyColumn(Modifier.heightIn(max = 340.dp)) {
     items(found, key = { it.id }) { service ->
      Column(Modifier.fillMaxWidth().clickable { name = service.name; link = service.publicLink; nameManuallyEdited = false; catalogOpen = false }.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
       Text(service.name, fontWeight = FontWeight.SemiBold)
       Text(t("category_${service.category}"), color = Muted, fontSize = 11.sp)
       Text(service.domains.first(), color = Muted, fontSize = 11.sp)
      }
     }
    }
   }
  }, confirmButton = { TextButton(onClick = { catalogOpen = false }) { Text(t("cancel")) } })
  if (error != null) AlertDialog(onDismissRequest = { error = null }, text = { Text(t(error!!)) }, confirmButton = { TextButton(onClick = { error = null }) { Text("OK") } })
  if (delete) AlertDialog(onDismissRequest = { delete = false }, text = { Text(t("deleteAsk")) }, confirmButton = { TextButton(onClick = { onDelete?.invoke(); delete = false }) { Text(t("delete")) } }, dismissButton = { TextButton(onClick = { delete = false }) { Text(t("cancel")) } })
 }
}
fun dateFormat(lang: String): DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.MEDIUM).withLocale(Locale.forLanguageTag(lang))
