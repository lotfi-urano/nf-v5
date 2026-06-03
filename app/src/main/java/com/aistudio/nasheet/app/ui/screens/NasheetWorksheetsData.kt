package com.aistudio.nasheet.app.ui.screens

import androidx.compose.ui.geometry.Offset

data class MissingWordChallenge(
    val wordWithBlank: String,
    val correctAnswer: String,
    val displayForm: String,
    val options: List<String>,
    val correctIdx: Int,
    val emoji: String
)

data class GridStartImageChallenge(
    val emoji: String,
    val name: String,
    val startsWith: Boolean
)

data class LetterWorksheet(
    val letter: String,
    val letterName: String,
    val keyWord: String,
    val keyEmoji: String,
    val guidePoints: List<Offset>,
    val dotPoints: List<Offset>,
    val missingWords: List<MissingWordChallenge>,
    val circlesChallenge: List<GridStartImageChallenge>
)

val worksheets = listOf(
    LetterWorksheet(
        letter = "ن",
        letterName = "النون",
        keyWord = "نَمِرٌ",
        keyEmoji = "🐯",
        guidePoints = listOf(
            Offset(0.2f, 0.4f),
            Offset(0.2f, 0.75f),
            Offset(0.5f, 0.85f),
            Offset(0.8f, 0.75f),
            Offset(0.8f, 0.4f)
        ),
        dotPoints = listOf(
            Offset(0.5f, 0.2f)
        ),
        missingWords = listOf(
            MissingWordChallenge("..ظَّارَاتٌ", "نَظَّارَاتٌ", "نـ", listOf("نـ", "ـنـ", "ن"), 0, "👓"),
            MissingWordChallenge("عِ..بٌ", "عِنَبٌ", "ـنـ", listOf("نـ", "ـنـ", "ن"), 1, "🍇"),
            MissingWordChallenge("حِصَا..", "حِصَانٌ", "ن", listOf("نـ", "ـنـ", "ن"), 2, "🐎")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🐜", "نَمْلَةٌ", true),
            GridStartImageChallenge("🍎", "تُفَّاحَةٌ", false),
            GridStartImageChallenge("🪟", "نَافِذَةٌ", true),
            GridStartImageChallenge("📚", "كُتُبٌ", false),
            GridStartImageChallenge("⭐", "نَجْمَةٌ", true),
            GridStartImageChallenge("🐟", "سَمَكَةٌ", false)
        )
    ),
    LetterWorksheet(
        letter = "ج",
        letterName = "الجيم",
        keyWord = "جَزَرٌ",
        keyEmoji = "🥕",
        guidePoints = listOf(
            Offset(0.25f, 0.3f),
            Offset(0.75f, 0.3f),
            Offset(0.6f, 0.3f),
            Offset(0.3f, 0.55f),
            Offset(0.5f, 0.8f),
            Offset(0.75f, 0.65f)
        ),
        dotPoints = listOf(
            Offset(0.48f, 0.52f)
        ),
        missingWords = listOf(
            MissingWordChallenge("..مَلٌ", "جَمَلٌ", "جـ", listOf("جـ", "ـجـ", "ج"), 0, "🐪"),
            MissingWordChallenge("عِ..لٌ", "عِجْلٌ", "ـجـ", listOf("جـ", "ـجـ", "ج"), 1, "🐄"),
            MissingWordChallenge("بُرْ..", "بُرْجٌ", "ج", listOf("جـ", "ـجـ", "ج"), 2, "🏢")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🍵", "فِنْجَانٌ", false),
            GridStartImageChallenge("🐪", "جَمَلٌ", true),
            GridStartImageChallenge("🔑", "مِفْتَاحٌ", false),
            GridStartImageChallenge("🧀", "جُبْنٌ", true),
            GridStartImageChallenge("✏️", "قَلَمٌ", false),
            GridStartImageChallenge("🔔", "جَرَسٌ", true)
        )
    ),
    LetterWorksheet(
        letter = "خ",
        letterName = "الخاء",
        keyWord = "خُبْزٌ",
        keyEmoji = "🍞",
        guidePoints = listOf(
            Offset(0.25f, 0.35f),
            Offset(0.75f, 0.35f),
            Offset(0.6f, 0.35f),
            Offset(0.3f, 0.58f),
            Offset(0.5f, 0.8f),
            Offset(0.75f, 0.68f)
        ),
        dotPoints = listOf(
            Offset(0.5f, 0.18f)
        ),
        missingWords = listOf(
            MissingWordChallenge("..رُوفٌ", "خَرُوفٌ", "خـ", listOf("خـ", "ـخـ", "خ"), 0, "🐑"),
            MissingWordChallenge("صَ..رٌ", "صَخْرٌ", "ـخـ", listOf("خـ", "ـخـ", "خ"), 1, "🪨"),
            MissingWordChallenge("شَيْ..", "شَيْخٌ", "خ", listOf("خـ", "ـخـ", "خ"), 2, "👴")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🍚", "أُرْزٌ", false),
            GridStartImageChallenge("🍑", "خَوْخٌ", true),
            GridStartImageChallenge("💼", "حَقِيبَةٌ", false),
            GridStartImageChallenge("💍", "خَاتَمٌ", true),
            GridStartImageChallenge("⏰", "سَاعَةٌ", false),
            GridStartImageChallenge("⛺", "خَيْمَةٌ", true)
        )
    ),
    LetterWorksheet(
        letter = "ح",
        letterName = "الحاء",
        keyWord = "حِذَاءٌ",
        keyEmoji = "👟",
        guidePoints = listOf(
            Offset(0.25f, 0.35f),
            Offset(0.75f, 0.35f),
            Offset(0.6f, 0.35f),
            Offset(0.3f, 0.58f),
            Offset(0.5f, 0.8f),
            Offset(0.75f, 0.68f)
        ),
        dotPoints = emptyList(),
        missingWords = listOf(
            MissingWordChallenge("..ذَاءٌ", "حِذَاءٌ", "حـ", listOf("حـ", "ـحـ", "ح"), 0, "👟"),
            MissingWordChallenge("بَ..رٌ", "بَحْرٌ", "ـحـ", listOf("حـ", "ـحـ", "ح"), 1, "🌊"),
            MissingWordChallenge("رِي..", "رِيحٌ", "ح", listOf("حـ", "ـحـ", "ح"), 2, "💨")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🥛", "حَلِيبٌ", true),
            GridStartImageChallenge("📱", "هَاتِفٌ", false),
            GridStartImageChallenge("✏️", "مِقْلَمَةٌ", false),
            GridStartImageChallenge("🐋", "حُوتٌ", true),
            GridStartImageChallenge("🍌", "مَوْزٌ", false),
            GridStartImageChallenge("🫏", "حِمَارٌ", true)
        )
    ),
    LetterWorksheet(
        letter = "ذ",
        letterName = "الذال",
        keyWord = "ذُرَةٌ",
        keyEmoji = "🌽",
        guidePoints = listOf(
            Offset(0.7f, 0.35f),
            Offset(0.4f, 0.55f),
            Offset(0.35f, 0.75f),
            Offset(0.7f, 0.75f)
        ),
        dotPoints = listOf(
            Offset(0.55f, 0.18f)
        ),
        missingWords = listOf(
            MissingWordChallenge("..رَةٌ", "ذُرَةٌ", "ذ", listOf("ذ", "ـذ", "ذ"), 0, "🌽"),
            MissingWordChallenge("حِ..اءٌ", "حِذَاءٌ", "ـذ", listOf("ذ", "ـذ", "ذ"), 1, "👟"),
            MissingWordChallenge("أُسْتَا..", "أُسْتَاذٌ", "ذ", listOf("ذ", "ـذ", "ذ"), 2, "👨‍🏫")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🪙", "ذَهَبٌ", true),
            GridStartImageChallenge("📏", "مِسْطَرَةٌ", false),
            GridStartImageChallenge("🍽️", "صَحْنٌ", false),
            GridStartImageChallenge("🚪", "بَابٌ", false),
            GridStartImageChallenge("🐕", "ذَيْلٌ", true),
            GridStartImageChallenge("🐺", "ذِئْبٌ", true)
        )
    ),
    LetterWorksheet(
        letter = "د",
        letterName = "الدال",
        keyWord = "دَلْوٌ",
        keyEmoji = "🪣",
        guidePoints = listOf(
            Offset(0.7f, 0.3f),
            Offset(0.4f, 0.5f),
            Offset(0.35f, 0.75f),
            Offset(0.7f, 0.75f)
        ),
        dotPoints = emptyList(),
        missingWords = listOf(
            MissingWordChallenge("..بٌّ", "دِبٌّ", "د", listOf("د", "ـد", "د"), 0, "🐻"),
            MissingWordChallenge("دَوْ..َةٌ", "دَوْدَةٌ", "ـد", listOf("د", "ـد", "د"), 1, "🐛"),
            MissingWordChallenge("يَ..", "يَدٌ", "د", listOf("د", "ـد", "د"), 2, "🫲")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge(" Rooster ", "دِيكٌ", true),
            GridStartImageChallenge("🌸", "وَرْدَةٌ", false),
            GridStartImageChallenge("📓", "دَفْتَرٌ", true),
            GridStartImageChallenge("🎈", "بَالُونٌ", false),
            GridStartImageChallenge("🚲", "دَرَّاجَةٌ", true),
            GridStartImageChallenge("🐭", "فَأْرٌ", false)
        )
    ),
    LetterWorksheet(
        letter = "ع",
        letterName = "العين",
        keyWord = "عَيْنٌ",
        keyEmoji = "👁️",
        guidePoints = listOf(
            Offset(0.7f, 0.3f),
            Offset(0.4f, 0.4f),
            Offset(0.65f, 0.5f),
            Offset(0.2f, 0.65f),
            Offset(0.5f, 0.85f),
            Offset(0.75f, 0.75f)
        ),
        dotPoints = emptyList(),
        missingWords = listOf(
            MissingWordChallenge("..نَبٌ", "عِنَبٌ", "عـ", listOf("عـ", "ـعـ", "ع"), 0, "🍇"),
            MissingWordChallenge("مُ..لِّمٌ", "مُعَلِّمٌ", "ـعـ", listOf("عـ", "ـعـ", "ع"), 1, "👨‍🏫"),
            MissingWordChallenge("إِصْبَ..", "إِصْبَعٌ", "ع", listOf("عـ", "ـعـ", "ع"), 2, "💅")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🥩", "لَحْمٌ", false),
            GridStartImageChallenge("🐦", "عُصْفُورٌ", true),
            GridStartImageChallenge("🥚", "بَيْضَةٌ", false),
            GridStartImageChallenge("🍯", "عَسَلٌ", true),
            GridStartImageChallenge("🧴", "عِطْرٌ", true),
            GridStartImageChallenge("🪑", "كُرْسِيٌّ", false)
        )
    ),
    LetterWorksheet(
        letter = "غ",
        letterName = "الغين",
        keyWord = "غَزَالٌ",
        keyEmoji = "🦌",
        guidePoints = listOf(
            Offset(0.7f, 0.35f),
            Offset(0.4f, 0.45f),
            Offset(0.65f, 0.55f),
            Offset(0.2f, 0.7f),
            Offset(0.5f, 0.88f),
            Offset(0.75f, 0.78f)
        ),
        dotPoints = listOf(
            Offset(0.55f, 0.18f)
        ),
        missingWords = listOf(
            MissingWordChallenge("..يْمَةٌ", "غَيْمَةٌ", "غـ", listOf("غـ", "ـغـ", "غ"), 0, "☁️"),
            MissingWordChallenge("بَبَّ..اءٌ", "بَبَّغَاءٌ", "ـغـ", listOf("غـ", "ـغـ", "غ"), 1, "🦜"),
            MissingWordChallenge("صَمْ..", "صَمْغٌ", "غ", listOf("غـ", "ـغـ", "غ"), 2, "🧪")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🐦", "غُرَابٌ", true),
            GridStartImageChallenge("🥄", "مِلْعَقَةٌ", false),
            GridStartImageChallenge("🌿", "غُصْنٌ", true),
            GridStartImageChallenge("✏️", "مِبْرَاةٌ", false),
            GridStartImageChallenge("🧺", "غَسَّالَةٌ", true),
            GridStartImageChallenge("🥔", "بَطَاطَا", false)
        )
    ),
    LetterWorksheet(
        letter = "ز",
        letterName = "الزاي",
        keyWord = "زَرَافَةٌ",
        keyEmoji = "🦒",
        guidePoints = listOf(
            Offset(0.65f, 0.35f),
            Offset(0.55f, 0.55f),
            Offset(0.35f, 0.75f),
            Offset(0.2f, 0.8f)
        ),
        dotPoints = listOf(
            Offset(0.65f, 0.2f)
        ),
        missingWords = listOf(
            MissingWordChallenge("..يْتُونٌ", "زَيْتُونٌ", "ز", listOf("ز", "ـز", "ز"), 0, "🫒"),
            MissingWordChallenge("مِيـ..َانٌ", "مِيزَانٌ", "ـز", listOf("ز", "ـز", "ز"), 1, "⚖️"),
            MissingWordChallenge("مَوْ..", "مَوْزٌ", "ز", listOf("ز", "ـز", "ز"), 2, "🍌")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🍓", "فَرَاوِلَةٌ", false),
            GridStartImageChallenge("🪡", "إِبْرَةٌ", false),
            GridStartImageChallenge("⛵", "زَوْرَقٌ", true),
            GridStartImageChallenge("🪟", "زُجَاجٌ", true),
            GridStartImageChallenge("🛢️", "زَيْتٌ", true),
            GridStartImageChallenge("🦆", "بَطَّةٌ", false)
        )
    ),
    LetterWorksheet(
        letter = "ت",
        letterName = "التاء",
        keyWord = "تِمْسَاحٌ",
        keyEmoji = "🐊",
        guidePoints = listOf(
            Offset(0.25f, 0.5f),
            Offset(0.25f, 0.75f),
            Offset(0.75f, 0.75f),
            Offset(0.75f, 0.5f)
        ),
        dotPoints = listOf(
            Offset(0.42f, 0.35f),
            Offset(0.58f, 0.35f)
        ),
        missingWords = listOf(
            MissingWordChallenge("..مْرٌ", "تَمْرٌ", "تـ", listOf("تـ", "ـتـ", "ت"), 0, "🌴"),
            MissingWordChallenge("دَفْ..رٌ", "دَفْتَرٌ", "ـتـ", listOf("تـ", "ـتـ", "ت"), 1, "📓"),
            MissingWordChallenge("حُو..", "حُوتٌ", "ت", listOf("تـ", "ـتـ", "ت"), 2, "🐋")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🪞", "مِرْآةٌ", false),
            GridStartImageChallenge("🫐", "تُوتٌ", true),
            GridStartImageChallenge("🧅", "بَصَلٌ", false),
            GridStartImageChallenge("👑", "تَاجٌ", true),
            GridStartImageChallenge("🐑", "خَرُوفٌ", false),
            GridStartImageChallenge("🐉", "تِنِّينٌ", true)
        )
    ),
    LetterWorksheet(
        letter = "ض",
        letterName = "الضاد",
        keyWord = "ضِفْدَعٌ",
        keyEmoji = "🐸",
        guidePoints = listOf(
            Offset(0.2f, 0.7f),
            Offset(0.45f, 0.45f),
            Offset(0.7f, 0.7f),
            Offset(0.2f, 0.7f),
            Offset(0.2f, 0.85f),
            Offset(0.4f, 0.85f),
            Offset(0.4f, 0.7f)
        ),
        dotPoints = listOf(
            Offset(0.45f, 0.3f)
        ),
        missingWords = listOf(
            MissingWordChallenge("..رْسٌ", "ضِرْسٌ", "ضـ", listOf("ضـ", "ـضـ", "ض"), 0, "🦷"),
            MissingWordChallenge("خُ..رٌ", "خُضَرٌ", "ـضـ", listOf("ضـ", "ـضـ", "ض"), 1, "🥦"),
            MissingWordChallenge("أَرْ..", "أَرْضٌ", "ض", listOf("ضـ", "ـضـ", "ض"), 2, "🌍")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🦷", "ضِرْسٌ", true),
            GridStartImageChallenge("👮", "ضَابِطٌ", true),
            GridStartImageChallenge("🚗", "سَيَّارَةٌ", false),
            GridStartImageChallenge("🍬", "سُكَّرٌ", false),
            GridStartImageChallenge("🍞", "خُبْزٌ", false),
            GridStartImageChallenge("🐺", "ضَبُعٌ", true)
        )
    ),
    LetterWorksheet(
        letter = "ص",
        letterName = "الصاد",
        keyWord = "صَارُوخٌ",
        keyEmoji = "🚀",
        guidePoints = listOf(
            Offset(0.2f, 0.7f),
            Offset(0.45f, 0.45f),
            Offset(0.7f, 0.7f),
            Offset(0.2f, 0.7f),
            Offset(0.2f, 0.85f),
            Offset(0.4f, 0.85f),
            Offset(0.4f, 0.7f)
        ),
        dotPoints = emptyList(),
        missingWords = listOf(
            MissingWordChallenge("..قْرٌ", "صَقْرٌ", "صـ", listOf("صـ", "ـصـ", "ص"), 0, "🦅"),
            MissingWordChallenge("قَ..رٌ", "قَصْرٌ", "ـصـ", listOf("صـ", "ـصـ", "ص"), 1, "🏰"),
            MissingWordChallenge("مِقَ..", "مِقَصٌّ", "ص", listOf("صـ", "ـصـ", "ص"), 2, "✂️")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🪳", "صُرْصُورٌ", true),
            GridStartImageChallenge("🏮", "فَانُوسٌ", false),
            GridStartImageChallenge("🐚", "صَدَفَةٌ", true),
            GridStartImageChallenge("📿", "سُبْحَةٌ", false),
            GridStartImageChallenge("📦", "صُنْدُوقٌ", true),
            GridStartImageChallenge("👕", "قَمِيصٌ", false)
        )
    ),
    LetterWorksheet(
        letter = "و",
        letterName = "الواو",
        keyWord = "وَرْدَةٌ",
        keyEmoji = "🌹",
        guidePoints = listOf(
            Offset(0.55f, 0.45f),
            Offset(0.7f, 0.35f),
            Offset(0.55f, 0.25f),
            Offset(0.4f, 0.35f),
            Offset(0.55f, 0.45f),
            Offset(0.45f, 0.65f),
            Offset(0.25f, 0.8f)
        ),
        dotPoints = emptyList(),
        missingWords = listOf(
            MissingWordChallenge("..رْدَةٌ", "وَرْدَةٌ", "و", listOf("و", "ـو", "و"), 0, "🌹"),
            MissingWordChallenge("إِ..زَةٌ", "إِوَزَّةٌ", "ـو", listOf("و", "ـو", "و"), 1, "🪿"),
            MissingWordChallenge("دَلْ..", "دَلْوٌ", "و", listOf("و", "ـو", "و"), 2, "🪣")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("1️⃣", "وَاحِدٌ", true),
            GridStartImageChallenge("👦", "وَلَدٌ", true),
            GridStartImageChallenge("🍉", "بِطِّيخٌ", false),
            GridStartImageChallenge("👗", "فُسْتَانٌ", false),
            GridStartImageChallenge("🛋️", "وِسَادَةٌ", true),
            GridStartImageChallenge("🏠", "بَيْتٌ", false)
        )
    ),
    LetterWorksheet(
        letter = "ي",
        letterName = "الياء",
        keyWord = "يَدٌ",
        keyEmoji = "🫲",
        guidePoints = listOf(
            Offset(0.7f, 0.35f),
            Offset(0.5f, 0.25f),
            Offset(0.4f, 0.4f),
            Offset(0.55f, 0.55f),
            Offset(0.25f, 0.7f),
            Offset(0.5f, 0.85f),
            Offset(0.7f, 0.75f)
        ),
        dotPoints = listOf(
            Offset(0.42f, 0.94f),
            Offset(0.58f, 0.94f)
        ),
        missingWords = listOf(
            MissingWordChallenge("..قْطِينٌ", "يَقْطِينٌ", "يـ", listOf("يـ", "ـيـ", "ي"), 0, "🎃"),
            MissingWordChallenge("فِـ..لٌ", "فِيلٌ", "ـيـ", listOf("يـ", "ـيـ", "ي"), 1, "🐘"),
            MissingWordChallenge("شَا..", "شَايٌ", "ي", listOf("يـ", "ـيـ", "ي"), 2, "🍵")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🕊️", "حَمَامَةٌ", false),
            GridStartImageChallenge("🏫", "سبُّورَةٌ", false),
            GridStartImageChallenge("🍊", "يُوسُفِيٌّ", true),
            GridStartImageChallenge("🪑", "مَكْتَبٌ", false),
            GridStartImageChallenge("💎", "يَاقُوتٌ", true),
            GridStartImageChallenge("🥬", "مَلْفُوفٌ", false)
        )
    ),
    LetterWorksheet(
        letter = "ق",
        letterName = "القاف",
        keyWord = "قِرْدٌ",
        keyEmoji = "🐒",
        guidePoints = listOf(
            Offset(0.6f, 0.35f),
            Offset(0.75f, 0.25f),
            Offset(0.6f, 0.15f),
            Offset(0.45f, 0.25f),
            Offset(0.6f, 0.35f),
            Offset(0.5f, 0.35f),
            Offset(0.25f, 0.5f),
            Offset(0.25f, 0.75f),
            Offset(0.75f, 0.75f),
            Offset(0.75f, 0.45f)
        ),
        dotPoints = listOf(
            Offset(0.45f, 0.05f),
            Offset(0.6f, 0.05f)
        ),
        missingWords = listOf(
            MissingWordChallenge("..فْلٌ", "قِفْلٌ", "قـ", listOf("قـ", "ـقـ", "ق"), 0, "🔒"),
            MissingWordChallenge("سَ..فٌ", "سَقْفٌ", "ـقـ", listOf("قـ", "ـقـ", "ق"), 1, "🏠"),
            MissingWordChallenge("سُو..", "سُوقٌ", "ق", listOf("قـ", "ـقـ", "ق"), 2, "🏪")
        ),
        circlesChallenge = listOf(
            GridStartImageChallenge("🌾", "قَمْحٌ", true),
            GridStartImageChallenge("🐿️", "سِنْجَابٌ", false),
            GridStartImageChallenge("❤️", "قَلْبٌ", true),
            GridStartImageChallenge("🧣", "وِشَاحٌ", false),
            GridStartImageChallenge("🥊", "قُفَّازَاتٌ", true),
            GridStartImageChallenge("🥬", "خَسٌّ", false)
        )
    )
)
