package com.github.pksokolowski.coroutinesfun.di

import android.app.Application
import androidx.room.Room
import com.github.pksokolowski.coroutinesfun.db.AppDatabase
import com.github.pksokolowski.coroutinesfun.db.dao.AnimalsDao
import com.github.pksokolowski.coroutinesfun.db.dao.PrimeCandidateDao
import com.github.pksokolowski.coroutinesfun.repository.AnimalsRepository
import com.github.pksokolowski.coroutinesfun.repository.PrimeCandidatesRepository
import com.github.pksokolowski.coroutinesfun.repository.implementations.AnimalsRepositoryImpl
import com.github.pksokolowski.coroutinesfun.repository.implementations.PrimeCandidatesRepositoryImpl
import com.github.pksokolowski.coroutinesfun.utils.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun providesSomeDependency(): String {
        return "some text"
    }

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesAnimalsDao(db: AppDatabase): AnimalsDao {
        return db.animalsDao()
    }

    @Singleton
    @Provides
    fun providesPrimeCandidatesDao(db: AppDatabase): PrimeCandidateDao {
        return db.primeCandidatesDao()
    }

    @Provides
    fun providesAnimalsRepository(animalsDao: AnimalsDao): AnimalsRepository {
        return AnimalsRepositoryImpl(animalsDao)
    }

    @Provides
    fun providesPrimeCandidatesRepository(primeCandidateDao: PrimeCandidateDao): PrimeCandidatesRepository {
        return PrimeCandidatesRepositoryImpl(primeCandidateDao)
    }
}