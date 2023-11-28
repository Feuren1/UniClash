package project.main.uniclash.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.MyMarker
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.enqueue

sealed interface PostCrittersUIState { //TODO: CritterS to Critter?
    data class HasEntries(
        val critter: CritterUsable?,
        val isLoading: Boolean,
    ) : PostCrittersUIState
}
class WildEncounterViewModel(
    private val critterService: CritterService
) : ViewModel() {
    private val wildEncounterMarker = SelectedMarker.SELECTEDMARKER.takeMarker()
    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching WildEncounterViewModel ")
        }
    }
    fun getWildEncounterMarker(): MyMarker?{
        return wildEncounterMarker
    }

    val critters = MutableStateFlow( //TODO: change critterS to critter, is not a list
        PostCrittersUIState.HasEntries( //TODO: here too
            critter = null,
            isLoading = false
        )
    )

    fun addWildEncounterToUser(){
        viewModelScope.launch {
            critters.update { it.copy(isLoading = true) }
            try {
                val response = critterService.postCatchedCritter(1,
                    wildEncounterMarker!!.critterUsable!!.critterId).enqueue()
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
    }
    /*fun addWildEncounterToUser(){
        viewModelScope.launch {
            critters.update { it.copy(isLoading = true) }
            try {
                var critterForStudent = CritterForStudent(1,1,"testi",1,1)
                val response = critterService.postStudentCritter(1, critterForStudent).enqueue()
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
    }*/


    companion object {
        fun provideFactory(
            critterService: CritterService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WildEncounterViewModel(
                        critterService,
                    ) as T
                }
            }
    }
}
