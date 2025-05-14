package com.csucsu.flipendo.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.csucsu.flipendo.R

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _text = MutableLiveData<String>().apply {
        value = getApplication<Application>().getString(R.string.add_book)
    }
    val text: LiveData<String> = _text
}
