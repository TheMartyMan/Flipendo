package com.csucsu.flipendo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface FileHistoryDao {
    @Query("SELECT * FROM file_history ORDER BY opened_at DESC")
    fun getAllFlow(): Flow<List<FileHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: FileHistory)

    @Delete
    suspend fun delete(f: FileHistory)

    @Query("SELECT * FROM file_history WHERE uri = :uriString LIMIT 1")
    suspend fun getByUri(uriString: String): FileHistory?

    @Query("DELETE FROM file_history")
    suspend fun deleteAll()
}

