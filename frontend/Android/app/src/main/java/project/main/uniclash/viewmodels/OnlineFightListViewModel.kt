package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.datatypes.Critter
import project.main.uniclash.datatypes.OnlineFight
import project.main.uniclash.retrofit.OnlineFightService

sealed interface OnlineFightsUIState {
    data class HasEntries(
        var onlineFights: List<OnlineFight>,
        val isLoading: Boolean,
    ) : OnlineFightsUIState
}
class OnlineFightListViewModel(
    private val onlineFightService: OnlineFightService,
    private val application: Application
) : ViewModel() {
    //TAG for logging
    private val TAG = OnlineFightListViewModel::class.java.simpleName
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(application)
    }

    val onlineFights = MutableStateFlow(
        OnlineFightsUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    @SuppressLint("MissingPermission")
    fun loadOnlineFights() {
        viewModelScope.launch {
            onlineFights.update { it.copy(isLoading = true) }
                try {
                    val response =
                        onlineFightService.getOnlineFights().enqueue()
                    Log.d(TAG, "loadOnlineFightList: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "loadOnlineFightList: success")
                        val onlineFightList = response.body()!!
                        Log.d(TAG, "loadOnlineFightList: $onlineFightList")

                        onlineFights.update {
                            it.copy(
                                onlineFights = onlineFightList,
                                isLoading = false
                            )
                        }
                    }
                    filterFights()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    private fun filterFights() {
        viewModelScope.launch {
            val currentUserStudentId = userDataManager.getStudentId()

            onlineFights.value.onlineFights = onlineFights.value.onlineFights.filter { fight ->
                fight.studentId == currentUserStudentId
            }
        }
    }


    companion object {
        fun provideFactory(
            onlineFightService: OnlineFightService,
            application: Application,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OnlineFightListViewModel(
                        onlineFightService,
                        application
                    ) as T
                }
            }
    }

    init {
        loadOnlineFights()
    }
}