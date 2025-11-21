package com.example.petcare

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
@HiltAndroidApp
class PetCareApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

    }
}