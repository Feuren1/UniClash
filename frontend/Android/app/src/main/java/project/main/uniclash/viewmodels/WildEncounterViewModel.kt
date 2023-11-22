package project.main.uniclash.viewmodels

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import project.main.uniclash.MapActivity
import project.main.uniclash.WildEncounterActivity
import project.main.uniclash.datatypes.CritterForStudent
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.MyMarker
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.enqueue
import retrofit2.Call

sealed interface PostCrittersUIState {
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

    val critters = MutableStateFlow(
        PostCrittersUIState.HasEntries(
            critter = null,
            isLoading = false
        )
    )
    fun addWildEncounterToUser(){
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
    }

   /* fun loadCritterUsable(id: Int) {
        viewModelScope.launch {
            critterUsable.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritterUsable(id).enqueue()
                Log.d(TAG, "loadCritterUsable: $response")
                if (response.isSuccessful) {
                    response.body()?.let {
                        critterUsable.update { state ->
                            state.copy(critterUsable = it, isLoading = false)
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
