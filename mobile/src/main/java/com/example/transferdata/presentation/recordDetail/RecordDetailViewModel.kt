package com.example.transferdata.presentation.recordDetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transferdata.common.utils.JsonHandler.getDecodedJsonParam
import com.example.transferdata.common.utils.SavingDatasetStatus
import com.example.transferdata.common.utils.ScreenState
import com.example.transferdata.database.model.HeartRateGenericData
import com.example.transferdata.database.model.RecordEntity
import com.example.transferdata.database.repository.RecordDatabase
import com.example.transferdata.dataset.DatasetGenerator
import com.example.transferdata.navigation.MainNavGraphArgs.RECORD_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RecordDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recordDatabase: RecordDatabase,
    private val datasetGenerator: DatasetGenerator
) : ViewModel() {
    private val recordId = savedStateHandle.getDecodedJsonParam<Long>(RECORD_ID)

    private val _record = MutableStateFlow<RecordEntity?>(null)
    val record = _record.asStateFlow()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    private val _saveDatasetDownloadStatus = MutableStateFlow<SavingDatasetStatus?>(null)
    val saveDatasetDownloadStatus = _saveDatasetDownloadStatus.asStateFlow()

    private val _saveDatasetShareStatus = MutableStateFlow<SavingDatasetStatus?>(null)
    val saveDatasetShareStatus = _saveDatasetShareStatus.asStateFlow()

    private val _heartRateSmartWatch = MutableStateFlow<List<HeartRateGenericData>>(emptyList())
    val heartRateSmartWatch = _heartRateSmartWatch.asStateFlow()

    private val _heartRatePolar = MutableStateFlow<List<HeartRateGenericData>>(emptyList())
    val heartRatePolar = _heartRatePolar.asStateFlow()

    fun getContent() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            try {
                _record.value = recordDatabase.recordDao().getById(recordId)
                val starTime = _record.value?.starRecordingNanos ?: 0L
                val endTime = _record.value?.stopRecordingNanos ?: Long.MAX_VALUE
                val heartRateSW =
                    recordDatabase.heartRateSmartwatchDao().getAllDataFromRecord(recordId)
                val heartRateP = recordDatabase.heartRatePolarDao().getAllDataFromRecord(recordId)
                _heartRateSmartWatch.value = heartRateSW
                    .map {
                        HeartRateGenericData(
                            it.heartRate,
                            it.timestamp + (record.value?.clockSkewSmartwatchNanos ?: 0L)
                        )
                    }
                    .filter { it.timestamp in starTime..endTime }
                _heartRatePolar.value = heartRateP.filter { it.timestamp in starTime..endTime }
                _screenState.value = ScreenState.Content
            } catch (e: Exception) {
                _screenState.value = ScreenState.Error(e.message ?: "Unknown error")
            }

        }
    }

    fun onFileCreatedDownload(file: File) {
        viewModelScope.launch {
            datasetGenerator.generateDataset(recordId, file)
                .flowOn(Dispatchers.IO)
                .collect { status ->
                    _saveDatasetDownloadStatus.value = status
                    Log.d("RecordDetailViewModel", "Saving dataset to download status: $status")
                }
        }
    }

    fun onFileCreatedShare(file: File) {
        viewModelScope.launch {
            datasetGenerator.generateDataset(recordId, file)
                .flowOn(Dispatchers.IO)
                .collect { status ->
                    _saveDatasetShareStatus.value = status
                    Log.d("RecordDetailViewModel", "Saving dataset to share status: $status")
                }
        }
    }

    fun dismissSaveDatasetDownloadStatus() {
        _saveDatasetDownloadStatus.value = null
    }

    fun dismissSaveDatasetShareStatus() {
        _saveDatasetShareStatus.value = null
    }
}