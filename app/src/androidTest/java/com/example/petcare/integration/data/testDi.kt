package com.example.petcare.integration.data
import android.content.Context
import dagger.Module
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.example.petcare.di.FirebaseModule
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirebaseModule::class]
)
object EmulatorFirebaseModule  {
    private fun host() = "10.0.2.2"

    private const val USE_EMULATOR = true
    private const val FIRESTORE_PORT = 8080;
    private const val AUTH_PORT = 9099;
    private const val STORAGE_PORT = 9199;

    @Provides
    @Singleton
    fun provideFirebaseApp(@ApplicationContext ctx: Context): FirebaseApp {
        FirebaseApp.getApps(ctx).firstOrNull()?.let { return it }

        val options = FirebaseOptions.Builder()
            .setProjectId("demo-petcare")
            .setApplicationId("1:123:android:demo")
            .setStorageBucket("demo-petcare.appspot.com")
            .build()

        return FirebaseApp.initializeApp(ctx, options)
            ?: error("FirebaseApp.initializeApp returned null")
    }

    @Provides
    @Singleton
    fun provideFirestore(app: FirebaseApp): FirebaseFirestore {
        val db = FirebaseFirestore.getInstance(app)
        if (USE_EMULATOR) {
            db.useEmulator(host(), FIRESTORE_PORT)
        }
        return db
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(app: FirebaseApp): FirebaseStorage {
        val storage = FirebaseStorage.getInstance(app)
        if (USE_EMULATOR) {
            storage.useEmulator(host(), STORAGE_PORT)
        }
        return storage
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(app: FirebaseApp): FirebaseAuth {
        val auth = FirebaseAuth.getInstance(app)
        if (USE_EMULATOR) {
            auth.useEmulator(host(), AUTH_PORT)
        }
        return auth
    }
}