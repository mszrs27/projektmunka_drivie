package com.dvainsolutions.drivie.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dvainsolutions.drivie.service.AccountService
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.service.StorageService
import com.dvainsolutions.drivie.service.impl.AccountServiceImpl
import com.dvainsolutions.drivie.service.impl.FirestoreServiceImpl
import com.dvainsolutions.drivie.service.impl.StorageServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideAccountService(): AccountService = AccountServiceImpl()

    @Provides
    @Singleton
    fun provideFirestoreService(): FirestoreService = FirestoreServiceImpl()

    @Provides
    @Singleton
    fun provideStorageService(): StorageService = StorageServiceImpl()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext app: Context): DataStore<Preferences> = app.dataStore
}