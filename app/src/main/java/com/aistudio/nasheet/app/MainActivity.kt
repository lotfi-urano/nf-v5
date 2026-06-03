package com.aistudio.nasheet.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.aistudio.nasheet.app.ui.NasheetViewModel
import com.aistudio.nasheet.app.ui.NasheetViewModelFactory
import com.aistudio.nasheet.app.ui.navigation.MainAppNavigation
import com.aistudio.nasheet.app.ui.screens.NasheetTTS
import com.aistudio.nasheet.app.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private var tts: NasheetTTS? = null

    private val viewModel: NasheetViewModel by viewModels {
        NasheetViewModelFactory((application as NasheetApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Safe TTS init — if it fails, app still runs without voice
        try {
            tts = NasheetTTS(this)
        } catch (e: Exception) {
            Log.e("MainActivity", "TTS initialization failed, continuing without voice.", e)
            tts = null
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppNavigation(viewModel = viewModel, tts = tts)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("MainActivity", "TTS shutdown error", e)
        }
        tts = null
    }
}
