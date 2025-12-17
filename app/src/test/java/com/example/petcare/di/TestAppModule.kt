package com.example.petcare.di

import com.example.petcare.data.fake_providers.FakePetProvider
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakeMedicationEventRepository
import com.example.petcare.data.fake_repos.FakeMedicationRepository
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakePetRepository
import com.example.petcare.data.fake_repos.FakePetShareCodeRepository
import com.example.petcare.data.fake_repos.FakeTaskRepository
import com.example.petcare.data.fake_repos.FakeUserRepository
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.domain.repository.IUserRepository
import com.example.petcare.domain.repository.IPetShareCodeRepository
import com.example.petcare.domain.repository.ITaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideFakeUserProvider(): IUserProvider {
        return FakeUserProvider()
    }

    @Provides
    @Singleton
    fun provideFakePetProvider(): IPetProvider {
        return FakePetProvider()
    }

    @Provides
    @Singleton
    fun provideFakeMedicationRepository(): IMedicationRepository {
        return FakeMedicationRepository()
    }

    @Provides
    @Singleton
    fun provideFakeMedicationEventRepository(): IMedicationEventRepository {
        return FakeMedicationEventRepository()
    }

    @Provides
    @Singleton
    fun provideFakePetRepository(): IPetRepository {
        return FakePetRepository()
    }

    @Provides
    @Singleton
    fun provideFakePetMemberRepository(): IPetMemberRepository {
        return FakePetMemberRepository()
    }

    @Provides
    @Singleton
    fun provideFakeUserRepository(): IUserRepository {
        return FakeUserRepository()
    }

    @Provides
    @Singleton
    fun provideFakePetShareCodeRepository(): IPetShareCodeRepository {
        return FakePetShareCodeRepository()
    }

    @Provides
    @Singleton
    fun provideFakeTaskRepository(): ITaskRepository {
        return FakeTaskRepository()
    }
}
