package com.github.pksokolowski.coroutinesfun.di

import com.github.pksokolowski.coroutinesfun.features.testable.api.IItemsApi
import com.github.pksokolowski.coroutinesfun.features.testable.api.ItemsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    @NetworkDispatcher
    fun providesNetworkDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    fun providesItemsApi(@NetworkDispatcher networkDispatcher: CoroutineDispatcher): IItemsApi {
        return ItemsApi(networkDispatcher)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkDispatcher