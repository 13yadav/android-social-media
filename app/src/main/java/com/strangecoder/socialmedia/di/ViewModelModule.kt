package com.strangecoder.socialmedia.di

import com.strangecoder.socialmedia.domain.AuthRepository
import com.strangecoder.socialmedia.data.repositories.AuthRepositoryImpl
import com.strangecoder.socialmedia.domain.MainRepository
import com.strangecoder.socialmedia.data.repositories.MainRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindMainRepository(impl: MainRepositoryImpl): MainRepository
}