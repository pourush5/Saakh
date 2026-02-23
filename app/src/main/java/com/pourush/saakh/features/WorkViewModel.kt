package com.pourush.saakh.features
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pourush.saakh.core.database.WorkEntry
import com.pourush.saakh.core.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val repository: WorkRepository
) : ViewModel() {

    // The UI observes this list. When the DB changes, this updates automatically.
    val workEntries: StateFlow<List<WorkEntry>> = repository.allWorkEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addEntry(date: Long, hours: Float, wage: Double, notes: String) {
        viewModelScope.launch {
            val entry = WorkEntry(
                date = date,
                hoursWorked = hours,
                wageRate = wage,
                notes = notes
            )
            repository.addWorkEntry(entry)
        }
    }

    fun deleteEntry(entry: WorkEntry) {
        viewModelScope.launch {
            repository.deleteWorkEntry(entry)
        }
    }
}