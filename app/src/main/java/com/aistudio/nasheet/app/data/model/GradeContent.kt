package com.aistudio.nasheet.app.data.model

data class GradeContent(
    val words: List<Pair<String, String>>,  // (كلمة بتشكيل، إيموجي)
    val sentences: List<String>,
    val letters: List<String>,              // للصف الأول فقط
    val mathRange: IntRange,
    val mathOps: List<String>,
    val maxAttempts: Int,
    val timeSeconds: Int,
    val difficulty: Float                   // 0.0 → 1.0
)

fun getContentForGrade(schoolLevel: String): GradeContent = when {

    schoolLevel.contains("الأول") || schoolLevel.contains("أولى") || schoolLevel.contains("أول") -> GradeContent(
        words = listOf(
            "بَابٌ" to "🚪", "كِتَابٌ" to "📖", "يَدٌ" to "🤚",
            "أَسَدٌ" to "🦁", "دَجَاجَةٌ" to "🐔", "قَلَمٌ" to "✏️"
        ),
        sentences = listOf("هَذَا بَابٌ", "هَذِهِ يَدٌ"),
        letters = listOf("ب","ت","ث","ج","ح","خ","د","ذ","ر","ز"),
        mathRange = 1..20,
        mathOps = listOf("+"),
        maxAttempts = 3,
        timeSeconds = 90,
        difficulty = 0.2f
    )

    schoolLevel.contains("الثاني") || schoolLevel.contains("ثانية") || schoolLevel.contains("ثاني") -> GradeContent(
        words = listOf(
            "مَدْرَسَةٌ" to "🏫", "حَقِيبَةٌ" to "🎒",
            "فَرَاشَةٌ" to "🦋", "سَيَّارَةٌ" to "🚗",
            "شَجَرَةٌ" to "🌳", "سَمَكَةٌ" to "🐟"
        ),
        sentences = listOf(
            "ذَهَبَ الوَلَدُ إِلَى المَدْرَسَةِ",
            "أَكَلَتِ البِنْتُ التُّفَّاحَةَ"
        ),
        letters = emptyList(),
        mathRange = 1..100,
        mathOps = listOf("+", "-"),
        maxAttempts = 4,
        timeSeconds = 60,
        difficulty = 0.4f
    )

    schoolLevel.contains("الثالث") || schoolLevel.contains("ثالثة") || schoolLevel.contains("ثالث") -> GradeContent(
        words = listOf(
            "مُعَلِّمَةٌ" to "👩🏫", "مَكْتَبَةٌ" to "📚",
            "حَدِيقَةٌ" to "🌻", "عَصْفُورٌ" to "🐦",
            "قُنْفُذٌ" to "🦔", "ضِفْدَعٌ" to "🐸"
        ),
        sentences = listOf(
            "يَلْعَبُ الأَطْفَالُ فِي الحَدِيقَةِ",
            "تَقْرَأُ المُعَلِّمَةُ الكِتَابَ",
            "ذَهَبُوا إِلَى السُّوقِ مَعًا"
        ),
        letters = emptyList(),
        mathRange = 1..1000,
        mathOps = listOf("+", "-", "×"),
        maxAttempts = 5,
        timeSeconds = 45,
        difficulty = 0.6f
    )

    schoolLevel.contains("الرابع") || schoolLevel.contains("رابعة") || schoolLevel.contains("رابع") -> GradeContent(
        words = listOf(
            "مُسْتَشْفَىً" to "🏥", "مُهَنْدِسٌ" to "👷",
            "مُخْتَبَرٌ" to "🔬", "مُتْحَفٌ" to "🏛️",
            "مُجْتَمَعٌ" to "👥", "مُحِيطٌ" to "🌊"
        ),
        sentences = listOf(
            "كَتَبَ التِّلْمِيذُ الدَّرْسَ فِي الكُرَّاسَةِ",
            "تُحِبُّ سَارَةُ أَنْ تَرْسُمَ الأَشْكَالَ الجَمِيلَةَ",
            "سَافَرَ الأَبُ إِلَى الجَزَائِرِ العَاصِمَةِ"
        ),
        letters = emptyList(),
        mathRange = 1..10000,
        mathOps = listOf("+", "-", "×", "÷"),
        maxAttempts = 5,
        timeSeconds = 35,
        difficulty = 0.75f
    )

    schoolLevel.contains("الخامس") || schoolLevel.contains("خامسة") || schoolLevel.contains("خامس") -> GradeContent(
        words = listOf(
            "اِسْتِقْلَالٌ" to "🇩🇿", "حَضَارَةٌ" to "🏺",
            "دِيمُقْرَاطِيَّةٌ" to "🗳️", "مُوَاطَنَةٌ" to "📜",
            "اِقْتِصَادٌ" to "💹", "جُغْرَافِيَا" to "🗺️"
        ),
        sentences = listOf(
            "الجَزَائِرُ بِلَادٌ جَمِيلَةٌ تَقَعُ فِي شَمَالِ أَفْرِيقِيَا",
            "العِلْمُ نُورٌ وَالجَهْلُ ظَلَامٌ فَاجْتَهِدْ لِتَنَالَ المَعْرِفَةَ",
            "يَعْمَلُ الجَزَائِرِيُّونَ مِنْ أَجْلِ بِنَاءِ وَطَنِهِمْ"
        ),
        letters = emptyList(),
        mathRange = 1..100000,
        mathOps = listOf("+", "-", "×", "÷", "كسور", "%"),
        maxAttempts = 6,
        timeSeconds = 25,
        difficulty = 1.0f
    )

    else -> getContentForGrade("الثاني ابتدائي") // افتراضي
}
