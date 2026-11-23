app/src/main/java/com/cem/jarvis/MainActivity.kt:53:class MainActivity : Activity() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:118:    override fun onCreate(b: Bundle?) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:156:    override fun onResume() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:166:    override fun onPause() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:177:    override fun onDestroy() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:184:    override fun onBackPressed() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:201:    private fun showCrash(msg: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:251:    private fun buildUi() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:281:    private fun showPage(i: Int) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:296:    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
app/src/main/java/com/cem/jarvis/MainActivity.kt:298:    private fun add(col: LinearLayout, v: View) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:304:    private fun scrollOf(v: View): ScrollView {
app/src/main/java/com/cem/jarvis/MainActivity.kt:310:    private fun chip(text: String, onClick: () -> Unit): Button {
app/src/main/java/com/cem/jarvis/MainActivity.kt:319:    private fun buildHome(): View {
app/src/main/java/com/cem/jarvis/MainActivity.kt:446:            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:447:            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) { ref
app/src/main/java/com/cem/jarvis/MainActivity.kt:448:            override fun afterTextChanged(s: android.text.Editable?) {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:469:    private fun attachOrbGesture() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:498:    private fun openChat() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:517:    private fun closeChat() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:536:    private fun kbOpen(): Boolean {
app/src/main/java/com/cem/jarvis/MainActivity.kt:543:    override fun onNewIntent(intent: Intent?) { super.onNewIntent(intent); setIntent(in
app/src/main/java/com/cem/jarvis/MainActivity.kt:545:    private fun placeOrb(animate: Boolean = false) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:567:    private fun showOutputPicker() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:603:    private fun hideKeyboard() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:611:    private fun plusMenu() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:624:    private fun micOrSend() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:632:    private fun startVoice() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:643:    private fun stopVoice() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:656:    private fun speakerTap() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:667:    private fun refreshHomeMic() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:673:    private fun pulseOrb(x: Float) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:678:    private fun prefill(text: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:684:    private fun sendComposer() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:692:    private fun buildVoice(): View {
app/src/main/java/com/cem/jarvis/MainActivity.kt:725:    private fun buildAssist(): View {
app/src/main/java/com/cem/jarvis/MainActivity.kt:772:    private fun buildFilesPage(): View {
app/src/main/java/com/cem/jarvis/MainActivity.kt:786:    private fun openAndroidFilePicker() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:798:    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:816:    private fun saveFileRecord(name: String, uriStr: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:822:    private fun loadInternalFileList() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:852:    private fun deleteFileRecord(index: Int) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:862:    private fun buildSettings(): View {
app/src/main/java/com/cem/jarvis/MainActivity.kt:878:    private fun renderFacts(box: LinearLayout) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:895:    private fun keyField(col: LinearLayout, hintText: String, prefKey: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:915:    private fun saveSettings() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:920:    private fun flagLabel(flag: String): String =
app/src/main/java/com/cem/jarvis/MainActivity.kt:923:    private fun flagButton(flag: String): Button {
app/src/main/java/com/cem/jarvis/MainActivity.kt:938:    private fun refreshToggles() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:945:    private fun newsTopic(): String = store.str("news", "Türkiye").ifBlank { "Türkiye" 
app/src/main/java/com/cem/jarvis/MainActivity.kt:947:    private fun buildSidebar(): LinearLayout {
app/src/main/java/com/cem/jarvis/MainActivity.kt:974:    private fun openSide() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:980:    private fun closeSide() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:985:    private fun refreshSideList() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1005:    private fun newChat() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1013:    private fun openConv(c: Conv) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1020:    private fun exportConv() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1030:    private fun renderConv(c: Conv) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1041:    private fun oyGonder(v: Int, t: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1047:    private fun labelOf(role: String): String = if (role == "user") "Sen" else "J"
app/src/main/java/com/cem/jarvis/MainActivity.kt:1049:    private fun addBubble(who: String, text: String, srcs: List<Pair<String, String>> 
app/src/main/java/com/cem/jarvis/MainActivity.kt:1060:    private fun clearSuggest() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1070:    private fun suggestTap(s: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1076:    private fun showThinking() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1087:    private fun orbThink(st: Int) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1092:    private fun hideThinking() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1099:    private fun say(display: String, spoken: String = display, srcs: List<Pair<String,
app/src/main/java/com/cem/jarvis/MainActivity.kt:1105:    private fun sayGizli(metin: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1110:    private fun setOrb(state: Int) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1136:    private fun onTtsInit(status: Int) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1141:            override fun onStart(id: String?) { runOnUiThread { setOrb(2); if (voiceTu
app/src/main/java/com/cem/jarvis/MainActivity.kt:1142:            override fun onDone(id: String?) { runOnUiThread { setOrb(0); stopBarge();
app/src/main/java/com/cem/jarvis/MainActivity.kt:1143:            override fun onError(id: String?) { runOnUiThread { setOrb(0); stopBarge()
app/src/main/java/com/cem/jarvis/MainActivity.kt:1145:            override fun onRangeStart(id: String?, start: Int, end: Int, frame: Int) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1151:    private fun speakNow(t: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1166:    private fun stopBarge() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1172:    private fun bargeHit(res: Bundle?, b: SpeechRecognizer): Boolean {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1184:    private fun startBarge() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1190:            override fun onReadyForSpeech(p: Bundle?) {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:1191:            override fun onBeginningOfSpeech() {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:1192:            override fun onRmsChanged(v: Float) {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:1193:            override fun onBufferReceived(x: ByteArray?) {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:1194:            override fun onEndOfSpeech() {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:1195:            override fun onEvent(t: Int, p: Bundle?) {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:1196:            override fun onPartialResults(p: Bundle?) { bargeHit(p, b) }
app/src/main/java/com/cem/jarvis/MainActivity.kt:1197:            override fun onResults(res: Bundle?) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1200:            override fun onError(err: Int) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1212:    private fun loopActive(): Boolean = continuous || awaitingCommand || store.flag("h
app/src/main/java/com/cem/jarvis/MainActivity.kt:1214:    private fun afterSpeech() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1218:    private fun restartLater(ms: Long) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1222:    private fun hasMic(): Boolean =
app/src/main/java/com/cem/jarvis/MainActivity.kt:1225:    private fun setContinuous(on: Boolean) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1232:    private fun contLabel(): String = if (continuous) "Sürekli dinleme: AÇIK" else "Sü
app/src/main/java/com/cem/jarvis/MainActivity.kt:1234:    private fun toggleContinuous() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1244:    private fun setWake(on: Boolean) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1262:    private fun stopAll() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1273:    private fun micTap() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1282:    private fun setMicUi(on: Boolean) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1290:    private fun keepScreen(on: Boolean) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1295:    private fun listen(manual: Boolean = false) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1303:            override fun onReadyForSpeech(p: Bundle?) { listeningNow = true; setMicUi(
app/src/main/java/com/cem/jarvis/MainActivity.kt:1304:            override fun onBeginningOfSpeech() {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:1305:            override fun onRmsChanged(v: Float) { pulseOrb(((v + 2f) / 12f).coerceIn(0
app/src/main/java/com/cem/jarvis/MainActivity.kt:1306:            override fun onBufferReceived(b: ByteArray?) {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:1307:            override fun onEndOfSpeech() {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:1308:            override fun onPartialResults(p: Bundle?) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1312:            override fun onEvent(t: Int, p: Bundle?) {}
app/src/main/java/com/cem/jarvis/MainActivity.kt:1313:            override fun onError(err: Int) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1321:            override fun onResults(res: Bundle?) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1353:    private fun wakeFilter(text: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1368:    private fun confirm(msg: String, onYes: () -> Unit, onNo: () -> Unit) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1377:    private fun handle(raw: String, fromAi: Boolean = false) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1405:    private fun safeStart(i: Intent, failMsg: String): Boolean {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1415:    private fun localCommand(raw: String, s: String): Boolean {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1504:    private fun hasAiKey(): Boolean = store.str("bulut").isNotBlank() || Ai.ALL.any { 
app/src/main/java/com/cem/jarvis/MainActivity.kt:1505:    private fun keyName(who: String): String = if (who == "gemini") "key" else who
app/src/main/java/com/cem/jarvis/MainActivity.kt:1506:    private fun providerKeys(): Map<String, String> = Ai.ALL.associateWith { store.str
app/src/main/java/com/cem/jarvis/MainActivity.kt:1507:    private fun providerModels(): Map<String, String> = mapOf(
app/src/main/java/com/cem/jarvis/MainActivity.kt:1512:    private fun providerOrder(): List<String> = Ai.ALL
app/src/main/java/com/cem/jarvis/MainActivity.kt:1514:    private fun needsLive(t: String): Boolean {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1520:    private fun askAi(text: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1543:    private fun doResearch(q: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1561:    private fun doTranslate(lang: String, text: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1576:    private fun testProvider(who: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1587:    private fun showTest(t: String) { if (::testOut.isInitialized) testOut.text = t }
app/src/main/java/com/cem/jarvis/MainActivity.kt:1589:    private fun orderLabel(): String = store.str("order", "auto")
app/src/main/java/com/cem/jarvis/MainActivity.kt:1591:    private fun pickOrder() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1597:    private fun setNewsText(t: String) { if (::newsView.isInitialized) newsView.text =
app/src/main/java/com/cem/jarvis/MainActivity.kt:1598:    private fun setStockText(t: String) { if (::stockView.isInitialized) stockView.tex
app/src/main/java/com/cem/jarvis/MainActivity.kt:1600:    private fun fetchNews(topic: String, speak: Boolean) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1616:    private fun fetchWatchlist(speak: Boolean) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1626:    private fun refreshTasks() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1636:    private fun addTask(text: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1643:    private fun addTaskFromUi() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1648:    private fun toggleTask(i: Int) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1656:    private fun removeDoneTasks() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1667:    private fun refreshReminders() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1674:    private fun refreshDevice() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1678:    private fun refreshStatus() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1686:    private fun exportMemory() {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1692:    private fun memCloud(raw: String): Boolean {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1734:    private fun searchFacts(query: String): List<String> {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1743:    private fun torch(on: Boolean): Boolean {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1751:    private fun openApp(name: String) {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1758:    private fun isOnline(): Boolean {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1764:    private fun batteryInfo(): Pair<Int, Boolean>? {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1773:    private fun batteryText(): String {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1778:    private fun storageLine(): String = try {
app/src/main/java/com/cem/jarvis/MainActivity.kt:1783:    private fun greeting(): String {
app/src/main/java/com/cem/jarvis/Reminders.kt:24:object Reminders {
app/src/main/java/com/cem/jarvis/Reminders.kt:31:    class Item(val id: Int, val time: Long, val text: String)
app/src/main/java/com/cem/jarvis/Reminders.kt:32:    class Parsed(val time: Long, val text: String)
app/src/main/java/com/cem/jarvis/Reminders.kt:36:    private fun load(ctx: Context): MutableList<Item> {
app/src/main/java/com/cem/jarvis/Reminders.kt:51:    private fun save(ctx: Context, list: List<Item>) {
app/src/main/java/com/cem/jarvis/Reminders.kt:59:    fun upcoming(ctx: Context): List<Item> {
app/src/main/java/com/cem/jarvis/Reminders.kt:66:    private fun firePending(ctx: Context, id: Int, text: String): PendingIntent {
app/src/main/java/com/cem/jarvis/Reminders.kt:75:    private fun openAppPending(ctx: Context, rid: Int = 0, rtext: String = ""): PendingInte
app/src/main/java/com/cem/jarvis/Reminders.kt:83:    private fun schedule(ctx: Context, item: Item) {
app/src/main/java/com/cem/jarvis/Reminders.kt:91:    fun add(ctx: Context, time: Long, text: String): Item {
app/src/main/java/com/cem/jarvis/Reminders.kt:103:    fun remove(ctx: Context, id: Int) {
app/src/main/java/com/cem/jarvis/Reminders.kt:109:    fun cancelAll(ctx: Context) {
app/src/main/java/com/cem/jarvis/Reminders.kt:117:    fun restoreAll(ctx: Context) {
app/src/main/java/com/cem/jarvis/Reminders.kt:133:    fun notificationsEnabled(ctx: Context): Boolean {
app/src/main/java/com/cem/jarvis/Reminders.kt:138:    fun notifyNow(ctx: Context, id: Int, text: String) {
app/src/main/java/com/cem/jarvis/Reminders.kt:164:    fun tryHandle(ctx: Context, raw: String, say: (String) -> Unit): Boolean {
app/src/main/java/com/cem/jarvis/Reminders.kt:204:    fun warnIfDisabled(ctx: Context): String =
app/src/main/java/com/cem/jarvis/Reminders.kt:208:    fun describe(time: Long): String {
app/src/main/java/com/cem/jarvis/Reminders.kt:270:    private fun num(raw: String): Int? {
app/src/main/java/com/cem/jarvis/Reminders.kt:283:    private fun cut(s: String, m: MatchResult): String =
app/src/main/java/com/cem/jarvis/Reminders.kt:286:    fun parse(s: String, now: Calendar): Parsed? {
app/src/main/java/com/cem/jarvis/Reminders.kt:425:class ReminderReceiver : BroadcastReceiver() {
app/src/main/java/com/cem/jarvis/Reminders.kt:426:    override fun onReceive(ctx: Context, intent: Intent) {
app/src/main/java/com/cem/jarvis/Reminders.kt:437:class BootReceiver : BroadcastReceiver() {
app/src/main/java/com/cem/jarvis/Reminders.kt:438:    override fun onReceive(ctx: Context, intent: Intent) {
app/src/main/java/com/cem/jarvis/Store.kt:8:class Conv(val id: String, var title: String, var updated: Long, val msgs: MutableList<Pair<Stri
app/src/main/java/com/cem/jarvis/Store.kt:10:class Store(ctx: Context) {
app/src/main/java/com/cem/jarvis/Store.kt:18:    fun str(k: String, def: String = ""): String = prefs.getString(k, def) ?: def
app/src/main/java/com/cem/jarvis/Store.kt:20:    fun put(k: String, v: String) {
app/src/main/java/com/cem/jarvis/Store.kt:24:    fun flag(k: String, def: Boolean): Boolean = prefs.getBoolean(k, def)
app/src/main/java/com/cem/jarvis/Store.kt:26:    fun setFlag(k: String, v: Boolean) {
app/src/main/java/com/cem/jarvis/Store.kt:30:    fun convs(): List<Conv> = cache.sortedByDescending { it.updated }
app/src/main/java/com/cem/jarvis/Store.kt:32:    fun conv(id: String): Conv? = cache.firstOrNull { it.id == id }
app/src/main/java/com/cem/jarvis/Store.kt:34:    fun newConv(): Conv {
app/src/main/java/com/cem/jarvis/Store.kt:42:    fun current(): Conv {
app/src/main/java/com/cem/jarvis/Store.kt:49:    fun setCurrent(c: Conv) {
app/src/main/java/com/cem/jarvis/Store.kt:53:    fun addMsg(c: Conv, role: String, text: String) {
app/src/main/java/com/cem/jarvis/Store.kt:61:    fun deleteConv(id: String) {
app/src/main/java/com/cem/jarvis/Store.kt:66:    fun clearConvs() {
app/src/main/java/com/cem/jarvis/Store.kt:72:    private fun save() {
app/src/main/java/com/cem/jarvis/Store.kt:83:    private fun loadConvs() {
app/src/main/java/com/cem/jarvis/Store.kt:103:    fun facts(): String = str("facts")
app/src/main/java/com/cem/jarvis/Store.kt:105:    fun addFact(f: String) {
app/src/main/java/com/cem/jarvis/Store.kt:110:    fun clearFacts() {
app/src/main/java/com/cem/jarvis/Store.kt:114:    fun tasks(): JSONArray = try {
app/src/main/java/com/cem/jarvis/Store.kt:120:    fun saveTasks(arr: JSONArray) {
app/src/main/java/com/cem/jarvis/Store.kt:124:    fun watch(): List<String> = str("watch").split(",").map { it.trim() }.filter { it.isNotEmp
app/src/main/java/com/cem/jarvis/Store.kt:126:    fun addWatch(sym: String) {
app/src/main/java/com/cem/jarvis/Store.kt:132:    fun exportJson(): String = JSONObject()
app/src/main/java/com/cem/jarvis/Store.kt:138:    fun importJson(text: String): Boolean {
app/src/main/java/com/cem/jarvis/Store.kt:157:    fun clearAllData(keepKeys: Boolean) {
app/src/main/java/com/cem/jarvis/Util.kt:5:class MathParser(private val s: String) {
app/src/main/java/com/cem/jarvis/Util.kt:8:    fun parse(): Double {
app/src/main/java/com/cem/jarvis/Util.kt:14:    private fun expr(): Double {
app/src/main/java/com/cem/jarvis/Util.kt:24:    private fun term(): Double {
app/src/main/java/com/cem/jarvis/Util.kt:34:    private fun factor(): Double {
app/src/main/java/com/cem/jarvis/Util.kt:53:fun evalMath(input: String): Double? {
app/src/main/java/com/cem/jarvis/Util.kt:67:fun formatNumber(v: Double): String =
app/src/main/java/com/cem/jarvis/Util.kt:71:fun waNumber(raw: String): String {
app/src/main/java/com/cem/jarvis/Util.kt:92:fun cleanSuffixes(word: String): String {
app/src/main/java/com/cem/jarvis/Views.kt:28:object Pal {
app/src/main/java/com/cem/jarvis/Views.kt:47:class HoloView(c: Context) : View(c) {
app/src/main/java/com/cem/jarvis/Views.kt:103:    private fun hash(i: Int): Double {
app/src/main/java/com/cem/jarvis/Views.kt:108:    private fun put(cv: Canvas, x: Float, y: Float, size: Float, a: Int, col: Int) {
app/src/main/java/com/cem/jarvis/Views.kt:114:    override fun onDraw(cv: Canvas) {
app/src/main/java/com/cem/jarvis/Views.kt:250:class WaveView(c: Context) : View(c) {
app/src/main/java/com/cem/jarvis/Views.kt:255:    override fun onDraw(cv: Canvas) {
app/src/main/java/com/cem/jarvis/Views.kt:282:object Ui {
app/src/main/java/com/cem/jarvis/Views.kt:283:    fun dp(ctx: Context, v: Int): Int = (v * ctx.resources.displayMetrics.density + 0.5f).toIn
app/src/main/java/com/cem/jarvis/Views.kt:286:    fun rounded(fill: String, stroke: String?, radius: Float): GradientDrawable {
app/src/main/java/com/cem/jarvis/Views.kt:294:    fun column(ctx: Context): LinearLayout {
app/src/main/java/com/cem/jarvis/Views.kt:300:    fun row(ctx: Context): LinearLayout {
app/src/main/java/com/cem/jarvis/Views.kt:307:    fun text(ctx: Context, s: String, size: Float = 15f, color: String = Pal.TEXT, bold: Boole
app/src/main/java/com/cem/jarvis/Views.kt:316:    fun section(ctx: Context, s: String): TextView {
app/src/main/java/com/cem/jarvis/Views.kt:323:    fun card(ctx: Context, s: String): TextView {
app/src/main/java/com/cem/jarvis/Views.kt:334:    fun button(ctx: Context, s: String, onClick: () -> Unit): Button {
app/src/main/java/com/cem/jarvis/Views.kt:348:    fun primary(ctx: Context, s: String, onClick: () -> Unit): Button {
app/src/main/java/com/cem/jarvis/Views.kt:355:    fun input(ctx: Context, hint: String): EditText {
app/src/main/java/com/cem/jarvis/Views.kt:369:    private fun iconRes(ctx: Context, name: String): Int =
app/src/main/java/com/cem/jarvis/Views.kt:372:    fun icon(ctx: Context, name: String, sizeDp: Int = 22, color: String = Pal.TEXT): ImageVie
app/src/main/java/com/cem/jarvis/Views.kt:383:    fun iconBtn(
app/src/main/java/com/cem/jarvis/Views.kt:411:    fun swapIcon(btn: FrameLayout, name: String, color: String) {
app/src/main/java/com/cem/jarvis/Views.kt:418:    fun badge(ctx: Context, s: String, color: String): TextView {
app/src/main/java/com/cem/jarvis/Views.kt:425:    fun chip(ctx: Context, s: String, active: Boolean, onClick: () -> Unit): TextView {
app/src/main/java/com/cem/jarvis/Views.kt:438:    fun listRow(ctx: Context, ico: String, title: String, sub: String?, trailing: View?, onCli
app/src/main/java/com/cem/jarvis/Views.kt:469:    fun switchRow(ctx: Context, title: String, sub: String?, checked: Boolean, onChange: (Bool
app/src/main/java/com/cem/jarvis/Views.kt:492:    fun line(ctx: Context, who: String, text: String, sources: List<Pair<String, String>> = em
app/src/main/java/com/cem/jarvis/Views.kt:526:            fun act(label: String, run: () -> Unit): TextView {
app/src/main/java/com/cem/jarvis/Commands.kt:17:object Commands {
app/src/main/java/com/cem/jarvis/Commands.kt:18:    private fun go(ctx: Context, i: Intent, say: (String) -> Unit, ok: String): Boolean {
app/src/main/java/com/cem/jarvis/Commands.kt:24:    private fun media(ctx: Context, code: Int) {
app/src/main/java/com/cem/jarvis/Commands.kt:30:    private fun playing(c: android.media.session.MediaController?): Boolean =
app/src/main/java/com/cem/jarvis/Commands.kt:33:    private fun mcOf(ctx: Context, pkg: String?): android.media.session.MediaController? = t
app/src/main/java/com/cem/jarvis/Commands.kt:39:    private fun mediaVerify(ctx: Context, want: Boolean, okMsg: String, failMsg: String, say
app/src/main/java/com/cem/jarvis/Commands.kt:50:    private fun musicVerify(ctx: Context, pkg: String, aname: String, q: String, say: (Strin
app/src/main/java/com/cem/jarvis/Commands.kt:65:    private fun volume(ctx: Context, dir: Int) {
app/src/main/java/com/cem/jarvis/Commands.kt:70:    private fun parseTime(s: String): Pair<Int, Int>? {
app/src/main/java/com/cem/jarvis/Commands.kt:88:    private fun callIntent(ctx: Context, num: String): Intent =
app/src/main/java/com/cem/jarvis/Commands.kt:91:    private fun phoneOf(ctx: Context, name: String): String? {
app/src/main/java/com/cem/jarvis/Commands.kt:99:    private fun intl(n: String): String {
app/src/main/java/com/cem/jarvis/Commands.kt:104:    fun tryHandle(ctx: Context, s: String, say: (String) -> Unit): Boolean {
app/src/main/java/com/cem/jarvis/Commands.kt:112:    private fun tryOne(ctx: Context, s: String, say: (String) -> Unit): Boolean {
app/src/main/java/com/cem/jarvis/Notif.kt:10:class AriesNotifService : NotificationListenerService() {
app/src/main/java/com/cem/jarvis/Notif.kt:21:        fun oku(ctx: Context, say: (String) -> Unit) {
app/src/main/java/com/cem/jarvis/Notif.kt:47:    override fun onCreate() {
app/src/main/java/com/cem/jarvis/Notif.kt:57:    override fun onDestroy() {
app/src/main/java/com/cem/jarvis/Notif.kt:64:    override fun onListenerConnected() { instance = this }
app/src/main/java/com/cem/jarvis/Notif.kt:65:    override fun onListenerDisconnected() { instance = null }
app/src/main/java/com/cem/jarvis/Notif.kt:67:    override fun onNotificationPosted(sbn: StatusBarNotification) {
app/src/main/java/com/cem/jarvis/Notif.kt:96:    private fun soyle(cumle: String) {
app/src/main/java/com/cem/jarvis/Notif.kt:102:    private fun uygulamaAdi(paket: String): String = try {
app/src/main/java/com/cem/jarvis/Notif.kt:106:    private fun kaynakCumlesi(paket: String, gonderen: String): String {
