package com.aistudio.nasheet.app.data.api

import com.aistudio.nasheet.app.data.database.ActivityLog
import com.aistudio.nasheet.app.data.database.ChildProfile
import com.aistudio.nasheet.app.data.database.DiagnosticResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.aistudio.nasheet.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.CertificatePinner
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// Data Transfer Objects for Sync requests
data class SyncResponse(val success: Boolean, val message: String, val syncedAt: Long = System.currentTimeMillis())

interface NasheetApiService {
    @POST("api/v1/sync/profile")
    suspend fun syncProfile(
        @Header("Authorization") apiKey: String,
        @Body profile: ChildProfile
    ): SyncResponse

    @POST("api/v1/sync/logs")
    suspend fun syncActivityLogs(
        @Header("Authorization") apiKey: String,
        @Body logs: List<ActivityLog>
    ): SyncResponse

    @POST("api/v1/sync/diagnostic")
    suspend fun syncDiagnosticResults(
        @Header("Authorization") apiKey: String,
        @Body results: List<DiagnosticResult>
    ): SyncResponse
}

object NasheetApiClient {
    private var currentBaseUrl = "https://api.nasheet.app/"

    fun getBaseUrl(): String = currentBaseUrl

    fun updateBaseUrl(newUrl: String) {
        val formattedUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        currentBaseUrl = formattedUrl
        _service = createService(formattedUrl)
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }
        
        // تفعيل أو إيقاف تثبيت الشهادة (Certificate Pinning)
        // قم بتغيير هذه القيمة إلى true لتفعيل التثبيت الكامل في وضع الإنتاج بعد وضع الـ pins الصحيحة
        val isPinningEnabled = false
        
        val builder = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logging)

        if (isPinningEnabled) {
            val certificatePinner = CertificatePinner.Builder()
                // الـ Pin الأساسي (الشهادة المنشورة حالياً)
                .add("api.nasheet.app", "sha256/47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=")
                // الـ Backup Pin (لتجنب توقف الخدمة مستقبلاً)
                .add("api.nasheet.app", "sha256/47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=")
                .build()
            builder.certificatePinner(certificatePinner)
        }

        builder.build()
    }

    private fun createService(baseUrl: String): NasheetApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NasheetApiService::class.java)
    }

    private var _service: NasheetApiService? = null

    val service: NasheetApiService
        get() {
            if (_service == null) {
                _service = createService(currentBaseUrl)
            }
            return _service!!
        }
}

