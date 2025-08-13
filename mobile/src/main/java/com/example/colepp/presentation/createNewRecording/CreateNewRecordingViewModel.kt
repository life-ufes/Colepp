package com.example.colepp.presentation.createNewRecording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNewRecordingViewModel @Inject constructor() : ViewModel() {

    private val _nameValue = MutableStateFlow("")
    val nameValue = _nameValue.asStateFlow()

    fun onNameValueChange(name: String) {
        viewModelScope.launch {
            _nameValue.value = name
            _enableButton.value = name.length > 3
        }
    }

    private val _descriptionValue = MutableStateFlow("")
    val descriptionValue = _descriptionValue.asStateFlow()

    fun onDescriptionValueChange(description: String) {
        viewModelScope.launch {
            _descriptionValue.value = description
        }
    }

    private val _enableButton = MutableStateFlow(false)
    val enableButton = _enableButton.asStateFlow()
}