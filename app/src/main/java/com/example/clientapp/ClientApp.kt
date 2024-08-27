package com.example.clientapp

import android.app.Application
import com.google.firebase.FirebaseApp

// The app starts, and the Android system initializes the MyApplication class.
// The onCreate() method of MyApplication is called, where Firebase is initialized.
// Then, the MainActivity is started by the system.
// The onCreate() method of MainActivity is called.
class ClientApp: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}