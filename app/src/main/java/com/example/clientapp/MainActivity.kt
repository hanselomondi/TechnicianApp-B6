package com.example.clientapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.clientapp.openai_functions.OpenAIObject
import com.example.clientapp.ui.theme.TechnicianAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TechnicianAppTheme {
                AppNavigation()

                val apiKey =  BuildConfig.OPENAI_API_KEY //BuildConfig.OPENAI_API_KEY
                //OpenAIObject
            }
        }
    }
}
