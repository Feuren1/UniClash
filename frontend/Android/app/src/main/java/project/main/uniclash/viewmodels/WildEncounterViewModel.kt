package project.main.uniclash.viewmodels

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.MarkerWildEncounter
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.userDataManager.UserDataManager

sealed interface PostCrittersUIState { //TODO: CritterS to Critter?
    data class HasEntries(
        val critter: CritterUsable?,
        val isLoading: Boolean,
    ) : PostCrittersUIState
}
class WildEncounterViewModel(
    private val critterService: CritterService,
    private val application: Application
) : ViewModel() {

    private val markerData = SelectedMarker.SELECTEDMARKER.takeMarker()
    private val wildEncounterMarker = if(markerData is MarkerWildEncounter){markerData} else {null}
    private var catchChance = 0.0
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(application)
    }

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching WildEncounterViewModel ")
        }
    }
    fun getWildEncounterMarker(): MarkerWildEncounter?{
        return wildEncounterMarker
    }

    val critters = MutableStateFlow( //TODO: change critterS to critter, is not a list
        PostCrittersUIState.HasEntries( //TODO: here too
            critter = null,
            isLoading = false
        )
    )

    private fun isCatchSuccessful(): Boolean {
        val randomValue = Math.random() * 100.0

        return randomValue <= catchChance
    }

     fun addWildEncounterToUser() : String {
        if(isCatchSuccessful()) {
            viewModelScope.launch {
                critters.update { it.copy(isLoading = true) }
                try {
                    val response = critterService.postCatchedCritter(
                        userDataManager.getStudentId(),
                        wildEncounterMarker!!.critterUsable!!.critterId
                    ).enqueue()
                    Log.d(TAG, "loadCrittersUsable: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "Success: ${response.body()}")
                        response.body()?.let {
                            critters.update { state ->
                                state.copy(critter = it, isLoading = false)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return "Critter caught!\nYou receive 25EP and 1 Credit"
        } else{
            return "Critter escapes!"
        }
    }

    fun calculateCatchChance(critterLevel :Int) : Double{
        var chance = 0.0
        viewModelScope.launch {
            val userLevel = userDataManager.getLevel()
            var difference = userLevel?.minus(critterLevel)
            if (difference != null) {
                if(difference > 0){
                    difference = 0
                }
                chance = (100 -difference*-1).toDouble()
            }
        }
        catchChance = chance
        return chance
    }


    companion object {
        fun provideFactory(
            critterService: CritterService,
            application: Application,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WildEncounterViewModel(
                        critterService,
                        application
                    ) as T
                }
            }
    }
}
