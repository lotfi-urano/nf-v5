package com.aistudio.nasheet.app.ui.screens

import com.aistudio.nasheet.app.data.constants.AppConstants

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.nasheet.app.R
import com.aistudio.nasheet.app.ui.theme.*

// --- GLOBAL DECLARED SHARED CLASSROOM STATES ---
private var sharedClassCode by mutableStateOf("")
private var sharedClassName by mutableStateOf("")
private val sharedStudentsList = mutableStateListOf<String>()

fun addStudentToTeacherClass(studentName: String) {
    if (studentName.isNotBlank() && !sharedStudentsList.contains(studentName)) {
        sharedStudentsList.add(studentName)
    }
}

@Composable
fun TeacherStepsProgress(currentStep: Int, onStepClick: (Int) -> Unit) {
    val steps = listOf("الملف المهني 👨‍🏫", "إنشاء الصف 🏫", "دفتر الطلاب 🎓")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, label ->
            val isActive = index <= currentStep
            val isCurrent = index == currentStep
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = index < currentStep) { onStepClick(index) }
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isCurrent) Color(0xFF10B981) else if (isActive) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                        .border(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = if (isActive) Color(0xFF10B981) else SlateBorder,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isActive && !isCurrent) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Done",
                            tint = Color(0xFF047857),
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = (index + 1).toString(),
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color.White else Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = if (isActive) Color(0xFF1E293B) else Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            }
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .height(2.dp)
                        .background(if (index < currentStep) Color(0xFF10B981) else SlateBorder)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun CanvasRoleAvatar(
    drawableId: Int,
    baseColor: Color = Color.Transparent,
    accentColor: Color = Color.Transparent,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(96.dp)
            .clip(CircleShape)
            .border(2.dp, Color(0xFFCBD5E1), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

data class DiagnosticQuestion(
    val id: Int,
    val text: String,
    val category: String, // "adhd", "dyslexia", "writing", "visual"
    val sectionName: String
)

val diagnosticQuestions = listOf(
    // Section 1: Attention and Focus (الانتباه والتركيز 🎯)
    DiagnosticQuestion(1, "هل يتشتت الطفل بسرعة أثناء النشاط؟", "adhd", "الانتباه والتركيز 🎯"),
    DiagnosticQuestion(2, "هل يجد صعوبة في إكمال المهمة للنهاية؟", "adhd", "الانتباه والتركيز 🎯"),
    DiagnosticQuestion(3, "هل ينسى التعليمات بسرعة؟", "adhd", "الانتباه والتركيز 🎯"),
    DiagnosticQuestion(4, "هل يحتاج تكرار التعليمات عدة مرات؟", "adhd", "الانتباه والتركيز 🎯"),

    // Section 2: Reading and Letters (القراءة والحروف 🔤)
    DiagnosticQuestion(5, "هل يخلط بين الحروف المتشابهة؟ مثل: ب / ت / ث", "dyslexia", "القراءة والحروف 🔤"),
    DiagnosticQuestion(6, "هل يجد صعوبة في ربط الحرف بصوته؟", "dyslexia", "القراءة والحروف 🔤"),
    DiagnosticQuestion(7, "هل يقرأ ببطء مقارنة بعمره؟", "dyslexia", "القراءة والحروف 🔤"),
    DiagnosticQuestion(8, "هل يعكس الحروف أو الكلمات أحيانًا؟", "dyslexia", "القراءة والحروف 🔤"),

    // Section 3: Writing (الكتابة ✏️)
    DiagnosticQuestion(9, "هل يمسك القلم بصعوبة؟", "writing", "الكتابة ✏️"),
    DiagnosticQuestion(10, "هل خطه غير واضح جدًا؟", "writing", "الكتابة ✏️"),
    DiagnosticQuestion(11, "هل يواجه مشكلة في نسخ الكلمات؟", "writing", "الكتابة ✏️"),
    DiagnosticQuestion(12, "هل يتعب بسرعة أثناء الكتابة؟", "writing", "الكتابة ✏️"),

    // Section 4: Visual Perception (الإدراك البصري 🧩)
    DiagnosticQuestion(13, "هل يجد صعوبة في إيجاد الاختلافات بين الصور؟", "visual", "الإدراك البصري 🧩"),
    DiagnosticQuestion(14, "هل يخلط بين الأشكال المتشابهة؟", "visual", "الإدراك البصري 🧩"),
    DiagnosticQuestion(15, "هل يواجه مشكلة في ترتيب الصور أو الخطوات؟", "visual", "الإدراك البصري 🧩")
)

// 1. ONBOARDING VIEW (التهيئة الذكية - مسارات الولي والمعلم المترابطة)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingView(
    onRegister: (name: String, age: Int, level: String, difficulty: String, pName: String, pPhone: String, pEmail: String, avatar: String) -> Unit,
    onLogin: (name: String, parentCode: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) -> Unit,
    onRegisterParentEmail: (name: String, phone: String, email: String, password: String, childName: String, childAge: Int, level: String, difficulty: String, avatar: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) -> Unit = { _,_,_,_,_,_,_,_,_,_,_ -> },
    onRegisterTeacherEmail: (name: String, email: String, password: String, school: String, experience: String, specialty: String, classroom: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) -> Unit = { _,_,_,_,_,_,_,_,_ -> },
    onLoginEmail: (email: String, password: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) -> Unit = { _,_,_,_ -> },
    isFirebaseAvailable: Boolean = false
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var currentScreenMode by remember { mutableStateOf("role_select") }
    var selectedRole by remember { mutableStateOf("parent") }

    // Parent states
    var parentNameInput by remember { mutableStateOf("") }
    var parentPhoneInput by remember { mutableStateOf("") }
    var parentEmailInput by remember { mutableStateOf("") }
    var parentPasswordInput by remember { mutableStateOf("") }
    var childName by remember { mutableStateOf("") }
    var childAge by remember { mutableStateOf(7) }
    var selectedAvatar by remember { mutableStateOf("🦖") }
    var enteredClassCode by remember { mutableStateOf("") }
    val selectedLevel = remember(childAge) {
        when (childAge) {
            in 4..7 -> "الأولى ابتدائي"
            8 -> "الثانية ابتدائي"
            9 -> "الثالثة ابتدائي"
            10 -> "الرابعة ابتدائي"
            else -> "الخامسة ابتدائي"
        }
    }
    var showLinkSuccessDialog by remember { mutableStateOf(false) }
    var hasLinkedCodeSuccessfully by remember { mutableStateOf(false) }

    // Challenges Survey (15 dynamic questions)
    val surveyAnswers = remember { mutableStateMapOf<Int, String>() }
    var currentSurveySection by remember { mutableStateOf(1) } // 1, 2, 3, 4, or 5 (result screen)

    // Teacher states
    var teacherName by remember { mutableStateOf("") }
    var teacherEmail by remember { mutableStateOf("") }
    var teacherPasswordInput by remember { mutableStateOf("") }
    var teacherPhone by remember { mutableStateOf("") }
    var teacherSchool by remember { mutableStateOf("") }
    var teacherExperience by remember { mutableStateOf("٣ - ٥ سنوات 🏫") }
    var teacherSpecialization by remember { mutableStateOf("أخصائي صعوبات تعلم 🧠") }
    var showSpecialtyDropdown by remember { mutableStateOf(false) }
    var classroomNameInput by remember { mutableStateOf("") }
    var hasClassroomBeenCreated by remember { mutableStateOf(false) }
    var teacherActiveTab by remember { mutableStateOf(0) }
    var manualStudentNameInput by remember { mutableStateOf("") }

    val childAvatars = listOf(
        "🦖" to "ديناصور شجاع",
        "🧑‍🚀" to "رائد فضاء",
        "🦁" to "أسد جرجرة 🇩🇿",
        "🦊" to "فَنَك الصحراء 🦊",
        "🤖" to "عبقري نشيط 🤖",
        "🚀" to "مكوك المعرفة"
    )
    val specializationList = listOf("أخصائي صعوبات تعلم 🧠", "معلم لغة عربية مساند 📚", "مربي طفولة مبكرة 👶")

    Scaffold(containerColor = Color.Transparent) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFCFAF5),
                            Color(0xFFF7FDFE),
                            Color(0xFFEEF2FF)
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
            when (currentScreenMode) {
                "role_select" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 0.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "This app was programmed by lotfi",
                            fontSize = 11.sp,
                            color = CharcoalText.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 4.dp),
                            textAlign = TextAlign.Center
                        )

                        // Beautiful centered NACHIT logo without card borders & perfectly compact
                        Image(
                            painter = painterResource(id = R.drawable.nasheet_app_icon_1780234200444),
                            contentDescription = "شعار تطبيق نشيط",
                            modifier = Modifier
                                .width(400.dp)
                                .height(220.dp)
                                .padding(0.dp)
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Text("مرحباً بك في تطبيق نَشِيط 🤖✨", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = CharcoalText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("المنصة الذكية للأطفال ذوي صعوبات التعلم والتركيز. اختر دورك للبدء:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = CharcoalText, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)

                        // Updated Large Interactive Cards matching Neo-Brutalism of Presentation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Parent Card
                            Card(
                                onClick = {
                                    selectedRole = "parent"
                                    currentScreenMode = "auth_choice"
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = PastelMint),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(180.dp)
                                    .border(1.2.dp, SlateBorder, RoundedCornerShape(24.dp))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CanvasRoleAvatar(
                                        drawableId = R.drawable.img_parent_avatar_1779626344997,
                                        baseColor = Color(0xFF10B981),
                                        accentColor = PastelMint
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("ولي أمر", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = CharcoalText)
                                }
                            }

                            // Teacher Card
                            Card(
                                onClick = {
                                    selectedRole = "teacher"
                                    currentScreenMode = "auth_choice"
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = PastelBlue),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(180.dp)
                                    .border(1.2.dp, SlateBorder, RoundedCornerShape(24.dp))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CanvasRoleAvatar(
                                        drawableId = R.drawable.img_teacher_avatar_1779626324852,
                                        baseColor = Color(0xFF3B82F6),
                                        accentColor = PastelBlue
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("أستاذ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = CharcoalText)
                                }
                            }
                        }

                        // Added 3 High-Fidelity Presentation Style Feature Cards (Matching Presentation Slides)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "مميزات منصة نشيط الذكية 🌟",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = CharcoalText,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        // Feature Card 1: تبييت الاحتياجات
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)), // light mint green
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.2.dp, SlateBorder, RoundedCornerShape(24.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.2.dp, SlateBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_feature_needs),
                                        contentDescription = "تحديد الاحتياجات",
                                        modifier = Modifier.fillMaxSize().padding(2.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "تحديد الاحتياجات",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = CharcoalText,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "| تقييم شامل لتحديد نقاط القوة والضعف بدقة مذهلة",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText.copy(alpha = 0.85f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "| خطط تعليمية مخصصة ومبنية على مستويات التقييم لحلول ذكية",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText.copy(alpha = 0.85f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Feature Card 2: متابعة الأبطال
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // light off-white amber
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.2.dp, SlateBorder, RoundedCornerShape(24.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.2.dp, SlateBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_feature_heroes),
                                        contentDescription = "متابعة الأبطال",
                                        modifier = Modifier.fillMaxSize().padding(2.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "متابعة الأبطال",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = CharcoalText,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "| مراقبة التقدم والتحصيل اليومي بدقة وجودة عالية",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText.copy(alpha = 0.85f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "| تقارير وإحصائيات ذكية شاملة لأولياء الأمور والمختصين",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText.copy(alpha = 0.85f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Feature Card 3: أدوات المعلم
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)), // light lavender blue
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.2.dp, SlateBorder, RoundedCornerShape(24.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.2.dp, SlateBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_feature_tools),
                                        contentDescription = "أدوات المعلم",
                                        modifier = Modifier.fillMaxSize().padding(2.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "أدوات المعلم",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = CharcoalText
                                    )
                                    Text(
                                        "| تصميم وسائل تعليمية تفاعلية ومساندة ومحتوى ممتع",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText.copy(alpha = 0.85f)
                                    )
                                    Text(
                                        "| إدارة تفصيلية كاملة للصفوف وتقديم الملاحظات بفعالية",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                "auth_choice" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { currentScreenMode = "role_select" }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رجوع", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(if (selectedRole == "parent") PastelMint else PastelBlue)
                                .border(1.2.dp, SlateBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    id = if (selectedRole == "parent") {
                                        R.drawable.img_parent_avatar_1779626344997
                                    } else {
                                        R.drawable.img_teacher_avatar_1779626324852
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Text(
                            text = if (selectedRole == "parent") "التحقق والولوج لبيئة الولي 👨‍👩‍👦" else "التحقق والولوج لبيئة الأستاذ 🏫",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "منصة نشيط تمكنك من متابعة ذكاء ومهارات طفلك الإدراكية باستمرار. كيف ترغب في المتابعة؟",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CharcoalText.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Choice Card 1: Register (إنشاء حساب جديد)
                        Card(
                            onClick = {
                                if (selectedRole == "parent") {
                                    currentScreenMode = "parent_info_register"
                                } else {
                                    currentScreenMode = "teacher_register"
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.2.dp, SlateBorder, RoundedCornerShape(24.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .background(PastelMint, RoundedCornerShape(14.dp))
                                        .border(1.dp, SlateBorder, RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✨", fontSize = 24.sp)
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        "إنشاء حساب جديد لأول مرة ✨",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = CharcoalText
                                    )
                                    Text(
                                        "ابدأ بإنشاء ملف تعريف جديد لطفلك لتهيئة ألعابه التشخيصية المخصصة.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CharcoalText.copy(alpha = 0.7f)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowRight,
                                    contentDescription = "Go",
                                    tint = CharcoalText
                                )
                            }
                        }

                        // Choice Card 2: Login (تسجيل دخول حساب مسجل سابقاً)
                        Card(
                            onClick = {
                                currentScreenMode = "login_screen"
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.2.dp, SlateBorder, RoundedCornerShape(24.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .background(PastelBlue, RoundedCornerShape(14.dp))
                                        .border(1.dp, SlateBorder, RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🔑", fontSize = 24.sp)
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        "تسجيل دخول لحساب سابق 🔑",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = CharcoalText
                                    )
                                    Text(
                                        "لديك حساب مسجل بالفعل؟ قم بإدخال اسم الطفل المعرف ورمز الولي (ID) للمتابعة فورا.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CharcoalText.copy(alpha = 0.7f)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowRight,
                                    contentDescription = "Go",
                                    tint = CharcoalText
                                )
                            }
                        }
                    }
                }
                "login_screen" -> {
                    var loginChildName by remember { mutableStateOf("") }
                    var loginParentId by remember { mutableStateOf("") }
                    var loginEmail by remember { mutableStateOf("") }
                    var loginPassword by remember { mutableStateOf("") }
                    var isCloudLoginMode by remember { mutableStateOf(true) }
                    var isLoggingIn by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { currentScreenMode = "auth_choice" }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رجوع للخيارات", fontWeight = FontWeight.Bold)
                            }

                            TextButton(onClick = { currentScreenMode = "role_select" }) {
                                Icon(
                                    imageVector = Icons.Rounded.Home,
                                    contentDescription = "Main",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("الرئيسية 🏠", fontWeight = FontWeight.Bold)
                            }
                        }

                        val isTeacher = selectedRole == "teacher"

                        Text(
                            text = if (isTeacher) "بوابة المعلم: تسجيل دخول 🏫🔑" else "بوابة الولي: تسجيل دخول الحساب 🔑",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (isTeacher) {
                                "أدخل بيانات الحساب لتتمكن من مزامنة وتعديل سجلات طلابك ودفاتر تقييمهم."
                            } else {
                                "أدخل بيانات الحساب المسجلة مسبقاً لاسترجاع ملف طفلك وسجل تقدمه والتقييمات الذكية."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = CharcoalText.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selection Pills for Mode Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isCloudLoginMode) SoftTeal else Color.Transparent)
                                    .clickable { isCloudLoginMode = true }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "تسجيل سحابي (البريد) ☁️",
                                    color = if (isCloudLoginMode) Color.White else CharcoalText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (!isCloudLoginMode) SoftTeal else Color.Transparent)
                                    .clickable { isCloudLoginMode = false }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "بالكود السريع 🔑",
                                    color = if (!isCloudLoginMode) Color.White else CharcoalText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (isCloudLoginMode) {
                            // Cloud Input 1: Email Address
                            OutlinedTextField(
                                value = loginEmail,
                                onValueChange = { loginEmail = it },
                                label = { Text("البريد الإلكتروني المسجل") },
                                placeholder = { Text("example@domain.com") },
                                leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = "Email", tint = SoftTeal) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CharcoalText,
                                    unfocusedTextColor = CharcoalText,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = SoftTeal,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedLabelColor = SoftTeal
                                )
                            )

                            // Cloud Input 2: Password
                            var isLoginPasswordVisible by remember { mutableStateOf(false) }
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text("رمز المرور السري") },
                                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = "Password", tint = SoftTeal) },
                                trailingIcon = {
                                    val icon = if (isLoginPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                                    IconButton(onClick = { isLoginPasswordVisible = !isLoginPasswordVisible }) {
                                        Icon(imageVector = icon, contentDescription = "Toggle password")
                                    }
                                },
                                visualTransformation = if (isLoginPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CharcoalText,
                                    unfocusedTextColor = CharcoalText,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = SoftTeal,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedLabelColor = SoftTeal
                                )
                            )
                        } else {
                            // Input 1: Child's or Teacher's Name
                            OutlinedTextField(
                                value = loginChildName,
                                onValueChange = { loginChildName = it },
                                label = { Text(if (isTeacher) "الاسم الكامل للأستاذ *" else "اسم الطفل كاملاً *") },
                                placeholder = { Text(if (isTeacher) "مثال: أستاذ أحمد علي" else "مثال: يوسف أحمد") },
                                leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = "User Name", tint = SoftTeal) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CharcoalText,
                                    unfocusedTextColor = CharcoalText,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = SoftTeal,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedLabelColor = SoftTeal
                                )
                            )

                            // Input 2: Parent ID Code / Teacher ID Code
                            OutlinedTextField(
                                value = loginParentId,
                                onValueChange = { loginParentId = it },
                                label = { Text(if (isTeacher) "رمز الأستاذ التعريفي (Teacher ID) *" else "رمز الولي التعريفي (Parent ID) *") },
                                placeholder = { Text(if (isTeacher) "مثال: TCH-123456" else "مثال: NST-123456") },
                                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = "Code ID", tint = SoftTeal) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CharcoalText,
                                    unfocusedTextColor = CharcoalText,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = SoftTeal,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedLabelColor = SoftTeal
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Login CTA button
                        Button(
                            onClick = {
                                if (isCloudLoginMode) {
                                    val emailTrimmed = loginEmail.trim()
                                    val passwordTrimmed = loginPassword.trim()
                                    if (emailTrimmed.isEmpty() || passwordTrimmed.isEmpty()) {
                                        Toast.makeText(context, "⚠️ الرجاء إدخال البريد الإلكتروني وكلمة المرور للمتابعة!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        isLoggingIn = true
                                        onLoginEmail(
                                            emailTrimmed,
                                            passwordTrimmed,
                                            { displayName ->
                                                isLoggingIn = false
                                                Toast.makeText(context, "👋 مرحباً بك يا $displayName! تم تسجيل دخولك سحابياً بنجاح.", Toast.LENGTH_LONG).show()
                                            },
                                            { errMsg ->
                                                isLoggingIn = false
                                                Toast.makeText(context, "⚠️ خطأ في الدخول السحابي: $errMsg", Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    }
                                } else {
                                    if (loginChildName.trim().isEmpty() || loginParentId.trim().isEmpty()) {
                                        val errorToastStr = if (isTeacher) {
                                            "الرجاء إدخال اسم الأستاذ والرمز التعريفي للمتابعة!"
                                        } else {
                                            "الرجاء إدخال كلاً من اسم الطفل والرمز التعريفي للمتابعة!"
                                        }
                                        Toast.makeText(context, errorToastStr, Toast.LENGTH_LONG).show()
                                    } else {
                                        isLoggingIn = true
                                        onLogin(
                                            loginChildName.trim(),
                                            loginParentId.trim().uppercase(),
                                            {
                                                isLoggingIn = false
                                                Toast.makeText(context, "👋 أهلاً بك مجدداً! تم تسجيل دخولك بنجاح.", Toast.LENGTH_LONG).show()
                                            },
                                            { errorMsg ->
                                                isLoggingIn = false
                                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    }
                                }
                            },
                            enabled = !isLoggingIn,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftTeal,
                                contentColor = Color.White
                            )
                        ) {
                            if (isLoggingIn) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    "تسجيل الدخول الفوري 🚀",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Added: Secondary Back to Auth Choices Button
                        OutlinedButton(
                            onClick = { currentScreenMode = "auth_choice" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(1.2.dp, SlateBorder, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = CharcoalText,
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "رجوع لخيارات الدخول ↩️",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Added: Support Button to go to Main Welcome screen
                        OutlinedButton(
                            onClick = { currentScreenMode = "role_select" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(1.2.dp, SlateBorder, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = CharcoalText,
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "تغيير نوع الحساب (الرئيسية) 🚪",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Helpful tip card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PastelBlue.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    "💡 لتبسيط المراجعة والتقييم:",
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalText,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "إذا لم يكن لديك حساب فريد بعد، يرجى إنشاء حساب جديد أولاً وسيقوم النظام بتوليد رمز تعريفي (ID) خاص بك تلقائياً وعرضه في شاشة الإعدادات لتقوم بنسخه واستعماله لاحقاً.",
                                    color = CharcoalText.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                "parent_info_register" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { currentScreenMode = "auth_choice" }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رجوع", fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = "بيانات ولي الأمر 👨‍👩‍👦",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = "الرجاء إدخال بياناتك أولاً لإنشاء حساب المتابعة الذكية لطفلك بنجاح.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                            color = CharcoalText.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Parent Name Input
                        OutlinedTextField(
                            value = parentNameInput,
                            onValueChange = { parentNameInput = it },
                            label = { Text("الاسم واللقب * (مطلوب)") },
                            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = "Parent Name", tint = Color(0xFF10B981)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CharcoalText,
                                unfocusedTextColor = CharcoalText,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedLabelColor = Color(0xFF10B981),
                                unfocusedLabelColor = CharcoalText.copy(alpha = 0.6f)
                            )
                        )

                        // Parent Phone Input
                        OutlinedTextField(
                            value = parentPhoneInput,
                            onValueChange = { parentPhoneInput = it },
                            label = { Text("رقم الهاتف * (مطلوب)") },
                            leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = "Parent Phone", tint = Color(0xFF10B981)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CharcoalText,
                                unfocusedTextColor = CharcoalText,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedLabelColor = Color(0xFF10B981),
                                unfocusedLabelColor = CharcoalText.copy(alpha = 0.6f)
                            )
                        )

                        // Parent Email Input (Required for Cloud Access)
                        OutlinedTextField(
                            value = parentEmailInput,
                            onValueChange = { parentEmailInput = it },
                            label = { Text("البريد الإلكتروني للولي  * (مطلوب للمزامنة)") },
                            leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = "Parent Email", tint = Color(0xFF10B981)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CharcoalText,
                                unfocusedTextColor = CharcoalText,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedLabelColor = Color(0xFF10B981),
                                unfocusedLabelColor = CharcoalText.copy(alpha = 0.6f)
                            )
                        )

                        // Parent Password Input
                        var isParentPasswordVisible by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = parentPasswordInput,
                            onValueChange = { parentPasswordInput = it },
                            label = { Text("كلمة المرور للحساب السحابي * (6 رموز فأكثر)") },
                            leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = "Parent Password", tint = Color(0xFF10B981)) },
                            trailingIcon = {
                                val icon = if (isParentPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                                IconButton(onClick = { isParentPasswordVisible = !isParentPasswordVisible }) {
                                    Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                                }
                            },
                            visualTransformation = if (isParentPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CharcoalText,
                                unfocusedTextColor = CharcoalText,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedLabelColor = Color(0xFF10B981),
                                unfocusedLabelColor = CharcoalText.copy(alpha = 0.6f)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val nameTrimmed = parentNameInput.trim()
                                val phoneTrimmed = parentPhoneInput.trim()
                                val emailTrimmed = parentEmailInput.trim()
                                val passTrimmed = parentPasswordInput.trim()
                                
                                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$".toRegex()

                                if (nameTrimmed.isEmpty() || phoneTrimmed.isEmpty() || emailTrimmed.isEmpty() || passTrimmed.isEmpty()) {
                                    Toast.makeText(context, "الرجاء ملء كافة الحقول الإلزامية (*) بما في ذلك البريد وكلمة المرور للمتابعة!", Toast.LENGTH_LONG).show()
                                } else if (!emailTrimmed.matches(emailRegex)) {
                                    Toast.makeText(context, "⚠️ الرجاء إدخال بريد إلكتروني صحيح وصالح للمزامنة.", Toast.LENGTH_SHORT).show()
                                } else if (passTrimmed.length < 6) {
                                    Toast.makeText(context, "⚠️ يجب أن تكون كلمة المرور 6 رموز أو أكثر لحماية حساب طفلك.", Toast.LENGTH_SHORT).show()
                                } else {
                                    currentScreenMode = "parent_profile"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(2.5.dp, Color(0xFF1E1B4B), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                "التالي: بيانات الطفل البطل 🧒✨",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                "parent_profile" -> {
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { currentScreenMode = "parent_info_register" }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رجوع")
                            }
                        }
                        Text("ملف بطلنا الصغير 🧒✨", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = CharcoalText)
                        OutlinedTextField(
                            value = childName,
                            onValueChange = { childName = it },
                            label = { Text("اسم الطفل البطل") },
                            leadingIcon = { Icon(Icons.Rounded.Face, contentDescription = "Face", tint = Color(0xFF10B981)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CharcoalText,
                                unfocusedTextColor = CharcoalText,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedLabelColor = Color(0xFF10B981),
                                unfocusedLabelColor = CharcoalText.copy(alpha = 0.6f)
                            )
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("عمر الطفل: $childAge سنوات", fontWeight = FontWeight.Bold, color = CharcoalText)
                            Slider(value = childAge.toFloat(), onValueChange = { childAge = it.toInt() }, valueRange = 4f..12f, modifier = Modifier.weight(1f).padding(horizontal = 12.dp))
                        }

                        Text("اختر الصورة الرمزية المفضلة 🦖:", fontWeight = FontWeight.Bold, color = CharcoalText, modifier = Modifier.align(Alignment.Start))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            childAvatars.chunked(3).forEach { chunk ->
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                    chunk.forEach { (emoji, desc) ->
                                        val isSelected = selectedAvatar == emoji
                                        val scale by animateFloatAsState(targetValue = if (isSelected) 1.08f else 1.0f, label = "avatar_scale")
                                        val avatarTheme = when (emoji) {
                                            "🦖" -> Pair(Color(0xFFECFDF5), Color(0xFF10B981)) // Green
                                            "🧑‍🚀" -> Pair(Color(0xFFEEF2FF), Color(0xFF6366F1)) // Indigo
                                            "🐱" -> Pair(Color(0xFFFFF1F2), Color(0xFFF43F5E)) // Rose
                                            "🤖" -> Pair(Color(0xFFECFEFF), Color(0xFF06B6D4)) // Cyan
                                            "🦊" -> Pair(Color(0xFFFFF7ED), Color(0xFFF97316)) // Orange
                                            "🦁" -> Pair(Color(0xFFFFFBEB), Color(0xFFD97706)) // Gold (Lion)
                                            "🎨" -> Pair(Color(0xFFFFF1F2), Color(0xFFF43F5E)) // Rose/Pink
                                            "🚀" -> Pair(Color(0xFFF5F3FF), Color(0xFF8B5CF6)) // Purple
                                            "🐨" -> Pair(Color(0xFFF1F5F9), Color(0xFF64748B)) // Slate
                                            else -> Pair(Color(0xFFF0FDF4), Color(0xFF10B981))
                                        }
                                        val animatedBgColor by animateColorAsState(targetValue = if (isSelected) avatarTheme.first else Color.White, label = "avatar_bg")
                                        val animatedBorderColor by animateColorAsState(targetValue = if (isSelected) avatarTheme.second else Color(0xFFE2E8F0), label = "avatar_border")
                                        val animatedBorderWidth by animateDpAsState(targetValue = if (isSelected) 2.5.dp else 1.dp, label = "avatar_border_width")

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(72.dp)
                                                .graphicsLayer {
                                                    scaleX = scale
                                                    scaleY = scale
                                                }
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(animatedBgColor)
                                                .border(animatedBorderWidth, animatedBorderColor, RoundedCornerShape(16.dp))
                                                .clickable { selectedAvatar = emoji },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(emoji, fontSize = 28.sp)
                                                Text(desc, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = CharcoalText)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🎯", fontSize = 22.sp)
                                    Text(
                                        text = "المسار التعليمي الذكي المخصص لعمر البطل:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF065F46)
                                    )
                                }
                                
                                val ageLevelInfo = when (childAge) {
                                    in 4..7 -> Pair(
                                        "مستوى تأسيس البراعم 🎒 (عمر $childAge سنوات)",
                                        "يتعلم طفلك الحركات الأساسية الفصحى، أصوات مخارج الحروف، الحساب وجمع الأرقام حتى 20، وآداب التحية والمعاملة اللطيفة."
                                    )
                                    8 -> Pair(
                                        "مستوى التطوير والربط اللغوي 📖 (عمر 8 سنوات)",
                                        "يركز البرنامج على الفروق الإملائية للتاء، الجمع والطرح حتى 100، وطرق المحافظة الفعالة على بيئة ومحيط قسمه."
                                    )
                                    9 -> Pair(
                                        "مستوى القواعد التطبيقية والعلوم 📐 (عمر 9 سنوات)",
                                        "يدرس البطل الهمزات، حفظ وممارسة جداول الضرب، بالإضافة لآداب الحوار وتجنب السلوكيات السيئة."
                                    )
                                    10 -> Pair(
                                        "مستوى المفاهيم المتقدمة والجغرافيا 🌍 (عمر 10 سنوات)",
                                        "يتعلم البطل حل الكسور، عمليات القسمة المركبة، مع التعرف الشامل على الجغرافيا والإنتاج الوطني."
                                    )
                                    else -> Pair(
                                        "مستوى النضوج الفكري والتاريخ 🇩🇿 (عمر $childAge سنة)",
                                        "يتعلم قواعد الإعراب المتكاملة، الأعداد العشرية المتطورة، وتاريخ الثورة والاستقلال الوطني الجزائري المجيد."
                                    )
                                }

                                Text(
                                    text = ageLevelInfo.first,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF047857)
                                )
                                Text(
                                    text = ageLevelInfo.second,
                                    fontSize = 11.sp,
                                    color = CharcoalText.copy(alpha = 0.85f),
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = PastelYellow.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().border(1.dp, AmberBorder, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("رمز معلم الصف / كود القسم (اختياري) 🏫:", fontWeight = FontWeight.Bold, color = CharcoalText)
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = enteredClassCode,
                                        onValueChange = { enteredClassCode = it },
                                        placeholder = { Text("مثلاً: ${AppConstants.DEFAULT_TEACHER_CODE}", fontSize = 12.sp) },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(0.7f)
                                            .height(52.dp),
                                        textStyle = MaterialTheme.typography.bodyMedium,
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = CharcoalText,
                                            unfocusedTextColor = CharcoalText,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            disabledContainerColor = Color.White,
                                            focusedBorderColor = Color(0xFF10B981),
                                            unfocusedBorderColor = Color(0xFFCBD5E1)
                                        )
                                    )
                                    Button(
                                        onClick = {
                                            if (enteredClassCode.trim().isNotEmpty()) {
                                                if (sharedClassCode.isEmpty()) {
                                                    sharedClassCode = enteredClassCode.trim().uppercase()
                                                    sharedClassName = "القسم الإبداعي للأستاذ 🎒"
                                                }
                                                showLinkSuccessDialog = true
                                                hasLinkedCodeSuccessfully = true
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
                                    ) {
                                        Text("ربط")
                                    }
                                }
                                if (hasLinkedCodeSuccessfully) {
                                    Text("✅ تم ربط حساب الطفل بقسم $sharedClassName بنجاح!", color = Color(0xFF047857), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { currentScreenMode = "role_select" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF1F5F9),
                                    contentColor = CharcoalText
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("رجوع", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            Button(
                                onClick = {
                                    if (childName.trim().isNotBlank()) currentScreenMode = "parent_survey"
                                    else Toast.makeText(context, "الرجاء إدخال اسم الطفل 🧒", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                            ) {
                                Text("استمرار للاستبيان", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Forward",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
                "parent_survey" -> {
                    // Filter questions for the active section (1, 2, 3, 4)
                    val activeQuestions = diagnosticQuestions.filter { q ->
                        when (currentSurveySection) {
                            1 -> q.id in 1..4
                            2 -> q.id in 5..8
                            3 -> q.id in 9..12
                            4 -> q.id in 13..15
                            else -> false
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Title header
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = {
                                    if (currentSurveySection > 1) {
                                        currentSurveySection--
                                    } else {
                                        currentScreenMode = "parent_profile"
                                    }
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.dp, SlateBorder, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = CharcoalText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "التقييم الاستكشافي الذكي 🧠",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = SoftTeal,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Right
                                )
                                Text(
                                    text = "خطوة بسيطة لتوجيه طفلك نحو النجاح ✨",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedSlate.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Right
                                )
                            }
                        }

                        // Beautiful Progress Indicator Stepper
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.8f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val sections = listOf(
                                    "الانتباه" to "🎯",
                                    "القراءة" to "🔤",
                                    "الكتابة" to "✏️",
                                    "الإدراك" to "🧩",
                                    "النتيجة" to "📊"
                                )
                                sections.forEachIndexed { index, (label, emoji) ->
                                    val secNum = index + 1
                                    val isActive = currentSurveySection == secNum
                                    val isCompleted = currentSurveySection > secNum
                                    
                                    val circleBgColor by animateColorAsState(
                                        targetValue = when {
                                            isActive -> SoftTeal
                                            isCompleted -> Color(0xFF10B981)
                                            else -> Color(0xFFF1F5F9)
                                        },
                                        label = "stepper_circle_bg"
                                    )
                                    
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable(enabled = secNum < currentSurveySection) {
                                                currentSurveySection = secNum
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(circleBgColor)
                                                .border(
                                                    width = if (isActive) 2.dp else 0.dp,
                                                    color = if (isActive) SoftTeal.copy(alpha = 0.3f) else Color.Transparent,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isCompleted) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Check,
                                                    contentDescription = "Done",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = emoji,
                                                    fontSize = 18.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isActive) SoftTeal else CharcoalText.copy(alpha = 0.5f)
                                        )
                                    }
                                    if (index < sections.size - 1) {
                                        val lineColor by animateColorAsState(
                                            targetValue = if (currentSurveySection > secNum) Color(0xFF10B981) else Color(0xFFE2E8F0),
                                            label = "stepper_line_color"
                                        )
                                        Box(
                                            modifier = Modifier
                                                .height(3.dp)
                                                .weight(0.4f)
                                                .background(lineColor, RoundedCornerShape(1.5.dp))
                                                .align(Alignment.CenterVertically)
                                        )
                                    }
                                }
                            }
                        }

                        if (currentSurveySection in 1..4) {
                            // Section Headline Card
                            val (sectionTitle, sectionGradient, sectionIcon) = when (currentSurveySection) {
                                1 -> Triple("الانتباه والتركيز 🎯", Brush.horizontalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))), "🎯")
                                2 -> Triple("القراءة والحروف 🔤", Brush.horizontalGradient(listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))), "🔤")
                                3 -> Triple("الكتابة والخط ✏️", Brush.horizontalGradient(listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0))), "✏️")
                                else -> Triple("الإدراك البصري 🧩", Brush.horizontalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))), "🧩")
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(sectionGradient)
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                                    .padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.6f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(sectionIcon, fontSize = 24.sp)
                                    }
                                    
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        modifier = Modifier.weight(1f).padding(end = 12.dp)
                                    ) {
                                        Text(
                                            text = sectionTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = CharcoalText,
                                            textAlign = TextAlign.Right
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "يرجى الإجابة بملاحظة موضوعية لسلوك طفلك",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = MutedSlate.copy(alpha = 0.8f),
                                            textAlign = TextAlign.Right
                                        )
                                    }
                                }
                            }

                            // Active Questions List
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                activeQuestions.forEach { question ->
                                    val isAnswered = surveyAnswers.containsKey(question.id)
                                    val selectedAnswer = surveyAnswers[question.id]
                                    
                                    val animatedCardBorderColor by animateColorAsState(
                                        targetValue = when (selectedAnswer) {
                                            "نعم" -> Color(0xFFEF4444)
                                            "أحيانًا" -> Color(0xFFD97706)
                                            "لا" -> Color(0xFF059669)
                                            else -> SlateBorder
                                        },
                                        label = "card_border_color_anim"
                                    )
                                    
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = BorderStroke(
                                            width = if (isAnswered) 2.dp else 1.dp,
                                            color = animatedCardBorderColor
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = if (isAnswered) 3.dp else 0.dp
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = question.text,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalText,
                                                textAlign = TextAlign.Right,
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            // Options selector Row ("نعم", "أحيانًا", "لا")
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                val answerOptions = listOf(
                                                    "نعم" to Color(0xFFFEE2E2) to Color(0xFFEF4444),       // Red alert
                                                    "أحيانًا" to Color(0xFFFEF3C7) to Color(0xFFD97706),    // Yellow warn
                                                    "لا" to Color(0xFFD1FAE5) to Color(0xFF059669)         // Green fine
                                                )

                                                answerOptions.forEach { optionPair ->
                                                    val (labelColors, borderColor) = optionPair
                                                    val (labelText, selectedBgColor) = labelColors
                                                    val isSelected = surveyAnswers[question.id] == labelText
                                                    
                                                    val animatedBg by animateColorAsState(
                                                        targetValue = if (isSelected) selectedBgColor else Color(0xFFF8FAFF),
                                                        label = "choice_bg_anim"
                                                    )
                                                    val animatedBorder by animateColorAsState(
                                                        targetValue = if (isSelected) borderColor else SlateBorder,
                                                        label = "choice_border_anim"
                                                    )
                                                    
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .height(52.dp)
                                                            .clip(RoundedCornerShape(14.dp))
                                                            .background(animatedBg)
                                                            .border(
                                                                width = if (isSelected) 2.dp else 1.dp,
                                                                color = animatedBorder,
                                                                shape = RoundedCornerShape(14.dp)
                                                            )
                                                            .clickable {
                                                                surveyAnswers[question.id] = labelText
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            if (isSelected) {
                                                                val iconEmoji = when (labelText) {
                                                                    "نعم" -> "😞"
                                                                    "أحيانًا" -> "🤔"
                                                                    else -> "😊"
                                                                }
                                                                Text(iconEmoji, fontSize = 14.sp, modifier = Modifier.padding(end = 4.dp))
                                                            }
                                                            Text(
                                                                text = labelText,
                                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                                color = if (isSelected) borderColor else CharcoalText,
                                                                fontSize = 14.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Bottom actions inside active questionnaire
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (currentSurveySection > 1) {
                                            currentSurveySection--
                                        } else {
                                            currentScreenMode = "parent_profile"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = CharcoalText
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp)
                                        .border(1.5.dp, SlateBorder, RoundedCornerShape(18.dp)),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                        contentDescription = "الرجوع", 
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("السابق", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Button(
                                    onClick = {
                                        // Check if all active questions are answered
                                        val unansweredCount = activeQuestions.count { !surveyAnswers.containsKey(it.id) }
                                        if (unansweredCount > 0) {
                                            Toast.makeText(context, "الرجاء الإجابة على جميع الأسئلة للمتابعة ✍️✨", Toast.LENGTH_SHORT).show()
                                        } else {
                                            currentSurveySection++
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SoftTeal,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(54.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                                ) {
                                    Text("المتابعة والتالي", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward, 
                                        contentDescription = "التالي", 
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        } else {
                            // Section 5: The Diagnostic Report Screen (النتيجة)
                            val adhdScore = (if (surveyAnswers[1] == "نعم") 1f else if (surveyAnswers[1] == "أحيانًا") 0.5f else 0f) +
                                            (if (surveyAnswers[2] == "نعم") 1f else if (surveyAnswers[2] == "أحيانًا") 0.5f else 0f) +
                                            (if (surveyAnswers[3] == "نعم") 1f else if (surveyAnswers[3] == "أحيانًا") 0.5f else 0f) +
                                            (if (surveyAnswers[4] == "نعم") 1f else if (surveyAnswers[4] == "أحيانًا") 0.5f else 0f)

                            val dyslexiaScore = (if (surveyAnswers[5] == "نعم") 1f else if (surveyAnswers[5] == "أحيانًا") 0.5f else 0f) +
                                                (if (surveyAnswers[6] == "نعم") 1f else if (surveyAnswers[6] == "أحيانًا") 0.5f else 0f) +
                                                (if (surveyAnswers[7] == "نعم") 1f else if (surveyAnswers[7] == "أحيانًا") 0.5f else 0f) +
                                                (if (surveyAnswers[8] == "نعم") 1f else if (surveyAnswers[8] == "أحيانًا") 0.5f else 0f)

                            val writingScore = (if (surveyAnswers[9] == "نعم") 1f else if (surveyAnswers[9] == "أحيانًا") 0.5f else 0f) +
                                               (if (surveyAnswers[10] == "نعم") 1f else if (surveyAnswers[10] == "أحيانًا") 0.5f else 0f) +
                                               (if (surveyAnswers[11] == "نعم") 1f else if (surveyAnswers[11] == "أحيانًا") 0.5f else 0f) +
                                               (if (surveyAnswers[12] == "نعم") 1f else if (surveyAnswers[12] == "أحيانًا") 0.5f else 0f)

                            val visualScore = (if (surveyAnswers[13] == "نعم") 1f else if (surveyAnswers[13] == "أحيانًا") 0.5f else 0f) +
                                              (if (surveyAnswers[14] == "نعم") 1f else if (surveyAnswers[14] == "أحيانًا") 0.5f else 0f) +
                                              (if (surveyAnswers[15] == "نعم") 1f else if (surveyAnswers[15] == "أحيانًا") 0.5f else 0f)
                            
                            val totalScore = adhdScore + dyslexiaScore + writingScore + visualScore
                            val isMostYes = totalScore >= 3.0f

                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.5.dp, SlateBorder),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    // Stunning Result Header with Gradient Background
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = if (isMostYes) listOf(Color(0xFFFEF2F2), Color(0xFFFEE2E2))
                                                             else listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5))
                                                )
                                            )
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "التقرير الاستكشافي المبدئي 📊",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isMostYes) Color(0xFF991B1B) else Color(0xFF065F46)
                                            )
                                            Text(
                                                text = "مبني على ذكاء المنصة التكيفي والتصنيفات الأربعة",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CharcoalText.copy(alpha = 0.6f)
                                            )
                                        }
                                    }

                                    // Pulse Ring Mascot Display
                                    Box(
                                        modifier = Modifier
                                            .size(84.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isMostYes) Color(0xFFFCA5A5).copy(alpha = 0.4f)
                                                else Color(0xFF86EFAC).copy(alpha = 0.4f)
                                            )
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .background(
                                                    if (isMostYes) Color(0xFFFEE2E2) else Color(0xFFD1FAE5)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = if (isMostYes) "🧠" else "🌟", fontSize = 42.sp)
                                        }
                                    }

                                    Text(
                                        text = if (isMostYes) {
                                            "تم تحديد بعض المجالات التي يفضل دعم البطل فيها بعناية إضافية ممتعة!"
                                        } else {
                                            "جاهزية طفلك ممتازة وسلوكياته التعليمية تبدو متوازنة للغاية بفضل الله!"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 26.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    // Divider line
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        thickness = 1.dp,
                                        color = SlateBorder.copy(alpha = 0.7f)
                                    )

                                    // Metric progress labels (Arabic layouts perfectly formatted)
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            text = "مؤشر الحاجة للدعم والتمكين الإضافي 📈",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = CharcoalText,
                                            modifier = Modifier.align(Alignment.End)
                                        )

                                        val progressItems = listOf(
                                            Triple("الانتباه والتركيز 🎯", adhdScore / 4.0f, Color(0xFFD97706)),
                                            Triple("القراءة ونطق الحروف 🔤", dyslexiaScore / 4.0f, Color(0xFF3B82F6)),
                                            Triple("المهارات الكتابية والخط ✏️", writingScore / 4.0f, Color(0xFFEC4899)),
                                            Triple("الإدراك البصري والترتيب 🧩", visualScore / 3.0f, Color(0xFF10B981))
                                        )

                                        progressItems.forEach { (title, percentage, mainColor) ->
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    val badgeText = when {
                                                        percentage == 0f -> "ممتاز جداً 🌟"
                                                        percentage <= 0.3f -> "متزن وآمن 👍"
                                                        percentage <= 0.6f -> "تحت الملاحظة 🔍"
                                                        else -> "بحاجة للدعم والعناية 💡"
                                                    }
                                                    
                                                    Text(
                                                        text = badgeText,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = mainColor,
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(mainColor.copy(alpha = 0.08f))
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                    
                                                    Text(
                                                        text = title,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = CharcoalText
                                                    )
                                                }
                                                
                                                // Beautiful custom track with glowing visual styling
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(10.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFFF1F5F9))
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxHeight()
                                                            .fillMaxWidth(fraction = percentage.coerceIn(0.01f, 1f))
                                                            .clip(CircleShape)
                                                            .background(
                                                                Brush.linearGradient(
                                                                    colors = listOf(mainColor, mainColor.copy(alpha = 0.7f))
                                                                )
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    // Guide Alert Box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFF0F9FF))
                                            .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(16.dp))
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.End,
                                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                                            ) {
                                                Text(
                                                    text = "💡 مواءمة ذكية فورية",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFF0369A1),
                                                    textAlign = TextAlign.Right
                                                )
                                                Text(
                                                    text = "سيقوم نظام الألعاب التفاعلية والأنشطة بتثقيف طفلك $childName والتركيز بصورة تكيفية وموجهة على المجالات التي تتطلب تمكيناً أكبر.",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF0F172A),
                                                    textAlign = TextAlign.Right,
                                                    lineHeight = 18.sp
                                                )
                                            }
                                            Text("🔮", fontSize = 24.sp, modifier = Modifier.padding(top = 2.dp))
                                        }
                                    }
                                }
                            }

                            // Bottom Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        surveyAnswers.clear()
                                        currentSurveySection = 1
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFEE2E2),
                                        contentColor = Color(0xFFEF4444)
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp)
                                        .border(1.5.dp, Color(0xFFFCA5A5), RoundedCornerShape(18.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh, 
                                        contentDescription = "إعادة الاختبار",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إعادة 🔄", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                Button(
                                    onClick = {
                                        // Determine difficulty type based on scores as active metrics
                                        val resolvedDifficulty = if (totalScore < 3.0f) {
                                            "none"
                                        } else {
                                            val maxValObj = listOf(
                                                "adhd" to adhdScore,
                                                "dyslexia" to dyslexiaScore,
                                                "slow_learning" to (writingScore + visualScore)
                                            ).maxByOrNull { it.second }
                                            
                                            maxValObj?.first ?: "none"
                                        }
                                        
                                        onRegisterParentEmail(
                                            parentNameInput.trim(),
                                            parentPhoneInput.trim(),
                                            parentEmailInput.trim(),
                                            parentPasswordInput.trim(),
                                            childName.trim(),
                                            childAge,
                                            selectedLevel,
                                            resolvedDifficulty,
                                            selectedAvatar,
                                            {
                                                Toast.makeText(context, "🎉 تم تفعيل الحساب الموحد بالتزامن السحابي بنجاح!", Toast.LENGTH_LONG).show()
                                            },
                                            { errMsg ->
                                                Toast.makeText(context, "⚠️ تنبيه: تم حفظ حساب طفلك محلياً. لم تكتمل المزامنة بسبب: $errMsg", Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF10B981),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier
                                        .weight(1.6f)
                                        .height(54.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                                ) {
                                    Text("حفظ والبدء 🚀", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }
                        }

                        // THE_END_OF_NEW_SURVEY_WIZARD_MARKER
                        /*
                            val optionsList = listOf(
                                Triple("Dyslexia", "عسر القراءة\n(Dyslexia)", optDyslexia),
                                Triple("ADHD", "تشتت وفرط حركة\n(ADHD)", optAdhd),
                                Triple("SlowLearning", "بطء تعلم وحاجة\nلمساند فوري", optSlowLearning)
                            )
                            optionsList.chunked(2).forEach { rowItems ->
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                    rowItems.forEach { (type, label, isSelected) ->
                                        val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1.0f, label = "scale")
                                        val strokeBgColor by animateColorAsState(targetValue = if (isSelected) Color(0xFF10B981) else Color(0xFFCBD5E1), label = "strokeBgColor")
                                        val strokeWidth by animateDpAsState(targetValue = if (isSelected) 2.5.dp else 1.5.dp, label = "strokeWidth")
                                        val cardBgColor by animateColorAsState(targetValue = if (isSelected) Color(0xFFD1FAE5) else Color(0xFFF1F5F9), label = "cardBgColor")

                                        Card(
                                            onClick = {
                                                when (type) {
                                                    "Dyslexia" -> optDyslexia = !optDyslexia
                                                    "ADHD" -> optAdhd = !optAdhd
                                                    "SlowLearning" -> optSlowLearning = !optSlowLearning
                                                }
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = cardBgColor
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(140.dp)
                                                .graphicsLayer {
                                                    scaleX = scale
                                                    scaleY = scale
                                                }
                                                .border(
                                                    strokeWidth,
                                                    strokeBgColor,
                                                    RoundedCornerShape(20.dp)
                                                ),
                                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 10.dp else 4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize().padding(12.dp)
                                            ) {
                                                val emoji = when (type) {
                                                    "Dyslexia" -> "📚"
                                                    "ADHD" -> "🧠"
                                                    else -> "⏳"
                                                }

                                                Text(
                                                    text = emoji,
                                                    fontSize = 90.sp,
                                                    modifier = Modifier
                                                        .align(Alignment.BottomStart)
                                                        .graphicsLayer(alpha = 0.28f)
                                                        .offset(x = (-12).dp, y = 12.dp)
                                                )

                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.SpaceBetween,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(38.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                    if (isSelected) SoftTeal.copy(alpha = 0.2f)
                                                                    else CharcoalText.copy(alpha = 0.05f)
                                                                ),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(emoji, fontSize = 20.sp)
                                                        }

                                                        if (isSelected) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(24.dp)
                                                                    .clip(CircleShape)
                                                                    .background(SoftTeal),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Rounded.Check,
                                                                    contentDescription = "Selected",
                                                                    tint = Color.White,
                                                                    modifier = Modifier.size(14.dp)
                                                                )
                                                            }
                                                        } else {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(24.dp)
                                                                    .clip(CircleShape)
                                                                    .border(1.5.dp, SlateBorder, CircleShape)
                                                            )
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(4.dp))

                                                    Text(
                                                        text = label,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = CharcoalText,
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 2,
                                                        lineHeight = 18.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { currentScreenMode = "parent_profile" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF0FDF4),
                                    contentColor = Color(0xFF15803D)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .border(1.5.dp, Color(0xFF86EFAC), RoundedCornerShape(16.dp)),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تعديل البيانات", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Button(
                                onClick = {
                                    val resolvedDifficulty = when {
                                        optDyslexia -> "dyslexia"
                                        optAdhd -> "adhd"
                                        optSlowLearning -> "slow_learning"
                                        else -> "none"
                                    }
                                    onRegisterParentEmail(
                                        parentNameInput.trim(),
                                        parentPhoneInput.trim(),
                                        parentEmailInput.trim(),
                                        parentPasswordInput.trim(),
                                        childName.trim(),
                                        childAge,
                                        selectedLevel,
                                        resolvedDifficulty,
                                        selectedAvatar,
                                        {
                                            Toast.makeText(context, "🎉 تم تفعيل حسابك وحساب بطلك سحابياً!", Toast.LENGTH_SHORT).show()
                                        },
                                        { errMsg ->
                                            Toast.makeText(context, "⚠️ خطأ في التسجيل السحابي: $errMsg\nتم الحفظ محلياً.", Toast.LENGTH_LONG).show()
                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                            ) {
                                Text("حفظ والبدء 🚀", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                        */
                    }
                }
                "teacher_register" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { currentScreenMode = "auth_choice" }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رجوع")
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                "بوابة المعلم",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TeacherStepsProgress(currentStep = 0, onStepClick = { step ->
                            currentScreenMode = when(step) {
                                1 -> "teacher_create_class"
                                2 -> "teacher_add_students"
                                else -> "teacher_register"
                            }
                        })

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PastelMint.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("💡", fontSize = 28.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("مرحباً بك يا مربي وعون الأجيال! ✨", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CharcoalText)
                                    Text("الملف التعريفي والمهني يسمح بتلقي تقارير الصعوبات وسيناريوهات اللعب التشخيصية تلقائياً.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = CharcoalText)
                                }
                            }
                        }

                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, SlateBorder)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("المعلومات المهنية الأساسية 📋", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CharcoalText)
                                OutlinedTextField(
                                    value = teacherName,
                                    onValueChange = { teacherName = it },
                                    label = { Text("الاسم الكامل للأستاذ") },
                                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = "Name", tint = Color(0xFF10B981)) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = CharcoalText,
                                        unfocusedTextColor = CharcoalText,
                                        focusedBorderColor = Color(0xFF10B981),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedLabelColor = Color(0xFF10B981)
                                    )
                                )
                                OutlinedTextField(
                                    value = teacherEmail,
                                    onValueChange = { teacherEmail = it },
                                    label = { Text("البريد الإلكتروني المهني") },
                                    leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = "Email", tint = Color(0xFF10B981)) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = CharcoalText,
                                        unfocusedTextColor = CharcoalText,
                                        focusedBorderColor = Color(0xFF10B981),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedLabelColor = Color(0xFF10B981)
                                    )
                                )
                                OutlinedTextField(
                                    value = teacherPasswordInput,
                                    onValueChange = { teacherPasswordInput = it },
                                    label = { Text("كلمة المرور السحابية * (6 رموز أو أكثر)") },
                                    leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = "Password", tint = Color(0xFF10B981)) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                                    ),
                                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = CharcoalText,
                                        unfocusedTextColor = CharcoalText,
                                        focusedBorderColor = Color(0xFF10B981),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedLabelColor = Color(0xFF10B981)
                                    )
                                )
                                OutlinedTextField(
                                    value = teacherPhone,
                                    onValueChange = { teacherPhone = it },
                                    label = { Text("رقم الهاتف") },
                                    leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = "Phone", tint = Color(0xFF10B981)) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = CharcoalText,
                                        unfocusedTextColor = CharcoalText,
                                        focusedBorderColor = Color(0xFF10B981),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedLabelColor = Color(0xFF10B981)
                                    )
                                )
                                OutlinedTextField(
                                    value = teacherSchool,
                                    onValueChange = { teacherSchool = it },
                                    label = { Text("المدرسة") },
                                    leadingIcon = { Icon(Icons.Rounded.Home, contentDescription = "School", tint = Color(0xFF10B981)) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = CharcoalText,
                                        unfocusedTextColor = CharcoalText,
                                        focusedBorderColor = Color(0xFF10B981),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedLabelColor = Color(0xFF10B981)
                                     )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Navigation Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { currentScreenMode = "auth_choice" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF0FDF4),
                                    contentColor = Color(0xFF15803D)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .border(1.5.dp, Color(0xFF86EFAC), RoundedCornerShape(16.dp)),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("رجوع", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Button(
                                onClick = {
                                    val trimmedName = teacherName.trim()
                                    val trimmedEmail = teacherEmail.trim()
                                    val trimmedPassword = teacherPasswordInput.trim()
                                    val trimmedPhone = teacherPhone.trim()
                                    val trimmedSchool = teacherSchool.trim()
                                    
                                    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
                                    
                                    if (trimmedName.isEmpty() || trimmedEmail.isEmpty() || trimmedPassword.isEmpty() || trimmedPhone.isEmpty() || trimmedSchool.isEmpty()) {
                                        Toast.makeText(context, "⚠️ الرجاء إدخال كافة البيانات المطلوبة والرمز السري لحسابك.", Toast.LENGTH_SHORT).show()
                                    } else if (!trimmedEmail.matches(emailRegex)) {
                                        Toast.makeText(context, "⚠️ الرجاء إدخال بريد إلكتروني صحيح وصالح.", Toast.LENGTH_SHORT).show()
                                    } else if (trimmedPassword.length < 6) {
                                        Toast.makeText(context, "⚠️ يجب أن تكون كلمة المرور 6 رموز أو أكثر لحماية حسابك التعليمي.", Toast.LENGTH_SHORT).show()
                                    } else if (trimmedPhone.length < 8) {
                                        Toast.makeText(context, "⚠️ الرجاء إدخال رقم هاتف صحيح (8 أرقام على الأقل).", Toast.LENGTH_SHORT).show()
                                    } else {
                                        onRegisterTeacherEmail(
                                            trimmedName,
                                            trimmedEmail,
                                            trimmedPassword,
                                            trimmedSchool,
                                            teacherExperience,
                                            teacherSpecialization,
                                            classroomNameInput,
                                            {
                                                currentScreenMode = "teacher_create_class"
                                            },
                                            { errorMsg ->
                                                Toast.makeText(context, "⚠️ فشل التسجيل السحابي: $errorMsg", Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f).height(52.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                            ) {
                                Text("التالي", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Forward",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
                "teacher_create_class" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { currentScreenMode = "teacher_register" }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رجوع")
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                "تأسيس الصف",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TeacherStepsProgress(currentStep = 1, onStepClick = { step ->
                            currentScreenMode = when(step) {
                                0 -> "teacher_register"
                                2 -> "teacher_add_students"
                                else -> "teacher_create_class"
                            }
                        })

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PastelBlue.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("🏫", fontSize = 28.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "إنشاء القسم الفصلي السحري",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )
                                    Text(
                                        "بمجرد توفير اسم الفصل، سنولد رمز التحاق ذكي لربطه بأولياء الموالين تلقائياً.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = CharcoalText
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, SlateBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text("تفاصيل مجموعة الفصل ✏️", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CharcoalText)

                                OutlinedTextField(
                                    value = classroomNameInput,
                                    onValueChange = { classroomNameInput = it },
                                    label = { Text("اسم القسم والصف الدراسي") },
                                    placeholder = { Text("مثال: الصف الثاني - أمل وبناء") },
                                    leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = "Class name", tint = Color(0xFF10B981)) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = CharcoalText,
                                        unfocusedTextColor = CharcoalText,
                                        focusedBorderColor = Color(0xFF10B981),
                                        focusedLabelColor = Color(0xFF10B981)
                                    )
                                )

                                Text("المرحلة الدراسية لطلابك:", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 14.sp)
                                var selectedGrade by remember { mutableStateOf("الصف الثاني الابتدائي 📚") }
                                val grades = listOf("الأول الابتدائي 🎓", "الثاني الابتدائي 📚", "الثالث الابتدائي 🏫")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    grades.forEach { g ->
                                        val isSel = selectedGrade == g
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSel) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF8FAFF))
                                                .border(
                                                    width = if (isSel) 2.dp else 1.dp,
                                                    color = if (isSel) Color(0xFF10B981) else SlateBorder,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { selectedGrade = g }
                                                .padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = g.substringBefore(" "),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) Color(0xFF047857) else CharcoalText
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (classroomNameInput.trim().isNotEmpty()) {
                                    sharedClassCode = AppConstants.generateDynamicClassroomCode()
                                    sharedClassName = classroomNameInput.trim()
                                    hasClassroomBeenCreated = true
                                    Toast.makeText(context, "تم إنشاء الصف وتوليد الكود سحرياً! ✨", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "الرجاء إدخال اسم الصف أولاً", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (hasClassroomBeenCreated) Color(0xFF047857) else Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                if (hasClassroomBeenCreated) "تحديث الرمز والكود لصف جديد 🪄" else "توليد الرمز والكود السحري للصفي 🪄",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (hasClassroomBeenCreated) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PastelMint.copy(alpha = 0.5f)),
                                border = BorderStroke(2.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("رمز الانضمام السريع للطلاب والأولياء:", fontWeight = FontWeight.Bold, color = CharcoalText)

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.White)
                                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                            .padding(horizontal = 24.dp, vertical = 12.dp)
                                    ) {
                                        Text(
                                            sharedClassCode,
                                            fontSize = 44.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF047857),
                                            letterSpacing = 2.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(sharedClassCode.ifBlank { AppConstants.DEFAULT_TEACHER_CODE }))
                                            Toast.makeText(context, "تم نسخ رمز الصف بنجاح! 📋", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        border = BorderStroke(1.dp, Color(0xFF10B981)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Copy", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("نسخ الرمز الذكي 📋", color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { currentScreenMode = "teacher_register" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF0FDF4),
                                        contentColor = Color(0xFF15803D)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .weight(0.4f)
                                        .height(54.dp)
                                        .border(1.5.dp, Color(0xFF86EFAC), RoundedCornerShape(16.dp)),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("رجوع", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Button(
                                    onClick = {
                                        if (hasClassroomBeenCreated && classroomNameInput.trim().isNotBlank()) {
                                            currentScreenMode = "teacher_add_students"
                                        } else {
                                            Toast.makeText(context, "⚠️ الرجاء تحديد تفاصيل الفصل وتوليد الكود السحري أولاً.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF10B981),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.weight(0.6f).height(54.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                                ) {
                                    Text("دفتر الطلاب", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Forward",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                "teacher_add_students" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { currentScreenMode = "teacher_create_class" }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رجوع")
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                "دفتر الفصل",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TeacherStepsProgress(currentStep = 2, onStepClick = { step ->
                            currentScreenMode = when(step) {
                                0 -> "teacher_register"
                                1 -> "teacher_create_class"
                                else -> "teacher_add_students"
                            }
                        })

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PastelPeach.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("🎓", fontSize = 28.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "دفتر تحضير وإدارة طلاب الصف",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )
                                    Text(
                                        "أضف طلابك لتسجيل نتائج التقييمات وتشخيص حالات صعوبات التعلم لديهم ومتابعة تقدمهم.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = CharcoalText
                                    )
                                }
                            }
                        }

                        TabRow(
                            selectedTabIndex = teacherActiveTab,
                            containerColor = CreamBackground,
                            contentColor = Color(0xFF10B981),
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[teacherActiveTab]),
                                    color = Color(0xFF10B981)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                        ) {
                            Tab(
                                selected = teacherActiveTab == 0,
                                onClick = { teacherActiveTab = 0 },
                                text = { Text("قائمة الرزمة ✍️", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                            )
                            Tab(
                                selected = teacherActiveTab == 1,
                                onClick = { teacherActiveTab = 1 },
                                text = { Text("دعوة الولي 📨", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                            )
                            Tab(
                                selected = teacherActiveTab == 2,
                                onClick = { teacherActiveTab = 2 },
                                text = { Text("أدوات المعلم 🪄✨", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                            )
                        }

                        if (teacherActiveTab == 0) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, SlateBorder)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("أضف بطلاً جديداً للفصل:", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 13.sp)
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            OutlinedTextField(
                                                value = manualStudentNameInput,
                                                onValueChange = { manualStudentNameInput = it },
                                                placeholder = { Text("مثال: عبد الرحمن بن علي") },
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.weight(1f),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedTextColor = CharcoalText,
                                                    unfocusedTextColor = CharcoalText,
                                                    focusedBorderColor = Color(0xFF10B981)
                                                )
                                            )
                                            Button(
                                                onClick = {
                                                    if (manualStudentNameInput.trim().isNotEmpty()) {
                                                        sharedStudentsList.add(manualStudentNameInput.trim())
                                                        manualStudentNameInput = ""
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.height(52.dp)
                                            ) {
                                                Text("أضف ➕")
                                            }
                                        }
                                    }
                                }

                                if (sharedStudentsList.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Face,
                                                contentDescription = "Empty",
                                                tint = MutedSlate.copy(alpha = 0.5f),
                                                modifier = Modifier.size(56.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                "لا يوجد طلاب مسجلون بعد.",
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalText,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                "أضف أسماء الطلاب الذين يعانون من صعوبات في القراءة لنشخصهم سوياً.",
                                                color = MutedSlate,
                                                fontSize = 11.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 24.dp)
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(CreamBackground)
                                            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(sharedStudentsList) { std ->
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                                border = BorderStroke(1.dp, SlateBorder),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(32.dp)
                                                                .clip(CircleShape)
                                                                .background(PastelMint),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text("🎓", fontSize = 16.sp)
                                                        }
                                                        Text(std, fontWeight = FontWeight.Bold, color = CharcoalText)
                                                    }
                                                    IconButton(onClick = { sharedStudentsList.remove(std) }) {
                                                        Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (teacherActiveTab == 1) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    colors = CardDefaults.cardColors(containerColor = CreamBackground),
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(1.dp, SlateBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text("شارك الكود السحري لربط حساب ولي الأمر بالأستاذ تلقائياً:", textAlign = TextAlign.Center, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CharcoalText)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color.White)
                                                .border(2.dp, Color(0xFF10B981), RoundedCornerShape(16.dp))
                                                .padding(horizontal = 24.dp, vertical = 10.dp)
                                        ) {
                                            Text(sharedClassCode.ifBlank { AppConstants.DEFAULT_TEACHER_CODE }, fontSize = 40.sp, fontWeight = FontWeight.Black, color = Color(0xFF047857))
                                        }

                                        Button(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(sharedClassCode.ifBlank { AppConstants.DEFAULT_TEACHER_CODE }))
                                                Toast.makeText(context, "تم نسخ الكود الخاص بالصف! 📋", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                            border = BorderStroke(1.dp, Color(0xFF10B981)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("نسخ الكود 📋", color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                val codeToShare = sharedClassCode.ifBlank { AppConstants.DEFAULT_TEACHER_CODE }
                                                 val shareText = "أهلاً بك! انضم إلى صفي التعليمي في تطبيق نشيط باستخدام الرمز السحري: $codeToShare 🎓✨\nقم بتحميل تطبيق نشيط لمتابعة مهارات طفلك الإدراكية واللغوية وتخطيط دعمه الفردي بنجاح."
                                                 try {
                                                     val sendIntent = android.content.Intent().apply {
                                                         action = android.content.Intent.ACTION_SEND
                                                         putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                                         type = "text/plain"
                                                     }
                                                     val shareIntent = android.content.Intent.createChooser(sendIntent, "مشاركة رمز الصف عبر:")
                                                     context.startActivity(shareIntent)
                                                 } catch (e: Exception) {
                                                     Toast.makeText(context, "لم نتمكن من فتح قائمة المشاركة ⚠️", Toast.LENGTH_SHORT).show()
                                                 }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("مشاركة الكود عبر التطبيقات 💬", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else {
                            TeacherMagicalToolsWorkspace(
                                modifier = Modifier.weight(1f),
                                classroomName = classroomNameInput.ifBlank { "الصف الثاني - أمل وبناء" }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { currentScreenMode = "teacher_create_class" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF0FDF4),
                                    contentColor = Color(0xFF15803D)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(0.4f)
                                    .height(54.dp)
                                    .border(1.5.dp, Color(0xFF86EFAC), RoundedCornerShape(16.dp)),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رجوع", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Button(
                                onClick = { currentScreenMode = "role_select" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(0.6f).height(54.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                            ) {
                                Text("إتمام وحفظ 🚀", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
            } // closes Column
            } // closes Box

        if (showLinkSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showLinkSuccessDialog = false },
                title = { Text("تم ربط القسم بنجاح! 🎉", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold) },
                text = { Text("الاسم: $sharedClassName\nالرمز: $sharedClassCode\n\nتم ربط حساب بطلك الصغير بقسم الأستاذ بنجاح! 🎓✨", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                confirmButton = {
                    Button(onClick = { showLinkSuccessDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)) {
                        Text("موافق", color = Color.White)
                    }
                }
            )
        }
    }
}

// ==========================================
// TEACHER MAGICAL WORKSPACE & DIAGNOSTICS INTERPRETATIONS
// ==========================================
@Composable
fun TeacherMagicalToolsWorkspace(
    modifier: Modifier = Modifier,
    classroomName: String
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    
    var selectedStudentIndex by remember { mutableStateOf(0) }
    val studentsData = remember {
        listOf(
            StudentDiagnosticInfo(
                name = "عبد الرحمن سليم",
                avatar = "🧒",
                age = 7,
                diagnosisType = "عسر القراءة الفونولوجي (Phonological)",
                phonicScore = 0.45f,
                attentionScore = 0.85f,
                fluencyScore = 0.50f,
                notes = "يواجه صعوبة بالغة في دمج الحروف المفردة لتكوين كلمة ثلاثية مشكولة. يُوصى بالتركيز على التقطيع الصوتي لمخارج الأحرف في الأسبوع الأول."
            ),
            StudentDiagnosticInfo(
                name = "فاطمة الزهراء جابر",
                avatar = "👧",
                age = 8,
                diagnosisType = "انعكاس الحروف بصرياً (Visual Reversal)",
                phonicScore = 0.80f,
                attentionScore = 0.70f,
                fluencyScore = 0.85f,
                notes = "تخلط بين كتابة الحروف المتشابهة في اتجاه الرسم مثل (ب/ت/ث) وقراءة حرف المناداة (ن/ي). تفضل اللمس الحسي وتطبيق تحدي مسار الذاكرة للتمييز التفاعلي."
            ),
            StudentDiagnosticInfo(
                name = "ياسين المرابط",
                avatar = "👦",
                age = 7,
                diagnosisType = "صعوبات التتبع البصري وتشتت الانتباه",
                phonicScore = 0.60f,
                attentionScore = 0.35f,
                fluencyScore = 0.40f,
                notes = "يتجاوز صفوف القراءة بسرعة نتيجة الاندفاعية ولا يركز في الحركات القصيرة (ـَـُـِ). يُوصى باستخدام ألعاب القراءة السريعة والأصوات الداعمة الدافئة."
            ),
            StudentDiagnosticInfo(
                name = "نور الهدى الشاوي",
                avatar = "👩",
                age = 8,
                diagnosisType = "صعوبات طلاقة ومخارج الحروف",
                phonicScore = 0.85f,
                attentionScore = 0.90f,
                fluencyScore = 0.60f,
                notes = "تستوعب النص بشكل ممتاز، لكنها تبطئ أثناء نطق الكلمات المدمجة وتتجنب مخارج الأحرف الحلقية (خ/ح). تحتاج لتشجيع مستمر من الولي عبر 'تحدي الحكواتي الصغير'."
            )
        )
    }
    
    val currentStudent = studentsData[selectedStudentIndex]
    
    var selectedCardLevel by remember { mutableStateOf(0) } // 0: Letters, 1: Words, 2: Sentences
    var cardIndex by remember { mutableStateOf(0) }
    var cardThemeKey by remember { mutableStateOf("desert") } // desert, deep_sea, cosmic
    
    val cardDataLetters = listOf(
        CardItem("بَ / تَ / ثَ", "التمييز بين النبرات ونقاط الحروف المتطابقة", "علاقة بصرية حسية 🔍"),
        CardItem("جَ / حَ / خَ", "التمييز الفونولوجي لمخارج الجوف والحلق", "الوعي السمعي والموقع 🎙️"),
        CardItem("سَ / شَ / صَ", "تمارين الحفز لمخارج التفشي والهمس", "التحكم بالنبرة والصفير 🎶"),
        CardItem("رَ / زَ / دَ", "التحكم في تكرار الحرف وموقع ارتكاز اللسان", "تأهيل مخارج المد 📈")
    )
    val cardDataWords = listOf(
        CardItem("دَ - رَ - سَ (دَرَسَ)", "تجميع المقاطع الفونولوجية الثلاثية المنفصلة", "تدريب الفهم السريع 📖"),
        CardItem("كَ - تَ - بَ (كَتَبَ)", "حبس النفس ونطق الحروف المتصلة بحركاتها المتطابقة", "الكتابة الذهنية والرسم ✍️"),
        CardItem("فَ - تِ - حَ (فَتِحَ)", "التحول من الفتح إلى الكسر بمرونة فائقة", "ليونة مخارج الوصل 🚀"),
        CardItem("شَ - رِ - بَ (شَرِبَ)", "ربط الشين المعجمة الحركية بالباء السفلية", "القراءة التكرارية المرحة 🏆")
    )
    val cardDataSentences = listOf(
        CardItem("نَشِيطٌ يَقْرَأُ بِشَغَفٍ 🎉", "تحفيز نبرة القراءة التعبيرية السلسة والثقة بالنفس", "تنمية الثقة الكلية 🥇"),
        CardItem("ذَهَبَ البَطَلُ إِلَى المَدْرَسَةِ 🏫", "قياس مهارة الوصل بين الكلمات الخمسة بحركاتها التامة", "تدريب التنفس المتكامل ✨"),
        CardItem("الطفلُ يُشَاهِدُ نَجْماً لَامِعاً 🌌", "التدريب على المد الطويل المتصل وتثبيت الوعي الصوتي", "التتبع البصري الشمولي 🔭")
    )
    
    val activeList = when (selectedCardLevel) {
        0 -> cardDataLetters
        1 -> cardDataWords
        else -> cardDataSentences
    }
    
    val currentCard = activeList[cardIndex % activeList.size]
    
    val currentCardTheme = when (cardThemeKey) {
        "deep_sea" -> CardThemeProps(
            bgGradient = Brush.verticalGradient(listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD))),
            borderColor = Color(0xFF0284C7),
            textColor = Color(0xFF0369A1),
            accentTextColor = Color(0xFF075985),
            emoji = "🐳"
        )
        "cosmic" -> CardThemeProps(
            bgGradient = Brush.verticalGradient(listOf(Color(0xFFF3E8FF), Color(0xFFE9D5FF))),
            borderColor = Color(0xFF9333EA),
            textColor = Color(0xFF6B21A8),
            accentTextColor = Color(0xFF581C87),
            emoji = "🚀"
        )
        else -> CardThemeProps(
            bgGradient = Brush.verticalGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))),
            borderColor = Color(0xFFD97706),
            textColor = Color(0xFF92400E),
            accentTextColor = Color(0xFF78350F),
            emoji = "🏜️"
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, SlateBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFECFDF5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📊", fontSize = 18.sp)
                    }
                    Column {
                        Text(
                            text = "سجل وبيانات تشخيص الفصل 🔬",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = "قسم الأستاذ: $classroomName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                    }
                }
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(studentsData.size) { index ->
                        val stu = studentsData[index]
                        val isSelected = selectedStudentIndex == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Color(0xFF10B981).copy(alpha = 0.12f) else Color(0xFFF8FAFF))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF10B981) else SlateBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedStudentIndex = index }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(stu.avatar, fontSize = 16.sp)
                                Text(
                                    text = stu.name,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF047857) else CharcoalText,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                
                Divider(color = SlateBorder.copy(alpha = 0.6f))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFF), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الطفل: ${currentStudent.name} (عمر ${currentStudent.age} سنوات)",
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText,
                            fontSize = 13.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEEF2FF))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = currentStudent.diagnosisType,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4F46E5)
                            )
                        }
                    }
                    
                    StudentStatIndicator(
                        title = "الوعي الصوتي والفونولوجي 🎙️",
                        score = currentStudent.phonicScore,
                        color = Color(0xFF10B981)
                    )
                    StudentStatIndicator(
                        title = "مستوى الانتباه والتتبع البصري 👀",
                        score = currentStudent.attentionScore,
                        color = Color(0xFF3B82F6)
                    )
                    StudentStatIndicator(
                        title = "طلاقة وسرعة القراءة السمعية ⚡",
                        score = currentStudent.fluencyScore,
                        color = Color(0xFFF59E0B)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "📝 توصية المعلم العيادية:\n${currentStudent.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalText,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, SlateBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFF7ED)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🪄", fontSize = 18.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مولد مواد وبطاقات الدعم التفاعلية 📇",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = "ابتكر بطاقات تشكيل وقراءة بمؤثرات ودعم بصري ملون",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                    }
                }
                
                val options = listOf("أحرف متشابهة 🔠", "كلمات مشكولة 📖", "جمل طلاقة 🚀")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    options.forEachIndexed { idx, op ->
                        val isSelected = selectedCardLevel == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable {
                                    selectedCardLevel = idx
                                    cardIndex = 0
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = op,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF10B981) else Color(0xFF64748B)
                            )
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("قالب الفن اللوني:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CharcoalText)
                    Spacer(modifier = Modifier.weight(1f))
                    
                    listOf("desert" to "🏜️ رملي", "deep_sea" to "🌊 مائي", "cosmic" to "🌌 فضائي").forEach { th ->
                        val isThSel = cardThemeKey == th.first
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isThSel) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF8FAFF))
                                .border(
                                    1.dp,
                                    if (isThSel) Color(0xFF10B981) else SlateBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { cardThemeKey = th.first }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(th.second, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(currentCardTheme.bgGradient)
                        .border(3.dp, currentCardTheme.borderColor, RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "بطاقة دعم المستوى:  ${options[selectedCardLevel].substringBefore(" ")}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentCardTheme.textColor
                        )
                        Text(currentCardTheme.emoji, fontSize = 20.sp)
                    }
                    
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentCard.title,
                            fontSize = if (selectedCardLevel == 2) 22.sp else 38.sp,
                            fontWeight = FontWeight.Black,
                            color = currentCardTheme.textColor,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentCard.subtitle,
                            fontSize = 10.sp,
                            color = currentCardTheme.accentTextColor,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = currentCard.badge,
                            fontSize = 9.sp,
                            color = currentCardTheme.textColor,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (cardIndex > 0) cardIndex-- else cardIndex = activeList.size - 1
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Text("السابق ➡️", color = CharcoalText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = {
                            var speechText = "نطق موجه لمستوى "
                            speechText += when (selectedCardLevel) {
                                0 -> "الأحرف: ${currentCard.title}"
                                1 -> "الكلمات: ${currentCard.title}"
                                else -> "الجملة الطليقة: ${currentCard.title}"
                            }
                            Toast.makeText(context, "🎙️ نطق توجيهي مساند: $speechText", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.4f)
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = "Play sound", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نطق مساند 🗣️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = { cardIndex++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Text("⬅️ التالي", color = CharcoalText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, SlateBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEEF2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✉️", fontSize = 18.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مولد الخطط الفردية (IEP) وربط العائلات 📨",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = "أرسل برنامج أسبوعي وموجّه ألعاب مقترحة لولي الأمر",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFF), RoundedCornerShape(16.dp))
                        .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "📜 الخطة التربوية الفردية المقترحة للبطل: ${currentStudent.name}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B),
                        fontSize = 12.sp
                    )
                    
                    Text(
                        text = "🎯 الهدف العام: التغلب على ${currentStudent.diagnosisType} لتمكينه من طلاقة قراءة ممتازة تكيُّفية.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CharcoalText
                    )
                    
                    Text(
                        text = "📅 الجدول التدريبي اليومي بالتطبيق المساند:\n" +
                            "• الأحد والاثنين: لعب لعبة 'سفينة الحروف' لتجميع الحركات المقترحة.\n" +
                            "• الثلاثاء والأربعاء: تمارين تركيب الكلمات الثلاثية ببطاقات التفتيت الصوتي.\n" +
                            "• الخميس والجمعة: قراءة كتاب القصة التفاعلية بصالون القراءة وبنفس مريح.",
                        fontSize = 11.sp,
                        color = CharcoalText,
                        lineHeight = 16.sp
                    )
                }
                
                Button(
                    onClick = {
                        val whatsappMessage = """
                            🎓 خطة الدعم التربوي الفردي الفورية للبطل: ${currentStudent.name} ✨
                            
                            مرحباً يا ولي أمر بطلنا المميز، يسعدنا تواصل الأستاذ معك عبر تطبيق نشيط لمعالجة صعوبة: `${currentStudent.diagnosisType}` بنجاح وتأسيس ممتاز.
                            
                            🎯 الهدف الأسبوعي:
                            تمكين بطلنا من تجاوز الحواجز الفونولوجية البصرية عبر اللعب والعلاج التفاعلي اللين.
                            
                            📅 خطة التدخل المقترحة للبيت:
                            - الأحد والاثنين: لعب "تحدي الذاكرة البصرية وبناء الجمل" لزيادة التتبع البصري.
                            - الثلاثاء والأربعاء: اللعب بلعبة "بناء الجمل السحرية" بالتطبيق لتوطيد تركيب الكلمة.
                            - الخميس والجمعة: تفعيل ميزة ميكروفون نطق الحروف لمتابعة تقدم مخارجه الصوتية.
                            
                            تطبيق نشيط • رعاية إدراكية ذكية متكاملة 🧒🦖💡
                        """.trimIndent()
                        
                        try {
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, whatsappMessage)
                                type = "text/plain"
                            }
                            val shareIntent = android.content.Intent.createChooser(sendIntent, "إرسال خطة الدعم الفردية عبر:")
                            context.startActivity(shareIntent)
                        } catch (e: Exception) {
                            clipboardManager.setText(AnnotatedString(whatsappMessage))
                            Toast.makeText(context, "تم نسخ الخطة التربوية كاملة لتبويب الحافظة! 📋", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مشاركة خطة الدعم مع أسرة ${currentStudent.name} 💬✨",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Sub-components & classes used by workspace
data class StudentDiagnosticInfo(
    val name: String,
    val avatar: String,
    val age: Int,
    val diagnosisType: String,
    val phonicScore: Float,
    val attentionScore: Float,
    val fluencyScore: Float,
    val notes: String
)

data class CardItem(
    val title: String,
    val subtitle: String,
    val badge: String
)

data class CardThemeProps(
    val bgGradient: Brush,
    val borderColor: Color,
    val textColor: Color,
    val accentTextColor: Color,
    val emoji: String
)

@Composable
fun StudentStatIndicator(
    title: String,
    score: Float,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
            Text("${(score * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2E8F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(score)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
