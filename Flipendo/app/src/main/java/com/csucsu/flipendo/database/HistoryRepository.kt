package com.csucsu.flipendo.database

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.flow.Flow


class HistoryRepository(private val dao: FileHistoryDao, private val context: Context) {
    val historyFlow: Flow<List<FileHistory>> = dao.getAllFlow()

    suspend fun clearAll() {
        dao.deleteAll()
    }

    suspend fun delete(file: FileHistory) {
        dao.delete(file)
    }

    suspend fun findByUri(uriString: String): FileHistory? {
        return dao.getByUri(uriString)
    }


    private fun queryDisplayName(uri: Uri): String {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index >= 0) {
                return cursor.getString(index)
            }
        }
        return uri.lastPathSegment ?: "unknown"
    }

    suspend fun upsert(uri: Uri) {
        dao.getByUri(uri.toString())?.let { existing ->
            dao.delete(existing)
        }
        val name = queryDisplayName(uri)
        dao.upsert(FileHistory(displayName = name, uriString = uri.toString()))
    }
}
