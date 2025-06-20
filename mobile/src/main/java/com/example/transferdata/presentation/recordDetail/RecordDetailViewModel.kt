package com.example.transferdata.presentation.recordDetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transferdata.common.utils.JsonHandler.getDecodedJsonParam
import com.example.transferdata.common.utils.ScreenState
import com.example.transferdata.database.model.RecordEntity
import com.example.transferdata.database.repository.RecordDatabase
import com.example.transferdata.navigation.MainNavGraphArgs.RECORD_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RecordDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recordDatabase: RecordDatabase
) : ViewModel() {
    private val recordId = savedStateHandle.getDecodedJsonParam<Long>(RECORD_ID)

    private val _record = MutableStateFlow<RecordEntity?>(null)
    val record = _record.asStateFlow()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    fun getContent() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            try {
                _record.value = recordDatabase.recordDao().getById(recordId)
                _screenState.value = ScreenState.Content

                //teste
                val ts = (recordDatabase.accelerometerPolarDao().getAllDataFromRecord(recordId)
                    .first().timestamp + 946_684_800_000_000_000L) / 1_000_000L

                val cal = Calendar.getInstance()
                cal.timeInMillis = ts
                Log.d("RecordDetailViewModel", "Timestamp: ${cal.time}")
                Log.d("RecordDetailViewModel", "Today: ${Date()}")
            } catch (e: Exception) {
                _screenState.value = ScreenState.Error(e.message ?: "Unknown error")
            }

        }
    }
}