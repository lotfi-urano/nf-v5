package com.aistudio.nasheet.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aistudio.nasheet.app.ui.NasheetViewModel
import com.aistudio.nasheet.app.ui.screens.*
import com.aistudio.nasheet.app.ui.theme.CharcoalText
import com.aistudio.nasheet.app.ui.theme.SlateBorder
import com.aistudio.nasheet.app.ui.theme.SoftTeal

// Sealed class representing standard app destinations
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Sandbox : Screen("sandbox")
    object GameReading : Screen("game_reading")
    object GameFocus : Screen("game_focus")
    object Diagnostic : Screen("diagnostic")
    object ParentDashboard : Screen("parent_dashboard")
    object Notifications : Screen("notifications")
}

@Composable
fun MainAppNavigation(viewModel: NasheetViewModel, tts: com.aistudio.nasheet.app.ui.screens.NasheetTTS?) {
    val navController = rememberNavController()
    val activeProfile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val logs by viewModel.activityLogs.collectAsStateWithLifecycle()
    val results by viewModel.diagnosticResults.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    val isProfileLoaded by viewModel.isProfileLoaded.collectAsStateWithLifecycle()
    var showParentLockDialog by remember { mutableStateOf(false) }
    
    // Track the current route to color the navigation bar icons properly
    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route

    // Synchronize profile state ONLY after DB has loaded to avoid false redirect on cold start
    LaunchedEffect(isProfileLoaded, activeProfile) {
        if (!isProfileLoaded) return@LaunchedEffect
        if (activeProfile == null) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(0) { inclusive = true }
            }
        } else if (currentRoute == Screen.Onboarding.route) {
            navController.navigate(Screen.Sandbox.route) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            // Elegant M3 Bottom bar displayed on core screens only
            if (activeProfile != null && (currentRoute == Screen.Sandbox.route || currentRoute == Screen.ParentDashboard.route || currentRoute == Screen.Notifications.route)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color.White, RoundedCornerShape(28.dp))
                        .border(1.2.dp, SlateBorder, RoundedCornerShape(28.dp))
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. الرئيسية
                    val isSandbox = currentRoute == Screen.Sandbox.route
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate(Screen.Sandbox.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = "Home",
                            tint = if (isSandbox) SoftTeal else CharcoalText.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "الرئيسية",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSandbox) FontWeight.Black else FontWeight.Bold,
                            color = if (isSandbox) SoftTeal else CharcoalText.copy(alpha = 0.6f)
                        )
                    }

                    // 2. الدروس (Interactive book lessons trigger)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                tts?.speak("اختر مغامرة لتبدأ التعلم الحركي الممتع!")
                                navController.navigate(Screen.Sandbox.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Lessons",
                            tint = CharcoalText.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "الدروس",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText.copy(alpha = 0.6f)
                        )
                    }

                    // 3. Central FAB (+) for starting specialized Diagnostic Test
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .offset(y = (-10).dp)
                            .background(SoftTeal, CircleShape)
                            .border(1.2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            .clickable {
                                tts?.speak("لنبدأ التقييم التفاعلي الذكي سوياً يا بطل!")
                                navController.navigate(Screen.Diagnostic.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Start Quiz",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // 4. التوصيات
                    val isNotifications = currentRoute == Screen.Notifications.route
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate(Screen.Notifications.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Icons.Rounded.Notifications,
                                contentDescription = "Alerts",
                                tint = if (isNotifications) SoftTeal else CharcoalText.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                            val unreadCount = notifications.count { !it.isRead }
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "التوصيات",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isNotifications) FontWeight.Black else FontWeight.Bold,
                            color = if (isNotifications) SoftTeal else CharcoalText.copy(alpha = 0.6f)
                        )
                    }

                    // 5. النتائج (Parent gate lock wrapper)
                    val isParent = currentRoute == Screen.ParentDashboard.route
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showParentLockDialog = true },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Parent Control",
                            tint = if (isParent) SoftTeal else CharcoalText.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "النتائج",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isParent) FontWeight.Black else FontWeight.Bold,
                            color = if (isParent) SoftTeal else CharcoalText.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (!isProfileLoaded) {
            // شاشة تحميل مؤقتة ريثما يُحمَّل Room — تمنع الشاشة الفارغة عند الإطلاق أو العودة من الخلفية
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(color = SoftTeal)
            }
            return@Scaffold
        }

        // Use a fixed startDestination to avoid NavHost recomposition crash
        val startDest = remember { Screen.Onboarding.route }

        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingView(
                    onRegister = { name, age, level, difficulty, pName, pPhone, pEmail, avatarEmoji ->
                        viewModel.registerChild(name, age, level, difficulty, pName, pPhone, pEmail, avatarEmoji)
                        navController.navigate(Screen.Sandbox.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    onLogin = { name, parentCode, onSuccess, onFailure ->
                        viewModel.loginChild(
                            name = name,
                            code = parentCode,
                            onSuccess = {
                                onSuccess()
                                navController.navigate(Screen.Sandbox.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            },
                            onFailure = { errorMsg ->
                                onFailure(errorMsg)
                            }
                        )
                    },
                    onRegisterParentEmail = { pName, pPhone, pEmail, pPassword, childName, childAge, level, difficulty, avatar, onSuccess, onFailure ->
                        viewModel.registerParentWithEmailAndPassword(
                            name = pName,
                            phone = pPhone,
                            email = pEmail,
                            password = pPassword,
                            childName = childName,
                            childAge = childAge,
                            schoolLevel = level,
                            difficultyType = difficulty,
                            avatarEmoji = avatar,
                            onSuccess = {
                                onSuccess()
                                navController.navigate(Screen.Sandbox.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            },
                            onFailure = { errorMsg ->
                                onFailure(errorMsg)
                            }
                        )
                    },
                    onRegisterTeacherEmail = { name, email, password, school, experience, specialty, classroom, onSuccess, onFailure ->
                        viewModel.registerTeacherWithEmailAndPassword(
                            name = name,
                            email = email,
                            password = password,
                            school = school,
                            experience = experience,
                            specialization = specialty,
                            classCode = classroom,
                            onSuccess = {
                                onSuccess()
                                navController.navigate(Screen.Sandbox.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            },
                            onFailure = { errorMsg ->
                                onFailure(errorMsg)
                            }
                        )
                    },
                    onLoginEmail = { email, password, onSuccess, onFailure ->
                        viewModel.loginWithEmailAndPassword(
                            email = email,
                            password = password,
                            onSuccess = { displayName ->
                                onSuccess(displayName)
                                navController.navigate(Screen.Sandbox.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            },
                            onFailure = { errorMsg ->
                                onFailure(errorMsg)
                            }
                        )
                    },
                    isFirebaseAvailable = viewModel.isFirebaseAuthAvailable()
                )
            }

            composable(Screen.Sandbox.route) {
                val child = activeProfile
                val deepLinkTrigger by viewModel.deepLinkTrigger.collectAsStateWithLifecycle()
                if (child != null) {
                    KidSandboxView(
                        child = child,
                        results = results,
                        onPlayReading = { navController.navigate(Screen.GameReading.route) },
                        onPlayFocus = { navController.navigate(Screen.GameFocus.route) },
                        onStartDiagnostic = { navController.navigate(Screen.Diagnostic.route) },
                        onCompleteActivity = { title, cat, score, duration, stars ->
                            viewModel.completeActivity(title, cat, score, duration, stars)
                        },
                        onRedeemBadge = { badgeName, cost ->
                            viewModel.redeemBadge(badgeName, cost)
                        },
                        tts = tts,
                        deepLinkTrigger = deepLinkTrigger,
                        onClearDeepLink = { viewModel.clearDeepLinkTrigger() }
                    )
                }
            }

            composable(Screen.GameReading.route) {
                val child = activeProfile
                if (child != null) {
                    DyslexiaReadingGame(
                        schoolLevel = child.schoolLevel,
                        onCompleteGame = { score, duration, stars ->
                            viewModel.completeActivity("أحجية الكلمات وسحر الحروف", "القراءة والتهجئة", score, duration, stars)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() },
                        tts = tts
                    )
                }
            }

            composable(Screen.GameFocus.route) {
                val child = activeProfile
                if (child != null) {
                    AdhdFocusGame(
                        schoolLevel = child.schoolLevel,
                        onCompleteGame = { score, duration, stars ->
                            viewModel.completeActivity("ألوان البالونات والتركيز", "التركيز والذاكرة", score, duration, stars)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() },
                        tts = tts
                    )
                }
            }

            composable(Screen.Diagnostic.route) {
                DiagnosticAssessmentView(
                    onComplete = { reading, focus, memory, writing, visualAuditory ->
                        viewModel.submitDiagnostic(reading, focus, memory, writing, visualAuditory)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() },
                    tts = tts
                )
            }

            composable(Screen.ParentDashboard.route) {
                val child = activeProfile
                val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
                val currentBaseUrl = com.aistudio.nasheet.app.data.api.NasheetApiClient.getBaseUrl()
                val context = androidx.compose.ui.platform.LocalContext.current
                if (child != null) {
                    ParentDashboardView(
                        profile = child,
                        logs = logs,
                        results = results,
                        syncStatus = syncStatus,
                        currentBaseUrl = currentBaseUrl,
                        onSyncNow = { viewModel.syncAllDataNow() },
                        onUpdateBaseUrl = { url -> viewModel.updateNetworkBaseUrl(context, url) },
                        onReset = {
                            viewModel.resetApp()
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onBackToKid = {
                            navController.navigate(Screen.Sandbox.route) {
                                popUpTo(Screen.Sandbox.route) { inclusive = true }
                            }
                        },
                        onLogout = {
                            viewModel.logoutAuthUser {
                                navController.navigate(Screen.Onboarding.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onLinkTeacherClass = { code, onSuccess, onFailure ->
                            viewModel.linkTeacherClass(
                                code = code,
                                onSuccess = { className ->
                                    addStudentToTeacherClass(child.name)
                                    onSuccess(className)
                                },
                                onFailure = onFailure
                            )
                        },
                        onUnlinkTeacherClass = { onSuccess ->
                            viewModel.unlinkTeacherClass(onSuccess)
                        }
                    )
                }
            }

            composable(Screen.Notifications.route) {
                AppNotificationsView(
                    notifications = notifications,
                    onMarkAsRead = { viewModel.dismissNotification(it) }
                )
            }
        }

        // Security parent authentication Lock dialog layer
        if (showParentLockDialog) {
            ParentVerifyLockDialog(
                onDismiss = { showParentLockDialog = false },
                onVerified = {
                    showParentLockDialog = false
                    navController.navigate(Screen.ParentDashboard.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun ParentVerifyLockDialog(onDismiss: () -> Unit, onVerified: () -> Unit) {
    var answerInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // سؤال عشوائي في كل مرة لمنع الأطفال من تعلّم الإجابة الثابتة
    val question = remember {
        val a = (2..9).random()
        val b = (2..9).random()
        Triple(a, b, a + b)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "بوابة التحقق للأولياء والأساتذة 🔒",
                fontWeight = FontWeight.Bold,
                color = CharcoalText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "لمنع دخول طفلكم بالخطأ إلى لوحة الإحصاءات والتحكم الإعدادية، يرجى حل هذه المسألة المبسطة لتجاوز القفل:",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = CharcoalText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "${question.first} + ${question.second} = ؟",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftTeal
                )

                OutlinedTextField(
                    value = answerInput,
                    onValueChange = { answerInput = it },
                    placeholder = { Text("أكتب الإجابة هنا") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (answerInput.trim() == question.third.toString()) {
                        onVerified()
                    } else {
                        errorMessage = "الإجابة خاطئة! يرجى إعادة المحاولة من فضلك."
                        answerInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
            ) {
                Text("تأكيد ودخول", color = Color.White)
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
