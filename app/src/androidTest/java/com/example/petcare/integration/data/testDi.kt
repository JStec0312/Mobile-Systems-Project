package com.example.petcare.integration.data
import android.content.Context
import com.example.petcare.data.fake_repos.FakeMedicationRepository
import com.example.petcare.data.fake_repos.FakeNotificationRepository
import com.example.petcare.data.fake_repos.FakePetRepository
import com.example.petcare.data.fake_repos.FakePetShareCodeRepository
import com.example.petcare.data.fake_repos.FakeTaskRepository
import com.example.petcare.data.fake_repos.FakeUserRepository
import com.example.petcare.data.fake_repos.FakeWalkRepository
import com.example.petcare.data.repository.MedicationRepository
import com.example.petcare.data.repository.NotificationSettingsRepository
import com.example.petcare.data.repository.PetRepository
import com.example.petcare.data.repository.PetShareCodeRepository
import com.example.petcare.data.repository.TaskRepository
import com.example.petcare.data.repository.UserRepository
import com.example.petcare.data.repository.WalkRepository
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
import com.example.petcare.di.RepositoryModule
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.INotificationSettingsRepository
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.domain.repository.IPetShareCodeRepository
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.domain.repository.IUserRepository
import com.example.petcare.domain.repository.IWalkRepository
import com.google.firebase.firestore.firestoreSettings
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
                .setProjectId("demo-petcare")                  // dowolny string
                .setApplicationId("1:123:android:demo")        // dowolny string
                .setStorageBucket("demo-petcare.appspot.com")  // dowolny string
                .setApiKey("fake-api-key")                     // KLUCZ: musi być jakikolwiek niepusty
                .build()

            return FirebaseApp.initializeApp(ctx, options)
                ?: error("FirebaseApp.initializeApp returned null even with explicit options")
    }

    @Provides
    @Singleton
    fun provideFirestore(app: FirebaseApp): FirebaseFirestore {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance(app)
        if (USE_EMULATOR) {
            db.useEmulator(host(), FIRESTORE_PORT)
        }
        db.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = false

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

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TrueRepositoryModule{
    @Provides
    @Singleton
    fun provideUserRepository(auth: FirebaseAuth, db: FirebaseFirestore) : IUserRepository {
        return UserRepository(auth = auth, db = db);
    }

    @Provides
    @Singleton
    fun providePetRepository(auth: FirebaseAuth, db: FirebaseFirestore) : IPetRepository {
        return PetRepository(auth = auth, db = db);
    }
    @Provides
    @Singleton
    fun provideMedicationRepository(auth: FirebaseAuth, db: FirebaseFirestore): IMedicationRepository{
        return MedicationRepository(auth = auth, db = db)
    }
    @Provides
    @Singleton
    fun provideNotificationSettingsRepository(auth: FirebaseAuth, db: FirebaseFirestore): INotificationSettingsRepository{
        return NotificationSettingsRepository(auth = auth, db = db)
    }

    @Provides
    @Singleton
    fun providePetShareCodeRepository(auth: FirebaseAuth, db: FirebaseFirestore) : IPetShareCodeRepository {
        return PetShareCodeRepository(auth = auth, db = db)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(auth: FirebaseAuth, db: FirebaseFirestore): ITaskRepository{
        return TaskRepository(auth = auth, db = db)
    }

    @Provides
    @Singleton
    fun provideWalkRepository(auth: FirebaseAuth, db: FirebaseFirestore): IWalkRepository{
        return WalkRepository(auth = auth, db = db)
    }
}