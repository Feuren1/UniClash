package project.main.uniclash.dataManagers

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import project.main.uniclash.dataStore

class PermissionManager(context: Context) {
    private val context = context
    private val dataStore = context.dataStore

    suspend fun storePublishLocationPermission(agreement: Boolean) {
        val PUBLISHLOCATION = booleanPreferencesKey("location")
        context.dataStore.edit { preferences ->
            preferences[PUBLISHLOCATION] = agreement
        }
    }

    private suspend fun publishLocationPermission(): Boolean? {
        val PUBLISHLOCATION = booleanPreferencesKey("location")
        return dataStore.data
            .map { preferences ->
                preferences[PUBLISHLOCATION]
            }.first()
    }

    suspend fun getPublishLocationPermission(): Boolean? {
        if(publishLocationPermission() == null){
            storePublishLocationPermission(false)
        }
        return publishLocationPermission()
    }
}
