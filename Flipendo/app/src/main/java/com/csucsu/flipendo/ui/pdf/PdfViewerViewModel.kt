package com.csucsu.flipendo.ui.pdf

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PdfViewerViewModel : ViewModel() {
    private val _isFullScreen = MutableLiveData(false)
    val isFullScreen: LiveData<Boolean> = _isFullScreen

    fun setFullScreen(enabled: Boolean) {
        _isFullScreen.value = enabled
    }
}
