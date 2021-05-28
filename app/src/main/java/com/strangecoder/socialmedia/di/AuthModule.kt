package com.strangecoder.socialmedia.di

import com.strangecoder.socialmedia.repositories.AuthRepository
import com.strangecoder.socialmedia.repositories.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object AuthModule {

    @ActivityScoped
    @Provides
    fun providesAuthRepository() = AuthRepositoryImpl() as AuthRepository
}