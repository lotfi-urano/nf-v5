# ============================================================
# Nasheet App - ProGuard Rules
# ============================================================

# Keep line numbers for crash stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---- Firebase ----
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keepnames class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ---- Firebase Auth ----
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.auth.internal.** { *; }

# ---- Firebase Firestore ----
-keep class com.google.firebase.firestore.** { *; }
-keepclassmembers class ** {
    @com.google.firebase.firestore.PropertyName *;
}

# ---- Kotlin & Coroutines ----
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**

# ---- AndroidX / Jetpack Compose ----
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }
-keep class androidx.room.** { *; }
-dontwarn androidx.**

# ---- Room Database ----
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# ---- Moshi JSON ----
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}
-dontwarn com.squareup.moshi.**

# ---- Retrofit / OkHttp ----
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# ---- App Data Classes (used with Moshi/Room/Firebase) ----
-keep class com.aistudio.nasheet.app.data.** { *; }
-keepclassmembers class com.aistudio.nasheet.app.data.** { *; }

# ---- ViewModel & Application ----
-keep class com.aistudio.nasheet.app.NasheetApplication { *; }
-keep class com.aistudio.nasheet.app.ui.NasheetViewModel { *; }
-keep class com.aistudio.nasheet.app.ui.NasheetViewModelFactory { *; }

# ---- Sealed classes (SyncState) ----
-keep class com.aistudio.nasheet.app.ui.SyncState { *; }
-keep class com.aistudio.nasheet.app.ui.SyncState$* { *; }
-keep class com.aistudio.nasheet.app.ui.CloudSyncStatus { *; }

# ---- Suppress common warnings ----
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
