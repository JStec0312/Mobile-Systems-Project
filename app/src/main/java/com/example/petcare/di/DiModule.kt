package com.example.petcare.di

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.petcare.data.device_api.LocationClient
import com.example.petcare.data.fake_repos.FakeMedicationEventRepository
import com.example.petcare.data.fake_repos.FakeMedicationRepository
import com.example.petcare.data.fake_repos.FakeNotificationRepository
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakePetRepository
import com.example.petcare.data.fake_repos.FakePetShareCodeRepository
import com.example.petcare.data.fake_repos.FakeTaskRepository
import com.example.petcare.data.fake_repos.FakeUserRepository
import com.example.petcare.data.fake_repos.FakeWalkRepository
import com.example.petcare.data.fake_repos.FakeWalkTrackPointRepository
import com.example.petcare.domain.device_api.ILocationClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.data.providers_impl.FakeUserProvider
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.implementation.PetProvider
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.INotificationRepository
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.domain.repository.IPetShareCodeRepository
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.domain.repository.IUserRepository
import com.example.petcare.domain.repository.IWalkRepository
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import com.example.petcare.domain.use_case.get_upcoming_items.GetUpcomingItemsUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.EntryPoint
import com.example.petcare.config.Settings
import com.example.petcare.data.repository.MedicationEventRepository
import com.example.petcare.data.repository.MedicationRepository
import com.example.petcare.data.repository.NotificationRepository
import com.example.petcare.data.repository.PetMemberRepository
import com.example.petcare.data.repository.PetRepository
import com.example.petcare.data.repository.PetShareCodeRepository
import com.example.petcare.data.repository.TaskRepository
import com.example.petcare.data.repository.WalkRepository
import com.example.petcare.data.repository.WalkTrackPointRepository
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.repository.IMedicationEventRepository
import dagger.hilt.android.qualifiers.ApplicationContext

import io.ktor.client.HttpClient
import com.example.petcare.data.remote.OpenAiVetGateway
import com.example.petcare.domain.pdf.MedicationPdfGenerator
import com.example.petcare.domain.remote.IVetAiGateway
import com.example.petcare.domain.repository.IMedicationPdfGenerator

// JEZELI USUNIESZ MI CHOCIAZ JEDEN KOMENTARZ TO CIE ZABIJE
const val mode = Settings.MODE // "PROD" albo "DEV"  albo DEV-FIREBASE
val modeUpper = mode.uppercase()
private val useFirebase = modeUpper != "DEV"
private fun host() = "10.0.2.2"
private const val FIRESTORE_PORT = 8080;
private const val AUTH_PORT = 9099;
private const val STORAGE_PORT = 9199;
private val useEmulator = modeUpper == "DEV-FIREBASE"
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context) =
        context.getSharedPreferences("petcare_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideUserProvider(
        sharedPreferences: SharedPreferences,
        auth: FirebaseAuth
    ): IUserProvider {
        return if (useFirebase) {
            UserProvider(auth = auth)
        } else {
            FakeUserProvider(sharedPreferences)
        }
    }


    @Provides
    @Singleton
    fun providePetProvider(): IPetProvider {
        return PetProvider();

    }

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient() // Podstawowy klient Ktor
    }

    @Provides
    @Singleton
    fun provideVetAiGateway(
        h: HttpClient
    ): IVetAiGateway {
        return OpenAiVetGateway(h)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }
    @Module @InstallIn(SingletonComponent::class) object RepositoryModule {
        @Provides
        @Singleton
        fun providePetRepository(auth: FirebaseAuth, db: FirebaseFirestore, storage: FirebaseStorage): IPetRepository =
            if (useFirebase) PetRepository(db = db, storage = storage) else FakePetRepository()

        @Provides
        @Singleton
        fun provideMedicationRepository(
            auth: FirebaseAuth,
            db: FirebaseFirestore
        ): IMedicationRepository =
            if (useFirebase) MedicationRepository(db = db) else FakeMedicationRepository()

        @Provides
        @Singleton
        fun provideNotificationSettingsRepository(
            auth: FirebaseAuth,
            db: FirebaseFirestore
        ): INotificationRepository =
            if (useFirebase) NotificationRepository(
                auth = auth,
                db = db
            ) else FakeNotificationRepository()

        @Provides
        @Singleton
        fun providePetShareCodeRepository(
            auth: FirebaseAuth,
            db: FirebaseFirestore
        ): IPetShareCodeRepository =
            if (useFirebase) PetShareCodeRepository(db = db) else FakePetShareCodeRepository()

        @Provides
        @Singleton
        fun provideTaskRepository(auth: FirebaseAuth, db: FirebaseFirestore): ITaskRepository =
            if (useFirebase) TaskRepository(db = db) else FakeTaskRepository()

        @Provides
        @Singleton
        fun provideWalkRepository(auth: FirebaseAuth, db: FirebaseFirestore): IWalkRepository =
            if (useFirebase) WalkRepository(auth = auth, db = db) else FakeWalkRepository()

        @Provides
        @Singleton
        fun providePetMemberRepository(
            auth: FirebaseAuth,
            db: FirebaseFirestore
        ): IPetMemberRepository =
            if (useFirebase) PetMemberRepository(
                db = db,
                auth = auth
            ) else FakePetMemberRepository()

        @Provides
        @Singleton
        fun provideWalkTrackPointRepository(db: FirebaseFirestore): IWalkTrackPointRepository =
            if (useFirebase) WalkTrackPointRepository(db = db) else FakeWalkTrackPointRepository()

        @Provides
        @Singleton
        fun provideMedicationEventRepository(
            auth: FirebaseAuth,
            db: FirebaseFirestore
        ): IMedicationEventRepository =
            if (useFirebase) MedicationEventRepository(firestore = db) else FakeMedicationEventRepository()

        @Provides
        @Singleton
        fun provideUserRepository(auth: FirebaseAuth, db: FirebaseFirestore): IUserRepository =
            if (useFirebase) com.example.petcare.data.repository.UserRepository(
                auth = auth,
                db = db
            ) else FakeUserRepository()

        @Provides
        @Singleton
        fun provideMedicationPdfGenerator(): IMedicationPdfGenerator = MedicationPdfGenerator();
    }
    @Module
    @InstallIn(SingletonComponent::class)
    object FirebaseModule {

        @Provides
        @Singleton
        fun provideFirebaseApp(@ApplicationContext ctx: Context): FirebaseApp {
            FirebaseApp.getApps(ctx).firstOrNull()?.let { return it }

            return if (useEmulator) {
                val options = FirebaseOptions.Builder()
                    .setProjectId("demo-petcare")                  // dowolny string
                    .setApplicationId("1:123:android:demo")        // dowolny string
                    .setStorageBucket("demo-petcare.appspot.com")  // dowolny string
                    .setApiKey("fake-api-key")                     // KLUCZ: musi być jakikolwiek niepusty
                    .build()

                FirebaseApp.initializeApp(ctx, options)
                    ?: error("FirebaseApp.initializeApp returned null even with explicit options")
            } else {
                // PROD i DEV (jesli DEV, i tak repo fake, ale niech app sie nie wywala)
                FirebaseApp.initializeApp(ctx)
                    ?: error("FirebaseApp.initializeApp returned null (check google-services.json)")
            }
        }

        @Provides
        @Singleton
        fun provideFirestore(app: FirebaseApp): FirebaseFirestore {
            val db = FirebaseFirestore.getInstance(app)
            if (useEmulator) {
                db.useEmulator(host(), FIRESTORE_PORT)
            }
            return db
        }

        @Provides
        @Singleton
        fun provideFirebaseAuth(app: FirebaseApp): FirebaseAuth {
            val auth = FirebaseAuth.getInstance(app)
            if (useEmulator) {
                auth.useEmulator(host(), AUTH_PORT)
            }
            return auth
        }

        @Provides
        @Singleton
        fun provideFirebaseStorage(app: FirebaseApp): FirebaseStorage {
            val storage = FirebaseStorage.getInstance(app)
            if (useEmulator) {
                storage.useEmulator(host(), STORAGE_PORT)
            }
            return storage
        }
    }

    @Module
    @InstallIn(SingletonComponent::class) // Dostępne w całej aplikacji
    object LocationModule {
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }

        @Provides
        @Singleton
        fun providceLocationClient(
            @ApplicationContext ctx: Context,
            fusedLocationProviderClient: FusedLocationProviderClient
        ): ILocationClient {
            return LocationClient(
                context = ctx,
                client = fusedLocationProviderClient
            )
        }
    }
}

