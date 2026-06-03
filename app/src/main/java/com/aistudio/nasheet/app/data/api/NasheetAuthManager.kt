package com.aistudio.nasheet.app.data.api

import android.util.Log

object NasheetAuthManager {
    private const val TAG = "NasheetAuthManager"

    fun isFirebaseAvailable(): Boolean = false

    fun signUp(
        email: String,
        password: String,
        displayName: String,
        onSuccess: (user: Any?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.w(TAG, "Firebase unavailable; simulating offline registration.")
        onSuccess(null)
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: (user: Any) -> Unit,
        onFailure: (String) -> Unit
    ) {
        onFailure("المزامنة السحابية غير مفعّلة في هذا الإصدار.")
    }

    fun signOut() {
        Log.d(TAG, "signOut called (offline mode)")
    }

    fun getSignedInUser(): Any? = null
}
