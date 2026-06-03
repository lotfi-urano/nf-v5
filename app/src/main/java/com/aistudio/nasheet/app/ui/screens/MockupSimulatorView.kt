package com.aistudio.nasheet.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.nasheet.app.R
import com.aistudio.nasheet.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockupSimulatorView(
    onClose: () -> Unit
) {
    // Current simulated screen index: 1 to 6
    var currentSimPage by remember { mutableStateOf(1) }

    // Simulated inputs for Screen 1
    var simUsername by remember { mutableStateOf("يوسف أحمد") }
    var simPassword by remember { mutableStateOf("••••••••") }

    // Simulated states for Screen 3 (تخصيص نشاط)
    var selectedSimRoleOption by remember { mutableStateOf(1) } // 1: متعلم, 2: أستاذ, 3: ولي
    var selectedSimSubject by remember { mutableStateOf("العربية") } // العربية, الرياضيات, الفرنسية, علوم

    // Simulated states for Screen 4 (نوع النشاط)
    var selectedSimActivityType by remember { mutableStateOf("تمارين") } // تمارين, ألعاب, مطابقة, ترتيب, تلوين, قراءة

    // Simulated states for Screen 5 (معاينة)
    var selectedSimAnswer by remember { mutableStateOf(15) } // 14, 15, 16

    // Simulated states for Screen 6 (تقارير)
    var selectedSimReportTab by remember { mutableStateOf("الأداء العام") } // الأنشطة, المواد, الأداء العام

    // Main layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "محاكي واجهات تطبيق نَشِيط 📱✨",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = "تصفح وتفاعل مع الواجهات الست للتصميم (الموكاب)",
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalText.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CharcoalText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.border(0.5.dp, SlateBorder)
            )
        },
        containerColor = Color(0xFFF1F5F9) // Slate gray background representing a device testing environment
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Interactive 1-6 Navigation Bar on top of the screen simulator
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "اختر الواجهة لعرض تفاصيلها الفورية:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..6).forEach { pageNum ->
                            val isActive = currentSimPage == pageNum
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { currentSimPage = pageNum }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isActive) SoftTeal else Color(0xFFF1F5F9)
                                        )
                                        .border(
                                            1.dp,
                                            if (isActive) Color.Transparent else SlateBorder,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pageNum.toString(),
                                        fontWeight = FontWeight.Black,
                                        color = if (isActive) Color.White else CharcoalText,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (pageNum) {
                                        1 -> "تسجيل"
                                        2 -> "الرئيسية"
                                        3 -> "التخصيص"
                                        4 -> "الألعاب"
                                        5 -> "المعاينة"
                                        else -> "التقارير"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isActive) SoftTeal else CharcoalText.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            // Simulated Device Screen Frame
            Card(
                shape = RoundedCornerShape(36.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 420.dp)
                    .fillMaxWidth()
                    .border(4.dp, CharcoalText, RoundedCornerShape(36.dp)) // Black border simulating a beautiful smartphone frame!
            ) {
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
                ) {
                    when (currentSimPage) {
                        1 -> SimulatedLoginScreen(
                            username = simUsername,
                            password = simPassword,
                            onUsernameChange = { simUsername = it },
                            onPasswordChange = { simPassword = it },
                            onLoginClick = { currentSimPage = 2 }
                        )
                        2 -> SimulatedMainDashboard(
                            onCustomClick = { currentSimPage = 3 },
                            onReportsClick = { currentSimPage = 6 }
                        )
                        3 -> SimulatedCustomizationScreen(
                            selectedOption = selectedSimRoleOption,
                            onOptionSelect = { selectedSimRoleOption = it },
                            selectedSubject = selectedSimSubject,
                            onSubjectSelect = { selectedSimSubject = it },
                            onNextClick = { currentSimPage = 4 }
                        )
                        4 -> SimulatedActivitySelection(
                            selectedType = selectedSimActivityType,
                            onTypeSelect = { selectedSimActivityType = it },
                            onCreateClick = { currentSimPage = 5 }
                        )
                        5 -> SimulatedPreviewScreen(
                            selectedAnswer = selectedSimAnswer,
                            onSelectAnswer = { selectedSimAnswer = it },
                            onSaveClick = { currentSimPage = 6 }
                        )
                        6 -> SimulatedReportsScreen(
                            selectedTab = selectedSimReportTab,
                            onTabSelect = { selectedSimReportTab = it }
                        )
                    }

                    // Bottom info bubble detailing simulation screen name
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .background(CharcoalText.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = when (currentSimPage) {
                                1 -> "1. شاشة تسجيل الدخول 🔑"
                                2 -> "2. الصفحة الرئيسية 🏠"
                                3 -> "3. تخصيص نشاط حسب الطلب 🛠️"
                                4 -> "4. اختيار نوع النشاط 🎮"
                                5 -> "5. معاينة النشاط وحفظه 🔍"
                                else -> "6. تقارير متابعة الأداء 📊"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 1. SHASHA AT-TASHJIL AD-DUKHUL (LOGIN)
// ==========================================
@Composable
fun SimulatedLoginScreen(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        
        // Circular Mascot Logo
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PastelMint)
                .border(1.2.dp, SlateBorder, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_mascot),
                contentDescription = "Mascot",
                modifier = Modifier.size(68.dp),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = "NACHIT",
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            color = SoftTeal
        )
        Text(
            text = "تعلم، العب و تفاعل !",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = CharcoalText.copy(alpha = 0.7f),
            modifier = Modifier.offset(y = (-6).dp)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateBorder, RoundedCornerShape(24.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "مرحباً بك في نشيط",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = CharcoalText
                )
                Text(
                    text = "سجل الدخول لمتابعة تعلمك",
                    fontSize = 12.sp,
                    color = CharcoalText.copy(alpha = 0.5f),
                    modifier = Modifier.offset(y = (-6).dp)
                )

                // Username field mockup
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    placeholder = { Text("اسم المستخدم", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Rounded.Person, "", tint = CharcoalText.copy(alpha = 0.4f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = SoftTeal,
                        unfocusedBorderColor = SlateBorder
                    )
                )

                // Password field mockup
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    placeholder = { Text("كلمة المرور", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Rounded.Lock, "", tint = CharcoalText.copy(alpha = 0.4f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = SoftTeal,
                        unfocusedBorderColor = SlateBorder
                    )
                )

                Text(
                    text = "نسيت كلمة المرور؟",
                    fontSize = 11.sp,
                    color = CharcoalText.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.Start)
                )

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("تسجيل الدخول", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Text(
            text = "ليس لديك حساب؟ إنشاء حساب جديد",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = SoftTeal,
            modifier = Modifier.clickable {  }
        )
    }
}

// ==========================================
// 2. AS-SAFHA AR-RA'ISIYYA (MAIN PANEL)
// ==========================================
@Composable
fun SimulatedMainDashboard(
    onCustomClick: () -> Unit,
    onReportsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // App bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Notifications, "Notification", tint = CharcoalText)
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "مرحبا، أحمد",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = CharcoalText
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PastelMint)
                        .border(1.dp, SoftTeal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🦖", fontSize = 20.sp)
                }
            }
        }

        // LEVEL BOX / RATING
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("مستواك الحالي", fontSize = 11.sp, color = CharcoalText.copy(alpha = 0.5f))
                    Text("ممتاز", fontWeight = FontWeight.Black, fontSize = 20.sp, color = SoftTeal)
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = 0.82f,
                            color = SoftTeal,
                            trackColor = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .width(90.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Text("82%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CharcoalText.copy(alpha = 0.5f))
                    }
                }

                // Trophy Cup Icon
                Text("🏆", fontSize = 42.sp)
            }
        }

        // GRID CARD OPTIONS
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // أنشطتي
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFE8F5E9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📗", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("أنشطتي", fontWeight = FontWeight.Black, fontSize = 13.sp, color = CharcoalText)
                        Text("تدرب وتعلم", fontSize = 10.sp, color = CharcoalText.copy(alpha = 0.5f))
                    }
                }

                // تقاريري
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.1.dp, SlateBorder, RoundedCornerShape(20.dp))
                        .clickable { onReportsClick() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFFFF3E0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📊", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("تقاريري", fontWeight = FontWeight.Black, fontSize = 13.sp, color = CharcoalText)
                        Text("متابعة أدائك", fontSize = 10.sp, color = CharcoalText.copy(alpha = 0.5f))
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // تخصيص نشاط
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.1.dp, SlateBorder, RoundedCornerShape(20.dp))
                        .clickable { onCustomClick() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFF3E5F5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧩", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("تخصيص نشاط", fontWeight = FontWeight.Black, fontSize = 13.sp, color = CharcoalText)
                        Text("حسب طلبك", fontSize = 10.sp, color = CharcoalText.copy(alpha = 0.5f))
                    }
                }

                // بطاقاتي
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.1.dp, SlateBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFE0F7FA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💳", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("بطاقاتي", fontWeight = FontWeight.Black, fontSize = 13.sp, color = CharcoalText)
                        Text("بطاقات تعليمية", fontSize = 10.sp, color = CharcoalText.copy(alpha = 0.5f))
                    }
                }
            }
        }

        // SIMULATED BOTTOM BAR MOCK
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(20.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏠", fontSize = 18.sp)
                Text("الرئيسية", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SoftTeal)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📗", fontSize = 18.sp)
                Text("أنشطتي", fontSize = 8.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📊", fontSize = 18.sp)
                Text("تقاريري", fontSize = 8.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⚙️", fontSize = 18.sp)
                Text("الإعدادات", fontSize = 8.sp, color = Color.Gray)
            }
        }
    }
}

// ==========================================
// 3. TAKHSIS NASHAT (CUSTOMIZATION)
// ==========================================
@Composable
fun SimulatedCustomizationScreen(
    selectedOption: Int,
    onOptionSelect: (Int) -> Unit,
    selectedSubject: String,
    onSubjectSelect: (String) -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App header with Back Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.KeyboardArrowLeft, "Next", tint = CharcoalText)
            Text("تخصيص نشاط", fontWeight = FontWeight.Black, fontSize = 16.sp, color = CharcoalText)
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CharcoalText)
        }

        Text(
            text = "اختر نوع الطلب",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = CharcoalText
        )

        // Triple User Options Card list
        listOf(
            1 to Triple("حسب حاجة المتعلم", "نشاط مناسب لمستوى وصعوبات المتعلم", Color(0xFFD1FAE5)),
            2 to Triple("حسب طلب الأستاذ", "أنشطة حسب الدرس أو الهدف التعليمي", Color(0xFFE0E7FF)),
            3 to Triple("حسب طلب الولي", "دعم وتعزيز التعلم في المنزل", Color(0xFFFFEDD5))
        ).forEach { (idx, triple) ->
            val isSelected = selectedOption == idx
            Card(
                onClick = { onOptionSelect(idx) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 1.5.dp else 0.8.dp,
                        color = if (isSelected) SoftTeal else SlateBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(triple.third, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (idx == 1) "👨‍🎓" else if (idx == 2) "👩‍🏫" else "👦", fontSize = 18.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(triple.first, fontWeight = FontWeight.Black, fontSize = 12.sp, color = CharcoalText)
                        Text(triple.second, fontSize = 10.sp, color = CharcoalText.copy(alpha = 0.5f))
                    }
                }
            }
        }

        Text(
            text = "اختر المادة",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = CharcoalText
        )

        // Horizontal Subject Badges Row exactly from Phone 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "العربية" to Pair("أ", Color(0xFFE8F5E9)),
                "الرياضيات" to Pair("123", Color(0xFFEFF6FF)),
                "الفرنسية" to Pair("A", Color(0xFFF3E5F5)),
                "علوم" to Pair("🧪", Color(0xFFFFECB3))
            ).forEach { (name, info) ->
                val isSelected = selectedSubject == name
                Card(
                    onClick = { onSubjectSelect(name) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (isSelected) 1.5.dp else 0.8.dp,
                            color = if (isSelected) SoftTeal else SlateBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(info.second, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(info.first, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                        }
                        Text(
                            name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("التالي", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// ==========================================
// 4. IKHTIYAR NAW' AN-NASHAT (GAME TYPE)
// ==========================================
@Composable
fun SimulatedActivitySelection(
    selectedType: String,
    onTypeSelect: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App header with Back Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.KeyboardArrowLeft, "Next", tint = CharcoalText)
            Text("اختر نوع النشاط", fontWeight = FontWeight.Black, fontSize = 16.sp, color = CharcoalText)
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CharcoalText)
        }

        // 2x3 Grid Layout as shown in Phone 4
        val activityList = listOf(
            Triple("تمارين", "حل مسائل وتدريبات", "📋"),
            Triple("ألعاب تعليمية", "تعلم من خلال اللعب", "🎮"),
            Triple("مطابقة", "وصل وتعلم", "🧩"),
            Triple("ترتيب", "رتب العناصر", "📊"),
            Triple("تلوين", "أنشطة إبداعية", "🎨"),
            Triple("قراءة وفهم", "تحسين مهارات القراءة", "📚")
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            rowItems(activityList.take(2), selectedType, onTypeSelect)
            rowItems(activityList.subList(2, 4), selectedType, onTypeSelect)
            rowItems(activityList.takeLast(2), selectedType, onTypeSelect)
        }

        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("إنشاء النشاط", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun ColumnScope.rowItems(
    items: List<Triple<String, String, String>>,
    selectedType: String,
    onTypeSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().weight(1f),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach { triple ->
            val isSelected = selectedType == triple.first
            Card(
                onClick = { onTypeSelect(triple.first) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(
                        width = if (isSelected) 1.5.dp else 0.8.dp,
                        color = if (isSelected) SoftTeal else SlateBorder,
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF1F5F9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(triple.third, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        triple.first,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = CharcoalText,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        triple.second,
                        fontSize = 8.sp,
                        color = CharcoalText.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ==========================================
// 5. MU'AYANAT AN-NASHAT (PREVIEW)
// ==========================================
@Composable
fun SimulatedPreviewScreen(
    selectedAnswer: Int,
    onSelectAnswer: (Int) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.KeyboardArrowLeft, "Next", tint = CharcoalText)
            Text("معاينة النشاط", fontWeight = FontWeight.Black, fontSize = 16.sp, color = CharcoalText)
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CharcoalText)
        }

         // Card representation from Phone 5
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "نشاط مخصص",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftTeal
                )
                Text(
                    "جمع الأعداد ضمن 20",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = CharcoalText
                )

                HorizontalDivider(color = Color(0xFFF1F5F9))

                listOf(
                    "المستوى: لعمر 9 سنوات" to "🎓",
                    "الهدف: تطبيق عملية الجمع" to "🎯",
                    "عدد الأسئلة: 10 أسئلة" to "❓"
                ).forEach { (text, emoji) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(emoji, fontSize = 14.sp)
                        Text(text, fontSize = 11.sp, color = CharcoalText.copy(alpha = 0.7f))
                    }
                }
            }
        }

        Text(
            "مثال من النشاط",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = CharcoalText
        )

         // Interactive Addition Card formula mock
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, SoftTeal.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "8 + 7 = ?",
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = CharcoalText
                )

                // Row of Answers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(14, 15, 16).forEach { answerNum ->
                        val isSelected = selectedAnswer == answerNum
                        Card(
                            onClick = { onSelectAnswer(answerNum) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SoftTeal else Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .border(
                                    width = if (isSelected) 0.dp else 1.dp,
                                    color = if (isSelected) Color.Transparent else SlateBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = answerNum.toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = if (isSelected) Color.White else CharcoalText
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSaveClick,
            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("حفظ النشاط", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// ==========================================
// 6. TAQARIR MUTA-BA'AT AD-ADA' (ANALYTICS)
// ==========================================
@Composable
fun SimulatedReportsScreen(
    selectedTab: String,
    onTabSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.KeyboardArrowLeft, "Next", tint = CharcoalText)
            Text("تقاريري", fontWeight = FontWeight.Black, fontSize = 16.sp, color = CharcoalText)
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CharcoalText)
        }

        // Top Horizontal Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("الأنشطة", "المواد", "الأداء العام").forEach { tabName ->
                val isSelected = selectedTab == tabName
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { onTabSelect(tabName) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        tabName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) CharcoalText else Color.Gray
                    )
                }
            }
        }

        // Circular Donut Progress Chart Box drawn with custom Canvas (Phone 6)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "نسبة التقدم",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText.copy(alpha = 0.5f)
                )

                // Beautiful drawn Donut canvas
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidthPx = 14.dp.toPx()
                        
                        // Orange arc (25% remaining background)
                        drawArc(
                            color = Color(0xFFFBBF24),
                            startAngle = -90f,
                            sweepAngle = 90f,
                            useCenter = false,
                            style = Stroke(width = strokeWidthPx)
                        )
                        // Green arc (75% completed segment)
                        drawArc(
                            color = SoftTeal,
                            startAngle = 0f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = strokeWidthPx)
                        )
                    }

                    Text(
                        "75%",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = SoftTeal
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))

                // Trio metric indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf(
                        Triple("الكفاءات المكتسبة", "15", Color(0xFF02A854)),
                        Triple("في طور التعلم", "5", Color(0xFFF59E0B)),
                        Triple("تحتاج دعم", "3", Color(0xFFEF4444))
                    ).forEach { (label, value, color) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                label,
                                fontSize = 8.sp,
                                color = CharcoalText.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .border(0.5.dp, color, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    value,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = color
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            "آخر الأنشطة المنجزة",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = CharcoalText
        )

        // List Item Card from mockup Phone 6
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.8.dp, SlateBorder, RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⭐", fontSize = 16.sp)
                    }
                    Column {
                        Text(
                            "جمع الأعداد ضمن 20",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = CharcoalText
                        )
                        Text(
                            "2024/05/20",
                            fontSize = 9.sp,
                            color = CharcoalText.copy(alpha = 0.4f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("ممتاز", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SoftTeal)
                }
            }
        }
    }
}
