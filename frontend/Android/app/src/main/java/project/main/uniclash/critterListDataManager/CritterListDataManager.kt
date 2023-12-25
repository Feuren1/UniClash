package project.main.uniclash.userDataManager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import project.main.uniclash.dataStore
import project.main.uniclash.datatypes.CritterUsable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow

class CritterListDataManager(context: Context) {
    private val context = context
    private val dataStore = context.dataStore

    suspend fun storeCritterList(critterUsables: List<CritterUsable>) {
        val CRITTER_LIST = stringSetPreferencesKey("critter_list")
        val critterSet = critterUsables.map { it.toString() }.toSet()

        context.dataStore.edit { preferences ->
            preferences[CRITTER_LIST] = critterSet
        }
    }

    suspend fun getCritterList(): ArrayList<CritterUsable> {
        val CRITTER_LIST = stringSetPreferencesKey("critter_list")

        val critterSet = dataStore.data
            .map { preferences ->
                preferences[CRITTER_LIST] ?: emptySet()
            }.first()

        // Assuming CritterUsable has a method to create an instance from a string representation
        return critterSet.mapTo(ArrayList()) { CritterUsable.fromString(it) }
    }

    suspend fun checkCritterListIsNotEmpty() : Boolean{
        if(getCritterList().isEmpty()){
            return false
        }
            return true
    }

    suspend fun clearCritterList(){
        val CRITTER_LIST = stringSetPreferencesKey("critter_list")
        context.dataStore.edit { preferences ->
            preferences[CRITTER_LIST] = emptySet()
        }
    }
}
