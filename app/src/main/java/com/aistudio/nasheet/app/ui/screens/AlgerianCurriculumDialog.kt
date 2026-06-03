package com.aistudio.nasheet.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.nasheet.app.ui.theme.CharcoalText

// ==========================================
// ALGERIAN EDUCATIONAL CURRICULUM 2026 (المنهج الدراسي الجزائري)
// ==========================================

data class AlgLesson(
    val title: String,
    val subject: String,
    val subjectColor: Color,
    val emoji: String,
    val content: String,
    val speakText: String
)

data class AlgQuestion(
    val text: String,
    val options: List<String>,
    val correctIdx: Int,
    val hint: String
)

data class AlgGradeData(
    val id: Int,
    val name: String,
    val icon: String,
    val welcomeMessage: String,
    val lessons: List<AlgLesson>,
    val questions: List<AlgQuestion>
)

val algerianGradesLessonsData = listOf(
    AlgGradeData(
        id = 1,
        name = "عمر 6 - 7 سنوات 🎒",
        icon = "🎒",
        welcomeMessage = "مرحباً يا بطل! هنا تتعلم مهارات التأسيس لعمر 6 و 7 سنوات: الحركات والحساب وآداب المعاملة لتكون عبقرياً ونشيطاً!",
        lessons = listOf(
            AlgLesson(
                title = "حكايات الحروف والحركات اللطيفة",
                subject = "اللغة العربية",
                subjectColor = Color(0xFF0D9488),
                emoji = "📖",
                content = "سنتعلم الحركات الثلاث الممتعة:\n- الفَتْحَة ( َ) ترفع فمنا للأعلى مثل: دَرَسَ 📕.\n- الضَّمَّة ( ُ) تضم شفتينا مثل: كُتِبَ ✍️.\n- الكَسْرَة ( ِ) تسحب فمنا للأسفل مثل: شَرِبَ 🥛.\n- السُّكون ( ْ) يوقف الحرف بهدوء مثل: أَبْ.\nهيا نتدرب على مخارجها السليمة يا بطل!",
                speakText = "سنتعلم الحركات الثلاث الممتعة. الفتحة ترفع فمنا للأعلى مثل دَرَسَ. الضمة تضم شفتينا مثل كُتِبَ. الكسرة تسحب فمنا للأسفل مثل شَرِبَ. السكون يوقف الحرف بهدوء مثل أَبْ."
            ),
            AlgLesson(
                title = "الأرقام اللطيفة والجمع حتى 20",
                subject = "الرياضيات",
                subjectColor = Color(0xFF4F46E5),
                emoji = "➕",
                content = "عزيزي الصغير، الأرقام هي أصدقاؤنا! لكي نجمع الأعداد نستخدم لبنات الحساب البسيطة:\nمثال: إذا كان لديك 5 حبات حلوى 🍬، وأعطتك المعلمة 3 حبات أخرى، فكم يصبح لديك؟\nالحل: 5 + 3 = 8 حبات حلوى شهية وسحرة!",
                speakText = "عزيزي الصغير، الأرقام هي أصدقاؤنا. لكي نجمع الأعداد نستخدم لبنات الحساب البسيطة. مثال: إذا كان لديك خمس حبات حلوى، وأعطتك المعلمة ثلاثة حبات أخرى، فكم يصبح لديك؟ الحل هو: خمسة زائد ثلاثة يساوي ثمانية حبات حلوى."
            ),
            AlgLesson(
                title = "آداب التحية والترحيب في مدرستي",
                subject = "التربية المدنية",
                subjectColor = Color(0xFFD97706),
                emoji = "🤝",
                content = "المسلم الصغير يقرأ السلام دائماً! عندما ندخل إلى بيتنا أو إلى قسمنا الدراسي نقول بصوت دافئ ومبتسم:\n\"السَّلامُ عَلَيْكُمْ وَرَحْمَةُ اللهِ وَبَرَكَاتُهُ\"\nفهذا ينشر المحبة والسرور والبركة بين الجميع.",
                speakText = "الطفل المؤدب يقرأ السلام دائماً. عندما ندخل إلى بيتنا أو إلى قسمنا الدراسي نقول بصوت واضح ومبتسم: السلام عليكم ورحمة الله وبركاته، فتنشر المحبة والابتسامة البريئة."
            )
        ),
        questions = listOf(
            AlgQuestion(
                text = "ما هي الحركة الموجودة فوق حرف الميم في كلمة 'مَدْرَسَة'؟",
                options = listOf("الفتحة ( َ)", "الضمة ( ُ)", "الكسرة ( ِ)"),
                correctIdx = 0,
                hint = "ننطق مَ بفتح الفم للأعلى!"
            ),
            AlgQuestion(
                text = "ما هو مجموع العملية الحسابية البسيطة: 7 + 5؟",
                options = listOf("11", "12", "13"),
                correctIdx = 1,
                hint = "احسب أصابعك بعد الرقم سبعة!"
            ),
            AlgQuestion(
                text = "التحية اللائقة التي نقولها لمعلمتنا عند دخول القسم هي:",
                options = listOf("الضحك والصراخ بصوت دوشة", "السلام عليكم ورحمة الله وبركاته", "تصبحين على خير يا أستاذة القديرة"),
                correctIdx = 1,
                hint = "التحية الإسلامية تدعو للسلام والمحبة"
            )
        )
    ),
    AlgGradeData(
        id = 2,
        name = "عمر 8 سنوات 📖",
        icon = "📖",
        welcomeMessage = "أهلاً بك يا بطل بعمر 8 سنوات المتميز! لقد كبِرت وصار وعيك أكثر بريقاً لنتعرف معاً على مهارات لغوية وحسابية شيّقة.",
        lessons = listOf(
            AlgLesson(
                title = "التاء المفتوحة والمربوطة في الكلمات",
                subject = "اللغة العربية",
                subjectColor = Color(0xFF0D9488),
                emoji = "✏️",
                content = "كيف ننطق ونكتب التاء؟\n1. التَّاء المَرْبُوطَة (ة / ـة): تُكتب في الأسماء المؤنثة، وتنطق هاء عند التوقف عليها مثل: (مَدْرَسَة) ننطقها (مدرسهْ).\n2. التَّاء المَفْتُوحَة (ت): تُكتب في الأفعال دائماً، وفي بعض الأسماء، وتنطق تاء دائماً مثل: (كَتَبْتُ، بَيْت).\nتأمل هذا الفرق جيدا يا بطل!",
                speakText = "كيف ننطق ونكتب التاء؟ التاء المربوطة تكتب في الأسماء المؤنثة وتنطق هاء عند الوقف مثل مدرسة. والملقبة بالتاء المفتوحة تكتب في الأفعال دائما وفي بعض الأسماء مثل كتبت وبيت وتنطق تاء في جميع الأحوال."
            ),
            AlgLesson(
                title = "الجمع والطرح الحسابي الذهني حتى 100",
                subject = "الرياضيات",
                subjectColor = Color(0xFF4F46E5),
                emoji = "📊",
                content = "نستخدم التفكيك والتركيب الحسابي لتسهيل العمليات الكبيرة بدون مجهود عناء:\n- مثال للجمع: 35 + 24 = (30+20) + (5+4) = 50 + 9 = 59.\n- مثال للطرح: 87 - 15 = (80-10) + (7-5) = 70 + 2 = 72.\nتدرب ذهنياً لتصبح سريعاً!",
                speakText = "نستخدم التفكيك والتركيب لتسهيل العمليات الحسابية الكبيرة. مثال للجمع: خمسة وثلاثون زائد أربعة وعشرون يساوي تسعة وخمسين. ومثال لطرح خمسة عشر من سبعة وثمانين يعطي اثنين وسبعين."
            ),
            AlgLesson(
                title = "المحافظة على بيئة ومحيط قسمي",
                subject = "التربية المدنية",
                subjectColor = Color(0xFFD97706),
                emoji = "🧹",
                content = "المدرسة هي بيتنا الثاني وموضع كرامتنا وعلمنا! من الواجبات الأساسية للتلميذ الذكي بنشاط:\n- رمي المهملات في سلتها المخصصة ونبذ القمامة.\n- المحافظة على الطاولات والجدران نظيفة بلا رسم عشوائي.\n- سقي نباتات الفناء لتبقى خضراء وجميلة وجمالية.",
                speakText = "المدرسة هي بيتنا الثاني وعنوان نظافتنا. من واجب التلميذ الذكي والنشيط رمي الأوراق في سلتها المخصصة والمحافظة على طاولتنا المدرسية نظيفة وسقي النباتات بالفناء."
            )
        ),
        questions = listOf(
            AlgQuestion(
                text = "كيف تُكتب كلمة 'فَرَاشَـ...' في نهايتها بالتأسيس اللغوي السليم؟",
                options = listOf("فَرَاشَة (ة)", "فَرَاشَت (ت)", "فَرَاشَهـ (هـ)"),
                correctIdx = 0,
                hint = "إذا سكنت في نهايتها تنطق هاء: فراشهْ!"
            ),
            AlgQuestion(
                text = "احسب الطرح الذهني التالي لذكائك السريع: 56 - 12 =",
                options = listOf("42", "44", "46"),
                correctIdx = 1,
                hint = "اطرح الآحاد من الآحاد والعشرات من العشرات!"
            ),
            AlgQuestion(
                text = "رأيت زميلك يرسم بأقلام الألوان على سطح الطاولة المدرسية. ماذا تفعل؟",
                options = listOf("أشجعه وأرسم معه شخصيات خيالية", "أنصحه بلطف أن يرسم في كراسه ويحافظ على نظافة القسم", "أتجاهل الأمر تماماً وأضحك بصوت صاخب"),
                correctIdx = 1,
                hint = "الطفل الرائد يحمي طاولاته ومدرسته الجميلة."
            )
        )
    ),
    AlgGradeData(
        id = 3,
        name = "عمر 9 سنوات 📐",
        icon = "📐",
        welcomeMessage = "مرحباً يا بطل بعمر 9 سنوات الذكي! ها قد بدأنا القواعد الإملائية وجدول ضرب الأرقام الممتع وصداقة الأخلاق المتكاملة!",
        lessons = listOf(
            AlgLesson(
                title = "رحلة الجملة الاسمية والجميلة والجملة الفعلية",
                subject = "اللغة العربية",
                subjectColor = Color(0xFF0D9488),
                emoji = "🎪",
                content = "الكلمات تجتمع لتبني لنا جملاً ذات معنى وتعبير سليم:\n1. الجُمْلَة الاسْمِيَّة: تبدأ باسم مفيد مثل: (الشَّمْسُ مُشْرِقَةٌ)، ولها ركنان: المبتدأ المرفوع والخبر المرفوع.\n2. الجُمْلَة الفِعْلِيَّة: تبدأ بفعل حركي نابض بالنشاط والحياة مثل: (يَسْبَحُ السَّمَكُ فِي المَاءِ).\nرائعة هي لغتنا العربية السامية!",
                speakText = "تنقسم الجمل إلى نوعين: الجملة الاسمية التي تبدأ باسم مثل الشمس مشرقة، وتتكون من مبتدأ وخبر. والجملة الفعلية التي تبدأ بفعل يعبر عن حركة أو عمل مثل يسبح السمك في الماء."
            ),
            AlgLesson(
                title = "سر جدول الضرب البسيط حتى 1000",
                subject = "الرياضيات",
                subjectColor = Color(0xFF4F46E5),
                emoji = "✖️",
                content = "الضرب هو عملية تكرار جمع ذكي وسريع!\nبدلاً من حساب (4 + 4 + 4 + 4 + 4) المجهد، نقول ببساطة:\n4 في 5 وتكتب (4 × 5) وهي تعني تكرار الرقم 4 خمس مرات، ليكون الناتج الفوري والسحري: 20!\nحفظ جداول الضرب ينشط ذكاءك ويوفر وقتك.",
                speakText = "الضرب هو تكرار جذاب وحساب ذكي للجمع السريع. بدلاً من حساب أربعة زائد أربعة زائد أربعة زائد أربعة وهو مجهد، نقول ببساطة أربعة في خمسة ويساوي عشرين! جدول الضرب يمنحك سرعة فائقة."
            ),
            AlgLesson(
                title = "آداب التحدث والحوار المثمر المبدع",
                subject = "التربية المدنية",
                subjectColor = Color(0xFFD97706),
                emoji = "🗣️",
                content = "لكي نكسب محبة واحترام الجميع، يعلمنا المنهج الجزائر آداب الحوار المشرقة والمفيدة:\n- التحدث بصوت منخفض ومؤدب هادئ دون صراخ وعصبية.\n- عدم مقاطعة كلام الآخرين والإنصات لهم باحترام حتى ينهوا حديثهم.\n- استخدام كلمات الشكر واللطف والتقدير لكل من والدينا وأساتذتنا.",
                speakText = "لكي نكسب محبة الجميع يعلمنا المنهج الجزائري آداب الحوار المشرقة. التحدث بالصوت الهادئ المريح، عدم مقاطعة الصديق أو المعلم، واستخدام كلمات الشكر المليئة باللطف والتقدير."
            )
        ),
        questions = listOf(
            AlgQuestion(
                text = "ما نوع الجملة التالية بالتعبير: 'قَرَأَ البَطَلُ قِصَّةً جَمِيلَةً'؟",
                options = listOf("جملة اسمية تبدأ باسم علم", "جملة فعلية تبدأ بحدث فعلي", "جملة ناقصة غير مفهومة المعنى"),
                correctIdx = 1,
                hint = "انظر للكلمة الأولى 'قَرَأَ' هل هي اسم أم فعل?"
            ),
            AlgQuestion(
                text = "ما هو حاصل ضرب العملية الحسابية: 6 × 7؟",
                options = listOf("42", "48", "36"),
                correctIdx = 0,
                hint = "تكرار الستة سبع مرات متتالية!"
            ),
            AlgQuestion(
                text = "عندما يختلف معك صديقك في اللعب والتنافس، التصرف اللائق والذكي هو:",
                options = listOf("أصرخ في وجهه وأقاطعه عن الكلام", "أحاوره بهدوء وأستمع إلى وجهة نظره بكل احترام وشجاعة", "أبكي وأخرب الألعاب وأتنمر عليه"),
                correctIdx = 1,
                hint = "الحوار الهادئ يحل كل المسائل والألعاب"
            )
        )
    ),
    AlgGradeData(
        id = 4,
        name = "عمر 10 سنوات 🏛️",
        icon = "🏛️",
        welcomeMessage = "مرحباً يا بطل بعمر 10 سنوات العبقري! هنا نتعمق في كسور الرياضيات، القسمة المركبة، وجغرافيا معالم هذا الوطن الشامخ!",
        lessons = listOf(
            AlgLesson(
                title = "حروف الجر وأثر الفاعل والمفعول",
                subject = "اللغة العربية",
                subjectColor = Color(0xFF0D9488),
                emoji = "📚",
                content = "دعنا نتعلم تركيباً نحوياً راقياً ينير الفصاحة:\n- حُرُوف الجَر: (مِنْ، إِلَى، عَنْ، عَلَى، فِي، كَـ، لَـ، بِـ)، تجر الاسم بعدها بالكسرة المشرقة (في المَدْرَسَةِ).\n- الفَاعِل: هو من قام بالفعل ويكون مرفوعاً بالضمة الظاهرة: (نَجَحَ التِّلْمِيذُ).\n- المَفْعُول بِه: هو الذي وقع عليه فعل الفاعل ويكون منصوباً بالفتحة: (قَرَأَ البَطَلُ الكِتَابَ).\nلغة الضاد بحر زاخر!",
                speakText = "تعال نتعلم المهارات النحوية. حروف الجر مثل من وإلى وفي، تجعل الاسم المجرور بعدها منتهياً بالكسرة. والفاعل هو من قام بالعمل ويكون مرفوعاً بالضمة مثل نجح التلميذ، أما المفعول به فيأتي منصوباً بالفتحة مثل قرأ الولد الكتاب."
            ),
            AlgLesson(
                title = "الأعداد الكبيرة وعالم العمليات الأربع الشامل",
                subject = "الرياضيات",
                subjectColor = Color(0xFF4F46E5),
                emoji = "📐",
                content = "في الصف الرابع، نطوع الأعداد والكميات الكبيرة بتنظيم الخانات:\n- ننجز الجمع والطرح والضرب بالخوارزميات السليمة.\n- القِسْمَة الإقْلِيدِيَّة: هي تقسيم كمية إلى حصص متساوية مع حساب المتبقي بدقة.\n- مثال: نقسم 63 تفاحة على 3 تلاميذ بالتساوي.\nالحل: 63 ÷ 3 = 21 تفاحة لكل تلميذ وبصفر باقٍ!",
                speakText = "في الصف الرابع نتعامل مع الأعداد الكبيرة بأقسامها بالتنظيم السليم. القسمة بالتساوي تسمى القسمة الإقليدية. مثال: نقسم ثلاثة وستين تفاحة على ثلاثة تلاميذ بالتساوي، فيأخذ كل تلميذ واحد وعشرين تفاحة ولا يتبقى شيء."
            ),
            AlgLesson(
                title = "معالم الجزائر التاريخية وجبال الأوراس الشامخة",
                subject = "التاريخ والجغرافيا",
                subjectColor = Color(0xFFD97706),
                emoji = "🇩🇿",
                content = "ثروات الجزائر وتاريخها المجيد مصدر فخرنا الدائم:\n- جِبَال الأَوْرَاس الأزَلِيَّة: هي قلعة الثورة المظفرة ومعقل انطلاق رصاصتها الأولى ضد المستعمر.\n- مَعَالِم أثريَّة: حي القصبة بالعاصمة التاريخية، وآثار تيمقاد الرومانية الخلابة بباتنة.\nدراسة تاريخ وطنك تزيدك حكمة وشجاعة!",
                speakText = "تاريخ الجزائر غني بالبطولات. جبال الأوراس الأزلية هي معقل اندلاع الثورة التحريرية المجيدة التي واجهت الاستعمار بكل بسالة. وبجانبها حي القصبة العتيق بالعاصمة وآثار تيمقاد الأثرية المذهلة بباتنة. كن فخوراً بوطنك الشجاع."
            )
        ),
        questions = listOf(
            AlgQuestion(
                text = "ما حركة الاسم المجرور الذي يسبقه حرف الجر (إِلَى) في الجملة؟",
                options = listOf("الضمة في الرفع", "الكسرة في الجر", "الفتحة في النصب"),
                correctIdx = 1,
                hint = "حروف الجر تجر الاسم بالكسرة الظاهرة!"
            ),
            AlgQuestion(
                text = "ما هو ناتج العملية الإقليدية البسيطة التالية: 100 ÷ 5؟",
                options = listOf("20 وبدون باقٍ", "25 والباقي واحد", "15 والباقي اثنان"),
                correctIdx = 0,
                hint = "إذا قمنا بتوزيع ورقة مئة دينار على خمسة أطفال بالتساوي!"
            ),
            AlgQuestion(
                text = "أين تقع جبال الأوراس الشامخة الشهيرة تاريخياً بقلعة الثورة؟",
                options = listOf("في الجزائر الحبيبة", "في القطب المتجمد الشمالي", "خارج الوطن العربي"),
                correctIdx = 0,
                hint = "تقع في قلب بلدنا الجزائر العزيزة الشاهدة على المجامع والبطولات الكبرى."
            )
        )
    ),
    AlgGradeData(
        id = 5,
        name = "عمر 11 - 12 سنة 🇩🇿",
        icon = "🇩🇿",
        welcomeMessage = "مرحباً يا بطل بعمر 11 و 12 سنة المتميز المخلص! أنت تتهيأ لأرقى رحلات التميز والدراسة لتظفر بالقمة وتقرأ لغة الإعجاز والتاريخ العظيم!",
        lessons = listOf(
            AlgLesson(
                title = "عمل إن وأخواتها وتصريف الأفعال المعتلة",
                subject = "اللغة العربية",
                subjectColor = Color(0xFF0D9488),
                emoji = "🖋️",
                content = "قواعد النحو الراقي تبني لسان فصيح وقدرة كتابية باهرة:\n- إِنَّ وَأَخَوَاتُهَا: (إِنَّ، أَنَّ، كَأَنَّ، لَكِنَّ، لَيْتَ، لَعَلَّ) تدخل على الجملة الاسمية فتنصب المبتدأ ويسمى اسمها، وترفع الخبر ويسمى خبرها! مثل: (إِنَّ العِلْمَ نُورٌ مفرط).\n- تصنيف وتصريف الأفعال المعتلة (المثال، الأجوف, الناقص) في كراستنا لإنتاج نصوص راقية المظهر والفائدة.",
                speakText = "قواعد السلسلة الفوقية للغة العربية. إن وأخواتها مثل إنّ، لعلّ، ليتّ، هي حروف ناسخة تدخل على الجملة الاسمية فتنصب المبتدأ وتصنع اسمها مثل إن العلم، وترفع الخبر مثل نور ليضيء العقول."
            ),
            AlgLesson(
                title = "الأعداد العشرية والنسب المئوية الذكية",
                subject = "الرياضيات",
                subjectColor = Color(0xFF4F46E5),
                emoji = "📈",
                content = "الأرقام هي لغة التطور الحديث، فلتتقن القواعد الحسابية الكبيرة:\n- العَدَدُ العُشْرِيّ: عدد يحتوي على فاصلة ناطقة تفصل الصحيح عن أجزائه مثل: (3.14).\n- النِّسْبَةُ المِئَوِيَّة (%): تعبير عن كسر مقامه 100.\n- مثال: توفير 50% يعني خصم نصف قيمة تذكرة الرحلة، لتكون رحلتك أرخص بكثير لذوي الاحتياجات الصديقة!",
                speakText = "الأعداد العشرية تحتوي على الفاصلة التي تفصل العدد الصحيح عن الأجزاء كقولنا أربعة فاصلة خمسة. والنسبة المئوية تعبر عن كمية من المئة كنسبة عشرين بالمئة وهي خصم مالي مميز في السوق. أنت ذكي وتحب الأرقام العالية."
            ),
            AlgLesson(
                title = "اندلاع ثورة أول نوفمبر وعيد الاستقلال المجيد",
                subject = "تاريخ ووطنية",
                subjectColor = Color(0xFFD97706),
                emoji = "🎆",
                content = "أعظم ما يحمله المواطن الجزائري الصغير هو الفخر بتضحيات وطنه الصامد:\n- اندلاع الكفاح المسلح: أول نوفمبر 1954 في جبال الأوراس ومختلف جهات الجزائر للتخلص من الاستعمار الغاشم.\n- عِيدُ الاسْتِقْلَالِ: 5 جويلية (يوليو) 1962 م بعد ثورة ملحمية نلتزم بذكراها ونبذل أرواحنا لخدمتها بنجاح ونشاط!\nمليون ونصف مليون شهيد مهدوا لك طريق النجاح!",
                speakText = "تحتفل الجزائر بنور الاستقلال الكامل في الخامس من جويلية عام ألف وتسعمائة واثنين وستين بعد ثورة ملحمية ضد الاستعمار دامت سبع سنوات ونصف وقدم فيها الوطن الغالي مليون ونصف مليون شهيد بطل لتنعم وطني بالحرية والاستقلال."
            )
        ),
        questions = listOf(
            AlgQuestion(
                text = "ما التغيير الإعرابي الذي يحدثه الحرف الناسخ 'إِنَّ' للجملة الاسمية؟",
                options = listOf("يرفع المبتدأ وينصب الخبر تمام العلم", "ينصب المبتدأ ويرفع الخبر عالي المقام", "يجعل المبتدأ والخبر ساكنين بهدوء"),
                correctIdx = 1,
                hint = "إنّ تنصب الاسم الأول وترفع الثاني!"
            ),
            AlgQuestion(
                text = "كراسة تكلفتها 200 دينار جزائري ومطروح عليها تخفيض 10%، كم تكون القيمة النقدية للتخفيض بالدينار؟",
                options = listOf("10 دينار جزائري", "20 دينار جزائري ومكافأة", "30 دينار جزائري تماماً"),
                correctIdx = 1,
                hint = "خصم 10 دنانير من كل 100 دينار، وبما أن السعر هو 200 دنانير!"
            ),
            AlgQuestion(
                text = "متى استيقظت شمس الحرية وحصلت الجزائر الفخورة على استقلالها الوطني والسيادة الكاملة؟",
                options = listOf("5 جويلية 1962 م بالتضحيات والشهداء", "1 نوفمبر 1954 م في اندلاع الكفاح", "20 أوت 1956 م في صدى مؤتمر الصومام"),
                correctIdx = 0,
                hint = "عيد الاستقلال هو يوم الفرحة العظمى لبلادنا في فصل الصيف الحار."
            )
        )
    )
)

@Composable
fun AlgerianCurriculumDialog(
    onDismiss: () -> Unit,
    onComplete: (title: String, category: String, score: Int, durationSeconds: Int, stars: Int) -> Unit,
    tts: NasheetTTS?,
    currentGradeFromProfile: String
) {
    val gradesData = remember { algerianGradesLessonsData }

    val filteredGrades = remember(currentGradeFromProfile) {
        val matchedId = when {
            currentGradeFromProfile.contains("الأول") || currentGradeFromProfile.contains("أولى") -> 1
            currentGradeFromProfile.contains("الثاني") || currentGradeFromProfile.contains("ثانية") -> 2
            currentGradeFromProfile.contains("الثالث") || currentGradeFromProfile.contains("ثالثة") -> 3
            currentGradeFromProfile.contains("الرابع") || currentGradeFromProfile.contains("رابعة") -> 4
            currentGradeFromProfile.contains("الخامس") || currentGradeFromProfile.contains("خامسة") -> 5
            else -> 1
        }
        gradesData.filter { it.id == matchedId }
    }

    // Active Grade State - initialized to matched ID or first grade
    var selectedGradeId by remember(filteredGrades) {
        mutableStateOf(filteredGrades.firstOrNull()?.id ?: 1)
    }

    val activeGrade = remember(selectedGradeId, filteredGrades) {
        filteredGrades.firstOrNull { it.id == selectedGradeId } ?: gradesData.first()
    }

    var selectedLessonIdx by remember { mutableStateOf(-1) } // -1 = no expanded lesson view
    
    // Quiz State for active level
    var currentQuestionIdx by remember { mutableStateOf(-1) } // -1 = quiz not started yet
    var scoreValue by remember { mutableStateOf(0) }
    var selectedQuizOption by remember { mutableStateOf(-1) }
    var answeredStatus by remember { mutableStateOf<Boolean?>(null) } // true=correct, false=wrong, null=not answered yet
    var showExplanation by remember { mutableStateOf(false) }
    var quizCompletedState by remember { mutableStateOf(false) }

    LaunchedEffect(selectedGradeId) {
        tts?.speak("المنهج الدراسي الجزائري المطور المخصص لعمرك الذكي! لقد اخترت ${activeGrade.name}. هيا ننهي الدروس والاختبارات لنكسب حزام التفوق!")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "المنهج الدراسي الجزائري 2026 🇩🇿📚",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color(0xFF0F766E),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Tab selector for all the five levels
                if (filteredGrades.size > 1) {
                    Text(
                        "اختر مستوى عمرك المنهجي:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = CharcoalText
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        filteredGrades.forEach { gd ->
                            val isSelected = gd.id == selectedGradeId
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Color(0xFF0F766E) else Color(0xFFF3F4F6)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF0D9488) else Color(0xFFE5E7EB),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        // Reset active state parameters on tab change
                                        selectedGradeId = gd.id
                                        selectedLessonIdx = -1
                                        currentQuestionIdx = -1
                                        scoreValue = 0
                                        selectedQuizOption = -1
                                        answeredStatus = null
                                        showExplanation = false
                                        quizCompletedState = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = gd.name,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else CharcoalText
                                )
                            }
                        }
                    }
                } else {
                    // Show a simple badge indicating their locked level
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "مستوى الأنشطة والدروس المناسبة لعمرك 🎯 :",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CharcoalText
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F766E))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = activeGrade.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Welcome highlight card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(activeGrade.icon, fontSize = 24.sp)
                        Text(
                            text = activeGrade.welcomeMessage,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Sub-panels
                if (currentQuestionIdx == -1 && !quizCompletedState) {
                    // LESSONS MODE
                    Text(
                        "الدروس التفاعلية المفضلة 🗣️📖",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = CharcoalText
                    )

                    // Lesson List
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        activeGrade.lessons.forEachIndexed { index, lesson ->
                            val isExpanded = selectedLessonIdx == index
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        if (isExpanded) 1.5.dp else 1.dp,
                                        if (isExpanded) lesson.subjectColor else Color(0xFFE5E7EB),
                                        RoundedCornerShape(16.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isExpanded) lesson.subjectColor.copy(alpha = 0.05f) else Color.White
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedLessonIdx = if (isExpanded) -1 else index
                                            if (selectedLessonIdx != -1) {
                                                tts?.speak("الدرس لليوم: ${lesson.title}. سنقرأ معاً بتمهل.")
                                            }
                                        }
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(lesson.emoji, fontSize = 20.sp)
                                            Column {
                                                Text(
                                                    text = lesson.title,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 13.sp,
                                                    color = CharcoalText
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .background(lesson.subjectColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        lesson.subject,
                                                        color = lesson.subjectColor,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = if (isExpanded) "🔽" else "◀️",
                                            fontSize = 12.sp,
                                            color = CharcoalText.copy(alpha = 0.5f)
                                        )
                                    }

                                    if (isExpanded) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White, RoundedCornerShape(12.dp))
                                                .border(0.5.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                text = lesson.content,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = CharcoalText,
                                                lineHeight = 20.sp
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                tts?.speak(lesson.speakText)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = lesson.subjectColor),
                                            modifier = Modifier.fillMaxWidth().height(36.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("🎙️", fontSize = 14.sp)
                                                Text(
                                                    "استمع لنطق وقراءة المعلم للدرس",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Launch quiz button
                    Button(
                        onClick = {
                            currentQuestionIdx = 0
                            scoreValue = 0
                            selectedQuizOption = -1
                            answeredStatus = null
                            showExplanation = false
                            tts?.speak("رائع! فلنبدأ اختبار السنة المميزة! ثلاث أسئلة شيقة تمهد مستواك بالكامل!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("🎯 ابدأ الاختبار التفاعلي للمستوى المختار", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else if (currentQuestionIdx != -1 && !quizCompletedState) {
                    // QUIZ IN PROGRESS
                    val question = activeGrade.questions[currentQuestionIdx]

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "السؤال ${currentQuestionIdx + 1} من 3",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0D9488)
                        )

                        Text(
                            "الدرجة المحققة: $scoreValue",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText.copy(alpha = 0.6f)
                        )
                    }

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E7EB))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((currentQuestionIdx + 1) / 3f)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(Color(0xFF0D9488))
                        )
                    }

                    // Question Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFF)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🤔❓", fontSize = 28.sp)
                            Text(
                                text = question.text,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E1B4B),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Speak question helper
                    IconButton(
                        onClick = {
                            tts?.speak(question.text)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(36.dp)
                            .background(Color(0xFFEFF6FF), CircleShape)
                    ) {
                        Text("🔊", fontSize = 14.sp)
                    }

                    // Options list
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        question.options.forEachIndexed { optionIdx, optionText ->
                            val isSelected = selectedQuizOption == optionIdx
                            val isCorrectAnswer = optionIdx == question.correctIdx

                            val bgSelectedColor = if (answeredStatus != null) {
                                if (isCorrectAnswer) Color(0xFFECFDF5) // green
                                else if (isSelected) Color(0xFFFEF2F2) // red
                                else Color.White
                            } else {
                                if (isSelected) Color(0xFFEEF2FF) else Color.White
                            }

                            val borderSelectedColor = if (answeredStatus != null) {
                                if (isCorrectAnswer) Color(0xFF10B981)
                                else if (isSelected) Color(0xFFEF4444)
                                else Color(0xFFE5E7EB)
                            } else {
                                if (isSelected) Color(0xFF4F46E5) else Color(0xFFE5E7EB)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(bgSelectedColor)
                                    .border(1.5.dp, borderSelectedColor, RoundedCornerShape(14.dp))
                                    .clickable(enabled = answeredStatus == null) {
                                        selectedQuizOption = optionIdx
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = optionText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )

                                    if (answeredStatus != null) {
                                        if (isCorrectAnswer) {
                                            Text("✅ صحيح", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                        } else if (isSelected) {
                                            Text("❌ خاطئ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .border(
                                                    1.5.dp,
                                                    if (isSelected) Color(0xFF4F46E5) else Color(0xFF9CA3AF),
                                                    CircleShape
                                                )
                                                .background(
                                                    if (isSelected) Color(0xFF4F46E5) else Color.Transparent,
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Action button: Check or Go Next
                    if (answeredStatus == null) {
                        Button(
                            onClick = {
                                if (selectedQuizOption != -1) {
                                    val isCorrect = selectedQuizOption == question.correctIdx
                                    answeredStatus = isCorrect
                                    if (isCorrect) {
                                        scoreValue += 33
                                        tts?.speak("إجابتك ممتازة وصحيحة يا بطل! أحسنت!")
                                    } else {
                                        tts?.speak("هذه الإجابة غير دقيقة، لكن لا بأس فالمهم أن نتعلم المهارات السليمة.")
                                    }
                                } else {
                                    tts?.speak("يرجى اختيار أحد المقترحات للمواصلة والتحقق!")
                                }
                            },
                            enabled = selectedQuizOption != -1,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("التحقق من إجابتك 🔍", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Explanation Box is shown
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("💡 تفصيل وتصويب تعليمي:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFD97706))
                                Text(question.hint, fontSize = 11.sp, color = CharcoalText)
                            }
                        }

                        Button(
                            onClick = {
                                if (currentQuestionIdx < 2) {
                                    currentQuestionIdx++
                                    selectedQuizOption = -1
                                    answeredStatus = null
                                } else {
                                    quizCompletedState = true
                                    // Calculate reward stars based on finalized grade score
                                    val finalScoreFormatted = if (scoreValue > 90) 100 else scoreValue
                                    val earnedStars = if (finalScoreFormatted >= 99) 30 else if (finalScoreFormatted >= 60) 20 else 10
                                    
                                    onComplete(
                                        "منهج 2026: قسم ${activeGrade.name}",
                                        "المنهج الدراسي والتقويم الموحد",
                                        finalScoreFormatted,
                                        80,
                                        earnedStars
                                    )
                                    tts?.speak("تهانينا الحارة يا بطل الجزائِر الأشم! لقد أنهيت اختبار التقييم المنهجي وحصلت على تقدير باهر ورصيد نجوم إضافية!")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (currentQuestionIdx < 2) "السؤال التالي ◀️" else "إنهاء واستلام النجوم ⭐🏆",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // QUIZ COMPLETED CERTIFICATE
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("🇩🇿🎖️ شهادة تفوق في المنهج الوطني 🎖️🇩🇿", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFFC2410C))
                        
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color(0xFFFEF3C7), CircleShape)
                                .border(2.dp, Color(0xFFF59E0B), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏆", fontSize = 54.sp)
                        }

                        Text(
                            text = "أنهى البطل النطاق الكامل لـ:\n${activeGrade.name}\nبتقييم رصين مقداره: $scoreValue %",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = CharcoalText,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            "المليُون ونصف مليون شهيد مهدوا لك ريادة الأمم بالعلم والاجتهاد والأخلاق الفاضلة. واصل تحدي الدروس دائماً ليلمع اسمك في لوحة المدرسة والبيت!",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalText.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                // Reset to default lessons mode to allow more practice
                                currentQuestionIdx = -1
                                scoreValue = 0
                                selectedQuizOption = -1
                                answeredStatus = null
                                quizCompletedState = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("تصفح الدروس مجدداً 📖", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق والعودة للسندباد 🚪", color = CharcoalText, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
