package com.aistudio.nasheet.app.ui.screens

import com.aistudio.nasheet.app.data.constants.AppConstants

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.nasheet.app.data.database.ActivityLog
import com.aistudio.nasheet.app.data.database.ChildProfile
import com.aistudio.nasheet.app.data.database.DiagnosticResult
import com.aistudio.nasheet.app.ui.theme.*
import com.aistudio.nasheet.app.ui.CloudSyncStatus
import com.aistudio.nasheet.app.ui.SyncState

// --- GLOBAL DECLARED SHARED ASSIGNMENTS STATES ---
data class AssignmentItem(val title: String, val subject: String, val status: String)

val sharedWeeklyAssignments = androidx.compose.runtime.mutableStateListOf(
    AssignmentItem("تأسيس الحركات البسيطة ونطقها السليم 🎒", "اللغة العربية 📚", "مكتملة ✅"),
    AssignmentItem("مراجعة وإتقان جدول ضرب الرقم ٢ كاملًا 🔢", "الرياضيات 🔢", "قيد الإنجاز ⏳")
)

// 2. PARENT AND TEACHER DASHBOARD (لوحة تحكم الكبار والأولياء)
@Composable
fun ParentDashboardView(
    profile: ChildProfile,
    logs: List<ActivityLog>,
    results: List<DiagnosticResult>,
    syncStatus: CloudSyncStatus = CloudSyncStatus(),
    currentBaseUrl: String = "https://api.nasheet.app/",
    onSyncNow: () -> Unit = {},
    onUpdateBaseUrl: (String) -> Unit = {},
    onReset: () -> Unit,
    onBackToKid: () -> Unit,
    onLogout: () -> Unit = {},
    onLinkTeacherClass: (String, (String) -> Unit, (String) -> Unit) -> Unit = { _, _, _ -> },
    onUnlinkTeacherClass: (() -> Unit) -> Unit = {}
) {
    val activeResult = results.firstOrNull()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CreamBackground,
                        Color(0xFFEEF2FF),
                        Color(0xFFFFF9E5).copy(alpha = 0.5f)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Beautiful centered NACHIT logo without card borders & perfectly compact
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.aistudio.nasheet.app.R.drawable.nasheet_app_icon_1780234200444),
                    contentDescription = "شعار تطبيق نشيط",
                    modifier = Modifier
                        .width(400.dp)
                        .height(220.dp)
                        .padding(0.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Welcomer header with Action switches
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(SoftTeal, Color(0xFF1D4ED8))
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .border(1.2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "لوحة المتابعة المشتركة 🤝",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "متابعة دقيقة لمهارات ${profile.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(profile.avatarEmoji, fontSize = 16.sp)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Overlapping Parent and Teacher Avatar indicators in the 3D clay style
                        Box(contentAlignment = Alignment.CenterStart) {
                            // Teacher avatar
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PastelBlue)
                                    .border(1.5.dp, Color.White, CircleShape)
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.aistudio.nasheet.app.R.drawable.img_teacher_avatar_1779626324852),
                                    contentDescription = "الأستاذ",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                            // Parent avatar (offset to overlap)
                            Box(
                                modifier = Modifier
                                    .offset(x = 22.dp)
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PastelMint)
                                    .border(1.5.dp, Color.White, CircleShape)
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.aistudio.nasheet.app.R.drawable.img_parent_avatar_1779626344997),
                                    contentDescription = "ولي الأمر",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(28.dp)) // padding for the overlap offset
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Star, "Stars", tint = PastelYellow, modifier = Modifier.size(20.dp))
                        Text(
                            "تحتوي حصيلته: ${profile.stars} نجمة ⭐",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PastelYellow
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = PastelMint),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🔥 أيام متتالية: ${profile.streakDays}",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Button to toggle back to child environment
                Button(
                    onClick = onBackToKid,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("بيئة الطفل 🧸", color = SoftTeal, fontWeight = FontWeight.Bold)
                }
                }
            }
        }

        // Parent & Teacher Identity and Unique ID card
        item {
            val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
            val context = androidx.compose.ui.platform.LocalContext.current

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = SoftTeal.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("👤", fontSize = 24.sp)
                            Text(
                                "حساب الولي / الأستاذ النشط",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                        }

                        // Cute mini active badge
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PastelMint),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "نشط حالياً ✨",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                        }
                    }

                    HorizontalDivider(color = CreamBackground, thickness = 1.dp)

                    // Details Column
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Parent Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("الاسم واللقب:", fontWeight = FontWeight.Bold, color = CharcoalText, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = profile.parentName.ifBlank { "غير مسجل" },
                                color = CharcoalText.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Parent Phone
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("رقم الهاتف:", fontWeight = FontWeight.Bold, color = CharcoalText, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = profile.parentPhone.ifBlank { "غير مسجل" },
                                color = CharcoalText.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Parent Email
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("البريد الإلكتروني:", fontWeight = FontWeight.Bold, color = CharcoalText, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = profile.parentEmail.ifBlank { "غير مسجل (اختياري)" },
                                color = CharcoalText.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // The unique ID Section with high prominence
                    Spacer(modifier = Modifier.height(4.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SoftTeal.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "الرمز التعريفي الفريد لكل حساب (Parent ID) 🆔",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SoftTeal,
                                textAlign = TextAlign.Center
                            )

                            // Displaying the unique ID code with a stunning visual monospace badge
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .border(
                                        width = 2.dp,
                                        color = SoftTeal,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = profile.displayParentId,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        color = SoftTeal,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Text(
                                text = "يمكنك استخدام هذا الرمز لتسجيل الدخول السريع أو ربط حساب المتابعة الذكي للولي.",
                                style = MaterialTheme.typography.labelMedium,
                                color = CharcoalText.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            // Copy Button with interactive premium feedback
                            Button(
                                onClick = {
                                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(profile.displayParentId))
                                    android.widget.Toast.makeText(context, "📋 تم نسخ الرمز التعريفي بنجاح!", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text(
                                    text = "نسخ الرمز التعريفي 📋",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- ADDED: Weekly Teacher/Parent Assignments Card ---
        item {
            var newAssignmentTitle by remember { mutableStateOf("") }
            var selectedSubject by remember { mutableStateOf("اللغة العربية 📚") }
            var showAddForm by remember { mutableStateOf(false) }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFFF59E0B).copy(alpha = 0.3f), // Amber border
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📋", fontSize = 24.sp)
                            Column {
                                Text(
                                    "دفتر التكاليف الأسبوعية والواجبات 🏫✨",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalText
                                )
                                Text(
                                    "كلف الطفل بمهام مخصصة تظهر مباشرة في واجهته لربح النجوم والمكافآت",
                                    fontSize = 11.sp,
                                    color = CharcoalText.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = CreamBackground, thickness = 1.dp)

                    // Assignment List
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (sharedWeeklyAssignments.isEmpty()) {
                            Text(
                                "لا توجد واجبات معينة حالياً لهذا الأسبوع.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalText.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            sharedWeeklyAssignments.forEachIndexed { idx, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFEFBF0), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            item.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CharcoalText
                                        )
                                        Text(
                                            item.subject,
                                            fontSize = 10.sp,
                                            color = CharcoalText.copy(alpha = 0.6f)
                                        )
                                    }
                                    
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            item.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.status.contains("مكتملة")) Color(0xFF059669) else Color(0xFFD97706)
                                        )
                                        
                                        IconButton(
                                            onClick = { sharedWeeklyAssignments.removeAt(idx) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Text("🗑️", fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Toggle form Button
                    Button(
                        onClick = { showAddForm = !showAddForm },
                        colors = ButtonDefaults.buttonColors(containerColor = if (showAddForm) Color(0xFFDC2626) else Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = if (showAddForm) "إغلاق نافذة التكليف ❌" else "تعيين واجب أسبوعي مخصص ➕",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (showAddForm) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFFDF5), RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFFEF3C7), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Text("إضافة واجب جديد لبطلنا 🎒:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                            
                            OutlinedTextField(
                                value = newAssignmentTitle,
                                onValueChange = { newAssignmentTitle = it },
                                placeholder = { Text("مثلاً: إتقان قراءة همزات درس السنة الثالثة", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )

                            // Select Subject
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val subjects = listOf("اللغة العربية 📚", "الرياضيات 🔢", "تحدي التركيز 🧠")
                                subjects.forEach { sub ->
                                    val isSelected = selectedSubject == sub
                                    Card(
                                        onClick = { selectedSubject = sub },
                                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFEF3C7) else Color.White),
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFFF59E0B) else Color(0xFFE2E8F0)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(sub, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    if (newAssignmentTitle.trim().isNotEmpty()) {
                                        sharedWeeklyAssignments.add(
                                            AssignmentItem(newAssignmentTitle.trim(), selectedSubject, "قيد الإنجاز ⏳")
                                        )
                                        newAssignmentTitle = ""
                                        showAddForm = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("تأكيد تعيين الواجب البيداغوجي 🎯", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // --- ADDED: Link with Teacher Class Card ---
        item {
            var inputClassCode by remember { mutableStateOf("") }
            var isLinking by remember { mutableStateOf(false) }
            val context = androidx.compose.ui.platform.LocalContext.current

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFF10B981).copy(alpha = 0.3f), // Mint/Emerald borders for educational linking
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🤝", fontSize = 24.sp)
                        Text(
                            "الربط والانتساب لصف الأستاذ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    }

                    HorizontalDivider(color = CreamBackground, thickness = 1.dp)

                    if (profile.teacherCode.isNotBlank()) {
                        // Success state: Linked with a classroom
                        val displayedClassName = if (profile.teacherCode == AppConstants.DEFAULT_TEACHER_CODE) {
                            AppConstants.DEFAULT_CLASSROOM_NAME
                        } else {
                            "صف الأستاذ (${profile.teacherCode}) 🏫"
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = PastelMint.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "تم ربط الحساب بنجاح! 🎉📚",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )

                                Text(
                                    text = "البطل مشترك حالياً في:\n$displayedClassName",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CharcoalText,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "رمز الانتساب الحالي: [ ${profile.teacherCode} ]",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CharcoalText.copy(alpha = 0.8f),
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Button(
                                    onClick = {
                                        onUnlinkTeacherClass {
                                            android.widget.Toast.makeText(context, "🚪 تم إلغاء الربط مع الأستاذ بنجاح.", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "إلغاء الربط بالصف 🚪",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    } else {
                        // Action state: Not Linked yet, show Input field
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "هل يمتلك طفلك رمز انتساب لصف الأستاذ؟ أدخله هنا لمشاركة تقارير التحصيل والتقدم تلقائياً مع معلمه.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalText.copy(alpha = 0.8f)
                            )

                            OutlinedTextField(
                                value = inputClassCode,
                                onValueChange = { inputClassCode = it },
                                placeholder = { Text("أدخل رمز الصف، مثال: ${AppConstants.DEFAULT_TEACHER_CODE}") },
                                leadingIcon = { Icon(Icons.Rounded.Star, contentDescription = "Class Code", tint = Color(0xFF10B981)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CharcoalText,
                                    unfocusedTextColor = CharcoalText,
                                    focusedBorderColor = Color(0xFF10B981),
                                    focusedLabelColor = Color(0xFF10B981),
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                )
                            )

                            Button(
                                onClick = {
                                    val cleanedCode = inputClassCode.trim().uppercase()
                                    if (cleanedCode.isEmpty()) {
                                        android.widget.Toast.makeText(context, "الرجاء إدخال كود صف صحيح!", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        isLinking = true
                                        onLinkTeacherClass(
                                            cleanedCode,
                                            { linkedName ->
                                                isLinking = false
                                                android.widget.Toast.makeText(context, "✨ تم الربط بنجاح بصف: $linkedName", android.widget.Toast.LENGTH_LONG).show()
                                            },
                                            { error ->
                                                isLinking = false
                                                android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    }
                                },
                                enabled = !isLinking,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                if (isLinking) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text(
                                        text = "تأكيد الربط مع الأستاذ 🔗",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            
                            // Visual hint
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PastelBlue.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "💡 تلميح: أدخل رمز الصف [ ${AppConstants.DEFAULT_TEACHER_CODE} ] المصمم سلفاً لتجربة الربط الذكي الفوري مع صف الأستاذ النموذجي الخاص بمنصة نشيط.",
                                    modifier = Modifier.padding(10.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CharcoalText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Child config summary info Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("تحليل الملف الذكي للطفل :", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CharcoalText)
                        Text("الاسم: ${profile.name} ، العمر: ${profile.age} سنوات", color = CharcoalText)
                        val dynamicAgeLevelText = when {
                            profile.schoolLevel.contains("الأول") || profile.schoolLevel.contains("أولى") -> "مستوى عمر 6 - 7 سنوات 🎒"
                            profile.schoolLevel.contains("الثاني") || profile.schoolLevel.contains("ثانية") -> "مستوى عمر 8 سنوات 📖"
                            profile.schoolLevel.contains("الثالث") || profile.schoolLevel.contains("ثالثة") -> "مستوى عمر 9 سنوات 📐"
                            profile.schoolLevel.contains("الرابع") || profile.schoolLevel.contains("رابعة") -> "مستوى عمر 10 سنوات 🌍"
                            profile.schoolLevel.contains("الخامس") || profile.schoolLevel.contains("خامسة") -> "مستوى عمر 11 - 12 سنة 🇩🇿"
                            else -> "مستوى مخصص حسب عمر ${profile.age} سنوات 🌱"
                        }
                        Text("مستوى الأنشطة والدروس: $dynamicAgeLevelText", color = CharcoalText)
                        Text(
                            text = "تهيئة الصعوبة المفعلة: " + when(profile.difficultyType) {
                                "dyslexia" -> "عسر القراءة (تعديل الحروف وتباعد المقاطع) 📚"
                                "adhd" -> "تشتت الانتباه وفرط الحركة (بيئة التركيز الأبسط) 🧠"
                                "slow_learning" -> "بطء التعلم (مساعد صوتي لا نهائي) ⏳"
                                else -> "التعلم القياسي والطبيعي 🌱"
                            },
                            fontWeight = FontWeight.Bold,
                            color = SoftTeal
                        )
                    }
                }
            }
        }

        // Diagnostic Assessment Status & Custom SVG/Canvas Circular Charts
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "نتائج التقييم التشخيصي الأول (Diagnostic Stats):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )

                    if (activeResult != null) {
                        // Custom Canvas Chart
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DiagnosticSpiderRadar(result = activeResult)
                        }

                        // Split detail breakdown
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            DiagnosticMetricRow("مهارات القراءة (Reading)", activeResult.readingScore, PastelMint)
                            DiagnosticMetricRow("ثبات التركيز ولفت البال (Focus Rate)", activeResult.focusScore, PastelPeach)
                            DiagnosticMetricRow("الذاكرة البصرية والسمعية (Memory)", activeResult.memoryScore, PastelBlue)
                            DiagnosticMetricRow("الكتابة وصعوبات التعبير (Writing)", activeResult.writingScore, PastelPink)
                            DiagnosticMetricRow("التمييز الحسي المشترك (Visual Auditory)", activeResult.visualAuditoryScore, PastelYellow)
                        }
                    } else {
                        // Empty states
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(CreamBackground, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Rounded.Warning, "No results", tint = CoralWarm)
                                Text("لم يجرِ الطفل التقييم التشخيصي التفاعلي بعد.", style = MaterialTheme.typography.bodyMedium, color = CharcoalText)
                                Text("يرجى البدء بالتقييم من شاشة بيئة الطفل.", style = MaterialTheme.typography.bodySmall, color = CharcoalText.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }

        // ADHD Highlights & Dyslexia strengths/mistakes checklist
        if (activeResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "نقاط القوة وحالات الضعف المرتبطة لـ ${profile.name} 🧠",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )

                        // Highlight strengths
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🥇 نقاط القوة البارزة لديه:", fontWeight = FontWeight.Bold, color = HexColor(0xFF43A047))
                            
                            val strengths = mutableListOf<String>()
                            if (activeResult.readingScore >= 60) strengths.add("+ التمييز السريع للكلمات المكتوبة ذات الروابط.")
                            if (activeResult.focusScore >= 60) strengths.add("+ قدرة على حصر الانتباه البصري لمدة تزيد عن الدقيقة.")
                            if (activeResult.memoryScore >= 60) strengths.add("+ استدعاء متين جداً لحيوانات وأشياء الشاشة المختفية.")
                            if (activeResult.visualAuditoryScore >= 60) strengths.add("+ تفريق ممتاز ووعي صوتي متميز لمقاطع الحروف ومخارجها.")

                            if (strengths.isEmpty()) {
                                Text("- يستمتع الطفل بتمارين التوجيه الصوتي ويستجيب بنجاح للتحفيزات المستمرة.")
                            } else {
                                strengths.forEach { str ->
                                    Text(str, style = MaterialTheme.typography.bodyMedium, color = CharcoalText)
                                }
                            }
                        }

                        HorizontalDivider()

                        // Frequent challenges
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("⚠️ التحديات والصعوبات المتكررة:", fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                            
                            val challenges = mutableListOf<String>()
                            if (activeResult.readingScore < 60) challenges.add("- بعض التباطؤ وصعوبة قراءة أو عزل الحروف القريبة شكلاً (ب، ت، ث).")
                            if (activeResult.focusScore < 60) challenges.add("- تشتت انتباه سريع أمام المؤثرات البصرية الثانوية والمتحركة.")
                            if (activeResult.memoryScore < 60) challenges.add("- صعوبة بسيطة في الذاكرة قصيرة المدى وتتطلب المراجعة المستمرة.")
                            if (activeResult.writingScore < 60) challenges.add("- تأخر في ترتيب ومطابقة الحروف لبناء الكلمات الإملائية المألوفة.")

                            if (challenges.isEmpty()) {
                                Text("- أداء الطفل ممتاز! مستوى التحصيل الدراسي والتركيز ضمن القدرة العالية.")
                            } else {
                                challenges.forEach { chal ->
                                    Text(chal, style = MaterialTheme.typography.bodyMedium, color = CharcoalText)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Activity Logs List (تاريخ الأنشطة)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سجل الأنشطة المنجزة ومستوى التقدم 📊",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Badge { Text("${logs.size} منجز") }
                    }

                    if (logs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(CreamBackground, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("لم يقم الطفل بأي ألعاب تعليمية حتى الآن.", style = MaterialTheme.typography.bodyMedium, color = CharcoalText.copy(alpha = 0.6f))
                        }
                    } else {
                        logs.take(5).forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CreamBackground, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(log.activityName, fontWeight = FontWeight.Bold, color = CharcoalText)
                                    Text("التصنيف: ${log.category}", style = MaterialTheme.typography.bodySmall, color = CharcoalText.copy(alpha = 0.7f))
                                }
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(text = "الدرجة: ${log.score}%", color = SoftTeal, fontWeight = FontWeight.Bold)
                                    Text(text = "${log.durationSeconds} ثانية", fontSize = 11.sp, color = CharcoalText.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Clinical & School IEP progress report generator item (جديد وممتاز)
        item {
            var includeDiagnostics by remember { mutableStateOf(true) }
            var includeLogs by remember { mutableStateOf(true) }
            var therapistNoteInput by remember { mutableStateOf("") }
            val context = LocalContext.current
            val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

            val generatedReportText = remember(includeDiagnostics, includeLogs, therapistNoteInput, profile, logs, activeResult) {
                buildString {
                    appendLine("==============================================")
                    appendLine("🎓 تقرير التقدم والتشخيص الأكاديمي الشامل للطفل 🎓")
                    appendLine("==============================================")
                    appendLine("اسم الطفل البطل: ${profile.name}")
                    appendLine("عمر الطفل: ${profile.age} سنوات")
                    val reportAgeLevel = when {
                        profile.schoolLevel.contains("الأول") || profile.schoolLevel.contains("أولى") -> "مستوى عمر 6 - 7 سنوات"
                        profile.schoolLevel.contains("الثاني") || profile.schoolLevel.contains("ثانية") -> "مستوى عمر 8 سنوات"
                        profile.schoolLevel.contains("الثالث") || profile.schoolLevel.contains("ثالثة") -> "مستوى عمر 9 سنوات"
                        profile.schoolLevel.contains("الرابع") || profile.schoolLevel.contains("رابعة") -> "مستوى عمر 10 سنوات"
                        profile.schoolLevel.contains("الخامس") || profile.schoolLevel.contains("خامسة") -> "مستوى عمر 11 - 12 سنة"
                        else -> "مخصص حسب العمر ${profile.age} سنوات"
                    }
                    appendLine("مستوى الأنشطة والدروس: $reportAgeLevel")
                    appendLine("إجمالي النجوم الحاصل عليها: ${profile.stars} ⭐")
                    appendLine("----------------------------------------------")
                    if (includeDiagnostics && activeResult != null) {
                        val diffAr = when(profile.difficultyType) {
                            "dyslexia" -> "عسر القراءة والتعلم البصري"
                            "adhd" -> "تشتت الانتباه وصعوبات التركيز"
                            "slow_learning" -> "تأخر النطق أو بطء التعلم"
                            else -> "تأسيس لغوي قياسي"
                        }
                        appendLine("🔬 نتائج التقييم التشخيصي والوعي الصوتي:")
                        appendLine("• نوع الصعوبة/التحدي: $diffAr")
                        appendLine("• الوعي الفونولوجي والمقاطع: ${activeResult.visualAuditoryScore}/100")
                        appendLine("• سرعة القراءة والطلاقة: ${activeResult.readingScore}/100")
                        appendLine("• الذاكرة البصرية السريعة: ${activeResult.memoryScore}/100")
                        appendLine("• الكتابة التتبع والخط: ${activeResult.writingScore}/100")
                        appendLine("• الانتباه والتركيز المرئي: ${activeResult.focusScore}/100")
                        appendLine("----------------------------------------------")
                    }
                    if (includeLogs) {
                        appendLine("📊 الأنشطة والتدريبات المنجزة بالمنصة:")
                        if (logs.isEmpty()) {
                            appendLine("• لا توجد أنشطة مسجلة حتى الآن.")
                        } else {
                            logs.take(5).forEach { log ->
                                appendLine("• ${log.activityName} - التصنيف: ${log.category} (الدرجة: ${log.score}%)")
                            }
                        }
                        appendLine("----------------------------------------------")
                    }
                    if (therapistNoteInput.isNotBlank()) {
                        appendLine("📝 ملاحظات وتوجيهات الأخصائي / ولي الأمر المخصصة:")
                        appendLine(therapistNoteInput)
                        appendLine("----------------------------------------------")
                    } else {
                        appendLine("📝 ملاحظات وتوجيهات مخصصة:")
                        appendLine("• نوصي بمواصلة ممارسة التمارين بمعدل 15 دقيقة يومياً بمساعدة التوجيه الصوتي الهاديء.")
                        appendLine("----------------------------------------------")
                    }
                    appendLine("تطبيق نشيط • رعاية إدراكية ذكية تأسيسية متكاملة 🧒🦖💡")
                    appendLine("توقيع المشرف الأكاديمي: ____________________ [ 🏛️ ]")
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
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
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEEF2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📃", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "مُولد التقارير العيادية والمدرسية الفورية 🏥✉️",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                            Text(
                                text = "شارك تقدم ولدك مع المدرسة والمشرفين وعلماء تقويم النطق بلمسة واحدة",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalText.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Divider(color = SlateBorder.copy(alpha = 0.6f))

                    // Customizable Options
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFF), RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "تحرير خيارات تصدير التقرير:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable { includeDiagnostics = !includeDiagnostics }
                        ) {
                            Checkbox(
                                checked = includeDiagnostics,
                                onCheckedChange = { includeDiagnostics = it },
                                colors = CheckboxDefaults.colors(checkedColor = SoftTeal)
                            )
                            Text("تضمين درجات التقييم والوعي الصوتي 📊", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CharcoalText)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable { includeLogs = !includeLogs }
                        ) {
                            Checkbox(
                                checked = includeLogs,
                                onCheckedChange = { includeLogs = it },
                                colors = CheckboxDefaults.colors(checkedColor = SoftTeal)
                            )
                            Text("تضمين قائمة الألعاب والدرجات المنجزة الأخيرة 🎒", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CharcoalText)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Custom Clinical Observation note
                        Text(
                            text = "إضافة ملاحظات الأخصائي والمعلم (اختياري) ✍️:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        OutlinedTextField(
                            value = therapistNoteInput,
                            onValueChange = { therapistNoteInput = it },
                            placeholder = { Text("مثال: يظهر بطلنا استجابة مثالية في تمييز الكلمات الثلاثية ذات الفتح، ونوصي الأهل بالتركيز على الأصوات المعجمة...", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftTeal,
                                unfocusedBorderColor = SlateBorder,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            maxLines = 3
                        )
                    }

                    // Clinical Document Parchment Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFCFAF2), RoundedCornerShape(16.dp))
                            .border(1.5.dp, Color(0xFFDCD2B4), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎴 معاينة المستند الرسمي", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB5A982))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("مُعتمَد 🏛️", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = generatedReportText,
                                    fontSize = 10.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    lineHeight = 14.sp,
                                    color = Color(0xFF5C523B)
                                )
                            }
                        }
                    }

                    // Share and Copy Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(generatedReportText))
                                android.widget.Toast.makeText(context, "📋 تم نسخ التقرير الكامل للحافظة بنجاح!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("نسخ التقرير 📋", color = CharcoalText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                try {
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, generatedReportText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = android.content.Intent.createChooser(sendIntent, "إرسال التقرير القياسي لـ ${profile.name} عبر:")
                                    context.startActivity(shareIntent)
                                } catch (e: Exception) {
                                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(generatedReportText))
                                    android.widget.Toast.makeText(context, "📋 تم النسخ للحافظة لمشاركة النص مباشرة!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("تصدير ومشاركة 📤", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Cloud Sync and Hostname Configuration Card
        item {
            val context = LocalContext.current
            var tempUrl by remember { mutableStateOf(currentBaseUrl) }
            LaunchedEffect(currentBaseUrl) {
                tempUrl = currentBaseUrl
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "خدمات الحفظ والمزامنة السحابية ☁️🔄",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    
                    Text(
                        text = "يتيح لك نشيط حفظ ملفات طفلك، التقييمات التفاعلية، وتقارير النشاطات لإدارتها سحابياً ومشاركتها مع المربين والأساتذة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalText.copy(alpha = 0.8f)
                    )

                    HorizontalDivider()

                    // Hostname config
                    Text(
                        text = "رابط خادم الاتصال (Backend API URL):",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        color = CharcoalText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = tempUrl,
                            onValueChange = { tempUrl = it },
                            placeholder = { Text("https://api.nasheet.app/") },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Button(
                            onClick = {
                                if (tempUrl.trim().isNotBlank()) {
                                    onUpdateBaseUrl(tempUrl.trim())
                                    android.widget.Toast.makeText(context, "💾 تم تحديث عنوان الخادم بنجاح ومزامنته!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("حفظ 💾", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CreamBackground),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "حالة مزامنة قواعد البيانات الفردية:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalText
                            )

                            // 1. Profile Sync status row
                            SyncStatusRow(
                                title = "👤 الملف التعريفي للطفل",
                                state = syncStatus.profileSyncState
                            )

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                            // 2. Diagnostic Sync status row
                            SyncStatusRow(
                                title = "📋 التقييمات التشخيصية",
                                state = syncStatus.diagnosticSyncState
                            )

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                            // 3. Activity Logs Sync status row
                            SyncStatusRow(
                                title = "🎮 سجل التمارين والأنشطة",
                                state = syncStatus.logsSyncState
                            )
                        }
                    }

                    // Sync Manual trigger button
                    val isAnySyncing = syncStatus.profileSyncState is SyncState.Syncing ||
                                       syncStatus.diagnosticSyncState is SyncState.Syncing ||
                                       syncStatus.logsSyncState is SyncState.Syncing
                    Button(
                        onClick = onSyncNow,
                        enabled = !isAnySyncing,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (isAnySyncing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جاري الاتصال والمزامنة...", color = Color.White)
                        } else {
                            Text("بدء مزامنة البيانات السحابية الآن 🔄", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    if (syncStatus.lastSyncTime > 0) {
                        val formatTime = java.text.SimpleDateFormat("hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date(syncStatus.lastSyncTime))
                        Text(
                            text = "تاريخ آخر محاولة مزامنة سحابية: $formatTime",
                            color = CharcoalText.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Reset Settings Area
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PastelPink.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("إدارة التطبيق وجلسة الولي/الأستاذ ⚙️", fontWeight = FontWeight.Bold, color = CharcoalText)
                    Text("انقر على أي من الخيارات أدناه لإدارة الحساب النشط أو تسجيل الخروج الفوري والعودة لبيئة الطفل المؤمنة.", style = MaterialTheme.typography.bodySmall, color = CharcoalText, textAlign = TextAlign.Center)
                    
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logout Parent/Teacher button
                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("تسجيل الخروج والعودة لشاشة الترحيب 🚪", fontWeight = FontWeight.Bold)
                        }

                        // Delete Account Button
                        Button(
                            onClick = { showDeleteConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralWarm, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("حذف حساب الطفل الحالي بالكامل 🗑️", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    "تحذير: حذف الحساب بالكامل! ⚠️",
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    "هل أنت متأكد من رغبتك في حذف الحساب بالكامل؟ هذا الإجراء سيؤدي لحذف اسم الطفل، ونتائج التقييم التشخيصي الذكي، وسجل الألعاب، ولن تتمكن من تتبع تقدمه مرة أخرى.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CharcoalText,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onReset()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralWarm)
                ) {
                    Text("نعم، حذف الحساب", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("إلغاء", color = CharcoalText, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

// Custom function to create a color based on Long value
fun HexColor(hexValue: Long): Color {
    return Color(hexValue)
}

// Custom Canvas chart drawing diagnostic metrics
@Composable
fun DiagnosticSpiderRadar(result: DiagnosticResult) {
    val scores = listOf(
        result.readingScore,
        result.focusScore,
        result.memoryScore,
        result.writingScore,
        result.visualAuditoryScore
    )

    Canvas(
        modifier = Modifier
            .size(140.dp)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2

        // Draw background circles
        val bgPaths = 4
        for (i in 1..bgPaths) {
            val r = radius * (i.toFloat() / bgPaths)
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.4f),
                radius = r,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw axes and score points
        val numPoints = 5
        val points = mutableListOf<Offset>()

        for (i in 0 until numPoints) {
            val angle = (2 * Math.PI * i / numPoints) - Math.PI / 2
            val axisX = center.x + radius * Math.cos(angle).toFloat()
            val axisY = center.y + radius * Math.sin(angle).toFloat()

            // Draw axis line
            drawLine(
                color = Color.LightGray.copy(alpha = 0.6f),
                start = center,
                end = Offset(axisX, axisY),
                strokeWidth = 1.dp.toPx()
            )

            // Map score to offset
            val scorePercent = (scores[i].toFloat() / 100).coerceIn(0.1f, 1.0f)
            val scoreX = center.x + radius * scorePercent * Math.cos(angle).toFloat()
            val scoreY = center.y + radius * scorePercent * Math.sin(angle).toFloat()
            points.add(Offset(scoreX, scoreY))
        }

        // Connect score points
        for (i in 0 until numPoints) {
            val nextIndex = (i + 1) % numPoints
            drawLine(
                color = SoftTeal,
                start = points[i],
                end = points[nextIndex],
                strokeWidth = 3.dp.toPx()
            )
            // Draw points dots
            drawCircle(
                color = CoralWarm,
                radius = 4.dp.toPx(),
                center = points[i]
            )
        }
    }
}

@Composable
fun DiagnosticMetricRow(title: String, score: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                Text(title, style = MaterialTheme.typography.bodyMedium, color = CharcoalText)
            }
            Text("$score / 100", fontWeight = FontWeight.Bold, color = SoftTeal)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score.toFloat() / 100 },
            color = if (score >= 60) color else CoralWarm,
            trackColor = Color.LightGray.copy(alpha = 0.3f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun SyncStatusRow(title: String, state: SyncState) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium, color = CharcoalText)
        
        when (state) {
            is SyncState.Idle -> {
                Text(
                    text = "جاهز للمزامنة 💤",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
            is SyncState.Syncing -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.6.dp, color = SoftTeal)
                    Text(
                        text = "جاري الحفظ...",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            is SyncState.Success -> {
                Text(
                    text = "مكتملة ومؤمنة ✅",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )
            }
            is SyncState.Error -> {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "فشل المزامنة ❌",
                        style = MaterialTheme.typography.bodySmall,
                        color = CoralWarm,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.errorMessage,
                        style = MaterialTheme.typography.labelSmall,
                        color = CoralWarm.copy(alpha = 0.8f),
                        textAlign = TextAlign.End,
                        modifier = Modifier.widthIn(max = 200.dp)
                    )
                }
            }
        }
    }
}


