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
import project.main.uniclash.datatypes.Counter
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.dataManagers.UserDataManager


sealed interface UseFartSprayUIState {
    data class HasEntries(
        val canBeUsed: Int,
        val isLoading: Boolean,
    ) : UseFartSprayUIState
}

class MapItemViewModel(private val inventoryService: InventoryService,) : ViewModel() {

    private val userDataManager: UserDataManager by lazy {
        UserDataManager(Application())
    }
    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching MapLocation ")
        }
    }

    val useFartSpray = MutableStateFlow(
        UseFartSprayUIState.HasEntries(
            canBeUsed = -1,
            isLoading = false
        )
    )

    fun useFartSpray(){
            viewModelScope.launch {
                useFartSpray.update { state ->
                    state.copy(isLoading = true, canBeUsed = -1)
                }
                try {
                    val response = inventoryService.useItem(userDataManager.getStudentId()!!, 3).enqueue()
                    if (response.isSuccessful) {
                        Log.d(TAG, "Success: ${response.body()}")
                        response.body()?.let {
                            if (it) {
                                Counter.RESPAWN.setCounter(3)
                                useFartSpray.update { state ->
                                    state.copy(canBeUsed = 1, isLoading = false)
                                }
                            } else {
                                useFartSpray.update { state ->
                                    state.copy(canBeUsed = 0, isLoading = false)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    fun resetCanBeUsedValue(){
        viewModelScope.launch {
            useFartSpray.update { state ->
                state.copy(isLoading = true, canBeUsed = -1)
            }
        }
    }

    companion object {
        private const val LOCATION_TAG = "MyLocationTag"

        fun provideFactory(inventoryService: InventoryService): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MapItemViewModel(inventoryService) as T
                }
            }
    }
}