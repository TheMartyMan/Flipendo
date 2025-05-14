package com.csucsu.flipendo.ui.history


import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.csucsu.flipendo.database.AppDatabase
import com.csucsu.flipendo.database.FileHistory
import com.csucsu.flipendo.database.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = HistoryRepository(
        AppDatabase.getInstance(application).fileHistoryDao(),
        application
    )

    val history: StateFlow<List<FileHistory>> = repo.historyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList<FileHistory>()
        )

    fun addOpenedFile(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.upsert(uri)
        }
    }

    fun removeOpenedFile(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1) Megkeressük az adatbázisban
            val entry = repo.findByUri(uri.toString())
            if (entry != null) {
                // 2) Töröljük a bejegyzést
                repo.delete(entry)
            }

            // 3) Csak ha van még grant-unk erre az URI-re, adjuk vissza
            val perms = getApplication<Application>()
                .contentResolver.persistedUriPermissions
            if (perms.any { it.uri == uri && it.isReadPermission }) {
                try {
                    getApplication<Application>().contentResolver
                        .releasePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                } catch (ise: SecurityException) {
                    //Log.e("SecurityException", "No permission for URI: $uri. Error:", ise)
                }
            }
        }
    }



    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.clearAll()
        }
    }
}