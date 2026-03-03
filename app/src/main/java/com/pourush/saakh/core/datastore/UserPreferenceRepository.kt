package com.pourush.saakh.core.datastore
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create the DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "saakh_prefs")

// Defining Roles
enum class UserRole {
    LABORER,
    CONTRACTOR,
    UNASSIGNED // Default state when they first install the app
}

// 3. The Clean Repository
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private val ROLE_KEY = stringPreferencesKey("user_role")

    // Read the role as a continuous stream (Flow)
    val userRoleFlow: Flow<UserRole> = dataStore.data.map { preferences ->
        val roleString = preferences[ROLE_KEY] ?: UserRole.UNASSIGNED.name
        try {
            UserRole.valueOf(roleString)
        } catch (e: IllegalArgumentException) {
            UserRole.UNASSIGNED
        }
    }

    // Save the new role
    suspend fun saveRole(role: UserRole) {
        dataStore.edit { preferences ->
            preferences[ROLE_KEY] = role.name
        }
    }
}