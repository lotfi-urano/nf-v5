package com.aistudio.nasheet.app.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "child_profiles")
data class ChildProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int,
    val schoolLevel: String, // e.g. "تمهيدي", "أولى ابتدائي", "ثانية ابتدائي"
    val difficultyType: String, // "none", "dyslexia" (عسر القراءة), "adhd" (تشتت الانتباه), "slow_learning" (بطء التعلم)
    val stars: Int = 0,
    val badgesString: String = "", // comma-separated titles
    val streakDays: Int = 0,
    val lastActiveDate: Long = 0L,
    val totalPlayMinutes: Int = 0,
    val avatarEmoji: String = "🧒",
    val parentEmail: String = "",
    val parentName: String = "",
    val parentPhone: String = "",
    val levelProgress: Int = 0,
    val parentCode: String = "",
    val teacherCode: String = ""
) {
    val displayParentId: String
        get() = if (parentCode.isNotBlank()) {
            parentCode
        } else {
            val hash = (parentPhone.ifBlank { name }).hashCode().let { if (it < 0) -it else it } % 900000 + 100000
            "NST-$hash"
        }
}

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val activityName: String, // e.g., "أحجية الكلمات", "لعبة المطابقة", "تركيز البالونات"
    val category: String, // "القراءة", "الترکيز والذاكرة", "الكتابة", "التمييز البصري"
    val score: Int,
    val durationSeconds: Int,
    val starsEarned: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "diagnostic_results")
data class DiagnosticResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val readingScore: Int, // 0 - 100
    val focusScore: Int,   // 0 - 100
    val memoryScore: Int,  // 0 - 100
    val writingScore: Int, // 0 - 100
    val visualAuditoryScore: Int, // 0 - 100
    val timestamp: Long = System.currentTimeMillis()
)

// Notifications
@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val text: String,
    val type: String, // "child" (تحفيز), "parent" (الأولياء), "recommendation" (توصية)
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Dao
interface AppDao {
    // Profiling
    @Query("SELECT * FROM child_profiles WHERE lastActiveDate > 0 ORDER BY lastActiveDate DESC LIMIT 1")
    fun getActiveProfile(): Flow<ChildProfile?>

    @Query("SELECT * FROM child_profiles WHERE name = :name AND parentCode = :code LIMIT 1")
    suspend fun findProfile(name: String, code: String): ChildProfile?

    @Query("SELECT * FROM child_profiles WHERE parentEmail = :email LIMIT 1")
    suspend fun findProfileByEmail(email: String): ChildProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ChildProfile)

    @Update
    suspend fun updateProfile(profile: ChildProfile)

    @Query("UPDATE child_profiles SET stars = stars + :addStars WHERE id = :profileId")
    suspend fun incrementStars(profileId: Int, addStars: Int)

    @Query("DELETE FROM child_profiles")
    suspend fun deleteAllProfiles()

    // Activity Logs
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllActivityLogs(): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLog)

    // Diagnostic results
    @Query("SELECT * FROM diagnostic_results ORDER BY timestamp DESC")
    fun getDiagnosticResults(): Flow<List<DiagnosticResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosticResult(result: DiagnosticResult)

    // Notifications
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Int)

    @Query("DELETE FROM app_notifications WHERE id NOT IN (SELECT id FROM app_notifications ORDER BY timestamp DESC LIMIT :keepCount)")
    suspend fun deleteOldNotifications(keepCount: Int)
}

@Database(
    entities = [ChildProfile::class, ActivityLog::class, DiagnosticResult::class, AppNotification::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
