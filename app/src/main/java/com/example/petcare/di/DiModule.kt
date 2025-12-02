package com.example.petcare.di

import android.content.Context
<<<<<<< Updated upstream
import com.example.petcare.data.repository.UserRepository
=======
import android.content.SharedPreferences
import com.example.petcare.data.fake_repos.FakeMedicationRepository
import com.example.petcare.data.fake_repos.FakeNotificationRepository
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakePetRepository
import com.example.petcare.data.fake_repos.FakePetShareCodeRepository
import com.example.petcare.data.fake_repos.FakeTaskRepository
import com.example.petcare.data.fake_repos.FakeUserRepository
import com.example.petcare.data.fake_repos.FakeWalkRepository
import com.example.petcare.data.fake_repos.FakeWalkTrackPointRepository
import com.example.petcare.data.repository.PetMemberRepository
import com.example.petcare.data.repository.WalkRepository
import com.example.petcare.domain.device_api.IWalkTracker
>>>>>>> Stashed changes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.implementation.PetProvider
import com.example.petcare.domain.repository.IUserRepository
<<<<<<< Updated upstream
=======
import com.example.petcare.domain.repository.IWalkRepository
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
>>>>>>> Stashed changes
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds

import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserProvider(): IUserProvider{
        return UserProvider();
    }

    @Provides
    @Singleton
    fun providePetProvider(): IPetProvider{
        return PetProvider();
    }

    @Provides
    @Singleton
    fun provideUserRepository(auth: FirebaseAuth, db: FirebaseFirestore) : IUserRepository {
        return UserRepository(auth = auth, db = db);
    }

}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {;

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
<<<<<<< Updated upstream
=======
}
@Module
@InstallIn(SingletonComponent::class) // Dostępne w całej aplikacji
object LocationModule {
    @Provides
    @Singleton
    fun provideLocationTracker(@ApplicationContext ctx: Context) : IWalkTracker{
        return com.example.petcare.data.device_api_impl.WalkTracker(ctx);
    }

>>>>>>> Stashed changes
}