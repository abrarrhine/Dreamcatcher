package edu.vt.cs5254.dreamcatcher

import android.app.Application

class DreamCatcherApp: Application() {
    override fun onCreate() {
        super.onCreate()
        DreamRepository.initialize(this)
    }
}