package com.aistudio.nasheet.app.ui.screens

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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.nasheet.app.R
import com.aistudio.nasheet.app.data.database.ChildProfile
import com.aistudio.nasheet.app.data.database.DiagnosticResult
import com.aistudio.nasheet.app.ui.theme.*

@Composable
fun KidSandboxView(
    child: ChildProfile,
    results: List<DiagnosticResult>,
    onPlayReading: () -> Unit,
    onPlayFocus: () -> Unit,
    onStartDiagnostic: () -> Unit,
    onCompleteActivity: (String, String, Int, Int, Int) -> Unit,
    onRedeemBadge: (String, Int) -> Unit,
    tts: NasheetTTS?,
    deepLinkTrigger: String? = null,
    onClearDeepLink: () -> Unit = {}
) {
    val needsDiagnostic = results.isEmpty()
    
    val avatarTheme = when (child.avatarEmoji) {
        "🦖" -> Pair(Color(0xFFECFDF5), Color(0xFF10B981)) // Green
        "🧑‍🚀" -> Pair(Color(0xFFEEF2FF), Color(0xFF6366F1)) // Indigo
        "🐱" -> Pair(Color(0xFFFFF1F2), Color(0xFFF43F5E)) // Rose
        "🤖" -> Pair(Color(0xFFECFEFF), Color(0xFF06B6D4)) // Cyan
        "🦊" -> Pair(Color(0xFFFFF7ED), Color(0xFFF97316)) // Orange
        "🐨" -> Pair(Color(0xFFF1F5F9), Color(0xFF64748B)) // Slate
        else -> Pair(Color(0xFFFFFBEB), Color(0xFFF59E0B)) // Amber
    }
    
    // Dialog states
    var showBooksDialog by remember { mutableStateOf(false) }
    var showSentenceDialog by remember { mutableStateOf(false) }
    var showAudioVisualDialog by remember { mutableStateOf(false) }
    var showShopDialog by remember { mutableStateOf(false) }
    var showMemoryDialog by remember { mutableStateOf(false) }
    var showNotebookDialog by remember { mutableStateOf(false) }
    var showChallengeWheelDialog by remember { mutableStateOf(false) }
    var showAlgerianCurriculumDialog by remember { mutableStateOf(false) }
    var showDesignSimulator by remember { mutableStateOf(false) }

    if (showDesignSimulator) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDesignSimulator = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            MockupSimulatorView(onClose = { showDesignSimulator = false })
        }
    }

    // Speak welcome on launch - only when screen is first displayed or name definitively changes
    LaunchedEffect(child.name) {
        val welcomeMsg = "أهلاً بك يا بطلنا الصغير ${child.name}! هيا بنا للعب والتعلم معاً اليوم."
        tts?.speak(welcomeMsg)
    }

    // React to Letters Notebook deep link
    LaunchedEffect(deepLinkTrigger) {
        if (deepLinkTrigger == "letters") {
            showBooksDialog = true
            onClearDeepLink()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE2F3F0), // Soft minty-teal shade at the top
                        Color(0xFFFCFAF4), // Creamy white in the middle
                        Color(0xFFFCFAF5)  // Warm beige cream at the bottom
                    )
                )
            )
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Bar Header matching the mockup styling
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الرئيسية",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = CharcoalText
            )

            // Notifications Bell & Star Counter Container
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Star Counter Badge
                Row(
                    modifier = Modifier
                        .background(PastelYellow, RoundedCornerShape(50))
                        .border(1.dp, AmberBorder, RoundedCornerShape(50))
                        .clickable { showShopDialog = true }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⭐", fontSize = 16.sp, color = AmberStarColor)
                    Text(
                        "${child.stars}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = CharcoalText
                    )
                }

                IconButton(
                    onClick = {
                        tts?.speak("ولديك اليوم توصيات ذكية جديدة مسلية يا بطل في قسم التوصيات بالأسفل!")
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, SlateBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = "Alerts",
                        tint = CharcoalText,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

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

        // Large Unified Dashboard Card nesting Welcome + circular indicators EXACTLY like presentation mockup
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2E7D32), Color(0xFF02A854)) // Beautiful vertical Nachit emerald gradients!
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome and Mascot Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "أهلاً يا بطل! 👋",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "متحمسون لتعليمك اليوم من خلال الألعاب التفاعلية الممتعة والمميزة كلياً!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "البطل: ${child.name} • مستوى تكيّفي ممتاز ${child.avatarEmoji}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    // Mascot Style with Chosen Avatar Emoji
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(avatarTheme.first, CircleShape)
                            .border(3.dp, avatarTheme.second, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = child.avatarEmoji,
                            fontSize = 38.sp
                        )
                    }
                }

                // Nested Side-by-Side Indicators (Perfect mockup replication)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Weekly completed percentage
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp)
                            .border(1.2.dp, SlateBorder, RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier.size(54.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = 0.85f,
                                    color = Color(0xFF10B981),
                                    strokeWidth = 5.6.dp,
                                    trackColor = Color(0xFFECFDF5),
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text(
                                    text = "85%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Black,
                                    color = CharcoalText
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "إنجاز الأسبوع ⭐",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = CharcoalText
                            )
                        }
                    }

                    // Completed lessons rate
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp)
                            .border(1.2.dp, SlateBorder, RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier.size(54.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = 12f / 15f,
                                    color = Color(0xFF3B82F6),
                                    strokeWidth = 5.6.dp,
                                    trackColor = Color(0xFFEFF6FF),
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text(
                                    text = "12/15",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Black,
                                    color = CharcoalText
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "الدروس المنجزة 🎒",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = CharcoalText
                            )
                        }
                    }
                }
            }
        }

        // Diagnostic requirement check banner
        if (needsDiagnostic) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.2.dp, SlateBorder, RoundedCornerShape(28.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(avatarTheme.first)
                            .border(1.2.dp, SlateBorder, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = child.avatarEmoji,
                            fontSize = 38.sp
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "تقييمك التشخيصي الأول جاهز! 🧭✨",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = CharcoalText
                        )
                        Text(
                            text = "للتعديل الأوتوماتيكي المناسب لصعوبة الألعاب وصوت الهجاء، يرجى إجراء التقييم الرائد.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalText.copy(alpha = 0.75f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = onStartDiagnostic,
                            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("ابدأ التقييم التفاعلي ⚡", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // "دروسك القادمة" (Your upcoming lessons) Category Horizontal Row matching Mock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "دروسك ومغامراتك القادمة ✨",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = CharcoalText
            )
            Text(
                text = "اسحب لرؤية المزيد 👈",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = SoftTeal
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Spelling/Reading (قراءة)
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFFDF4)), // very light vibrant mint/green bg
                modifier = Modifier
                    .width(190.dp)
                    .height(235.dp)
                    .border(1.5.dp, SlateBorder, RoundedCornerShape(26.dp))
                    .clickable { onPlayReading() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.2.dp, SlateBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_lesson_reading),
                                contentDescription = "قراءة",
                                modifier = Modifier.fillMaxSize().padding(1.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(PastelMint, RoundedCornerShape(10.dp))
                                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("سهل ⭐", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "لغة الضاد العربية",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText.copy(alpha = 0.65f)
                        )
                        Text(
                            text = "تحدي حروف الهجاء",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Black,
                            color = CharcoalText
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("التقدم للهدف", fontSize = 10.sp, color = CharcoalText.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                            Text("70%", fontSize = 10.sp, color = SoftTeal, fontWeight = FontWeight.Black)
                        }
                        LinearProgressIndicator(
                            progress = 0.7f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = SoftTeal,
                            trackColor = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.2.dp, SlateBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ادخل واحصد: 40 ⭐", fontSize = 11.sp, fontWeight = FontWeight.Black, color = CharcoalText)
                    }
                }
            }

            // Card 2: Math/Memory (رياضيات) - Dark Purple Contrast Card from Mockup
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF312E81)), // deep indigo
                modifier = Modifier
                    .width(190.dp)
                    .height(235.dp)
                    .border(1.5.dp, SlateBorder, RoundedCornerShape(26.dp))
                    .clickable { showMemoryDialog = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.2.dp, SlateBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_lesson_math),
                                contentDescription = "رياضيات",
                                modifier = Modifier.fillMaxSize().padding(1.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(PastelYellow, RoundedCornerShape(10.dp))
                                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("متوسط ⚡", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "طوفان الأرقام السريع",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "جمع كواكب الكرز",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("التقدم للهدف", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                            Text("25%", fontSize = 10.sp, color = PastelYellow, fontWeight = FontWeight.Black)
                        }
                        LinearProgressIndicator(
                            progress = 0.25f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = PastelYellow,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ادخل واحصد: 12 ⭐", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }

            // Card 3: Balloon/Focus (تركيز)
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = PastelPeach), // soft light orange/peach bg
                modifier = Modifier
                    .width(190.dp)
                    .height(235.dp)
                    .border(1.5.dp, SlateBorder, RoundedCornerShape(26.dp))
                    .clickable { onPlayFocus() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.2.dp, SlateBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_lesson_focus),
                                contentDescription = "تركيز",
                                modifier = Modifier.fillMaxSize().padding(1.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFCA5A5), RoundedCornerShape(10.dp)) // soft red/coral
                                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("تحدي 🔥", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "الذكاء وسرعة البديهة",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText.copy(alpha = 0.65f)
                        )
                        Text(
                            text = "فرقعة بالونات الحكمة",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Black,
                            color = CharcoalText
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("التقدم للهدف", fontSize = 10.sp, color = CharcoalText.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                            Text("90%", fontSize = 10.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Black)
                        }
                        LinearProgressIndicator(
                            progress = 0.9f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = CoralWarm,
                            trackColor = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.2.dp, SlateBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ادخل واحصد: 47 ⭐", fontSize = 11.sp, fontWeight = FontWeight.Black, color = CharcoalText)
                    }
                }
            }

            // Card 4: Audio Phonics Workshop 🗣️ (An exciting additional lesson!)
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)), // soft bright baby blue
                modifier = Modifier
                    .width(190.dp)
                    .height(235.dp)
                    .border(1.5.dp, SlateBorder, RoundedCornerShape(26.dp))
                    .clickable { 
                        tts?.speak("رائع يا بطل! لننطلق معاً إلى مغامرة الإملاء والنطق الصحيح للحروف والكلمات!")
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.2.dp, SlateBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_feature_tools),
                                contentDescription = "نطق الحروف",
                                modifier = Modifier.fillMaxSize().padding(1.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(PastelBlue, RoundedCornerShape(10.dp))
                                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("متوسط 🧠", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "النطق والهجاء الفوري",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText.copy(alpha = 0.65f)
                        )
                        Text(
                            text = "الأصوات والمخارج",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Black,
                            color = CharcoalText
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("التقدم للهدف", fontSize = 10.sp, color = CharcoalText.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                            Text("45%", fontSize = 10.sp, color = Color(0xFF3B82F6), fontWeight = FontWeight.Black)
                        }
                        LinearProgressIndicator(
                            progress = 0.45f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = Color(0xFF3B82F6),
                            trackColor = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.2.dp, SlateBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ادخل واحصد: 20 ⭐", fontSize = 11.sp, fontWeight = FontWeight.Black, color = CharcoalText)
                    }
                }
            }
        }

        // --- ADDED: Weekly Teacher/Parent Assignments Checklist ---
        Text(
            text = "واجبات المعلم والأولياء للأسبوع 🏫🎯",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = Color(0xFFD97706),
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // warm gentle amber/honey container
            border = BorderStroke(2.dp, Color(0xFFFBBF24)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (sharedWeeklyAssignments.isEmpty()) {
                    Text(
                        text = "رائع! لا يوجد واجبات معلقة حالياً. تمتع باللعب الحر! 🕊️✨",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78350F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    )
                } else {
                    Text(
                        text = "أكمل المهام التي كلفك بها معلمك أو أولياؤك لتسعدهم وتربح إعجابهم:",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F).copy(alpha = 0.85f),
                        lineHeight = 15.sp
                    )

                    sharedWeeklyAssignments.forEachIndexed { index, assignment ->
                        val isDone = assignment.status.contains("مكتملة")
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isDone) Color(0xFFD1FAE5) else Color.White, RoundedCornerShape(12.dp))
                                .border(1.dp, if (isDone) Color(0xFF34D399) else Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                                .clickable {
                                    val newStatus = if (isDone) "قيد الإنجاز ⏳" else "مكتملة ✅"
                                    sharedWeeklyAssignments[index] = assignment.copy(status = newStatus)
                                    if (!isDone) {
                                        tts?.speak("عمل جبار ومبهر! لقد أكملت الواجب الأسبوعي وسجلت عوناً أكاديمياً كبيراً!")
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isDone) "✅" else "🎯",
                                    fontSize = 16.sp
                                )
                                Column {
                                    Text(
                                        text = assignment.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDone) Color(0xFF065F46) else CharcoalText,
                                        style = androidx.compose.ui.text.TextStyle(
                                            textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                        )
                                    )
                                    Text(
                                        text = assignment.subject,
                                        fontSize = 10.sp,
                                        color = if (isDone) Color(0xFF065F46).copy(alpha = 0.7f) else CharcoalText.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isDone) Color(0xFF10B981) else Color(0xFFF59E0B),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isDone) "أنجزت 🎉" else "قيد العمل ⏳",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Algerian National Curriculum 2026 Section displaying its lessons in the style of upcoming lessons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "منهج الدراسي والدروس الوطنية 🇩🇿📚",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F766E)
            )
            Text(
                text = "اسحب المنهج 👈",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D9488)
            )
        }

        val activeCurriculumGrade = remember(child.schoolLevel) {
            val matchedId = when {
                child.schoolLevel.contains("الأول") || child.schoolLevel.contains("أولى") || child.schoolLevel.contains("أول") -> 1
                child.schoolLevel.contains("الثاني") || child.schoolLevel.contains("ثانية") || child.schoolLevel.contains("ثاني") -> 2
                child.schoolLevel.contains("الثالث") || child.schoolLevel.contains("ثالثة") || child.schoolLevel.contains("ثالث") -> 3
                child.schoolLevel.contains("الرابع") || child.schoolLevel.contains("رابعة") || child.schoolLevel.contains("رابع") -> 4
                child.schoolLevel.contains("الخامس") || child.schoolLevel.contains("خامسة") || child.schoolLevel.contains("خامس") -> 5
                else -> 1
            }
            algerianGradesLessonsData.firstOrNull { it.id == matchedId } ?: algerianGradesLessonsData.first()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            activeCurriculumGrade.lessons.forEach { lesson ->
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = lesson.subjectColor.copy(alpha = 0.05f)),
                    modifier = Modifier
                        .width(190.dp)
                        .height(235.dp)
                        .border(1.5.dp, lesson.subjectColor.copy(alpha = 0.6f), RoundedCornerShape(26.dp))
                        .clickable {
                            tts?.speak(lesson.speakText)
                            showAlgerianCurriculumDialog = true
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.2.dp, SlateBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lesson.emoji,
                                    fontSize = 28.sp
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .background(lesson.subjectColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .border(1.dp, lesson.subjectColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = lesson.subject,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = lesson.subjectColor
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "المنهج الجزائري 2026",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText.copy(alpha = 0.65f)
                            )
                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Black,
                                color = CharcoalText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(1.2.dp, SlateBorder, RoundedCornerShape(12.dp))
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("افتح المنهج 🚀", fontSize = 11.sp, fontWeight = FontWeight.Black, color = CharcoalText)
                        }
                    }
                }
            }
        }
        
        // Kids adventure path roadmap of Algerian Landmarks
        KidsLandmarkRoadmap(
            stars = child.stars,
            childAvatar = child.avatarEmoji,
            tts = tts
        )

        // Extra detailed mini adventures
        Text(
            text = "مغامرات إضافية 🌟",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = CharcoalText,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Interactive Books
            Card(
                modifier = Modifier.weight(1f).clickable { showBooksDialog = true },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📚", fontSize = 24.sp)
                    Text("كتب تفاعلية", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 12.sp)
                }
            }
            // Sentence Builder
            Card(
                modifier = Modifier.weight(1f).clickable { showSentenceDialog = true },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏗️", fontSize = 24.sp)
                    Text("تركيب جمل", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 12.sp)
                }
            }
            // Audio Visual
            Card(
                modifier = Modifier.weight(1f).clickable { showAudioVisualDialog = true },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔊", fontSize = 24.sp)
                    Text("تمييز سمعي", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 12.sp)
                }
            }
        }

        // Smart Voice & Empowerment category
        Text(
            text = "أدوات التمكين الصوتي الذكية 🚀💡",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = CharcoalText,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Interactive Smart Phonics Notebook
            Card(
                modifier = Modifier.weight(1f).clickable { showNotebookDialog = true },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📝", fontSize = 24.sp)
                    Text("المفكرة الصوتية", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 12.sp)
                }
            }
            // Daily Magical challenge wheel
            Card(
                modifier = Modifier.weight(1f).clickable { showChallengeWheelDialog = true },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎡", fontSize = 24.sp)
                    Text("عجلة التحدي", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 12.sp)
                }
            }
        }

        // Display Unlocked Badges and rewards suitcase
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "أوسمتك وشاراتك البطولية 🏆 :",
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText,
                    style = MaterialTheme.typography.titleMedium
                )

                val badges = child.badgesString.split(",").filter { it.isNotBlank() }
                if (badges.isEmpty()) {
                    Text(
                        "العب واحصد الدرجات للحصول على شاراتك البطولية الراقية والاستثنائية!",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalText.copy(alpha = 0.5f)
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        badges.forEach { badge ->
                           Box(
                                modifier = Modifier
                                    .background(PastelBlue, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(badge, fontWeight = FontWeight.Bold, color = CharcoalText)
                            }
                        }
                    }
                }

                HorizontalDivider(color = SlateBorder.copy(alpha = 0.7f), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_game_icons),
                        contentDescription = "حقيبة الجوائز والرموز الثلاثية الأبعاد الملحمية",
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, SlateBorder, RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "حقيبة الكنوز والأيقونات ثلاثية الأبعاد 🎒✨",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = "تصاميم أوسمة متميزة حصرية بانتظار تحقيقك للمهام وتجاوز مستويات التعلم بنشاط!",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalText.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Dialog Logic
    if (showBooksDialog) {
        InteractiveBooksDialog(
            onDismiss = { showBooksDialog = false },
            onCompleteBook = { title, score, stars ->
                onCompleteActivity(title, "القراءة والفهم", score, 120, stars)
            },
            tts = tts,
            schoolLevel = child.schoolLevel
        )
    }

    if (showSentenceDialog) {
        SentenceBuilderDialog(
            onDismiss = { showSentenceDialog = false },
            onComplete = { title, score, stars ->
                onCompleteActivity(title, "اللغة والكتابة", score, 90, stars)
            },
            tts = tts,
            schoolLevel = child.schoolLevel
        )
    }

    if (showAudioVisualDialog) {
        AudioVisualDialog(
            onDismiss = { showAudioVisualDialog = false },
            onComplete = { title, score, stars ->
                onCompleteActivity(title, "التمييز السمعي البصري", score, 60, stars)
            },
            tts = tts,
            schoolLevel = child.schoolLevel
        )
    }

    if (showShopDialog) {
        RewardsShopDialog(
            stars = child.stars,
            unlockedBadges = child.badgesString.split(",").filter { it.isNotBlank() },
            onDismiss = { showShopDialog = false },
            onRedeemBadge = onRedeemBadge,
            tts = tts
        )
    }

    if (showMemoryDialog) {
        MemoryGameDialog(
            onDismiss = { showMemoryDialog = false },
            onComplete = { title, score, stars ->
                onCompleteActivity(title, "الذاكرة والذكاء", score, 120, stars)
            },
            tts = tts,
            schoolLevel = child.schoolLevel
        )
    }

    if (showNotebookDialog) {
        SmartPhonicsNotebookDialog(
            onDismiss = { showNotebookDialog = false },
            onComplete = { title, score, stars ->
                onCompleteActivity(title, "اللغة والكتابة والتأسيس", score, 150, stars)
            },
            tts = tts,
            schoolLevel = child.schoolLevel
        )
    }

    if (showChallengeWheelDialog) {
        MagicChallengeWheelDialog(
            onDismiss = { showChallengeWheelDialog = false },
            onComplete = { title, score, stars ->
                onCompleteActivity(title, "النمو وحل المشكلات", score, 60, stars)
            },
            tts = tts,
            schoolLevel = child.schoolLevel
        )
    }

    if (showAlgerianCurriculumDialog) {
        AlgerianCurriculumDialog(
            onDismiss = { showAlgerianCurriculumDialog = false },
            onComplete = { title, category, score, duration, stars ->
                onCompleteActivity(title, category, score, duration, stars)
            },
            tts = tts,
            currentGradeFromProfile = child.schoolLevel
        )
    }
}

@Composable
fun KidsLandmarkRoadmap(
    stars: Int,
    childAvatar: String,
    tts: NasheetTTS?
) {
    val landmarks = listOf(
        Landmark("⛰️", "جبال الأوراس الشامخة", "جبال الأوراس هي رمز الكفاح الجزائري الشامخ ومنبع العزة والجهاد والوطنية والجمال الشاهد على انتصارات شعبنا!", 0),
        Landmark("🏰", "قلعة بني حماد", "قلعة بني حماد هي حصن أثري عريق بالمسيلة شيده حماد بن بلكين سنة ألف وسبعة ميلادية، وتعد تراثاً إنسانياً عالمياً رائعاً!", 15),
        Landmark("⚓", "سواحل قوراية ومنارتها", "سواحل بجاية وقوراية وشواطئنا الذهبية الشاهدة عبر القرون، تحمي سواحل بلادنا المليئة بالثروات والجمال!", 30),
        Landmark("🏛️", "قصور وادي ميزاب", "قصور غرداية ووادي ميزاب هي تحفة معمارية هندسية فريدة تمثل عمق ونضج وأصالة الحضارة الجزائرية العريقة!", 45),
        Landmark("🏜️", "رمال الصحراء الكبرى", "الصحراء الجزائرية الشاسعة برمال واحات الهقار الجميلة الشامخة تمثل النور والكرم والخلود لوطننا العزيز!", 60)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFEFDFB), RoundedCornerShape(26.dp))
            .border(2.dp, Color(0xFFE5A93C), RoundedCornerShape(26.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "خريطة مغامرات معالم الجزائر 🇩🇿✨",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF78350F)
            )
            Box(
                modifier = Modifier
                    .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "المستوى الحالي: $stars ⭐",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706)
                )
            }
        }

        Text(
            text = "اجمع المزيد من لآلئ النجاح في نشاطاتك لتفتح معالم وطنك الحبيب من الشرق والغرب إلى أقصى الجنوب! اضغط على كل معلم لسماع قصته الملهمة وتراثه العريق:",
            fontSize = 11.sp,
            color = CharcoalText.copy(alpha = 0.85f),
            lineHeight = 16.sp
        )

        // Custom horizontal scroll track for landmarks
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            landmarks.forEachIndexed { index, l ->
                val isUnlocked = stars >= l.requiredStars
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(105.dp)
                        .clickable {
                            if (isUnlocked) {
                                tts?.speak(l.desc)
                            } else {
                                tts?.speak("هذه المحطة مغلقة يا بطلنا المتميز! اجمع ${l.requiredStars} نجوم لفتحها واستكشاف تاريخها الفاخر.")
                            }
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                if (isUnlocked) Color(0xFFFFFBEB) else Color(0xFFF1F5F9),
                                CircleShape
                            )
                            .border(
                                width = 3.dp,
                                color = if (isUnlocked) Color(0xFFEF4444) else Color(0xFFCBD5E1),
                                shape = CircleShape
                            )
                    ) {
                        if (isUnlocked) {
                            Text(l.emoji, fontSize = 28.sp)
                        } else {
                            Text("🔒", fontSize = 20.sp)
                        }
                        
                        // Show child's custom avatar on top of the furthest unlocked landmark that matches their star count
                        val nextRequired = if (index < landmarks.size - 1) landmarks[index + 1].requiredStars else Int.MAX_VALUE
                        val isChildHere = stars >= l.requiredStars && stars < nextRequired
                        if (isChildHere) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-6).dp)
                                    .size(24.dp)
                                    .background(Color.White, CircleShape)
                                    .border(1.5.dp, Color(0xFFEF4444), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(childAvatar, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = l.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) CharcoalText else CharcoalText.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "⭐ ${l.requiredStars}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isUnlocked) Color(0xFF0F766E) else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

data class Landmark(
    val emoji: String,
    val name: String,
    val desc: String,
    val requiredStars: Int
)
