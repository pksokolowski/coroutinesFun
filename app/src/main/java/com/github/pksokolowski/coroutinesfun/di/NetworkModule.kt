package com.github.pksokolowski.coroutinesfun.di

import com.github.pksokolowski.coroutinesfun.features.testable.api.IStoreApi
import com.github.pksokolowski.coroutinesfun.features.testable.api.StoreApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineDispatcher

@InstallIn(ApplicationComponent::class)
@Module
object NetworkModule {

    @Provides
    fun providesItemsApi(@IoDispatcher networkDispatcher: CoroutineDispatcher): IStoreApi {
        return StoreApi(networkDispatcher)
    }
}