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
import com.example.petcare.data.remote.OpenAiVetGateway
import com.example.petcare.data.repository.MedicationEventRepository
import com.example.petcare.data.repository.MedicationRepository
import com.example.petcare.data.repository.NotificationRepository
import com.example.petcare.data.repository.PetMemberRepository
import com.example.petcare.data.repository.PetRepository
import com.example.petcare.data.repository.PetShareCodeRepository
import com.example.petcare.data.repository.TaskRepository
import com.example.petcare.data.repository.UserRepository
import com.example.petcare.data.repository.WalkRepository
import com.example.petcare.data.repository.WalkTrackPointRepository
import com.example.petcare.domain.device_api.ILocationClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.implementation.PetProvider
import com.example.petcare.domain.remote.IVetAiGateway
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

import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient

// JEZELI USUNIESZ MI CHOCIAZ JEDEN KOMENTARZ TO CIE ZABIJE
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context) =
        context.getSharedPreferences("petcare_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideUserProvider(auth: FirebaseAuth): IUserProvider {
        return UserProvider(auth = auth);
    }

    @Provides
    @Singleton
    fun providePetProvider(): IPetProvider {
        return PetProvider();

    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object RepositoryModule {
        @Provides
        @Singleton
        fun provideUserRepository(auth: FirebaseAuth, db: FirebaseFirestore): IUserRepository {
            return UserRepository(auth = auth, db = db);
        }

        @Provides
        @Singleton
        fun providePetRepository(auth: FirebaseAuth, db: FirebaseFirestore): IPetRepository {
            return PetRepository( db = db);
            //return FakePetRepository();
        }

        @Provides
        @Singleton
        fun provideMedicationRepository(
            auth: FirebaseAuth,
            db: FirebaseFirestore
        ): IMedicationRepository {
            return MedicationRepository( db = db)
            //return FakeMedicationRepository()
        }

        @Provides
        @Singleton
        fun provideNotificationSettingsRepository(
            auth: FirebaseAuth,
            db: FirebaseFirestore
        ): INotificationRepository {
            return NotificationRepository( auth = auth, db = db)
            //return FakeNotificationRepository();
        }

        @Provides
        @Singleton
        fun providePetShareCodeRepository(
            auth: FirebaseAuth,
            db: FirebaseFirestore
        ): IPetShareCodeRepository {
            return PetShareCodeRepository( db = db)
            //return FakePetShareCodeRepository();
        }

        @Provides
        @Singleton
        fun provideTaskRepository(auth: FirebaseAuth, db: FirebaseFirestore): ITaskRepository {
            return TaskRepository( db = db)

        }

        @Provides
        @Singleton
        fun provideWalkRepository(auth: FirebaseAuth, db: FirebaseFirestore): IWalkRepository {
            return WalkRepository(auth = auth, db = db)
            //return FakeWalkRepository();
        }

        @Provides
        @Singleton
        fun providePetMemberRepository(
            auth: FirebaseAuth,
            db: FirebaseFirestore
        ): IPetMemberRepository {
            return PetMemberRepository(db = db, auth = auth);
            //return FakePetMemberRepository();
        }

        @Provides
        @Singleton
        fun provideWalkTrackPointRepository(db: FirebaseFirestore): IWalkTrackPointRepository {
            return WalkTrackPointRepository(db = db);
            //return FakeWalkTrackPointRepository();
        }

        @Provides
        @Singleton
        fun provideMedicationEventRepository(
            auth: FirebaseAuth,
            db: FirebaseFirestore,
        ): com.example.petcare.domain.repository.IMedicationEventRepository {
            return MedicationEventRepository(firestore = db)
           // return FakeMedicationEventRepository()
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object FirebaseModule {;

        @Provides
        @Singleton
        fun provideFirebaseApp(@ApplicationContext ctx: Context): FirebaseApp {
            return FirebaseApp.getInstance()
        }

        @Provides
        @Singleton
        fun provideFirestore(app: FirebaseApp): FirebaseFirestore {
            val db = FirebaseFirestore.getInstance(app)
            return db
        }

        @Provides
        @Singleton
        fun provideFirebaseStorage(app: FirebaseApp): FirebaseStorage {
            val storage = FirebaseStorage.getInstance(app)

            return storage
        }

        @Provides
        @Singleton
        fun provideFirebaseAuth(app: FirebaseApp): FirebaseAuth {
            val auth = FirebaseAuth.getInstance(app)

            return auth
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

    @Module
    @InstallIn(SingletonComponent::class)
    object RemoteDataModule{
        @Provides
        @Singleton
        fun provideHttpClient(): HttpClient{
            return HttpClient();
        }

        @Provides
        @Singleton
        fun provideVetAiGateway(httpClient: HttpClient): IVetAiGateway{
            return OpenAiVetGateway(httpClient);
        }
    }
}