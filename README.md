<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/dacd2554-eb8f-4627-9929-c07ab6dc169e

## كيفية التحميل (How to Download)

يمكنك تحميل الملفات بعدة طرق:

1. **تحميل الملف المضغوط (الأسهل):**
   - اذهب إلى قسم [Releases](https://github.com/lotfi-urano/nf-v5/releases).
   - قم بتحميل ملف `nasheet_fixed-2.zip`.

2. **تحميل الكود المصدري:**
   - اضغط على زر **Code** الأخضر في أعلى الصفحة.
   - اختر **Download ZIP**.

3. **استخدام Git:**
   ```bash
   git clone https://github.com/lotfi-urano/nf-v5.git
   ```

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
