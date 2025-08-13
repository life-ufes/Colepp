package com.example.colepp.presentation.recordEdit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colepp.common.utils.JsonHandler.getDecodedJsonParam
import com.example.colepp.common.utils.ScreenState
import com.example.colepp.database.model.RecordEntity
import com.example.colepp.database.repository.RecordDatabase
import com.example.colepp.navigation.MainNavGraphArgs.RECORD_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recordDatabase: RecordDatabase
) : ViewModel() {
    private val recordId = savedStateHandle.getDecodedJsonParam<Long>(RECORD_ID)

    private val _record = MutableStateFlow<RecordEntity?>(null)
    val record = _record.asStateFlow()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    val buttonEnabled = _title.asStateFlow()
        .map { it.length >= MIN_TITLE_LENGTH }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false)

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun updateDescription(newDescription: String) {
        _description.value = newDescription
    }

    fun getContent() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            try {
                _record.value = recordDatabase.recordDao().getById(recordId)
                _title.value = _record.value?.title.orEmpty()
                _description.value = _record.value?.description.orEmpty()
                _screenState.value = ScreenState.Content
            } catch (e: Exception) {
                _screenState.value = ScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setContent(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            try {
                recordDatabase.recordDao().updateRecord(
                    id = recordId,
                    title = _title.value,
                    description = _description.value
                )
                _screenState.value = ScreenState.Content
                onSuccess()
            } catch (e: Exception) {
                _screenState.value = ScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    companion object {
        const val MIN_TITLE_LENGTH = 3
    }
}