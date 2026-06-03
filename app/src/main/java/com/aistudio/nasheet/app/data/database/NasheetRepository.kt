package com.aistudio.nasheet.app.data.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class NasheetRepository(private val appDao: AppDao) {

    val activeProfile: Flow<ChildProfile?> = appDao.getActiveProfile()
    val activityLogs: Flow<List<ActivityLog>> = appDao.getAllActivityLogs()
    val diagnosticResults: Flow<List<DiagnosticResult>> = appDao.getDiagnosticResults()
    val notifications: Flow<List<AppNotification>> = appDao.getAllNotifications()

    suspend fun saveProfile(profile: ChildProfile) = withContext(Dispatchers.IO) {
        appDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: ChildProfile) = withContext(Dispatchers.IO) {
        appDao.updateProfile(profile)
    }

    suspend fun clearAllProfiles() = withContext(Dispatchers.IO) {
        appDao.deleteAllProfiles()
    }

    suspend fun logActivity(activityName: String, category: String, score: Int, durationSeconds: Int, starsEarned: Int) = withContext(Dispatchers.IO) {
        val log = ActivityLog(
            activityName = activityName,
            category = category,
            score = score,
            durationSeconds = durationSeconds,
            starsEarned = starsEarned
        )
        appDao.insertActivityLog(log)

        // Increment active profile stars and award badges
        val currentProfile = appDao.getActiveProfile().firstOrNull()
        if (currentProfile != null) {
            val newStars = currentProfile.stars + coinsForScore(score, starsEarned)
            val currentBadges = currentProfile.badgesString.split(",").filter { it.isNotBlank() }.toMutableList()
            
            // Streak logic
            val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24L)
            val lastActiveRaw = currentProfile.lastActiveDate
            val lastActive = if (lastActiveRaw > 10000000L) lastActiveRaw / (1000 * 60 * 60 * 24L) else lastActiveRaw
            val newStreak = if (today - lastActive == 1L) currentProfile.streakDays + 1 else if (today == lastActive) currentProfile.streakDays else 1
            
            // Check for new badges
            val newBadge = when {
                newStars >= 100 && !currentBadges.contains("بطل النجوم ⭐") -> "بطل النجوم ⭐"
                newStars >= 50 && !currentBadges.contains("براعم مجتهدة 🌱") -> "براعم مجتهدة 🌱"
                currentBadges.isEmpty() -> "المستكشف الأول 🧭"
                else -> null
            }
            if (newBadge != null) {
                currentBadges.add(newBadge)
                
                // Also create a celebratory notification
                appDao.insertNotification(
                    AppNotification(
                        title = "نجاح هائل لـ ${currentProfile.name}! 🎉",
                        text = "لقد حصل على شارة جديدة: [$newBadge]. واصل العمل الرائع!",
                        type = "child"
                    )
                )
            }

            appDao.updateProfile(
                currentProfile.copy(
                    stars = newStars,
                    badgesString = currentBadges.joinToString(","),
                    streakDays = newStreak,
                    lastActiveDate = today,
                    totalPlayMinutes = currentProfile.totalPlayMinutes + (durationSeconds / 60)
                )
            )
        }
    }

    private fun coinsForScore(score: Int, defaultStars: Int): Int {
        return defaultStars + (score / 20) // bonus stars based on score
    }

    suspend fun logDiagnosticResult(result: DiagnosticResult) = withContext(Dispatchers.IO) {
        appDao.insertDiagnosticResult(result)

        // Generate automatic smart recommendations and alerts based on scores
        val currentProfile = appDao.getActiveProfile().firstOrNull() ?: return@withContext
        val profileName = currentProfile.name

        // Reading recommendation
        if (result.readingScore < 60) {
            appDao.insertNotification(
                AppNotification(
                    title = "توصية قراءة لـ $profileName 📚",
                    text = "أظهر التقييم حاجة إلى تعزيز مهارة القراءة. نوصي بلعب لعبة 'ترتيب الحروف وفك الأحجية' يومياً لمدة 10 دقائق.",
                    type = "recommendation"
                )
            )
        } else {
            appDao.insertNotification(
                AppNotification(
                    title = "تفوق متميز في القراءة لـ $profileName 🌟",
                    text = "مهارات القراءة والتمييز اللغوي ممتازة جداً! استمر في تمكين الطفل عبر الأنشطة المتقدمة.",
                    type = "parent"
                )
            )
        }

        // Focus and Memory recommendation
        if (result.focusScore < 60 || result.memoryScore < 60) {
            appDao.insertNotification(
                AppNotification(
                    title = "تدريب إدراكي لـ $profileName 🧠",
                    text = "لزيادة التركيز وتنشيط الذاكرة، ننصح ببرنامج 'ألوان البالونات المشتتة' لتقوية ثبات الانتباه البصري والسمعي.",
                    type = "recommendation"
                )
            )
        }

        // Writing or Visual audio
        if (result.visualAuditoryScore < 60) {
            appDao.insertNotification(
                AppNotification(
                    title = "توصية التمييز السمعي والبصري 🎨",
                    text = "يُقترح تدريبات المطابقة البصرية وعزل الأصوات اللفظية لمساعدة طفلك في التمييز السمعي وصعوبات التعلم المرتبطة به.",
                    type = "recommendation"
                )
            )
        }

        // Parent celebratory notification
        appDao.insertNotification(
            AppNotification(
                title = "اكتمل التقييم التشخيصي بنجاح 📋",
                text = "لقد أكمل $profileName التقييم بنجاح. تتوفر الآن إحصاءات تفصيلية وتوصيات علمية مخصصة في لوحة التحكم.",
                type = "parent"
            )
        )
    }

    suspend fun markNotificationAsRead(id: Int) = withContext(Dispatchers.IO) {
        appDao.markNotificationAsRead(id)
    }

    suspend fun cleanOldNotifications() = withContext(Dispatchers.IO) {
        appDao.deleteOldNotifications(keepCount = 50)
    }

    suspend fun addNotification(notification: AppNotification) = withContext(Dispatchers.IO) {
        appDao.insertNotification(notification)
        cleanOldNotifications()
    }

    suspend fun findProfile(name: String, code: String): ChildProfile? = withContext(Dispatchers.IO) {
        appDao.findProfile(name, code)
    }

    suspend fun findProfileByEmail(email: String): ChildProfile? = withContext(Dispatchers.IO) {
        appDao.findProfileByEmail(email)
    }
}
