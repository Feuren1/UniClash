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
import project.main.uniclash.datatypes.OnlineFightInformation
import project.main.uniclash.retrofit.OnlineFightService

sealed interface OnlineFightsUIState {
    data class HasEntries(
        var onlineFightsInformation: List<OnlineFightInformation>,
        val isLoading: Boolean,
    ) : OnlineFightsUIState
}

sealed interface SelectedCritterUIState {
    data class HasEntries(
        val isSelected: Boolean,
    ) : SelectedCritterUIState
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

    var selectedCritter = MutableStateFlow(
        SelectedCritterUIState.HasEntries(
            isSelected  = false
        )
    )

    @SuppressLint("MissingPermission")
    fun loadOnlineFights() {
        viewModelScope.launch {
            onlineFights.update { it.copy(isLoading = true) }
                try {
                    val response =
                        onlineFightService.getFightInformationList(userDataManager.getStudentId()!!).enqueue()
                    Log.d(TAG, "loadOnlineFightList: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "loadOnlineFightList: success")
                        val onlineFightList = response.body()!!
                        Log.d(TAG, "loadOnlineFightList: $onlineFightList")

                        onlineFights.update {
                            it.copy(
                                onlineFightsInformation = onlineFightList,
                                isLoading = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun insertCritter(fightConnectionId : Int) {
        viewModelScope.launch {
            try {
                val response =
                    onlineFightService.insertCritter(fightConnectionId,userDataManager.getStudentId()!!,userDataManager.getFightingCritterID()!!).enqueue()
                Log.d(TAG, "loadInsertCritter: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadInsertCritter: success")
                    val insertCritter = response.body()!!
                    Log.d(TAG, "loadInsertCritter: $insertCritter")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkSelectedCritter(){
        viewModelScope.launch {
            if (userDataManager.getFightingCritterID() != null && userDataManager.getFightingCritterID() != 0) {
                selectedCritter.update { it.copy(isSelected = true) }
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
        checkSelectedCritter()
    }
}