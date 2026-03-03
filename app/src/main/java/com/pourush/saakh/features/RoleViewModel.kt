package com.pourush.saakh.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pourush.saakh.core.datastore.UserPreferencesRepository
import com.pourush.saakh.core.datastore.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    // Expose the role as a StateFlow so the UI instantly reacts to changes.
    // Initial value is null so we know when DataStore is still loading from disk.
    val userRole: StateFlow<UserRole?> = repository.userRoleFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun saveRole(role: UserRole) {
        viewModelScope.launch {
            repository.saveRole(role)
        }
    }
}