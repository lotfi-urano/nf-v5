package com.aistudio.nasheet.app.ui.screens

import com.aistudio.nasheet.app.data.model.*
import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.nasheet.app.ui.theme.*
import java.util.Locale
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// TTS manager helper with singleton-like management for stability
class NasheetTTS(private val context: android.content.Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false
    private var isShutdown = false
    private var mediaPlayer: android.media.MediaPlayer? = null

    init {
        initializeTTS()
    }

    private fun initializeTTS() {
        if (isShutdown) return
        try {
            val appContext = context.applicationContext
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.forLanguageTag("ar"))
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        isReady = true
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("NasheetTTS", "Init failed", e)
        }
    }

    fun speak(text: String) {
        if (isReady && tts != null && !isShutdown) {
            try {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NasheetTTS_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                android.util.Log.e("NasheetTTS", "Speech error", e)
            }
        }
    }

    fun speakLetter(letter: String, letterName: String) {
        // Stop current audio
        stopLetterPlayer()

        // Normalize letter name/symbol
        val norm = letter.trim().replace("أ", "ا").replace("إ", "ا").replace("آ", "ا")
        val candidates = when {
            norm == "ا" -> listOf("alif")
            norm == "ب" || letterName.contains("ب") -> listOf("baa")
            norm == "ت" || letterName.contains("ت") -> listOf("taa")
            norm == "ث" || letterName.contains("ث") -> listOf("thaa")
            norm == "ج" || letterName.contains("ج") -> listOf("jeem")
            norm == "ح" || letterName.contains("ح") -> listOf("haa")
            norm == "خ" || letterName.contains("خ") -> listOf("khaa")
            norm == "د" || letterName.contains("د") -> listOf("daal")
            norm == "ذ" || letterName.contains("ذ") -> listOf("thaal")
            norm == "ر" || letterName.contains("ر") -> listOf("raa")
            norm == "ز" || letterName.contains("ز") -> listOf("zayn", "zaay", "zay")
            norm == "س" || letterName.contains("س") -> listOf("seen")
            norm == "ش" || letterName.contains("ش") -> listOf("sheen")
            norm == "ص" || letterName.contains("ص") -> listOf("saad")
            norm == "ض" || letterName.contains("ض") -> listOf("daad")
            norm == "ط" || letterName.contains("ط") -> listOf("toae", "toah", "toa", "taa")
            norm == "ظ" || letterName.contains("ظ") -> listOf("zoae", "zoah", "zoa")
            norm == "ع" || letterName.contains("ع") -> listOf("ayn", "ain")
            norm == "غ" || letterName.contains("غ") -> listOf("ghayn", "ghain")
            norm == "ف" || letterName.contains("ف") -> listOf("faa")
            norm == "ق" || letterName.contains("ق") -> listOf("qaaf", "qaf")
            norm == "ك" || letterName.contains("ك") -> listOf("kaaf", "kaf")
            norm == "ل" || letterName.contains("ل") -> listOf("laam", "lam")
            norm == "م" || letterName.contains("م") -> listOf("meem", "mim")
            norm == "ن" || letterName.contains("ن") -> listOf("noon", "nun")
            norm == "ه" || letterName.contains("ه") -> listOf("heh", "ha")
            norm == "و" || letterName.contains("و") -> listOf("waw", "waaw")
            norm == "ي" || letterName.contains("ي") -> listOf("yaa", "ya")
            else -> emptyList()
        }

        if (candidates.isEmpty()) {
            fallbackSpeech(letterName)
            return
        }

        playCandidateAtIndex(candidates, 0, letterName)
    }

    private fun playCandidateAtIndex(candidates: List<String>, index: Int, letterName: String) {
        if (index >= candidates.size) {
            fallbackSpeech(letterName)
            return
        }

        val phonetic = candidates[index]
        val url = "https://www.arabicreadingcourse.com/audio/isolated-letters/$phonetic.mp3"

        try {
            val mp = android.media.MediaPlayer()
            mediaPlayer = mp
            mp.setDataSource(url)
            mp.setOnPreparedListener {
                try {
                    it.start()
                } catch (e: Exception) {
                    android.util.Log.e("NasheetTTS", "Play failed, try next", e)
                    stopLetterPlayer()
                    playCandidateAtIndex(candidates, index + 1, letterName)
                }
            }
            mp.setOnErrorListener { _, _, _ ->
                stopLetterPlayer()
                playCandidateAtIndex(candidates, index + 1, letterName)
                true
            }
            mp.prepareAsync()
        } catch (e: Exception) {
            android.util.Log.e("NasheetTTS", "Error preparing $phonetic", e)
            stopLetterPlayer()
            playCandidateAtIndex(candidates, index + 1, letterName)
        }
    }

    private fun stopLetterPlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // Ignored
        } finally {
            mediaPlayer = null
        }
    }

    private fun fallbackSpeech(letterName: String) {
        speak("نطق الحرف كالتالي: $letterName")
    }

    fun setSpeechRate(rate: Float) {
        if (isReady && tts != null && !isShutdown) {
            try {
                tts?.setSpeechRate(rate)
            } catch (e: Exception) {
                android.util.Log.e("NasheetTTS", "Set speech rate error", e)
            }
        }
    }

    fun shutdown() {
        isShutdown = true
        isReady = false
        stopLetterPlayer()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            android.util.Log.e("NasheetTTS", "Shutdown error", e)
        } finally {
            tts = null
        }
    }
}

// Helper function to evaluate the user's letter trace using client-side AI heuristics
fun evaluateDrawingWithAI(
    tracePoints: List<Offset>,
    guidePoints: List<Offset>,
    canvasW: Int,
    canvasH: Int,
    onResult: (score: Int, success: Boolean, message: String) -> Unit
) {
    if (tracePoints.size < 6 || canvasW <= 0 || canvasH <= 0) {
        onResult(
            0,
            false,
            "تنبيه المعالج الذكي 🤖:\nمساحة الرسم والمسير فارغة تقريباً! قم بتتبع الخط الرمادي بالكامل قبل طلب الفحص."
        )
        return
    }

    // Convert tracePoints to normalized coordinates (0.0 .. 1.0)
    val userNorm = tracePoints.map { Offset(it.x / canvasW, it.y / canvasH) }

    // 1. Coverage / Average Minimum Distance to Guide Points (Chamfer distance)
    var totalMinDist = 0f
    guidePoints.forEach { gp ->
        var minDist = Float.MAX_VALUE
        userNorm.forEach { up ->
            val dx = up.x - gp.x
            val dy = up.y - gp.y
            val dist = kotlin.math.sqrt(dx*dx + dy*dy)
            if (dist < minDist) {
                minDist = dist
            }
        }
        totalMinDist += if (minDist == Float.MAX_VALUE) 1.0f else minDist
    }
    val avgMinDist = totalMinDist / guidePoints.size

    // 2. Out-of-bounds check (to identify non-guided scribbles outside boundaries)
    var outliersCount = 0
    userNorm.forEach { up ->
        var closeToAnyGP = false
        guidePoints.forEach { gp ->
            val dx = up.x - gp.x
            val dy = up.y - gp.y
            val dist = kotlin.math.sqrt(dx*dx + dy*dy)
            if (dist < 0.22f) { // threshold for being "near" the letter path
                closeToAnyGP = true
            }
        }
        if (!closeToAnyGP) {
            outliersCount++
        }
    }
    val outlierRatio = outliersCount.toFloat() / userNorm.size

    // 3. Sequential Progress Check (Did they follow the path in roughly order?)
    // Find the index of the user point closest to each guide point.
    val closestIndices = guidePoints.map { gp ->
        var bestIndex = -1
        var minDist = Float.MAX_VALUE
        userNorm.forEachIndexed { idx, up ->
            val dx = up.x - gp.x
            val dy = up.y - gp.y
            val dist = kotlin.math.sqrt(dx*dx + dy*dy)
            if (dist < minDist) {
                minDist = dist
                bestIndex = idx
            }
        }
        bestIndex
    }

    // Check if the closest user points generally follow a monotonic sequence (increasing index)
    var sequentialHits = 0
    for (i in 0 until closestIndices.size - 1) {
        if (closestIndices[i] != -1 && closestIndices[i+1] != -1 && closestIndices[i] < closestIndices[i+1]) {
            sequentialHits++
        }
    }
    val sequentialRatio = if (closestIndices.size > 1) {
        sequentialHits.toFloat() / (closestIndices.size - 1)
    } else {
        1.0f
    }

    // Compute final intelligence score
    // Max accuracy is achieved when avgMinDist is very low (< 0.08f)
    var baseScore = (1.0f - (avgMinDist - 0.02f).coerceIn(0f, 0.35f) / 0.35f) * 100f
    
    // Penalize outliers (scribbles outside the letter)
    val outlierPenalty = outlierRatio * 50f // deduct up to 50 points

    // Sequential bonus or penalty
    val sequentialBonus = (sequentialRatio - 0.5f) * 15f // range -7.5 to +7.5

    var finalScore = (baseScore - outlierPenalty + sequentialBonus).toInt()
    finalScore = finalScore.coerceIn(0, 100)

    val isSuccess = finalScore >= 90

    val message = if (isSuccess) {
        "ذكاء نشيط الاصطناعي 🧠🤖:\n مذهل ورائع جداً! دقة رسم وتتبع الحرف بلغت ${finalScore}% وهو ممتاز للغاية! خطك متناسق يطابق معايير الكتابة العربية الأصيلة. واصل العمل يا بطل! ⭐🏆"
    } else {
        when {
            outlierRatio > 0.45f -> "⚠️ خطأ في الرسم لتجاوز حدود الحرف! حاول مجدداً بتركيز وهدوء."
            avgMinDist > 0.2f -> "⚠️ خطأ في رسم الحرف لتشتت المسار! ابدأ من النقطة الرمادية وتتبع القلم."
            else -> "⚠️ خطأ في الرسم وتتبع المسار! الرجاء إعادة المحاولة بتمهل لترتفع دقتك."
        }
    }

    onResult(finalScore, isSuccess, message)
}

// 1. DIAGNOSTIC ASSESSMENT SCREEN
@Composable
fun DiagnosticAssessmentView(
    onComplete: (reading: Int, focus: Int, memory: Int, writing: Int, visualAuditory: Int) -> Unit,
    onBack: () -> Unit,
    tts: NasheetTTS?
) {
    var step by remember { mutableStateOf(1) } // 1 to 6
    
    // Scores
    var readingScore by remember { mutableStateOf(0) }
    var focusScore by remember { mutableStateOf(0) }
    var memoryScore by remember { mutableStateOf(0) }
    var writingScore by remember { mutableStateOf(0) }
    var visualAuditoryScore by remember { mutableStateOf(0) }

    val totalSteps = 5

    // Speak initial level instructions
    LaunchedEffect(step) {
        val instruction = when(step) {
            1 -> "المرحلة الأولى: اختبار القراءة. اختر الكلمة الصحيحة التي تعبر عن الصورة."
            2 -> "المرحلة الثانية: اختبار التركيز. انقر على البالون الذهبي ذو النبض السريع."
            3 -> "المرحلة الثالثة: اختبار الذاكرة. تذكر شكل الحيوانات التي ستختفي."
            4 -> "المرحلة الرابعة: اختبار صعوبات التعبير والكتابة. رتب الحروف لتكون كلمة قلم."
            5 -> "المرحلة الخامسة: التمييز البصري والسمعي. اختر الحرف المختلف بينها."
            6 -> "رائع ومبارك يا بطل! لقد أكملت التقييم بنجاح. إليك التقرير التشخيصي المفصل."
            else -> ""
        }
        tts?.speak(instruction)
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "التقييم التشخيصي التفاعلي 🧭",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = CreamBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (step <= 5) {
                // Linear Progress Indicators
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "المرحلة $step من $totalSteps", fontWeight = FontWeight.Bold, color = CharcoalText)
                        Text(text = "${(step * 100 / totalSteps)}%", fontWeight = FontWeight.Bold, color = CharcoalText)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { step.toFloat() / totalSteps },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = PastelMint,
                        trackColor = MutedSlate.copy(alpha = 0.2f)
                    )
                }

                // Fun Diagnostic Header Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = PastelBlue),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when(step) {
                                1 -> Icons.Rounded.Edit
                                2 -> Icons.Rounded.CheckCircle
                                3 -> Icons.Rounded.Face
                                4 -> Icons.Rounded.Edit
                                else -> Icons.Rounded.Search
                            },
                            contentDescription = "Diagnostic Icon",
                            tint = SoftTeal,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when(step) {
                                1 -> "اختبار القراءة والفهم البصري"
                                2 -> "اختبار ثبات ومستوى التركيز"
                                3 -> "اختبار الذاكرة قصيرة المدى"
                                4 -> "اختبار الإملاء وتكوين الرموز"
                                else -> "التمييز البصري والسمعي للمقاطع"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when(step) {
                                1 -> "انظر إلى الصورة واختر الكلمة المطابقة لها"
                                2 -> "ركز جيداً وانقر على الدائرة الذهبية المتغيرة!"
                                3 -> "أي من هذه الأشكال ظهر قبل قليل في الشاشة؟"
                                4 -> "ساعد صديقنا نشيط في ترتيب الحروف المبعثرة"
                                else -> "ابحث عن الحرف المختلف والفريد من بين الحروف المتشابهة"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = CharcoalText.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Diagnostic core interactive steps
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(2.dp, PastelPeach, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (step) {
                    1 -> Step1Reading { nextScore ->
                        readingScore = nextScore
                        step = 2
                    }
                    2 -> Step2Focus { nextScore ->
                        focusScore = nextScore
                        step = 3
                    }
                    3 -> Step3Memory { nextScore ->
                        memoryScore = nextScore
                        step = 4
                    }
                    4 -> Step4Writing { nextScore ->
                        writingScore = nextScore
                        step = 5
                    }
                    5 -> Step5VisualAuditory { nextScore ->
                        visualAuditoryScore = nextScore
                        step = 6
                    }
                    6 -> DiagnosticReportStep(
                        reading = readingScore,
                        focus = focusScore,
                        memory = memoryScore,
                        writing = writingScore,
                        visualAuditory = visualAuditoryScore,
                        onFinished = {
                            onComplete(readingScore, focusScore, memoryScore, writingScore, visualAuditoryScore)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DiagnosticReportStep(
    reading: Int,
    focus: Int,
    memory: Int,
    writing: Int,
    visualAuditory: Int,
    onFinished: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "🎉 تم الانتهاء! تقرير تقييم الطفل",
            color = SoftTeal,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Text(
            "إليك تقرير مبسط ومباشر لمستوى الطفل لولي الأمر والأستاذ للوقوف على كفاءاته الراهنة وتوجيهه بالشكل الصحيح:",
            color = CharcoalText,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        // Custom progress row
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportMetricBar("القراءة والربط البصري", reading, PastelMint, ReadingTint)
            ReportMetricBar("التركيز والتحكم الانتباهي", focus, FocusBg, FocusTint)
            ReportMetricBar("الذاكرة البصرية والسمعية", memory, MemoryBg, MemoryTint)
            ReportMetricBar("الإملاء وبناء الكلمات", writing, PastelPeach, WritingTint)
            ReportMetricBar("التمييز السمعي والبصري", visualAuditory, PastelPink, CoralWarm)
        }
        
        HorizontalDivider(color = SlateBorder)
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CreamBackground, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🧠 مخلص تحليل مهارات ومستوى الطفل المبدئي:", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 14.sp)
            
            val strengths = mutableListOf<String>()
            val challenges = mutableListOf<String>()
            
            if (reading >= 60) strengths.add("الربط البصري للكلمات") else challenges.add("بطء طفيف في ربط الحروف شكليًا")
            if (focus >= 60) strengths.add("تحكم انتباهي جيد") else challenges.add("تشتت سريع نسبيًا")
            if (memory >= 60) strengths.add("سرعة حفظ واستدعاء بصري") else challenges.add("يحتاج للتكرار والمراجعة المتواترة")
            if (writing >= 60) strengths.add("إملاء الحروف بشكل صحيح") else challenges.add("تأخر طفيف في ترتيب المقاطع الصوتية")
            if (visualAuditory >= 60) strengths.add("تمييز حسي وسمعي متكامل") else challenges.add("صعوبة التمييز السمعي لبعض الحروف المتطابقة")

            Text("🥇 نقاط القوة: " + strengths.joinToString("، "), style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))
            Text("⚠️ التحديات: " + if (challenges.isEmpty()) "لا توجد صعوبات ملحوظة!" else challenges.joinToString("، "), style = MaterialTheme.typography.bodySmall, color = Color(0xFFC62828))
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "💡 نصيحة الخبراء: بناءً على نتائج التقييم، سيفعل التطبيق تلقائيًا برامج التحفيز وتعديل سرعة المساعدة الصوتية والتباعد الصوتي.",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = SoftTeal
            )
        }
        
        Button(
            onClick = onFinished,
            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("دخول بيئة البطل الصغير والبدء باللعب 🧸", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ReportMetricBar(label: String, score: Int, bgColor: Color, barColor: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = CharcoalText)
            Text("$score%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = barColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score.toFloat() / 100 },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = bgColor
        )
    }
}

// STAGE 1: Reading Diagnostic Game
@Composable
private fun Step1Reading(onCompleted: (Int) -> Unit) {
    // Show Apple illustration and options: تفاحة، فراولة، أرنب، برتقال
    val options = listOf("تُفَاحَة 🍎", "بُرْتُقَال 🍊", "فَرَاوِلَة 🍓", "أَرْنَب 🐰")
    val correctIndex = 0

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🍎",
            fontSize = 72.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "ما هذا الشيء في الصورة؟",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = CharcoalText
        )

        options.forEachIndexed { index, opt ->
            Button(
                onClick = {
                    val score = if (index == correctIndex) 100 else 40
                    onCompleted(score)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when(index) {
                        0 -> PastelMint
                        1 -> PastelBlue
                        2 -> PastelPeach
                        else -> PastelPink
                    },
                    contentColor = CharcoalText
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(opt, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// STAGE 2: Focus Diagnostic Game
@Composable
private fun Step2Focus(onCompleted: (Int) -> Unit) {
    var clickCount by remember { mutableStateOf(0) }
    var hits by remember { mutableStateOf(0) }
    var targetActive by remember { mutableStateOf(true) }

    // Toggle target state to simulate movement
    LaunchedEffect(clickCount) {
        targetActive = true
        kotlinx.coroutines.delay(1000)
        targetActive = false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "انقر على الدائرة الذهبية فقط عندما تومض!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = CharcoalText,
            textAlign = TextAlign.Center
        )
        Text(
            text = "عدد النقرات: $clickCount من 3",
            style = MaterialTheme.typography.bodyMedium,
            color = CharcoalText
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(80.dp))
                .background(if (targetActive) PastelYellow else PastelBlue)
                .clickable {
                    if (targetActive) {
                        hits++
                    }
                    clickCount++
                    if (clickCount >= 3) {
                        val score = when (hits) {
                            3 -> 100
                            2 -> 80
                            1 -> 50
                            else -> 30
                        }
                        onCompleted(score)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Target",
                tint = if (targetActive) SoftTeal else Color.LightGray,
                modifier = Modifier.size(90.dp)
            )
        }
    }
}

// STAGE 3: Memory Diagnostic Game
@Composable
private fun Step3Memory(onCompleted: (Int) -> Unit) {
    var showItems by remember { mutableStateOf(true) }
    
    // Items to remember: Lion, Cat, Elephant
    val originalList = listOf("🦁 أسد", "🐱 قطة", "🐘 فيل")
    val selectionOptions = listOf("🐱 قطة", "🐶 كلب", "🦁 أسد", "🐻 دب", "🐘 فيل", "🐰 أرنب")

    val selectedAnswers = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        showItems = false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (showItems) "احفظ هذه الحيوانات بسرعة! ⏰" else "اختر الـ 3 حيوانات التي كانت في القائمة!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = CharcoalText,
            textAlign = TextAlign.Center
        )

        if (showItems) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                originalList.forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(PastelPeach, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                    }
                }
            }
            Text("ستختفي الشاشة بعد 3 ثواني", style = MaterialTheme.typography.bodySmall, color = Color.Red)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectionOptions.size) { index ->
                    val item = selectionOptions[index]
                    val isSelected = selectedAnswers.contains(item)
                    Button(
                        onClick = {
                            if (isSelected) {
                                selectedAnswers.remove(item)
                            } else {
                                if (selectedAnswers.size < 3) {
                                    selectedAnswers.add(item)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) PastelMint else Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CharcoalText.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Text(item, fontSize = 16.sp, color = CharcoalText, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Score is based on intersection with original list
                    val corrects = selectedAnswers.count { originalList.contains(it) }
                    val finalScore = when (corrects) {
                        3 -> 100
                        2 -> 70
                        1 -> 40
                        else -> 20
                    }
                    onCompleted(finalScore)
                },
                enabled = selectedAnswers.size == 3,
                colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تأكيد الاختيارات", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// STAGE 4: Writing Diagnostic Game
@Composable
private fun Step4Writing(onCompleted: (Int) -> Unit) {
    // Put "ق، ل، م" in correct order to write "قلم"
    val originalLetters = listOf("ق", "ل", "م")
    val randomizedLetters = remember { mutableStateListOf("ل", "م", "ق") }
    val currentSpelling = remember { mutableStateListOf<String>() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "رتب الحروف التالية لتكتب الكلمة المقابلة للصورة: ✏️",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = CharcoalText,
            textAlign = TextAlign.Center
        )
        Text(text = "🖊️ (قَلَم)", fontSize = 56.sp)

        // Selected letters box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(CreamBackground, RoundedCornerShape(12.dp))
                .border(1.dp, PastelPeach, RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentSpelling.isEmpty()) {
                Text("اضغط على الحروف في الأسفل بالترتيب الصحيح", color = CharcoalText.copy(alpha = 0.5f))
            } else {
                currentSpelling.toList().forEach { ch ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(48.dp)
                            .background(PastelMint, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(ch, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Options to click
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            randomizedLetters.toList().forEach { letter ->
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(PastelBlue, RoundedCornerShape(8.dp))
                        .clickable {
                            currentSpelling.add(letter)
                            randomizedLetters.remove(letter)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(letter, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    currentSpelling.clear()
                    randomizedLetters.clear()
                    randomizedLetters.addAll(listOf("ل", "م", "ق"))
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("إعادة المحاولة", color = CharcoalText)
            }

            Button(
                onClick = {
                    val formatted = currentSpelling.joinToString("")
                    val isCorrect = formatted == "قلم"
                    val score = if (isCorrect) 100 else 30
                    onCompleted(score)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                modifier = Modifier.weight(1f)
            ) {
                Text("تأكيد الكلمة", color = Color.White)
            }
        }
    }
}

// STAGE 5: Visual Auditory Diagnostic Game
@Composable
private fun Step5VisualAuditory(onCompleted: (Int) -> Unit) {
    // Find different letter: ب ب ب ت
    val letters = listOf("ب", "ب", "ب", "ت")
    val differentIndex = 3

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "اختر الحرف المختلف والفريد من بين الحروف التالية: 🤔",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = CharcoalText,
            textAlign = TextAlign.Center
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            letters.forEachIndexed { index, letter ->
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(PastelPeach, RoundedCornerShape(12.dp))
                        .clickable {
                            val score = if (index == differentIndex) 100 else 40
                            onCompleted(score)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(letter, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                }
            }
        }
    }
}


fun stripArabicDiacritics(text: String): String {
    return text.filter { it.code !in 0x064B..0x0652 }
}

// 2. PLAY GAME 1: READING / DYSLEXIA FOCUSED GAME
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DyslexiaReadingGame(
    schoolLevel: String,
    onCompleteGame: (score: Int, duration: Int, stars: Int) -> Unit,
    onBack: () -> Unit,
    tts: NasheetTTS?
) {
    val content = getContentForGrade(schoolLevel)
    val wordContent = remember { content.words.random() }
    val targetWordWithTashkeel = wordContent.first
    val targetWord = remember(targetWordWithTashkeel) { stripArabicDiacritics(targetWordWithTashkeel) }
    val targetEmoji = wordContent.second
    val wordLetters = targetWord.map { it.toString() }
    val randomPool = remember { mutableStateListOf(*(wordLetters + listOf("ا", "ل", "م")).shuffled().toTypedArray()) }
    val currentCombination = remember { mutableStateListOf<String>() }

    var gameScore by remember { mutableStateOf(0) }
    var gameCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tts?.speak("لعبة تهجئة الحروف. انقر على الحروف لبناء كلمة $targetWord بالترتيب الصحيح")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("أحجية القراءة وسحر الحروف 📚", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelMint)
            )
        },
        containerColor = CreamBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // High stimulation rewards bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PastelYellow, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("الموضوع: تهجئة الكلمات 📚", fontWeight = FontWeight.Bold, color = CharcoalText)
                Text("الجائزة المتوقعة: +10 نجوم ⭐", color = SoftTeal, fontWeight = FontWeight.Bold)
            }

            // Word Preview dys-friendly styledcard
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(targetEmoji, fontSize = 90.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "كَلِمة: ($targetWord)",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftTeal,
                        letterSpacing = 2.sp // Dyslexia-friendly wide spacing
                    )
                }
            }

            // Interaction Slate
            Card(
                colors = CardDefaults.cardColors(containerColor = PastelBlue.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("صندوق الكلمة المركبة", style = MaterialTheme.typography.titleMedium, color = CharcoalText)

                    // Built Combination
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        if (currentCombination.isEmpty()) {
                            Text(
                                "اضغط على الحروف في الأسفل بالترتيب لتكتب $targetWord",
                                color = CharcoalText.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            currentCombination.toList().forEach { ch ->
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(PastelMint, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        ch,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )
                                }
                            }
                        }
                    }

                    // Available letters
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        randomPool.toList().forEach { letter ->
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, SoftTeal, RoundedCornerShape(12.dp))
                                    .clickable {
                                        currentCombination.add(letter)
                                        randomPool.remove(letter)

                                        // Check spelling immediately
                                        val spelling = currentCombination.joinToString("")
                                        if (spelling == targetWord) {
                                            gameScore = 100
                                            gameCompleted = true
                                            tts?.speak("رائع وممتاز جداً! لقد ركبت كلمة $targetWord بشكل صحيح")
                                        } else if (currentCombination.size >= targetWord.length) {
                                            gameScore = 40
                                            gameCompleted = true
                                            tts?.speak("عمل رائع بمحاولة جيدة. يمكنك المحاولة مرة أخرى لاحقاً لتسجيل معدل كامل")
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    letter,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalText
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (gameCompleted) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PastelYellow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (gameScore == 100) "ممتاز! أحسنت يا بطل 🥇" else "لقد حاولت وأنجزت! ⭐",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onCompleteGame(gameScore, 60, if (gameScore == 100) 15 else 5)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
                        ) {
                            Text("الرئيسية وحفظ النجوم", color = Color.White)
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = {
                        // Reset
                        currentCombination.clear()
                        randomPool.clear()
                        randomPool.addAll((wordLetters + listOf("ا", "ل", "م")).shuffled())
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("مسح وإعادة المحاولة", color = CharcoalText)
                }
            }
        }
    }
}

// 3. PLAY GAME 2: FOCUS / ADHD MEMORY CHIPS
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdhdFocusGame(
    schoolLevel: String,
    onCompleteGame: (score: Int, duration: Int, stars: Int) -> Unit,
    onBack: () -> Unit,
    tts: NasheetTTS?
) {
    val content = getContentForGrade(schoolLevel)
    // ADHD focus balloon-pop game
    var score by rememberSaveable { mutableStateOf(0) }
    var streak by rememberSaveable { mutableStateOf(0) }
    var gameTimeOut by rememberSaveable { mutableStateOf(false) }
    
    // High-contrast, dyslexia and adhd active tiles
    var targetColor by rememberSaveable { mutableStateOf("قوي") } // "قوي" vs "مشتت"
    var attempts by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(attempts) {
        if (attempts >= content.maxAttempts) {
            gameTimeOut = true
            tts?.speak("رائع! لقد أكملت تمرين التركيز الذهبي.")
        }
    }

    LaunchedEffect(Unit) {
        tts?.speak("تمرين التركيز البصري. اضغط فقط على الزر ذو اللون الأخضر الهادئ")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ألوان البالونات المشتتة 🟢", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelPeach)
            )
        },
        containerColor = CreamBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Focus guidance text (speech/visual clue)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "الهدف: اضغط على الأخضر 🟢 وتجنب الأحمر 🔴",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "النقاط الحالية: $score  /  الرقم القياسي: $streak",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SoftTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Two big circles - active state generator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // RED CIRCLE
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(60.dp))
                        .background(Color(0xFFE57373))
                        .clickable {
                            streak = 0
                            attempts++
                            tts?.speak("احذر! هذا اللون الأحمر المشتت.")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔴 تجنبني", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // GREEN CIRCLE
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(60.dp))
                        .background(PastelMint)
                        .clickable {
                            score += 20
                            streak++
                            attempts++
                            tts?.speak("نعم! رائع جداً.")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🟢 اضغط هنا", color = CharcoalText, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (gameTimeOut) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PastelYellow)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "اكتمل التمرين بنجاح! 🧠",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = "معدل دقة تركيزك البصري: $score%",
                            style = MaterialTheme.typography.bodyLarge,
                            color = CharcoalText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onCompleteGame(score, 45, if (score >= 80) 12 else 6)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
                        ) {
                            Text("احفظ السجلات وارجع", color = Color.White)
                        }
                    }
                }
            } else {
                Text(
                    text = "تبقى ${5 - attempts} محاولات لتقييم التركيز الإدراكي",
                    color = CharcoalText.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 4. INTERACTIVE BOOKS WINDOW
data class Book(
    val title: String,
    val emoji: String,
    val pages: List<String>,
    val quizTitle: String,
    val options: List<String>,
    val correctIdx: Int,
    val successText: String
)

@Composable
fun InteractiveBooksDialog(
    onDismiss: () -> Unit,
    onCompleteBook: (bookName: String, score: Int, starsEarned: Int) -> Unit,
    tts: NasheetTTS?,
    schoolLevel: String
) {
    var activeMainTab by remember { mutableStateOf(1) } // Default to 1 (Workbook) per user interest

    // Original story library data
    val books = remember(schoolLevel) {
        when {
            (schoolLevel.contains("الأول") || schoolLevel.contains("أولى") || schoolLevel.contains("أول")) -> listOf(
                Book(
                    title = "أرنوب والحرف المفقود 🐰🔍",
                    emoji = "🐰",
                    pages = listOf(
                        "كان الأرنب الصغير أرنوب يبحث في الحديقة عن الحروف الجميلة ووجد حرف الألف لامعاً كالنجمة تحت شجرة الزيتون.",
                        "أمسك أرنوب بحرف الألف وصنع به كلمات مثل: أسد شجاع، وأناناس حلو المذاق، وسعد كونه نجح بنطق اللفظ بوضوح.",
                        "سؤال ذكي: ما هو الحرف الجميل الذي وجده الأرنب أرنوب تحت شجرة الزيتون؟"
                    ),
                    quizTitle = "سؤال ذكي: ما هو الحرف الجميل الذي وجده الأرنب أرنوب؟",
                    options = listOf("حرف الميم 🎒", "حرف الألف 🦁", "حرف الكاف ✏️"),
                    correctIdx = 1,
                    successText = "رائع جداً يا بطل! حرف الألف هو المكتوب السليم 🌟"
                ),
                Book(
                    title = "سمكة المياه الصافية 🐟💦",
                    emoji = "🐟",
                    pages = listOf(
                        "السمكة الصغيرة الجميلة سوسو تعيش بفرح ولعب في مياه بحيرة بجاية الزرقاء وتحب القفز بحيوية ولطافة.",
                        "ساعدت صديقتها السلحفاة حين تعثرت بين الصخور، فصارت البحيرة تنبض بالتعاون والحب والمحبة الكبيرة.",
                        "سؤال ذكي: أين تعيش السمكة سوسو الهادئة وسعيدة؟"
                    ),
                    quizTitle = "سؤال ذكي: أين تعيش السمكة سوسو الهادئة؟",
                    options = listOf("في مياه البحيرة 🌊", "فوق الأشجار العالية 🌳", "في رمال الصحراء 🏜️"),
                    correctIdx = 0,
                    successText = "مذهل وممتاز! السمكة تعيش في مياه البحيرة الهادئة 🐟"
                )
            )
            schoolLevel.contains("الثاني") || schoolLevel.contains("ثانية") || schoolLevel.contains("ثاني") -> listOf(
                Book(
                    title = "السلحفاة والنجوم السحرية ✨🐢",
                    emoji = "🐢",
                    pages = listOf(
                        "كان هناك سلحفاة صغيرة لطيفة تدعى سمسم تحب مراقبة النجوم اللامعة في السماء كل مساء متمنية أن تطير لتلمسها.",
                        "ذات يوم، ساعدها صديقها هدهد الطيب وحملها برفق عاليًا لترى النجوم قريبة، فشعرت بسعادة وحبٍ عظيمين.",
                        "سؤال ذكي: من الذي ساعد سلحفاة سمسم لتصل للنجوم في السماء؟"
                    ),
                    quizTitle = "سؤال ذكي: من الذي ساعد سلحفاة سمسم؟",
                    options = listOf("الأرنب السريع 🐰", "هدهد الطيب 🦜", "الأسد الغاضب 🦁"),
                    correctIdx = 1,
                    successText = "أحسنت التميز والفهم يا بطلنا! ربحت ثلاثين نجمة جديدة ومكافأة ممتازة 🎉"
                ),
                Book(
                    title = "هدهد وكنز المعرفة 🦜👑",
                    emoji = "🦜",
                    pages = listOf(
                        "طائر الهدهد الصغير ذكي جداً، يبحث دائماً في المكتبة القديمة عن قصص الحروف والكلمات السحرية المغذية للعقول.",
                        "وجد اليوم صفحة قديمة مرسوم عليها كنز المعرفة المشرق، ولا يستطيع فتحه إلا بمطابقة ترتيب الحروف بالشكل السليم.",
                        "سؤال ذكي: عمّ كان يبحث طائر الهدهد الصغير في المكتبة القديمة؟"
                    ),
                    quizTitle = "سؤال ذكي: عمّ كان يبحث طائر الهدهد الصغير؟",
                    options = listOf("قصص الحروف والكلمات 📚", "صناديق الذهب والأموال 💰", "برتقال وفواكه ملونة 🍎"),
                    correctIdx = 0,
                    successText = "أحسنت التميز والفهم يا بطلنا! ربحت ثلاثين نجمة جديدة ومكافأة ممتازة 🎉"
                )
            )
            schoolLevel.contains("الثالث") || schoolLevel.contains("ثالثة") || schoolLevel.contains("ثاطف") -> listOf(
                Book(
                    title = "الثعلب الطماع في واحة غرداية 🦊🌴",
                    emoji = "🦊",
                    pages = listOf(
                        "خرج الثعلب ماكر خلسة يبحث عن ثمار التين والزيتون اللذيذة في بساتين واحة غرداية الخضراء الساحرة.",
                        "لكنه وجد كيس تمر مفقود تركه بائع طيب، فنادى أصحابه ليتقاسموا التمر معاً وتعلم ألا يأخذ ما ليس له.",
                        "سؤال ذكي: ماذا تعلم الثعلب ماكر في الواحة بعد أن وجد الكيس؟"
                    ),
                    quizTitle = "سؤال ذكي: ماذا تعلم الثعلب ماكر في الواحة؟",
                    options = listOf("أن يأكل التين وحده 🍒", "ألا يأخذ ما ليس له ويشارك رفاقه 🤝", "أن يسرق الكيس ويسرع بالهرب 🦊"),
                    correctIdx = 1,
                    successText = "يا لك من بطل رائع! الأمانة والمشاركة هما الأخلاق الفاضلة الكوكبية ✨"
                ),
                Book(
                    title = "مغامرات الفنك فوق الرمال 🦊🏜️",
                    emoji = "🦊",
                    pages = listOf(
                        "الفنك الصغير فوفو يحب الجري والتسابق فوق الرمال الذهبية للتاصيلي في صحراء الجزائر، وهو يرتدي قبعة حمراء جميلة.",
                        "اليوم وجد خريطة قديمة تبين مغارة المعالم الوطنية الكبرى، فقرر الانطلاق في رحلة لحصد النجوم واستكشاف الآثار العريقة.",
                        "سؤال ذكي: ما هو التحدي الذي قرر الفنك فوفو خوضه بعد ايجاد الخريطة؟"
                    ),
                    quizTitle = "سؤال ذكي: ما هو التحدي الذي قرر الفنك فوفو خوضه؟",
                    options = listOf("السباق واللعب في الغابة 🌳", "الانطلاق في رحلة لاستكشاف الآثار 🇩🇿", "النوم والاسترخاء في الظل الكثيف 💤"),
                    correctIdx = 1,
                    successText = "مبارك الفوز! رحلتنا للآثار تزيدنا حكمة وعقلاً 👑"
                )
            )
            schoolLevel.contains("الرابع") || schoolLevel.contains("رابعة") || schoolLevel.contains("رابع") -> listOf(
                Book(
                    title = "أبطال الثورة والحرية 🎖️🇩🇿",
                    emoji = "🎖️",
                    pages = listOf(
                        "يحكي الجد لحفيده أحمد في بيوت حي القصبة العتيقة صدى نداء الحرية، وبطولة المجاهدين في جبال الأوراس قبل عقود.",
                        "قال الجد: بالصبر والعزم كتب الشجعان تاريخ بلادنا المشرق، وأورثونا وطناً حراً شامخاً بدمائهم الطاهرة ونبذ الاستعمار.",
                        "سؤال ذكي: عن أي مكان للأبطال والبطولة تحدث الجد وعن بطولاته؟"
                    ),
                    quizTitle = "سؤال ذكي: عن أي مكان للأبطال تحدث الجد؟",
                    options = listOf("جبال الأوراس الأزلية 🏔️", "شواطئ المحيط المتجمد ❄️", "جزيرة الكنز المفقودة البعيدة 🏴☠️"),
                    correctIdx = 0,
                    successText = "بوركت يا بطل! جبال الأوراس الشامخة منبع تاريخنا الأصيل."
                ),
                Book(
                    title = "رحلة نهر الشلف العذبة 🌊🛥️",
                    emoji = "🌊",
                    pages = listOf(
                        "ينساب نهر الشلف الطويل في بلادنا عابراً السهول والحقول الخصبة، يسقي شجر البرتقال والزيتون ويبعث الحياة في البساتين.",
                        "حين تعاون الفلاحون على تنظيف ضفافه ورعايتها، زادت المياه نقاء وعذوبة، وأثمرت الأرض حباً وسلاماً متبادلاً للجميع.",
                        "سؤال ذكي: ما الذي ساعد نهر الشلف على البقاء نقياً ومثمراً؟"
                    ),
                    quizTitle = "سؤال ذكي: ما الذي ساعد نهر الشلف على البقاء نقياً؟",
                    options = listOf("إهمال مجرى النهر ورمي النفايات ⚠️", "تعاون الفلاحين على تنظيف ضفافه 🧹", "سقوط الثلج فقط دون عمل ❄️"),
                    correctIdx = 1,
                    successText = "أحسنت جداً! العمل الجماعي ينقى ويحمي المحيط والبيئة الجميلة."
                )
            )
            else -> listOf(
                Book(
                    title = "مستقبل التكنولوجيا الأخضر 🛰️🌱",
                    emoji = "🛰️",
                    pages = listOf(
                        "يبحث المهندس الشاب أمين عن طرق لتشغيل مصانع الجزائر بالكامل باستخدام الطاقة الشمسية النظيفة القادمة من رمال وعمق الصحراء الكبرى.",
                        "باستخدام تقنيات التحليل الذكية والأقمار الاصطناعية، صمم مشروعاً يحافظ على هواء صحي وبيئة نقية ومستدامة للأجيال القادمة.",
                        "سؤال ذكي: ما هو مصدر الطاقة التي أراد أمين استخدامها لحماية البيئة؟"
                    ),
                    quizTitle = "سؤال ذكي: ما هو مصدر الطاقة التي أراد أمين استخدامها؟",
                    options = listOf("طاقة الفحم والنفط الملوث ⚠️", "الطاقة الشمسية النظيفة من الصحراء ☀️", "حرق الخشب والأوراق 🪵"),
                    correctIdx = 1,
                    successText = "مبهر ورائع! الطاقات البديلة النظيفة هي طريق المستقبل الواعد."
                ),
                Book(
                    title = "حكايات القصبة والآثار العريقة 🏺🏛️",
                    emoji = "🏛️",
                    pages = listOf(
                        "صعدت التلميذة ليلى أعلى تل حي القصبة العريق بالعاصمة، وتأملت الهندسة المعمارية الفاخرة للبيوت والمنارات القديمة الشامخة.",
                        "سجيلت في مفكرتها أن وراء كل حجر وباب خشبي منقوش قصة إبداع جزائري عتيق يعود لمئات السنين من الحضارة والصمود الأصيل.",
                        "سؤال ذكي: ما الذي أثار دهشة وإعجاب ليلى في حي القصبة العتيق؟"
                    ),
                    quizTitle = "سؤال ذكي: ما الذي أثار دهشة وإعجاب ليلى؟",
                    options = listOf("الهندسة المعمارية القديمة وصمود الحضارة 🏛️", "المحلات والألعاب الإلكترونية الحديثة 🏎️", "الرسم بالطباشير الملوث على الجدران 🧹"),
                    correctIdx = 0,
                    successText = "ممتاز جداً! التراث والحضارة وجه بلادنا الثقافي المشرق والراقي."
                )
            )
        }
    }

    var selectedBookIndex by remember { mutableStateOf(-1) }
    var pageNumber by remember { mutableStateOf(1) }
    var userQuizAnswer by remember { mutableStateOf(-1) }
    var quizSuccess by remember { mutableStateOf<Boolean?>(null) }

    // Worksheets states
    var selectedWorksheetIndex by remember { mutableStateOf(-1) }
    var worksheetTab by remember { mutableStateOf(0) }
    val tracePoints = remember { mutableStateListOf<Offset>() }
    var isTraceCompleted by remember { mutableStateOf(false) }

    val currentMissingWordState = remember { mutableStateListOf<Boolean>() }
    var isMissingLetterCompleted by remember { mutableStateOf(false) }

    val selectedCircles = remember { mutableStateListOf<Int>() }
    var isCircleChallengeCompleted by remember { mutableStateOf(false) }

    // New states for AI verification
    var isCheckingAI by remember { mutableStateOf(false) }
    var aiCheckResultText by remember { mutableStateOf<String?>(null) }
    var aiCheckScore by remember { mutableStateOf<Int?>(null) }
    var aiCheckSuccess by remember { mutableStateOf<Boolean?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    // Grid tracking for completed lessons
    val completedWorksheetIndexes = remember { mutableStateListOf<Int>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            if (selectedWorksheetIndex == -1 && selectedBookIndex == -1) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "كراس نشيط التفاعلي والقصص 📚🎨",
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { activeMainTab = 1 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeMainTab == 1) SoftTeal else Color(0xFFF1F5F9),
                                contentColor = if (activeMainTab == 1) Color.White else CharcoalText
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("✏️ كراس الحروف", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { activeMainTab = 0 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeMainTab == 0) SoftTeal else Color(0xFFF1F5F9),
                                contentColor = if (activeMainTab == 0) Color.White else CharcoalText
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("📖 القصص اللطيفة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                contentAlignment = Alignment.Center
            ) {
                if (activeMainTab == 1) {
                    if (selectedWorksheetIndex == -1) {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "خريطة دروس الحروف التفاعلية 🗺️✨\nاختر درساً لتبدأ التتبع وفحص الذكاء الاصطناعي وحصد الجوائز!",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            val rowCount = (worksheets.size + 1) / 2
                            for (r in 0 until rowCount) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    for (c in 0 until 2) {
                                        val index = r * 2 + c
                                        if (index < worksheets.size) {
                                            val ws = worksheets[index]
                                            val isCompleted = completedWorksheetIndexes.contains(index)
                                            
                                            val bgColors = listOf(
                                                Color(0xFFFFF5F5), // Soft Peach
                                                Color(0xFFF0F7FF), // Soft Blue
                                                Color(0xFFFFFDF0), // Soft Yellow
                                                Color(0xFFF3FEF7), // Soft Mint
                                                Color(0xFFFEF3FF)  // Soft Lavender
                                            )
                                            val cardColor = bgColors[index % bgColors.size]
                                            val borderStrokeColor = if (isCompleted) Color(0xFF4CAF50) else SlateBorder

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(115.dp)
                                                    .clip(RoundedCornerShape(18.dp))
                                                    .background(cardColor)
                                                    .border(
                                                        BorderStroke(
                                                            width = if (isCompleted) 2.2.dp else 1.dp, 
                                                            color = borderStrokeColor
                                                        ), 
                                                        RoundedCornerShape(18.dp)
                                                    )
                                                    .clickable {
                                                        selectedWorksheetIndex = index
                                                        tracePoints.clear()
                                                        isTraceCompleted = false
                                                        isMissingLetterCompleted = false
                                                        currentMissingWordState.clear()
                                                        repeat(ws.missingWords.size) { currentMissingWordState.add(false) }
                                                        selectedCircles.clear()
                                                        isCircleChallengeCompleted = false
                                                        worksheetTab = 0
                                                        
                                                        // Reset AI states
                                                        aiCheckResultText = null
                                                        aiCheckScore = null
                                                        aiCheckSuccess = null
                                                        isCheckingAI = false

                                                        tts?.speak("درس حرف ${ws.letterName}! لنكتب ونمرح معاً يا بطل!")
                                                    }
                                                    .padding(10.dp)
                                            ) {
                                                // Badge top right for lesson number
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .background(Color.White.copy(alpha = 0.82f), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "درس ${index + 1}",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = CharcoalText.copy(alpha = 0.75f)
                                                    )
                                                }

                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.SpaceBetween,
                                                    horizontalAlignment = Alignment.Start
                                                ) {
                                                    // Letter and Emoji
                                                    Column {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Text(
                                                                text = ws.letter,
                                                                fontSize = 28.sp,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = CharcoalText
                                                            )
                                                            Text(
                                                                text = ws.keyEmoji,
                                                                fontSize = 20.sp
                                                            )
                                                        }
                                                        Text(
                                                            text = "تعليم ${ws.letterName}",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = CharcoalText.copy(alpha = 0.8f)
                                                        )
                                                    }

                                                    // Status ribbon / badge
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFECEFF1))
                                                            .padding(horizontal = 6.dp, vertical = 3.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = if (isCompleted) "مكتمل ⭐🏆" else "ابدأ الدرس 🚀",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isCompleted) Color(0xFF2E7D32) else CharcoalText.copy(alpha = 0.7f)
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        val ws = worksheets[selectedWorksheetIndex]

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { selectedWorksheetIndex = -1 }) {
                                    Text("🔙 رجوع", color = CharcoalText, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                Text(
                                    text = "حرف ${ws.letterName} (${ws.letter})",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalText
                                )

                                Box(
                                    modifier = Modifier
                                        .background(PastelYellow, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("⭐ 30", fontSize = 10.sp, color = CharcoalText, fontWeight = FontWeight.Bold)
                                }
                            }

                            val pct = (if (isTraceCompleted) 33 else 0) + (if (isMissingLetterCompleted) 33 else 0) + (if (isCircleChallengeCompleted) 34 else 0)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("تقدّم ورقة العمل:", fontSize = 9.sp, color = CharcoalText.copy(alpha = 0.5f))
                                    Text("$pct%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SoftTeal)
                                }
                                LinearProgressIndicator(
                                    progress = pct / 100f,
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = SoftTeal,
                                    trackColor = Color(0xFFF1F5F9)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("✏️ تتبع واكتب", "🧩 الحرف الناقص", "🎯 حوّط الصور").forEachIndexed { tabIdx, label ->
                                    val active = worksheetTab == tabIdx
                                    val completed = when (tabIdx) {
                                        0 -> isTraceCompleted
                                        1 -> isMissingLetterCompleted
                                        else -> isCircleChallengeCompleted
                                    }

                                    Button(
                                        onClick = { worksheetTab = tabIdx },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (active) PastelPeach else if (completed) PastelMint else Color(0xFFF1F5F9),
                                            contentColor = CharcoalText
                                        ),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, if (active) SoftTeal else Color.Transparent)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            if (completed) Text("✅", fontSize = 8.sp)
                                        }
                                    }
                                }
                            }

                            Divider(color = SlateBorder, thickness = 1.dp)

                            LaunchedEffect(isTraceCompleted, isMissingLetterCompleted, isCircleChallengeCompleted, selectedWorksheetIndex) {
                                if (isTraceCompleted && isMissingLetterCompleted && isCircleChallengeCompleted && selectedWorksheetIndex != -1) {
                                    if (!completedWorksheetIndexes.contains(selectedWorksheetIndex)) {
                                        completedWorksheetIndexes.add(selectedWorksheetIndex)
                                    }
                                }
                            }

                            if (isTraceCompleted && isMissingLetterCompleted && isCircleChallengeCompleted) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(PastelMint.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                                        .border(2.dp, SoftTeal, RoundedCornerShape(14.dp))
                                        .padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🏆 ورقة عمل مكتملة بنجاح!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SoftTeal)
                                    Text("⭐ ⭐ ⭐", fontSize = 24.sp)
                                    Text(
                                        text = "مبارك يا بطل! أتممت أنشطة كراس حرف ${ws.letterName} بالكامل ونلت 30 نجمة!",
                                        textAlign = TextAlign.Center,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )

                                    Button(
                                        onClick = {
                                            onCompleteBook("كراس الحروف: ورقة عمل ${ws.letterName}", 100, 30)
                                            if (selectedWorksheetIndex + 1 < worksheets.size) {
                                                selectedWorksheetIndex++
                                                tracePoints.clear()
                                                isTraceCompleted = false
                                                isMissingLetterCompleted = false
                                                currentMissingWordState.clear()
                                                repeat(worksheets[selectedWorksheetIndex].missingWords.size) { currentMissingWordState.add(false) }
                                                selectedCircles.clear()
                                                isCircleChallengeCompleted = false
                                                worksheetTab = 0
                                                tts?.speak("دعنا نتمرن في بطاقة الحرف التالي: حرف ${worksheets[selectedWorksheetIndex].letterName}!")
                                            } else {
                                                selectedWorksheetIndex = -1
                                                tts?.speak("ممتاز جداً! لقد أكملت الكراس التفاعلي كلياً!")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = if (selectedWorksheetIndex + 1 < worksheets.size) "الحرف التالي ➡️" else "رجوع للرئيسية 🔙",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            } else {
                                when (worksheetTab) {
                                    0 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "قم برسم شكل حرف (${ws.letter}) بإصبعك متتبعاً الخط الرمادي:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalText,
                                                textAlign = TextAlign.Center
                                            )

                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Button(
                                                    onClick = { tts?.speakLetter(ws.letter, ws.letterName) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = PastelPeach),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                                ) {
                                                    Text("🔊 انطق الحرف", color = CharcoalText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Button(
                                                    onClick = { 
                                                        tracePoints.clear() 
                                                        aiCheckResultText = null
                                                        aiCheckScore = null
                                                        aiCheckSuccess = null
                                                        isCheckingAI = false
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = PastelPink),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                                ) {
                                                    Text("🔄 مسح الرسم", color = CharcoalText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            var canvasSizeW by remember { mutableStateOf(1) }
                                            var canvasSizeH by remember { mutableStateOf(1) }

                                            Box(
                                                modifier = Modifier
                                                    .size(190.dp)
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(Color(0xFFFFFDF9))
                                                    .border(2.5.dp, if (aiCheckSuccess == true) Color(0xFF4CAF50) else if (aiCheckSuccess == false) Color(0xFFF44336) else SlateBorder, RoundedCornerShape(20.dp))
                                                    .pointerInput(selectedWorksheetIndex) {
                                                        detectDragGestures(
                                                            onDragStart = { offset ->
                                                                if (!isTraceCompleted) {
                                                                    tracePoints.add(offset)
                                                                    aiCheckResultText = null
                                                                    aiCheckScore = null
                                                                    aiCheckSuccess = null
                                                                }
                                                            },
                                                            onDrag = { change, dragAmount ->
                                                                change.consume()
                                                                if (!isTraceCompleted) tracePoints.add(change.position)
                                                            }
                                                        )
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Canvas(modifier = Modifier.fillMaxSize()) {
                                                    val cw = size.width
                                                    val ch = size.height
                                                    canvasSizeW = cw.toInt()
                                                    canvasSizeH = ch.toInt()

                                                    val guide = ws.guidePoints.map { Offset(it.x * cw, it.y * ch) }
                                                    if (guide.isNotEmpty()) {
                                                        val path = androidx.compose.ui.graphics.Path()
                                                        path.moveTo(guide[0].x, guide[0].y)
                                                        for (i in 1 until guide.size) {
                                                            path.lineTo(guide[i].x, guide[i].y)
                                                        }
                                                        drawPath(
                                                            path = path,
                                                            color = Color.LightGray.copy(alpha = 0.6f),
                                                            style = Stroke(
                                                                width = 7f,
                                                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                                            )
                                                        )

                                                        guide.forEach { pt ->
                                                            drawCircle(color = PastelBlue, radius = 7f, center = pt)
                                                        }
                                                    }

                                                    ws.dotPoints.forEach { dp ->
                                                        val dotPt = Offset(dp.x * cw, dp.y * ch)
                                                        drawCircle(color = CharcoalText, radius = 9f, center = dotPt)
                                                    }

                                                    if (tracePoints.isNotEmpty()) {
                                                        val userPath = androidx.compose.ui.graphics.Path()
                                                        userPath.moveTo(tracePoints[0].x, tracePoints[0].y)
                                                        for (i in 1 until tracePoints.size) {
                                                            userPath.lineTo(tracePoints[i].x, tracePoints[i].y)
                                                        }
                                                        drawPath(
                                                            path = userPath,
                                                            color = if (aiCheckSuccess == true) Color(0xFF2E7D32) else if (aiCheckSuccess == false) Color(0xFFD32F2F) else SoftTeal.copy(alpha = 0.85f),
                                                            style = Stroke(
                                                                width = 12f,
                                                                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                                                join = androidx.compose.ui.graphics.StrokeJoin.Round
                                                            )
                                                        )
                                                    }
                                                }
                                                
                                                if (isCheckingAI) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(Color.White.copy(alpha = 0.85f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                            CircularProgressIndicator(color = SoftTeal, strokeWidth = 3.dp)
                                                            Text("جاري فحص الرسمة...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                                                        }
                                                    }
                                                }
                                            }

                                            if (!isTraceCompleted) {
                                                Button(
                                                    onClick = {
                                                        isCheckingAI = true
                                                        coroutineScope.launch {
                                                            delay(1200) // Beautiful analytical pause
                                                            evaluateDrawingWithAI(
                                                                tracePoints = tracePoints,
                                                                guidePoints = ws.guidePoints,
                                                                canvasW = canvasSizeW,
                                                                canvasH = canvasSizeH
                                                            ) { score, success, message ->
                                                                aiCheckScore = score
                                                                aiCheckSuccess = success
                                                                aiCheckResultText = message
                                                                isCheckingAI = false
                                                                if (success) {
                                                                    isTraceCompleted = true
                                                                }
                                                                if (success) tts?.speak("أحسنت كتابة وتتبع الحرف وبتقييم ذكي ممتاز!") else tts?.speak(message.substringAfter(":").trim())
                                                            }
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                                    enabled = !isCheckingAI && tracePoints.isNotEmpty()
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Text("🔍 تحقق", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }

                                            // Render AI diagnosis message if present
                                             (if (aiCheckSuccess != true) aiCheckResultText else null)?.let { feedbackMsg ->
                                                 val cardBg = if (aiCheckSuccess == true) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                                 val borderCol = if (aiCheckSuccess == true) Color(0xFF81C784) else Color(0xFFE57373)
                                                 val textCol = if (aiCheckSuccess == true) Color(0xFF1B5E20) else Color(0xFFB71C1C)

                                                 Column(
                                                     modifier = Modifier
                                                         .fillMaxWidth()
                                                         .background(cardBg, RoundedCornerShape(12.dp))
                                                         .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                                                         .padding(10.dp),
                                                     verticalArrangement = Arrangement.spacedBy(4.dp),
                                                     horizontalAlignment = Alignment.CenterHorizontally
                                                 ) {
                                                     Text(
                                                         text = feedbackMsg,
                                                         color = textCol,
                                                         fontSize = 11.sp,
                                                         fontWeight = FontWeight.Bold,
                                                         textAlign = TextAlign.Center
                                                     )
                                                 }
                                             }

                                             if (isTraceCompleted && aiCheckSuccess == true) {
                                                 Text("✅ أحسنت كتابة وتتبع الحرف وبتقييم ذكي فائق!", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                             }
                                        }
                                    }
                                    1 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "اختر شكل الحرف الصحيح ليكتمل الفراغ وبناء اللفظ:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalText,
                                                textAlign = TextAlign.Center
                                            )

                                            ws.missingWords.forEachIndexed { wordIdx, wordCh ->
                                                val solved = currentMissingWordState.getOrNull(wordIdx) == true

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(if (solved) PastelMint.copy(alpha = 0.5f) else Color(0xFFF9F9F9), RoundedCornerShape(10.dp))
                                                        .border(1.dp, if (solved) PastelMint else SlateBorder, RoundedCornerShape(10.dp))
                                                        .padding(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier.size(32.dp).background(PastelYellow, CircleShape),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(wordCh.emoji, fontSize = 18.sp)
                                                        }

                                                        Text(
                                                            text = if (solved) wordCh.correctAnswer else wordCh.wordWithBlank,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (solved) SoftTeal else CharcoalText
                                                        )
                                                    }

                                                    if (solved) {
                                                        Text("🌸 مكتملة! ✅", color = SoftTeal, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                                    } else {
                                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                            wordCh.options.forEachIndexed { optIdx, optForm ->
                                                                Box(
                                                                    modifier = Modifier
                                                                        .background(Color.White, RoundedCornerShape(6.dp))
                                                                        .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                                                                        .clickable {
                                                                            if (optIdx == wordCh.correctIdx) {
                                                                                currentMissingWordState[wordIdx] = true
                                                                                tts?.speak("ممتاز! نطق الكلمة: ${wordCh.correctAnswer}")
                                                                                if (currentMissingWordState.all { it }) {
                                                                                    isMissingLetterCompleted = true
                                                                                    tts?.speak("رائع جداً! مبروك، تم تجميع كل الكلمات المفقودة لحرف ${ws.letterName} بنجاح!")
                                                                                }
                                                                            } else {
                                                                                tts?.speak("شكل الحرف غير صحيح هنا، حاول مجدداً يا شاطر.")
                                                                            }
                                                                        }
                                                                        .padding(horizontal = 6.dp, vertical = 4.dp),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Text(optForm, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    2 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "المس لـ (تحويط) كل الصور التي تبدأ بحرف (${ws.letter})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalText,
                                                textAlign = TextAlign.Center
                                            )

                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                for (r in 0 until 3) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        for (c in 0 until 2) {
                                                            val index = r * 2 + c
                                                            if (index < ws.circlesChallenge.size) {
                                                                val item = ws.circlesChallenge[index]
                                                                val isSelected = selectedCircles.contains(index)

                                                                Box(
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .clip(RoundedCornerShape(10.dp))
                                                                        .background(if (isSelected) PastelMint else Color(0xFFF9F9F9))
                                                                        .border(
                                                                            border = BorderStroke(
                                                                                width = if (isSelected) 2.dp else 1.dp,
                                                                                color = if (isSelected) SoftTeal else SlateBorder
                                                                            ),
                                                                            shape = RoundedCornerShape(10.dp)
                                                                        )
                                                                        .clickable {
                                                                            if (item.startsWith) {
                                                                                if (!selectedCircles.contains(index)) {
                                                                                    selectedCircles.add(index)
                                                                                    tts?.speak("نعم بطل! ${item.name} تبدأ بحرف ${ws.letterName}")

                                                                                    val targetCount = ws.circlesChallenge.filter { it.startsWith }.size
                                                                                    if (selectedCircles.size == targetCount) {
                                                                                        isCircleChallengeCompleted = true
                                                                                        tts?.speak("ممتاز يا عبقري! عثرت على جميع الصور التي تبدأ بحرف ${ws.letterName} وحوّطتها بالأخضر الجميل!")
                                                                                    }
                                                                                } else {
                                                                                    selectedCircles.remove(index)
                                                                                }
                                                                            } else {
                                                                                tts?.speak("كلا! ${item.name} لا تبدأ بحرف ${ws.letterName}، ركز يا بطل!")
                                                                            }
                                                                        }
                                                                        .padding(6.dp),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                        Text(item.emoji, fontSize = 24.sp)
                                                                        Text(item.name, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (selectedBookIndex == -1) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "مكتبة القصص الممتعة المتناسقة مع عمرك الذكي! ⭐",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CharcoalText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            books.forEachIndexed { index, b ->
                                Button(
                                    onClick = {
                                        selectedBookIndex = index
                                        pageNumber = 1
                                        quizSuccess = null
                                        userQuizAnswer = -1
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (index == 0) PastelMint else PastelBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) {
                                    Text("📖 قصة: ${b.title}", color = CharcoalText, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        val currentBook = books.getOrNull(selectedBookIndex)
                        val bookTitle = currentBook?.title ?: ""
                        val pageText = currentBook?.pages?.getOrNull(pageNumber - 1) ?: ""

                        LaunchedEffect(selectedBookIndex, pageNumber) {
                            tts?.speak(pageText)
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "$bookTitle (صفحة $pageNumber)",
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(PastelYellow, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(currentBook?.emoji ?: "📖", fontSize = 36.sp)
                            }

                            Text(
                                text = pageText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = { tts?.speak(pageText) },
                                colors = ButtonDefaults.buttonColors(containerColor = PastelPeach),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🔊 استمع مجدداً للقصة", color = CharcoalText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            if (pageNumber == 3 && currentBook != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                currentBook.options.forEachIndexed { idx, opt ->
                                    Button(
                                        onClick = {
                                            userQuizAnswer = idx
                                            quizSuccess = (idx == currentBook.correctIdx)
                                            if (idx == currentBook.correctIdx) {
                                                tts?.speak(currentBook.successText)
                                            } else {
                                                tts?.speak("حاول مجدداً لتفهم القصة يا بطل.")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (userQuizAnswer == idx) {
                                                if (idx == currentBook.correctIdx) PastelMint else PastelPink
                                            } else Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                                    ) {
                                        Text(opt, color = CharcoalText, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedBookIndex != -1 && activeMainTab == 0) {
                val currentBook = books.getOrNull(selectedBookIndex)
                val bookTitle = currentBook?.title ?: ""

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (pageNumber > 1) {
                        TextButton(onClick = { pageNumber--; userQuizAnswer = -1; quizSuccess = null }) {
                            Text("السابق ↩️", color = CharcoalText, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (pageNumber < 3) {
                        Button(
                            onClick = { pageNumber++; userQuizAnswer = -1; quizSuccess = null },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
                        ) {
                            Text("التالي ⏩", color = Color.White, fontSize = 11.sp)
                        }
                    } else if (quizSuccess == true) {
                        Button(
                            onClick = {
                                onCompleteBook(bookTitle, 100, 30)
                                selectedBookIndex = -1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
                        ) {
                            Text("ربح النجوم ونهاية القصة ⭐🏆", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (selectedWorksheetIndex != -1) {
                        selectedWorksheetIndex = -1
                    } else if (selectedBookIndex != -1) {
                        selectedBookIndex = -1
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(
                    text = if (selectedWorksheetIndex != -1 || selectedBookIndex != -1) "رجوع للقائمة" else "إغلاق كراس التعلم x",
                    color = CharcoalText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

// 5. WORD & SENTENCE BUILDER GAME
@Composable
fun SentenceBuilderDialog(
    onDismiss: () -> Unit,
    onComplete: (activityName: String, score: Int, starsEarned: Int) -> Unit,
    tts: NasheetTTS?,
    schoolLevel: String
) {
    val targetSentence = remember(schoolLevel) {
        val gradeContent = getContentForGrade(schoolLevel)
        gradeContent.sentences.randomOrNull() ?: "أَنَا أُحِبُّ الْقِرَاءَةَ"
    }

    val originalWords = remember(targetSentence) {
        targetSentence.split(" ").filter { it.isNotBlank() }
    }

    val scrambledWords = remember(targetSentence) {
        mutableStateListOf<String>().apply {
            addAll(originalWords.shuffled())
        }
    }

    val currentCombination = remember { mutableStateListOf<String>() }
    var gameWon by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(targetSentence) {
        tts?.speak("لعبة تركيب الجمل الكرتونية. رتب الكلمات بالترتيب الصحيح لتصنع جملة $targetSentence")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "بناء وتركيب الكلمات والترجمات 🏗️🌟",
                fontWeight = FontWeight.Bold,
                color = CharcoalText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "اضغط على بطاقات الكلمات المبعثرة بالترتيب الصحيح لتكوين جملة كاملة ومفيدة:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CharcoalText,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📖", fontSize = 64.sp)
                }

                // Compiled Sentence Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(CreamBackground, RoundedCornerShape(16.dp))
                        .border(1.5.dp, PastelPeach, RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentCombination.isEmpty()) {
                        Text("رتب في هذا المستطيل 🧩", color = CharcoalText.copy(alpha = 0.5f))
                    } else {
                        currentCombination.forEach { word ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .background(PastelMint, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(word, fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Scrambled items to select from
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    scrambledWords.toList().forEach { word ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PastelBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable {
                                    currentCombination.add(word)
                                    scrambledWords.remove(word)
                                    tts?.speak(word)
                                }
                        ) {
                            Text(
                                text = word,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                if (gameWon == false) {
                    Text("الترتيب خاطئ قليلاً! حاول مجدداً يا بطل.", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                } else if (gameWon == true) {
                    Text("ممتاز! مبارك تركيبك لجملة: $targetSentence 💖🎉", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }

                // Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            currentCombination.clear()
                            scrambledWords.clear()
                            scrambledWords.addAll(originalWords.shuffled())
                            gameWon = null
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تفريغ المقاطع ↩️", color = CharcoalText)
                    }

                    Button(
                        onClick = {
                            val outcome = currentCombination.toList()
                            val correct = outcome == originalWords
                            gameWon = correct
                            if (correct) {
                                tts?.speak("$targetSentence! رائع جدا يا بطل الحروف.")
                            } else {
                                tts?.speak("الترتيب الحالي بحاجة لمراجعة")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                        shape = RoundedCornerShape(12.dp),
                        enabled = currentCombination.size == originalWords.size,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تحقق الترتيب ✅", color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            if (gameWon == true) {
                Button(
                    onClick = {
                        onComplete("مغامرة تركيب جملة قراءة", 100, 30)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("استلام 30 نجمة ⭐", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = CharcoalText)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

// 6. AUDIO-VISUAL ACTIVITIES
@Composable
fun AudioVisualDialog(
    onDismiss: () -> Unit,
    onComplete: (activityName: String, score: Int, starsEarned: Int) -> Unit,
    tts: NasheetTTS?,
    schoolLevel: String
) {
    var playedSecret by remember { mutableStateOf(false) }
    var selectedEmoji by remember { mutableStateOf("") }
    var resultSuccess by remember { mutableStateOf<Boolean?>(null) }

    val activeChallenge = remember(schoolLevel) {
        when {
            schoolLevel.contains("الأول") || schoolLevel.contains("أولى") || schoolLevel.contains("أول") -> Triple("هذا هو مواء القطة المخفي ميو ميو، القطة الكسلانة والجميلة. 🐱", "🐱", listOf("🦁 الأسد الشجاع", "🐱 القطة الكسلانة", "🐦 العصفور المغرد"))
            schoolLevel.contains("الثاني") || schoolLevel.contains("ثانية") || schoolLevel.contains("ثاني") -> Triple("هذا زئير الأسد القوي الشجاع، ملك الغابة! 🦁", "🦁", listOf("🦁 الأسد الشجاع", "🐱 القطة الكسلانة", "🐦 العصفور المغرد"))
            schoolLevel.contains("الثالث") || schoolLevel.contains("ثالثة") || schoolLevel.contains("ثالث") -> Triple("هذا صوت العصفور الصغير المغرد فوق شجرة الزيتون، يسبح ربه بصوت زقزقة لطيفة. 🐦", "🐦", listOf("🦁 الأسد الشجاع", "🐱 القطة الكسلانة", "🐦 العصفور المغرد"))
            schoolLevel.contains("الرابع") || schoolLevel.contains("رابعة") || schoolLevel.contains("رابع") -> Triple("هذا صوت زئير البحر وهدير الأمواج الهائن في شواطئ جيجل وبجاية الشامخة. 🌊", "🌊", listOf("🌊 أمواج البحر", "🏔️ جبال الأوراس", "🏜️ الصحراء الكبرى"))
            else -> Triple("هذا صوت عواصف الرمل وهبوب الرياح العاتية في واحات الصحراء الجزائرية الشاسعة. 💨", "💨", listOf("🌊 أمواج البحر", "🏔️ جبال الأوراس", "🏜️ الصحراء الكبرى"))
        }
    }

    val soundText = activeChallenge.first
    val targetTag = activeChallenge.second
    val options = activeChallenge.third

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "أنشطة الصوت والتمييز السمعي البصري 🔊🎨",
                fontWeight = FontWeight.Bold,
                color = CharcoalText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "اضغط أولاً لسماع الصوت المخفي بصوت واضح، ثم طابق الصوت مع بطاقة الكائن الصحيحة:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CharcoalText,
                    textAlign = TextAlign.Center
                )

                // Speaker Action
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(if (playedSecret) PastelMint else PastelPeach)
                        .clickable {
                            tts?.speak(soundText)
                            playedSecret = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playedSecret) Icons.Rounded.Check else Icons.Rounded.PlayArrow,
                        contentDescription = "Play sound",
                        tint = CharcoalText,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Text(
                    if (playedSecret) "🔊 الصوت الآن مسموع! اختر الإجابة في الأسفل:" else "اضغط هنا لتفعيل الصوت الخفي 🔊",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = SoftTeal
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Options
                options.forEach { opt ->
                    val isCorrect = opt.contains(targetTag)
                    Button(
                        onClick = {
                            if (!playedSecret) {
                                tts?.speak("يرجى الضغط على زر مشغل الصوت الأعلى أولاً بسماع أحجية الصوت الممتعة!")
                                return@Button
                            }
                            selectedEmoji = opt
                            resultSuccess = isCorrect
                            if (isCorrect) {
                                tts?.speak("نعم! صحيح تمامًا، أحسنت الإجابة المتميزة.")
                            } else {
                                tts?.speak("لا يا بطل، حاول من جديد لتجد الصوت وعمّا يعبر.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedEmoji == opt) {
                                if (isCorrect) PastelMint else PastelPink
                            } else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    ) {
                        Text(opt, color = CharcoalText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                if (resultSuccess == false) {
                    Text("الإجابة خاطئة! استمع ثانية للصوت المخفي وجرب.", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                } else if (resultSuccess == true) {
                    Text("يا لك من طفل خارق الذكاء! تعرفت على الصوت فوراً 👑⭐", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            if (resultSuccess == true) {
                Button(
                    onClick = {
                        onComplete("أحجية أصوات الكائنات والظواهر", 100, 30)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("استلام 30 نجمة ⭐🏆", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = CharcoalText)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

// 7. REWARDS SHOP COMPOSABLE
@Composable
fun RewardsShopDialog(
    stars: Int,
    unlockedBadges: List<String>,
    onDismiss: () -> Unit,
    onRedeemBadge: (badgeName: String, cost: Int) -> Unit,
    tts: NasheetTTS?
) {
    val prizeList = listOf(
        Pair("تاج الذكاء العبقري وعلم بلادي 👑🇩🇿", 30),
        Pair("وسام الفخر والشهاب الثوري 🎖️", 45),
        Pair("وشاح المعرفة لبلاد الجزائر الشامخة 🧣✨", 60),
        Pair("حزام الفنك الرائد والشجاع الفائق 🦊🥇", 80),
        Pair("درع جرجرة لحماية التراث الوطني 🛡️⛰️", 100)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "متجر المكافآت والأوسمة البطولية 🏆🌟",
                fontWeight = FontWeight.Bold,
                color = CharcoalText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Stars summary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PastelYellow, RoundedCornerShape(16.dp))
                        .border(1.dp, AmberBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐ حصيلتك الحالية:", fontWeight = FontWeight.Bold, color = CharcoalText)
                        Text("$stars نجمة ذهبية لافتة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AmberStarColor)
                    }
                }

                Text(
                    "استبدل نجومك البراقة التي كسبتها بجدٍ لفتح أوسمة فخرية جديدة تُعرض في ملفك الشخصي وتحفزك دائماً:",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalText,
                    textAlign = TextAlign.Center
                )

                prizeList.forEach { (prize, cost) ->
                    val isUnlocked = unlockedBadges.contains(prize)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isUnlocked) CreamBackground else Color.White, RoundedCornerShape(16.dp))
                            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(prize, fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 14.sp)
                            Text("التكلفة: $cost لؤلؤة/نجمة ⭐", style = MaterialTheme.typography.bodySmall, color = CharcoalText.copy(alpha = 0.6f))
                        }

                        if (isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .background(PastelMint, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("مفتوح 🔓", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 11.sp)
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (stars >= cost) {
                                        onRedeemBadge(prize, cost)
                                        tts?.speak("رائع و مذهل! لقد استبدلت نجومك بـ $prize الجديد اللامع لك.")
                                    } else {
                                        tts?.speak("عفواً يا بطلي، أنت بحاجة إلى المزيد من النجوم الذهبية لفتح هذا الوسام المرموق!")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                enabled = stars >= cost,
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("استبدال ⭐", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("رجوع للغرفة", color = CharcoalText, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

// 7. MEMORY CHALLENGE DIALOG
@Composable
fun MemoryGameDialog(
    onDismiss: () -> Unit,
    onComplete: (activityName: String, score: Int, starsEarned: Int) -> Unit,
    tts: NasheetTTS?,
    schoolLevel: String
) {
    var gameId by rememberSaveable { mutableStateOf(0) }
    
    val symbols = remember(gameId, schoolLevel) {
        when {
            schoolLevel.contains("الأول") || schoolLevel.contains("أولى") || schoolLevel.contains("أول") -> listOf("🍎", "🍌", "🧸", "🐱", "🍎", "🍌", "🧸", "🐱").shuffled()
            schoolLevel.contains("الثاني") || schoolLevel.contains("ثانية") || schoolLevel.contains("ثاني") -> listOf("🚗", "✈️", "🦁", "🐰", "🚗", "✈️", "🦁", "🐰").shuffled()
            schoolLevel.contains("الثالث") || schoolLevel.contains("ثالثة") || schoolLevel.contains("ثالث") -> listOf("☀️", "🌙", "☁️", "🌲", "☀️", "🌙", "☁️", "🌲").shuffled()
            schoolLevel.contains("الرابع") || schoolLevel.contains("رابعة") || schoolLevel.contains("رابع") -> listOf("🎒", "✏️", "📚", "📐", "🎒", "✏️", "📚", "📐").shuffled()
            else -> listOf("🛰️", "🧪", "🪐", "💡", "🛰️", "🧪", "🪐", "💡").shuffled()
        }
    }
    
    var revealedIndices by remember { mutableStateOf(setOf<Int>()) }
    var matchedIndices by remember { mutableStateOf(setOf<Int>()) }
    var firstSelectionIndex by remember { mutableStateOf<Int?>(null) }
    var isWin by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(gameId, schoolLevel) {
        revealedIndices = emptySet()
        matchedIndices = emptySet()
        firstSelectionIndex = null
        isWin = false
        val helperText = when {
            schoolLevel.contains("الأول") || schoolLevel.contains("أولى") || schoolLevel.contains("أول") -> "تمرين الذاكرة البصرية السريعة. اعثر على الفاكهة والألعاب المتطابقة وذكّرني بمكانها!"
            schoolLevel.contains("الثاني") || schoolLevel.contains("ثانية") || schoolLevel.contains("ثاني") -> "اعثر على الكائنات والحيوانات المتطابقة لتنشيط ذاكرتك البصرية يا بطل!"
            schoolLevel.contains("الثالث") || schoolLevel.contains("ثالثة") || schoolLevel.contains("ثالث") -> "تحدي الذاكرة لعناصر الطبيعة الخضراء والجميلة!"
            schoolLevel.contains("الرابع") || schoolLevel.contains("رابعة") || schoolLevel.contains("رابع") -> "اختبار الذاكرة للكتب المدرسية وأدوات الدراسة الهندسية!"
            else -> "تحدي الذاكرة البصرية الفائقة لرموز الفضاء والعلوم المشرقة!"
        }
        tts?.speak(helperText)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "أحجية الذاكرة البصرية الفائقة 🧠✨",
                fontWeight = FontWeight.Bold,
                color = CharcoalText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val promptText = when {
                    schoolLevel.contains("الأول") || schoolLevel.contains("أولى") || schoolLevel.contains("أول") -> "اقلب البطاقات لتعثر على كل زوجين متشابهين من الفواكه والألعاب اللذيذة:"
                    schoolLevel.contains("الثاني") || schoolLevel.contains("ثانية") || schoolLevel.contains("ثاني") -> "اقلب البطاقات لتعثر على الحيوانات والمركبات المتطابقة:"
                    schoolLevel.contains("الثالث") || schoolLevel.contains("ثالثة") || schoolLevel.contains("ثالث") -> "طابق بين عناصر الطبيعة الساحرة الهادئة:"
                    schoolLevel.contains("الرابع") || schoolLevel.contains("رابعة") || schoolLevel.contains("رابع") -> "طابق أدواتك الدراسية وحقيبتك الجميلة:"
                    else -> "تحدي رموز العلوم والتكنولوجيا والأقمار الصناعية الجزائرية:"
                }

                Text(
                    promptText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CharcoalText,
                    textAlign = TextAlign.Center
                )

                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(symbols.size) { index ->
                        val isRevealed = index in revealedIndices || index in matchedIndices
                        
                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable(enabled = !isRevealed && !isWin) {
                                    if (firstSelectionIndex == null) {
                                        firstSelectionIndex = index
                                        revealedIndices = revealedIndices + index
                                    } else {
                                        val firstIndex = firstSelectionIndex!!
                                        if (firstIndex != index) {
                                            revealedIndices = revealedIndices + index
                                            if (symbols[firstIndex] == symbols[index]) {
                                                matchedIndices = matchedIndices + firstIndex + index
                                                firstSelectionIndex = null
                                                revealedIndices = emptySet()
                                                if (matchedIndices.size == symbols.size) {
                                                    isWin = true
                                                    tts?.speak("رائع! ذاكرة حديدية مذهلة، لقد وجدت كل التطابقات!")
                                                } else {
                                                    tts?.speak("رائع! زوج متطابق 🌟")
                                                }
                                            } else {
                                                firstSelectionIndex = null
                                            }
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (index in matchedIndices) PastelMint else if (index in revealedIndices) Color.White else PastelBlue
                            ),
                            border = BorderStroke(1.dp, SlateBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                if (isRevealed) {
                                    Text(symbols[index], fontSize = 32.sp)
                                } else {
                                    Text("❓", fontSize = 24.sp, color = Color.White.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }

                if (!isWin && firstSelectionIndex == null && revealedIndices.isNotEmpty()) {
                    TextButton(onClick = { revealedIndices = emptySet() }) {
                        Text("إخفاء البطاقات غير المتطابقة", color = CoralWarm)
                    }
                }

                if (isWin) {
                    Text(
                        "مبروك! لقد انتصرت بذكائك الخارق 🎉",
                        fontWeight = FontWeight.Black,
                        color = SoftTeal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            if (isWin) {
                Button(
                    onClick = {
                        onComplete("تحدي الذاكرة البصرية للهواتف", 100, 40)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
                ) {
                    Text("استلام المكافأة ⭐", color = Color.White)
                }
            } else {
                Button(onClick = { gameId++ }) { Text("إعادة اللعبة 🔄") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = CharcoalText)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

// ==========================================
// 8. SMART PHONICS NOTEBOOK & SPEECH WRITER
// ==========================================
@Composable
fun SmartPhonicsNotebookDialog(
    onDismiss: () -> Unit,
    onComplete: (activityName: String, score: Int, starsEarned: Int) -> Unit,
    tts: NasheetTTS?,
    schoolLevel: String
) {
    var textInput by remember { mutableStateOf("") }
    var speechRate by remember { mutableStateOf(0.7f) } // Slow rate for phonetic articulation
    
    val savedWords = remember(schoolLevel) {
        val list = when {
            schoolLevel.contains("الأول") || schoolLevel.contains("أولى") || schoolLevel.contains("أول") -> listOf("دَرَسَ", "كَتَبَ", "رَسَمَ", "أَكَلَ", "أَرْنَبْ")
            schoolLevel.contains("الثاني") || schoolLevel.contains("ثانية") || schoolLevel.contains("ثاني") -> listOf("نَشِيطٌ", "بَطَلٌ", "سَعِيدٌ", "شُجَاعٌ", "لَطِيفٌ")
            schoolLevel.contains("الثالث") || schoolLevel.contains("ثالثة") || schoolLevel.contains("ثالث") -> listOf("أَشْجَارٌ", "سَمَاءٌ", "بُحَيْرَةٌ", "طَبِيعَةٌ", "شَمْسٌ")
            schoolLevel.contains("الرابع") || schoolLevel.contains("رابعة") || schoolLevel.contains("رابع") -> listOf("الْجَزَائِرُ", "الْقَصْبَةُ", "الأَوْرَاسُ", "شُهَدَاءُ", "تَارِيخٌ")
            else -> listOf("مُتَفَوِّقٌ", "إِبْدَاعٌ", "مُسْتَقْبَلٌ", "تَطْوِيرٌ", "عِلْمٌ")
        }
        list
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "دفتر الصوتيات والكاتب الذكي 🎙️✍️",
                fontWeight = FontWeight.Bold,
                color = CharcoalText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "اكتب أي كلمة باللغة العربية أو اختر كلمة من الكلمات المقترحة لتتعلم طريقة نطقها الصحيحة وببطء لتقوية مخارج الحروف المترابطة بالتوجيه السمعي البصري:",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalText,
                    textAlign = TextAlign.Center
                )

                // Words suggestions
                Text(
                    "💡 كلمات مقترحة لمستواك الدراسي:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F766E),
                    modifier = Modifier.align(Alignment.Start)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    savedWords.forEach { word ->
                        val isMatched = textInput == word
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isMatched) Color(0xFF0D9488) else Color(0xFFF3F4F6))
                                .border(1.dp, if (isMatched) Color(0xFF0D9488) else Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                                .clickable {
                                    textInput = word
                                    tts?.speak("اخترت كلمة $word")
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(word, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isMatched) Color.White else CharcoalText)
                        }
                    }
                }

                // Custom word input field
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("اكتب كلمتك هنا...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0F766E),
                        unfocusedBorderColor = Color(0xFFD1D5DB)
                    )
                )

                // Speech speed slider
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("سرعة النطق الصوتي الفونيمي:", fontSize = 11.sp, color = CharcoalText)
                        Text(
                            if (speechRate < 0.5f) "بطيء جداً 🐢" else if (speechRate < 0.8f) "بطيء هادئ 🚶" else "طبيعي ⚡",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                    Slider(
                        value = speechRate,
                        onValueChange = { speechRate = it },
                        valueRange = 0.3f..1.1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF0F766E),
                            activeTrackColor = Color(0xFF0D9488),
                            inactiveTrackColor = Color(0xFFE5E7EB)
                        )
                    )
                }

                Button(
                    onClick = {
                        if (textInput.isNotEmpty()) {
                            // Set speed rate on TTS
                            tts?.setSpeechRate(speechRate)
                            tts?.speak(textInput)
                        } else {
                            tts?.speak("الرجاء كتابة كلمة أولاً يا بطل!")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("انطق الكلمة بدقة 🔊✨", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textInput.isNotEmpty()) {
                        onComplete("تدريب الكاتب الذكي وصوت الحروف: $textInput", 100, 20)
                        onDismiss()
                    } else {
                        onComplete("تدريب الكاتب الذكي على الصوتيات", 80, 10)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
            ) {
                Text("أنجزت النطق والكتابة! استلم 20 ⭐", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = CharcoalText)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

// ==========================================
// 9. DAILY MAGIC CHALLENGE WHEEL
// ==========================================
@Composable
fun MagicChallengeWheelDialog(
    onDismiss: () -> Unit,
    onComplete: (activityName: String, score: Int, starsEarned: Int) -> Unit,
    tts: NasheetTTS?,
    schoolLevel: String
) {
    var isSpinning by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableStateOf(0f) }
    var selectedChallengeIndex by remember { mutableStateOf<Int?>(null) }
    var showAwardButton by remember { mutableStateOf(false) }

    val challenges = remember(schoolLevel) {
        when {
            schoolLevel.contains("الأول") || schoolLevel.contains("أولى") || schoolLevel.contains("أول") -> listOf(
                Pair("انطق حرف الألف بالمد الطويل (آ) 🎙️", "نطق سليم للمدود والحركات لتقوية النطق الأساسي"),
                Pair("صل الحرف الأول من اسم أرنوب 🐰", "التعرف الصوتي المترابط بالرسومات وحفز المرجعية"),
                Pair("انطق اسم الفأس والتفاحة بصوت مجزأ 🍎", "معالجة التردد والدمج اللفظي في الكلمة"),
                Pair("عد من 1 إلى 5 بالعد الهادئ 🔢", "تطوير الحساب الذهني والتوازن العصبي البصري"),
                Pair("تتبع حركة النجمة بالعين 👀", "تنشيط التتبع البصري السريع لمعالجة فرط الحركة"),
                Pair("صنف الأصوات البسيطة (ب / ت / ث) 🔊", "الوعي السمعي وإتمام معالجة التشويش الفونيمي")
            )
            schoolLevel.contains("الثاني") || schoolLevel.contains("ثانية") || schoolLevel.contains("ثاني") -> listOf(
                Pair("انطق كلمة سمكة وحركاتها الثلاثة 🐟", "مزاوجة التشكيل مع المظهر الصوتي الصحيح"),
                Pair("استخرج حرف السين من كلمة سمسم 🐢", "البحث عن الحروف في بنية الكلمات وتدوير الهجاء"),
                Pair("انطق كلمات التنوين بالفتح (سمكةً) 🎙️", "قراءة التنوين بطلاقة تامة لراحة المخارج"),
                Pair("اعثر على رمز السلحفاة والهدهد 🧠", "تنشيط الملاحظة الذهنية وسعة الذاكرة القريبة"),
                Pair("تأمل سطر من كتاب السلحفاة 📖", "زيادة طلاقة القراءة والوصول للمخارج الكاملة المطبوعة"),
                Pair("ميز بين التاء المربوطة والمفتوحة 🔊", "الوعي السمعي والهجائي المتكامل للألعاب والدروس")
            )
            schoolLevel.contains("الثالث") || schoolLevel.contains("ثالثة") || schoolLevel.contains("ثالث") -> listOf(
                Pair("صل واحة غرداية وثعلبها الذكي 🦊", "قيمة جغرافية وقصصية تزيد الانتماء والاستيعاب"),
                Pair("استخرج كلمة بها لام لغوية شمسية وقمرية ☀️", "قوانين القواعد النحوية واللفظية السليمة"),
                Pair("قراءة قصة واحة التمر السريعة 🌴", "زيادة سرعة القراءة والمدارك الحكائية الكونية"),
                Pair("اعثر على رمز الفنك والرمال بالذاكرة 🧠", "التمثيل اللطيف للرموز المترابطة بالجزائر الحبيبة"),
                Pair("عبر عن جمال الجزائر بجملة ثلاثية 🇩🇿", "بناء الجمل المعبرة والتوليد اللغوي الحر البديع"),
                Pair("ميز مخارج حروف الصفير (س / ص / ز) 🔊", "التمثيل السمعي الفونيمي العميق وتصفية الكلمات")
            )
            schoolLevel.contains("الرابع") || schoolLevel.contains("رابعة") || schoolLevel.contains("رابع") -> listOf(
                Pair("اذكل اسم بطل دافع عن جبال الأوراس 🏔️", "ترسيخ التاريخ والبطولة الوطنية الجزائرية الخالدة"),
                Pair("اقرأ جملة من حكاية المجاهدين الأبرار 📖", "الطلاقة الفصيحة والارتباط بقيم الشجاعة العريقة"),
                Pair("استخرج المفرد والجمع للجملة (وطن / أوطان) 🏗️", "تعديل النحو اللغوي وترتيب البنى المعجمية"),
                Pair("احسب نجوم ومكافآت أحمد الذهبية 🔢", "مسائل حسابية ذكية تفكك الجمود الحركي والتركيزي"),
                Pair("تتبع بالعين طائر الفنك فوق الكثبان 👀", "توجيه انتباه مسار العين لمعالجة تشتت النظر الحركي"),
                Pair("ميز مخارج حروف الحلق الصعبة (ح / خ / ع / غ) 🔊", "تصحيح مخارج الحروف الشجرية والصوتية المخفية")
            )
            else -> listOf(
                Pair("اذكر فائدة استخدام الطاقة الشمسية النظيفة ☀️", "تحفيز التفكير العلمي والمستقبل البيئي المستدام"),
                Pair("تحدث بمودة عن قمر صناعي جزائري في الفضاء 🛰️", "زيادة طموح الطالب نحو الفضاء والتكنولوجيا المشرقة"),
                Pair("فكك كلمة التكنولوجيا إلى مقاطع صوتية 🧪", "التحليل السمعي الدقيق للمصطلحات العلمية الكبرى"),
                Pair("تأمل حكاية ليلى ومباني القصبة الأثرية 🏛️", "المعمار التاريخي العتيق وإبراز الإثراء الحضاري"),
                Pair("ابحث عن حل لآفة تلوث البيئة من حولنا 🌱", "بث روح العمل التعاوني وصيانة الأمانات المحيطة"),
                Pair("استمع جيدا لمخارج الحروف المتشابهة (ض / ظ) 🔊", "المخارج الفصيحة وتجنب خلط الأصوات الصعبة باللسان")
            )
        }
    }

    LaunchedEffect(schoolLevel) {
        tts?.speak("مرحباً بك في عجلة التحديات اليومية السحرية! اضغط على زر تدوير العجلة لكي يقع عليك تحدٍ بطولي مثير وتكسب النجوم!")
    }

    // Spin Animation using LaunchedEffect
    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            tts?.speak("دوِّر عجلة التدريب السحري!")
            var speed = 30f
            while (speed > 1f) {
                rotationAngle += speed
                speed *= 0.92f
                kotlinx.coroutines.delay(40)
            }
            isSpinning = false
            // Derive which catalog segment has arrived
            val normalizedAngle = (rotationAngle % 360f + 360f) % 360f
            val index = ((normalizedAngle / 60f).toInt()) % challenges.size
            selectedChallengeIndex = index
            showAwardButton = true
            tts?.speak("تحديك السحري اليوم هو: ${challenges[index].first}! هل أنت مستعد لإنجازه بنجاح؟")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "عجلة التحديات والتحفيز السحري 🎯🎡",
                fontWeight = FontWeight.Bold,
                color = CharcoalText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "العجلة السحرية تختار يومياً تدريب ميكانيكي هادف لمعالجة تشتت التركيز وصعوبات القراءة ممتع كاللعب:",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalText,
                    textAlign = TextAlign.Center
                )

                // Spinning Wheel Canvas or Visual Representation
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        val strokeWidth = 12f
                        val radius = minOf(size.width, size.height) / 2f - strokeWidth
                        val center = Offset(size.width / 2f, size.height / 2f)

                        // Draw segments
                        val colors = listOf(
                            Color(0xFFFEF3C7), Color(0xFFFEE2E2), Color(0xFFE0F2FE),
                            Color(0xFFECFDF5), Color(0xFFF3E8FF), Color(0xFFFFF1F2)
                        )

                        // Draw colored segments
                        for (i in 0 until 6) {
                            drawArc(
                                color = colors[i],
                                startAngle = i * 60f + rotationAngle,
                                sweepAngle = 60f,
                                useCenter = true,
                                size = size
                            )
                        }

                        // Draw outer border
                        drawCircle(
                            color = Color(0xFF1E1B4B),
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth)
                        )

                        // Draw center bolt
                        drawCircle(
                            color = Color(0xFF1E1B4B),
                            radius = strokeWidth * 2,
                            center = center
                        )
                    }

                    // Pointer Overlay on top
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-6).dp)
                    ) {
                        Text("👇", fontSize = 28.sp)
                    }
                }

                Button(
                    onClick = {
                        if (!isSpinning) {
                            isSpinning = true
                            showAwardButton = false
                            selectedChallengeIndex = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSpinning
                ) {
                    Text("دوّر العجلة السحرية! 🎡✨", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Selected Challenge Result Highlight block
                if (selectedChallengeIndex != null) {
                    val chal = challenges[selectedChallengeIndex!!]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🎉 تحديك الذي اخترته العجلة السحرية 🎉", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFD97706))
                            Text(chal.first, fontWeight = FontWeight.Black, fontSize = 15.sp, color = CharcoalText, textAlign = TextAlign.Center)
                            Text(chal.second, fontSize = 11.sp, color = CharcoalText.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (showAwardButton && selectedChallengeIndex != null) {
                Button(
                    onClick = {
                        val chalName = challenges[selectedChallengeIndex!!].first
                        onComplete("تحدي العجلة السحرية: $chalName", 100, 15)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
                ) {
                    Text("أنجزت التحدي! استلم 15 ⭐", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = CharcoalText)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

/* DUPLICATE START
nerColor = Color(0xFFFFFBEB))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🎉 تحديك الذي اخترته العجلة السحرية 🎉", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFD97706))
                            Text(chal.first, fontWeight = FontWeight.Black, fontSize = 15.sp, color = CharcoalText, textAlign = TextAlign.Center)
                            Text(chal.second, fontSize = 11.sp, color = CharcoalText.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (showAwardButton && selectedChallengeIndex != null) {
                Button(
                    onClick = {
                        val chalName = challenges[selectedChallengeIndex!!].first
                        onComplete("تحدي العجلة السحرية: $chalName", 100, 15)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
                ) {
                    Text("أنجزت التحدي! استلم 15 ⭐", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = CharcoalText)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
       // Speak question helper
                    IconButton(
                        onClick = {
                            tts?.speak(question.text)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(36.dp)
                            .background(Color(0xFFEFF6FF), CircleShape)
                    ) {
                        Text("🔊", fontSize = 14.sp)
                    }

                    // Options list
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        question.options.forEachIndexed { optionIdx, optionText ->
                            val isSelected = selectedQuizOption == optionIdx
                            val isCorrectAnswer = optionIdx == question.correctIdx

                            val bgSelectedColor = if (answeredStatus != null) {
                                if (isCorrectAnswer) Color(0xFFECFDF5) // green
                                else if (isSelected) Color(0xFFFEF2F2) // red
                                else Color.White
                            } else {
                                if (isSelected) Color(0xFFEEF2FF) else Color.White
                            }

                            val borderSelectedColor = if (answeredStatus != null) {
                                if (isCorrectAnswer) Color(0xFF10B981)
                                else if (isSelected) Color(0xFFEF4444)
                                else Color(0xFFE5E7EB)
                            } else {
                                if (isSelected) Color(0xFF4F46E5) else Color(0xFFE5E7EB)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(bgSelectedColor)
                                    .border(1.5.dp, borderSelectedColor, RoundedCornerShape(14.dp))
                                    .clickable(enabled = answeredStatus == null) {
                                        selectedQuizOption = optionIdx
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = optionText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )

                                    if (answeredStatus != null) {
                                        if (isCorrectAnswer) {
                                            Text("✅ صحيح", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                        } else if (isSelected) {
                                            Text("❌ خاطئ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .border(
                                                    1.5.dp,
                                                    if (isSelected) Color(0xFF4F46E5) else Color(0xFF9CA3AF),
                                                    CircleShape
                                                )
                                                .background(
                                                    if (isSelected) Color(0xFF4F46E5) else Color.Transparent,
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Action button: Check or Go Next
                    if (answeredStatus == null) {
                        Button(
                            onClick = {
                                if (selectedQuizOption != -1) {
                                    val isCorrect = selectedQuizOption == question.correctIdx
                                    answeredStatus = isCorrect
                                    if (isCorrect) {
                                        scoreValue += 33
                                        tts?.speak("إجابتك ممتازة وصحيحة يا بطل! أحسنت!")
                                    } else {
                                        tts?.speak("هذه الإجابة غير دقيقة، لكن لا بأس فالمهم أن نتعلم المهارات السليمة.")
                                    }
                                    showExplanation = true
                                } else {
                                    tts?.speak("يرجى اختيار أحد المقترحات للمواصلة والتحقق!")
                                }
                            },
                            enabled = selectedQuizOption != -1,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("التحقق من إجابتك 🔍", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Explanation Box is shown
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("💡 تفصيل وتصويب تعليمي:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFD97706))
                                Text(question.hint, fontSize = 11.sp, color = CharcoalText)
                            }
                        }

                        Button(
                            onClick = {
                                if (currentQuestionIdx < 2) {
                                    currentQuestionIdx++
                                    selectedQuizOption = -1
                                    answeredStatus = null
                                    showExplanation = false
                                } else {
                                    quizCompletedState = true
                                    // Calculate reward stars based on finalized grade score
                                    val finalScoreFormatted = if (scoreValue > 90) 100 else scoreValue
                                    val earnedStars = if (finalScoreFormatted >= 99) 30 else if (finalScoreFormatted >= 60) 20 else 10
                                    
                                    onComplete(
                                        "منهج 2026: قسم ${activeGrade.name}",
                                        "المنهج الدراسي والتقويم الموحد",
                                        finalScoreFormatted,
                                        80,
                                        earnedStars
                                    )
                                    tts?.speak("تهانينا الحارة يا بطل الجزائِر الأشم! لقد أنهيت اختبار التقييم المنهجي وحصلت على تقدير باهر ورصيد نجوم إضافية!")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (currentQuestionIdx < 2) "السؤال التالي ◀️" else "إنهاء واستلام النجوم ⭐🏆",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // QUIZ COMPLETED CERTIFICATE
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("🇩🇿🎖️ شهادة تفوق في المنهج الوطني 🎖️🇩🇿", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFFC2410C))
                        
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color(0xFFFEF3C7), CircleShape)
                                .border(2.dp, Color(0xFFF59E0B), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏆", fontSize = 54.sp)
                        }

                        Text(
                            text = "أنهى البطل النطاق الكامل لـ:\n${activeGrade.name}\nبتقييم رصين مقداره: $scoreValue %",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = CharcoalText,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            "المليُون ونصف مليون شهيد مهدوا لك ريادة الأمم بالعلم والاجتهاد والأخلاق الفاضلة. واصل تحدي الدروس دائماً ليلمع اسمك في لوحة المدرسة والبيت!",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalText.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                // Reset to default lessons mode to allow more practice
                                currentQuestionIdx = -1
                                scoreValue = 0
                                selectedQuizOption = -1
                                answeredStatus = null
                                showExplanation = false
                                quizCompletedState = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("تصفح الدروس مجدداً 📖", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق والعودة للسندباد 🚪", color = CharcoalText, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
DUPLICATE END */


