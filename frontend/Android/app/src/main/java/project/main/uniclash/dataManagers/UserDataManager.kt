package project.main.uniclash.dataManagers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import project.main.uniclash.dataStore

class UserDataManager(context: Context) {
    private val context = context
    private val dataStore = context.dataStore

    suspend fun storeFightingCritterID(id: Int) {
        val CRITTER_ID = intPreferencesKey("critter_id")
        context.dataStore.edit { preferences ->
            preferences[CRITTER_ID] = id
        }
    }

    suspend fun getFightingCritterID(): Int? {
        val CRITTER_ID = intPreferencesKey("critter_id")
        return dataStore.data
            .map { preferences ->
                preferences[CRITTER_ID]
            }.first()
    }
    suspend fun storeUserId(id: String) {
        val USER_ID = stringPreferencesKey("user_id")
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    suspend fun getUserId(): String? {
        val USER_ID = stringPreferencesKey("user_id")
        return dataStore.data
            .map { preferences ->
                preferences[USER_ID]
            }.first()
    }

    suspend fun storeJWTToken(token: String) {
        val USER_TOKEN = stringPreferencesKey("user_token")
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    suspend fun getJWTToken(): String? {
        val USER_TOKEN = stringPreferencesKey("user_token")
        return dataStore.data
            .map { preferences ->
                preferences[USER_TOKEN]
            }.first()
    }

    suspend fun storeFCMToken(token: String) {
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        context.dataStore.edit { preferences ->
            preferences[FCM_TOKEN] = token
        }
    }

    suspend fun getFCMToken(): String? {
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        return dataStore.data
            .map { preferences ->
                preferences[FCM_TOKEN]
            }.first()
    }

    suspend fun storeStudentId(id: Int) {
        val STUDENT_ID = intPreferencesKey("student_id")
        context.dataStore.edit { preferences ->
            preferences[STUDENT_ID] = id
        }
    }

    suspend fun getStudentId(): Int? {
        val STUDENT_ID = intPreferencesKey("student_id")
        return dataStore.data
            .map { preferences ->
                preferences[STUDENT_ID]
            }.first()
    }

    suspend fun storePlacedBuildings(number: Int) {
        val PLACED_BUILDINGS = intPreferencesKey("placed_buildings")
        context.dataStore.edit { preferences ->
            preferences[PLACED_BUILDINGS] = number
        }
    }

    suspend fun getPlacedBuildings(): Int? {
        val PLACED_BUILDINGS = intPreferencesKey("placed_buildings")
        return dataStore.data
            .map { preferences ->
                preferences[PLACED_BUILDINGS]
            }.first()
    }

    suspend fun storeLevel(number: Int) {
        val LEVEL = intPreferencesKey("level")
        context.dataStore.edit { preferences ->
            preferences[LEVEL] = number
        }
    }

    suspend fun getLevel(): Int? {
        val LEVEL = intPreferencesKey("level")
        return dataStore.data
            .map { preferences ->
                preferences[LEVEL]
            }.first()
    }
}
