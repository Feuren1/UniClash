package project.main.uniclash.userDataManager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import project.main.uniclash.dataStore

class UserDataManager(context: Context) {
    private val context = context
    private val dataStore = context.dataStore

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
}
