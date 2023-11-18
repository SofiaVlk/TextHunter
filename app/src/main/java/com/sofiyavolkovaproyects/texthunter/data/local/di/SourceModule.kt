/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sofiyavolkovaproyects.texthunter.data.local.di

import android.content.Context
import androidx.room.Room
import com.sofiyavolkovaproyects.texthunter.data.local.database.AppDatabase
import com.sofiyavolkovaproyects.texthunter.data.local.database.SaveDocDao
import com.sofiyavolkovaproyects.texthunter.data.local.source.DefaultTHLocalImageSource
import com.sofiyavolkovaproyects.texthunter.data.local.source.THLocalImageSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class SourceModule {
    @Provides
    fun provideDocsItemDao(appDatabase: AppDatabase): SaveDocDao {
        return appDatabase.docsItemTypeDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "TextHunterDB"
        ).build()
    }

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun providesTHLocalImageSource(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ) : THLocalImageSource = DefaultTHLocalImageSource(context, ioDispatcher)

}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher