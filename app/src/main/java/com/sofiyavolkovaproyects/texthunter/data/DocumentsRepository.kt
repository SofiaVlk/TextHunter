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

package com.sofiyavolkovaproyects.texthunter.data

import com.sofiyavolkovaproyects.texthunter.data.local.database.DocumentItem
import com.sofiyavolkovaproyects.texthunter.data.local.database.SaveDocDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DocumentsRepository {
    suspend fun getSavedDocuments() : Flow<List<DocumentItem>>

    suspend fun add(title: String, body: String)

    suspend fun remove(document: DocumentItem)
    }

class DefaultDocumentsRepository @Inject constructor(
    private val docsItemTypeDao: SaveDocDao
) : DocumentsRepository {

    override suspend fun getSavedDocuments(): Flow<List<DocumentItem>> {
        return docsItemTypeDao.getDocumentItems()
    }

    override suspend fun add(title: String, body: String) {
        docsItemTypeDao.insertDocItem(DocumentItem(title = title, body = body))
    }

    override suspend fun remove(document: DocumentItem) {
        docsItemTypeDao.deleteDocItem(document)
    }
}
