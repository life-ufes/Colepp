package com.example.transferdata.common.di

import android.content.Context
import androidx.core.content.FileProvider
import com.example.transferdata.database.repository.RecordDatabase
import com.example.transferdata.dataset.DatasetGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasetGeneratorModule {

    @Provides
    @Singleton
    fun provideDatasetGenerator(@ApplicationContext context: Context, recordDatabase: RecordDatabase): DatasetGenerator {
        return DatasetGenerator(
            recordDatabase,
            { file ->
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileProvider",
                    file
                )
            }
        )
    }
}