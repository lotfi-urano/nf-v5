package com.aistudio.nasheet.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aistudio.nasheet.app.data.database.ActivityLog
import com.aistudio.nasheet.app.data.database.AppNotification
import com.aistudio.nasheet.app.data.database.ChildProfile
import com.aistudio.nasheet.app.data.database.DiagnosticResult
import com.aistudio.nasheet.app.data.database.NasheetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.aistudio.nasheet.app.data.constants.AppConstants
import com.aistudio.nasheet.app.data.api.NasheetApiClient
import com.aistudio.nasheet.app.BuildConfig
import android.util.Log

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val message: String, val lastSyncedAt: Long) : SyncState()
    data class Error(val errorMessage: String) : SyncState()
}

data class CloudSyncStatus(
    val profileSyncState: SyncState = SyncState.Idle,
    val logsSyncState: SyncState = SyncState.Idle,
    val diagnosticSyncState: SyncState = SyncState.Idle,
    val lastSyncTime: Long = 0L
)

class NasheetViewModel(private val repository: NasheetRepository) : ViewModel() {

    private val _syncStatus = MutableStateFlow(CloudSyncStatus())
    val syncStatus: StateFlow<CloudSyncStatus> = _syncStatus

    fun updateNetworkBaseUrl(context: android.content.Context, url: String) {
        com.aistudio.nasheet.app.data.api.NasheetPreferenceHelper.saveBaseUrl(context, url)
        com.aistudio.nasheet.app.data.api.NasheetApiClient.updateBaseUrl(url)
    }

    fun syncAllDataNow() {
        viewModelScope.launch {
            _syncStatus.value = _syncStatus.value.copy(
                profileSyncState = SyncState.Syncing,
                logsSyncState = SyncState.Syncing,
                diagnosticSyncState = SyncState.Syncing
            )

            val profile = activeProfile.value
            val logsList = activityLogs.value
            val resultsList = diagnosticResults.value
            val apiKey = "Bearer " + (BuildConfig.GEMINI_API_KEY.ifBlank { "TEST_DEFAULT_KEY" })

            // 1. Sync Profile
            if (profile != null) {
                try {
                    NasheetApiClient.service.syncProfile(apiKey, profile)
                    _syncStatus.value = _syncStatus.value.copy(
                        profileSyncState = SyncState.Success("تمت مزامنة الملف الشخصي بنجاح", System.currentTimeMillis())
                    )
                } catch (e: Throwable) {
                    val errMsg = mapErrorToMessage(e)
                    _syncStatus.value = _syncStatus.value.copy(
                        profileSyncState = SyncState.Error(errMsg)
                    )
                }
            } else {
                _syncStatus.value = _syncStatus.value.copy(
                    profileSyncState = SyncState.Success("لا يوجد ملف شخصي للفحص والمزامنة", System.currentTimeMillis())
                )
            }

            // 2. Sync Logs
            if (logsList.isNotEmpty()) {
                try {
                    NasheetApiClient.service.syncActivityLogs(apiKey, logsList)
                    _syncStatus.value = _syncStatus.value.copy(
                        logsSyncState = SyncState.Success("تمت مزامنة سجل الأنشطة والتمارين بنجاح", System.currentTimeMillis())
                    )
                } catch (e: Throwable) {
                    val errMsg = mapErrorToMessage(e)
                    _syncStatus.value = _syncStatus.value.copy(
                        logsSyncState = SyncState.Error(errMsg)
                    )
                }
            } else {
                _syncStatus.value = _syncStatus.value.copy(
                    logsSyncState = SyncState.Success("لا توجد تمارين منجزة للمزامنة حالياً", System.currentTimeMillis())
                )
            }

            // 3. Sync Diagnostic Results
            if (resultsList.isNotEmpty()) {
                try {
                    NasheetApiClient.service.syncDiagnosticResults(apiKey, resultsList)
                    _syncStatus.value = _syncStatus.value.copy(
                        diagnosticSyncState = SyncState.Success("تمت مزامنة التقييمات التشخيصية بنجاح", System.currentTimeMillis())
                    )
                } catch (e: Throwable) {
                    val errMsg = mapErrorToMessage(e)
                    _syncStatus.value = _syncStatus.value.copy(
                        diagnosticSyncState = SyncState.Error(errMsg)
                    )
                }
            } else {
                _syncStatus.value = _syncStatus.value.copy(
                    diagnosticSyncState = SyncState.Success("لا توجد تقييمات تشخيصية منشورة للمزامنة", System.currentTimeMillis())
                )
            }

            _syncStatus.value = _syncStatus.value.copy(lastSyncTime = System.currentTimeMillis())
        }
    }

    private fun mapErrorToMessage(t: Throwable): String {
        val msg = t.message ?: ""
        return when {
            t is java.net.SocketTimeoutException -> "انتهت مهلة المزامنة مع الخادم (Timeout)"
            t is java.net.UnknownHostException -> "تعذر الوصول للعنوان (تحقق من العنوان المكتوب أو اتصال الإنترنت)"
            t is java.io.IOException && msg.contains("pin", ignoreCase = true) -> "فشل المزامنة الآمنة: تعارض مفتاح التثبيت (SSL Pinning Mismatch)"
            t is retrofit2.HttpException -> {
                when (t.code()) {
                    401 -> "غير مصرح (Authorization Key Error)"
                    404 -> "مسار الخدمة غير موجود على السيرفر (404 Not Found)"
                    500 -> "خطأ سيرفر داخلي لدى الخادم (Internal Server Error 500)"
                    else -> "خطأ استجابة رقم (${t.code()})"
                }
            }
            else -> "فشل: ${t.localizedMessage ?: "عطل غير معروف بالشبكة"}"
        }
    }

    val activeProfile: StateFlow<ChildProfile?> = repository.activeProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    // true = تم تحميل البيانات من Room (سواء وُجد ملف شخصي أم لا)
    // false = لا يزال يتم التحميل (لا توجيه قبل هذه اللحظة)
    val isProfileLoaded: StateFlow<Boolean> = repository.activeProfile
        .map { true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    val activityLogs: StateFlow<List<ActivityLog>> = repository.activityLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val diagnosticResults: StateFlow<List<DiagnosticResult>> = repository.diagnosticResults
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val notifications: StateFlow<List<AppNotification>> = repository.notifications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _deepLinkTrigger = MutableStateFlow<String?>(null)
    val deepLinkTrigger: StateFlow<String?> = _deepLinkTrigger

    fun setDeepLinkTrigger(target: String?) {
        _deepLinkTrigger.value = target
    }

    fun clearDeepLinkTrigger() {
        _deepLinkTrigger.value = null
    }

    fun registerChild(
        name: String,
        age: Int,
        schoolLevel: String,
        difficultyType: String,
        parentName: String = "",
        parentPhone: String = "",
        parentEmail: String = "",
        avatarEmoji: String = "🦖"
    ) {
        viewModelScope.launch {
            val todayEpochDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24L)
            // Generate prominent unique master parent/teacher code
            val randomNum = (100000..999999).random()
            val generatedParentCode = "NST-$randomNum"

            val profile = ChildProfile(
                name = name,
                age = age,
                schoolLevel = schoolLevel,
                difficultyType = difficultyType,
                stars = 0,
                badgesString = "المستكشف الأول 🧭",
                streakDays = 1,
                lastActiveDate = todayEpochDay,
                parentName = parentName,
                parentPhone = parentPhone,
                parentEmail = parentEmail,
                parentCode = generatedParentCode,
                avatarEmoji = avatarEmoji
            )
            repository.saveProfile(profile)

            // Asynchronously sync child profile to cloud backend database
            viewModelScope.launch {
                try {
                    val apiKey = "Bearer " + (BuildConfig.GEMINI_API_KEY.ifBlank { "TEST_DEFAULT_KEY" })
                    NasheetApiClient.service.syncProfile(apiKey, profile)
                    Log.d("NasheetViewModel", "Profile synced successfully to cloud remote backup!")
                } catch (e: Throwable) {
                    Log.e("NasheetViewModel", "Graceful fallback: Cloud database inaccessible. Saving offline.", e)
                }
            }

            // Auto insert initial welcoming notifications
            repository.addNotification(
                AppNotification(
                    title = "مرحباً بطفلنا البطل $name! 🎉",
                    text = "أهلاً بك في تطبيق نشيط. نحن متحمسون جداً للتعلم واللعب معك اليوم!",
                    type = "child"
                )
            )

            repository.addNotification(
                AppNotification(
                    title = "اكتمل التسجيل بنجاح 🧭",
                    text = "يرجى مساعدة طفلكم في خوض 'التقييم التشخيصي' الأول لنتمكن من ضبط التمارين بما يناسب قدراته واحتياجاته الخاصة.",
                    type = "parent"
                )
            )
        }
    }

    fun submitDiagnostic(reading: Int, focus: Int, memory: Int, writing: Int, visualAuditory: Int) {
        viewModelScope.launch {
            val result = DiagnosticResult(
                readingScore = reading,
                focusScore = focus,
                memoryScore = memory,
                writingScore = writing,
                visualAuditoryScore = visualAuditory
            )
            repository.logDiagnosticResult(result)

            // Asynchronously sync diagnostics to remote database
            viewModelScope.launch {
                try {
                    val apiKey = "Bearer " + (com.aistudio.nasheet.app.BuildConfig.GEMINI_API_KEY.ifBlank { "TEST_DEFAULT_KEY" })
                    NasheetApiClient.service.syncDiagnosticResults(apiKey, listOf(result))
                    Log.d("NasheetViewModel", "Diagnostic results synced successfully to cloud!")
                } catch (e: Throwable) {
                    Log.e("NasheetViewModel", "Cloud diagnostics backup skipped (offline modes active).", e)
                }
            }
        }
    }

    fun completeActivity(name: String, category: String, score: Int, durationSeconds: Int, starsEarned: Int) {
        viewModelScope.launch {
            repository.logActivity(
                activityName = name,
                category = category,
                score = score,
                durationSeconds = durationSeconds,
                starsEarned = starsEarned
            )
        }
    }

    fun dismissNotification(id: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun redeemBadge(badgeName: String, cost: Int) {
        viewModelScope.launch {
            val currentProfile = activeProfile.value ?: return@launch
            if (currentProfile.stars >= cost) {
                val currentBadges = currentProfile.badgesString.split(",").filter { it.isNotBlank() }.toMutableList()
                if (!currentBadges.contains(badgeName)) {
                    currentBadges.add(badgeName)
                    val updated = currentProfile.copy(
                        stars = currentProfile.stars - cost,
                        badgesString = currentBadges.joinToString(",")
                    )
                    repository.updateProfile(updated)
                    // Celebrate notification
                    repository.addNotification(
                        AppNotification(
                            title = "شراء جائزة بنجاح 🏆",
                            text = "حصل البطل ${currentProfile.name} على جائزة متميزة: [$badgeName] مقابل استبدال $cost نجمة.",
                            type = "child"
                        )
                    )
                }
            }
        }
    }

    fun resetApp() {
        viewModelScope.launch {
            repository.clearAllProfiles()
            // Reset notifications or set default ones
            repository.addNotification(
                AppNotification(
                    title = "مرحباً بكم في نشيط!",
                    text = "قم بتهيئة حساب طفلكم للبدء في رحلة التعلم الممتعة.",
                    type = "parent"
                )
            )
        }
    }

    fun logoutActiveProfile() {
        viewModelScope.launch {
            val currentProfile = activeProfile.value
            if (currentProfile != null) {
                // Set lastActiveDate to 0 so it's excluded from active check, logging them out safely
                val updated = currentProfile.copy(lastActiveDate = 0L)
                repository.updateProfile(updated)
            }
        }
    }

    fun loginChild(name: String, code: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            val matched = repository.findProfile(name.trim(), code.trim())
            if (matched != null) {
                val updated = matched.copy(lastActiveDate = System.currentTimeMillis() / (1000 * 60 * 60 * 24L))
                repository.updateProfile(updated)
                
                repository.addNotification(
                    AppNotification(
                        title = "أهلاً بعودتك يا بطل ${matched.name}! 🔑✨",
                        text = "تم تسجيل الدخول بنجاح! نحن سعيدون جداً برؤيتك مجدداً اليوم ومواصلة رحلة الانجاز والتقدم.",
                        type = "child"
                    )
                )
                onSuccess()
            } else {
                onFailure("عذراً، لم نجد حساب مسجل بهذا الاسم أو الرمز التعريفي! يرجى التحقق وإعادة المحاولة.")
            }
        }
    }

    fun linkTeacherClass(code: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            val currentProfile = activeProfile.value
            if (currentProfile == null) {
                onFailure("عذراً، لا يوجد ملف تعريف نشط حالياً لربطه!")
                return@launch
            }
            
            val formattedCode = code.trim().uppercase()
            if (formattedCode.isEmpty()) {
                onFailure("يرجى إدخال رمز الصف أولاً!")
                return@launch
            }
            
            // Link it! We accept any code for now, but if it matches the generated teacher code or AppConstants.DEFAULT_TEACHER_CODE, we show specific success message
            val className = if (formattedCode == AppConstants.DEFAULT_TEACHER_CODE) {
                AppConstants.DEFAULT_CLASSROOM_NAME
            } else {
                "صف الأستاذ ($formattedCode) 🏫"
            }
            
            val updated = currentProfile.copy(teacherCode = formattedCode)
            repository.updateProfile(updated)
            
            repository.addNotification(
                AppNotification(
                    title = "تم الربط مع الأستاذ بنجاح 🤝📚",
                    text = "تم ربط البطل ${currentProfile.name} بالقسم الدراسي للأستاذ ($className) برمز الانتساب $formattedCode.",
                    type = "parent"
                )
            )
            onSuccess(className)
        }
    }

    fun unlinkTeacherClass(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentProfile = activeProfile.value ?: return@launch
            val updated = currentProfile.copy(teacherCode = "")
            repository.updateProfile(updated)
            repository.addNotification(
                AppNotification(
                    title = "تم إلغاء الربط مع الأستاذ 🚪",
                    text = "تم إلغاء ربط حساب الطفل بصف الأستاذ بنجاح.",
                    type = "parent"
                )
            )
            onSuccess()
        }
    }

    // --- Firebase Authentication Tracking & Handlers ---
    private val _authenticatedUser = MutableStateFlow<com.google.firebase.auth.FirebaseUser?>(com.aistudio.nasheet.app.data.api.NasheetAuthManager.getSignedInUser())
    val authenticatedUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _authenticatedUser

    fun refreshAuthUser() {
        _authenticatedUser.value = com.aistudio.nasheet.app.data.api.NasheetAuthManager.getSignedInUser()
    }

    fun isFirebaseAuthAvailable(): Boolean {
        return com.aistudio.nasheet.app.data.api.NasheetAuthManager.isFirebaseAvailable()
    }

    fun registerParentWithEmailAndPassword(
        name: String,
        phone: String,
        email: String,
        password: String,
        childName: String,
        childAge: Int,
        schoolLevel: String,
        difficultyType: String,
        avatarEmoji: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            com.aistudio.nasheet.app.data.api.NasheetAuthManager.signUp(
                email = email.trim(),
                password = password,
                displayName = name.trim(),
                onSuccess = { firebaseUser ->
                    registerChild(
                        name = childName.trim(),
                        age = childAge,
                        schoolLevel = schoolLevel,
                        difficultyType = difficultyType,
                        parentName = name.trim(),
                        parentPhone = phone.trim(),
                        parentEmail = email.trim(),
                        avatarEmoji = avatarEmoji
                    )
                    refreshAuthUser()
                    onSuccess()
                },
                onFailure = { errorMsg ->
                    onFailure(errorMsg)
                }
            )
        }
    }

    fun registerTeacherWithEmailAndPassword(
        name: String,
        email: String,
        password: String,
        school: String,
        experience: String,
        specialization: String,
        classCode: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            com.aistudio.nasheet.app.data.api.NasheetAuthManager.signUp(
                email = email.trim(),
                password = password,
                displayName = name.trim(),
                onSuccess = { firebaseUser ->
                    viewModelScope.launch {
                        repository.addNotification(
                            AppNotification(
                                title = "بوابة المعلم: تم تفعيل حسابك السحابي! 🏫",
                                text = "الأستاذ المتميز $name، تم ربط بريدك الإلكتروني $email سحابياً بنجاح لرصد تقييمات الطلاب وعلامات تحصيلهم.",
                                type = "child"
                            )
                        )
                        refreshAuthUser()
                        onSuccess()
                    }
                },
                onFailure = { errorMsg ->
                    onFailure(errorMsg)
                }
            )
        }
    }

    fun loginWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (displayName: String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            com.aistudio.nasheet.app.data.api.NasheetAuthManager.signIn(
                email = email.trim(),
                password = password,
                onSuccess = { firebaseUser ->
                    val userName = firebaseUser.displayName ?: email.trim().substringBefore("@")
                    viewModelScope.launch {
                        val matchedProfile = repository.findProfileByEmail(email.trim())
                        if (matchedProfile != null) {
                            val updated = matchedProfile.copy(lastActiveDate = System.currentTimeMillis() / (1000 * 60 * 60 * 24L))
                            repository.updateProfile(updated)
                        } else {
                            registerChild(
                                name = userName,
                                age = 7,
                                schoolLevel = "الأولى ابتدائي",
                                difficultyType = "none",
                                parentName = userName,
                                parentPhone = "",
                                parentEmail = email.trim(),
                                avatarEmoji = "🦁"
                            )
                        }
                        refreshAuthUser()
                        onSuccess(userName)
                    }
                },
                onFailure = { errorMsg ->
                    onFailure(errorMsg)
                }
            )
        }
    }

    fun logoutAuthUser(onSuccess: () -> Unit = {}) {
        com.aistudio.nasheet.app.data.api.NasheetAuthManager.signOut()
        viewModelScope.launch {
            repository.clearAllProfiles()
            refreshAuthUser()
            onSuccess()
        }
    }
}

class NasheetViewModelFactory(private val repository: NasheetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NasheetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NasheetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
