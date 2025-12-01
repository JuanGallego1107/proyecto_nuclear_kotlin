package com.apps_moviles.proyecto_nuclear_kotlin.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore
val Context.userDataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    private val USER_ID = intPreferencesKey("user_id")

    // Save user ID
    suspend fun saveUserId(id: Int) {
        context.userDataStore.edit { prefs ->
            prefs[USER_ID] = id
        }
    }

    // Get user ID as Flow
    val userId: Flow<Int?> = context.userDataStore.data
        .map { prefs ->
            prefs[USER_ID]
        }

    // Clear user ID (logout)
    suspend fun clearUserId() {
        context.userDataStore.edit { prefs ->
            prefs.remove(USER_ID)
        }
    }
}
