package com.csucsu.flipendo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "file_history",
    indices = [Index(value = ["uri"], unique = true)]
)

data class FileHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "uri") val uriString: String,
    @ColumnInfo(name = "opened_at") val openedAt: Long = System.currentTimeMillis()
)

