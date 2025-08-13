package com.example.colepp.common.di

import android.content.Context
import com.example.colepp.database.repository.RecordDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecordDatabaseModule {
    @Provides
    @Singleton
    fun provideRecordDatabase(
        @ApplicationContext context: Context
    ): RecordDatabase {
        return RecordDatabase.getInstance(context)
    }
}