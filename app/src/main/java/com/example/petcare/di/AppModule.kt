package com.example.petcare.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.implementation.PetProvider
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

}