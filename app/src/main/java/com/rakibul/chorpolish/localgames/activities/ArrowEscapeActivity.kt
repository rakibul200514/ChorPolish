package com.rakibul.chorpolish.localgames.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rakibul.chorpolish.ui.theme.ChorPolishTheme
import com.rakibul.chorpolish.localgames.arrowescape.ui.ArrowEscapeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArrowEscapeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChorPolishTheme {
                ArrowEscapeScreen(
                    onBack = { finish() }
                )
            }
        }
    }
}
