package project.main.uniclash.dataManagers

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import project.main.uniclash.dataStore
import project.main.uniclash.datatypes.CritterUsable
import androidx.datastore.preferences.core.stringSetPreferencesKey

/*
Could violate against "Single source of truth"
 */
class CritterListDataManager(context: Context) {
    private val context = context
    private val dataStore = context.dataStore

    private val userDataManager: UserDataManager by lazy {
        UserDataManager(Application())
    }

    suspend fun storeCritterList(critterUsables: List<CritterUsable>) {
            var id = "0"
            if(userDataManager.getUserId() != null){
                id = userDataManager.getUserId()!!
            }
            storeUserId(id)

            val CRITTER_LIST = stringSetPreferencesKey("critter_list")
            val critterSet = critterUsables.map { it.toString() }.toSet()

            context.dataStore.edit { preferences ->
                preferences[CRITTER_LIST] = critterSet
            }
    }

    suspend fun getCritterList(): ArrayList<CritterUsable> {
        if(!(userDataManager.getUserId().equals(getUserId()))) {
            clearCritterList()
            return ArrayList()
        }
            val CRITTER_LIST = stringSetPreferencesKey("critter_list")

            val critterSet = dataStore.data
                .map { preferences ->
                    preferences[CRITTER_LIST] ?: emptySet()
                }.first()

            // Assuming CritterUsable has a method to create an instance from a string representation
            return critterSet.mapTo(ArrayList()) { CritterUsable.fromString(it) }

    }

    private suspend fun storeUserId(id : String) {
        println("new userid")
        val USER_ID = stringPreferencesKey("user_idCritter")
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    private suspend fun getUserId(): String? {
        val USER_ID = stringPreferencesKey("user_idCritter")
        return dataStore.data
            .map { preferences ->
                preferences[USER_ID]
            }.first()
    }

    suspend fun checkCritterListIsNotEmpty() : Boolean{
        return getCritterList().isNotEmpty()
    }

    suspend fun clearCritterList(){
        val CRITTER_LIST = stringSetPreferencesKey("critter_list")
        context.dataStore.edit { preferences ->
            preferences[CRITTER_LIST] = emptySet()
        }
    }
}
