package com.aistudio.nasheet.app.data.security

import java.security.MessageDigest

object SecurityUtils {
    /**
     * Hashes a PIN or password string using SHA-256 with an added salt (to protect against rainbow table attacks).
     */
    fun hashString(input: String, salt: String = "NasheetSecretSalt2026"): String {
        return try {
            val bytes = (input + salt).toByteArray(Charsets.UTF_8)
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            digest.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            // Fallback to simple sanitization if MessageDigest is unavailable (highly unlikely in Android JVM)
            input.hashCode().toString()
        }
    }

    /**
     * Verifies if a raw input PIN matches a pre-hashed PIN.
     */
    fun verifyPin(input: String, hashed: String): Boolean {
        if (hashed.isBlank()) return false
        val computed = hashString(input)
        return computed == hashed
    }
}
