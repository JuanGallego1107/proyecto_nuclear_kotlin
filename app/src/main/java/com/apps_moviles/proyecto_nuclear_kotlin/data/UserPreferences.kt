package com.apps_moviles.proyecto_nuclear_kotlin.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore
val Context.userDataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    private val USER_ID = intPreferencesKey("user_id")
    private val USER_FULLNAME = stringPreferencesKey("user_fullname")

    // -----------------------------
    // SAVE USER DATA
    // -----------------------------
    suspend fun saveUser(id: Int, fullname: String) {
        context.userDataStore.edit { prefs ->
            prefs[USER_ID] = id
            prefs[USER_FULLNAME] = fullname
        }
    }

    suspend fun saveUserId(id: Int) {
        context.userDataStore.edit { prefs ->
            prefs[USER_ID] = id
        }
    }

    suspend fun saveUserFullname(fullname: String) {
        context.userDataStore.edit { prefs ->
            prefs[USER_FULLNAME] = fullname
        }
    }

    // -----------------------------
    // READ USER DATA
    // -----------------------------
    val userId: Flow<Int?> = context.userDataStore.data
        .map { prefs -> prefs[USER_ID] }

    val userFullname: Flow<String?> = context.userDataStore.data
        .map { prefs -> prefs[USER_FULLNAME] }

    // -----------------------------
    // CLEAR DATA (LOGOUT)
    // -----------------------------
    suspend fun clearUser() {
        context.userDataStore.edit { prefs ->
            prefs.remove(USER_ID)
            prefs.remove(USER_FULLNAME)
        }
    }
}
