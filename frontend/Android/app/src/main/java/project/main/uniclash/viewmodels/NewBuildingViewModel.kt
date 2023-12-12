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
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.MarkerWildEncounter
import project.main.uniclash.datatypes.NewArena
import project.main.uniclash.datatypes.NewStudentHub
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.datatypes.StudentHub
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.userDataManager.UserDataManager

sealed interface PostArenaUIState { //TODO: CritterS to Critter?
    data class HasEntries(
        val arena: Arena?,
        val isLoading: Boolean,
    ) : PostArenaUIState
}
sealed interface PostStudentHubUIState { //TODO: CritterS to Critter?
    data class HasEntries(
        val studentHub: StudentHub?,
        val isLoading: Boolean,
    ) : PostStudentHubUIState
}
class NewBuildingViewModel(
private  val arenaService: ArenaService,
    private val studentHubService: StudentHubService
) : ViewModel() {
    val arena = MutableStateFlow(
        PostArenaUIState.HasEntries(
            arena = null,
            isLoading = false
        )
    )

    val studentHub = MutableStateFlow(
        PostStudentHubUIState.HasEntries(
            studentHub = null,
            isLoading = false
        )
    )
    fun addArena(name : String, description : String, lat : String, long : String, pic : String){
        viewModelScope.launch {
            arena.update { it.copy(isLoading = true) }
            try {
                var newArena = NewArena(name,description,lat,long,0,pic)
                val response = arenaService.postArena(newArena).enqueue()
                Log.d(TAG, "loadArena: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body()?.let {
                        arena.update { state ->
                            state.copy(arena = it, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addStudentHub(name : String, description : String, lat : String, long : String, pic : String){
        viewModelScope.launch {
            studentHub.update { it.copy(isLoading = true) }
            try {
                var newStudentHub = NewStudentHub(name,description,lat,long,pic)
                val response = studentHubService.postStudentHub(newStudentHub).enqueue()
                Log.d(TAG, "loadStudentHub: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body()?.let {
                        studentHub.update { state ->
                            state.copy(studentHub = it, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        fun provideFactory(
            arenaService: ArenaService,
            studentHubService: StudentHubService
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NewBuildingViewModel(
                        arenaService,
                        studentHubService
                    ) as T
                }
            }
    }
}
