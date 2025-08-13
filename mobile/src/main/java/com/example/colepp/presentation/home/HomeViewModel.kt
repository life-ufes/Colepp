package com.example.colepp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colepp.common.utils.InstructionCard
import com.example.colepp.database.model.RecordEntity
import com.example.colepp.database.repository.RecordDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recordDatabase: RecordDatabase
) : ViewModel() {
    val recordings = recordDatabase.recordDao().getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _showDeleteDialog = MutableStateFlow<Pair<Boolean, RecordEntity?>>(false to null)
    val showDeleteDialog = _showDeleteDialog.asStateFlow()

    private val _section = MutableStateFlow(HOME_SECTION)
    val section = _section.asStateFlow()

    private val _expandedInstructions = MutableStateFlow<Map<InstructionCard, Boolean>>(emptyMap())
    val expandedInstructions = _expandedInstructions.asStateFlow()

    fun setSection(section: String) {
        _section.value = section
    }

    fun setShowDeleteDialog(record: RecordEntity) {
        _showDeleteDialog.value = true to record
    }

    fun dismissDeleteDialog() {
        _showDeleteDialog.value = false to null
    }

    fun deleteRecording(recordId: Long) {
        viewModelScope.launch {
            try {
                recordDatabase.recordDao().deleteById(recordId)
                _showDeleteDialog.value = false to null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleInstructionExpansion(instruction: InstructionCard) {
        _expandedInstructions.value = _expandedInstructions.value.toMutableMap().apply {
            this[instruction] = !this.getOrDefault(instruction, false)
        }
    }

    companion object {
        const val HOME_SECTION = "home_section"
        const val INSTRUCTIONS_SECTION = "instructions_section"
    }
}