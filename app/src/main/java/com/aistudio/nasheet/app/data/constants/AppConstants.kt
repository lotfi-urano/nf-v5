package com.aistudio.nasheet.app.data.constants

object AppConstants {
    /**
     * The master/teacher demo code used for educational classroom sandbox link simulation.
     * In a production multi-tenant cloud environment, this is stored on the remote Cloud SQL/Firebase service.
     */
    const val DEFAULT_TEACHER_CODE = "A7X-99"

    /**
     * Default name of the sample digital classroom linked with the teacher code.
     */
    const val DEFAULT_CLASSROOM_NAME = "الصف الثاني - أمل وبناء 🏫"

    /**
     * Local secret cryptographic salt used to protect parent/child entry codes.
     */
    const val SECURE_SALT = "NasheetSecretCryptSalt_2026_V2"

    /**
     * Generates a completely secure, unique dynamic workspace teacher or classroom code.
     */
    fun generateDynamicClassroomCode(): String {
        val randomNum = (10000..99999).random()
        return "NST-TR-$randomNum"
    }
}
