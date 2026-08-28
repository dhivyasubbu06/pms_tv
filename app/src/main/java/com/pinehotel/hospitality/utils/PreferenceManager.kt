package com.pinehotel.hospitality.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val ROOM_NUMBER_KEY = stringPreferencesKey("room_number")
    }

    suspend fun saveRoomNumber(roomNumber: String) {
        dataStore.edit { preferences ->
            preferences[ROOM_NUMBER_KEY] = roomNumber
        }
    }

    val roomNumberFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ROOM_NUMBER_KEY]
    }
}
