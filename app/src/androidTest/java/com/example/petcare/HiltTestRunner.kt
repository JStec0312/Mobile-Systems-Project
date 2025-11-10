package com.example.petcare

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

// This is the custom runner class
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        // We force the test to use HiltTestApplication
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}