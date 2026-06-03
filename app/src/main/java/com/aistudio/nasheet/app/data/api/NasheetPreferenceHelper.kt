package com.aistudio.nasheet.app.data.api

import android.content.Context

object NasheetPreferenceHelper {
    private const val PREFS_NAME = "NasheetPrefs"
    private const val KEY_BASE_URL = "api_base_url"

    fun getBaseUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_BASE_URL, "https://api.nasheet.app/") ?: "https://api.nasheet.app/"
    }

    fun saveBaseUrl(context: Context, url: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_BASE_URL, url).apply()
    }
}
