package project.main.uniclash.viewmodels

import android.app.Application
import android.util.Log
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.Critter
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.userDataManager.UserDataManager


sealed interface DelCritterUIState {
    data class HasEntries(
        val critter: String,
        val isLoading: Boolean,
    ) : DelCritterUIState
}

class CritterProfileViewModel(
    private val critterService: CritterService
): ViewModel() {
    private val TAG = CritterProfileViewModel::class.java.simpleName
    val userDataManager: UserDataManager by lazy {
        UserDataManager(Application())
    }
    val critter = MutableStateFlow(
        CritterUIState.HasEntries(
            critter = null,
            isLoading = false,
        )
    )

    val delCritter = MutableStateFlow(
        DelCritterUIState.HasEntries(
            critter = "null",
            isLoading = false,
        )
    )


    val critterUsable = MutableStateFlow(
        CritterUsableUIState.HasEntries(
            critterUsable = null,
            isLoading = false,
        )
    )
    fun loadCritterUsable(id: Int) {
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
    }

    fun delCritter(id: Int) {
        viewModelScope.launch {
            delCritter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.delCritter(id).enqueue()
                Log.d(TAG, "delete Critter: $response")
                if (response.isSuccessful) {
                    response.body()?.let {
                        delCritter.update { state ->
                            state.copy(critter = it, isLoading = false)
                        }
                    }
                }
                critterUsable.update { state->
                    state.copy(critterUsable = null, isLoading = false)
                }
                critter.update { state->
                    state.copy(critter = null, isLoading = false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun storeFightingCritter(){
        viewModelScope.launch {
            userDataManager.storeFightingCritterID(critter.value.critter!!.id)
        }
    }
    fun loadCritter(id: Int) {
        viewModelScope.launch {
            critter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritters(id).enqueue()
                Log.d(TAG, "loadCritter: $response")
                if (response.isSuccessful) {
                    println(response.body())
                    response.body()?.let {
                        critter.update { state ->
                            state.copy(critter = it, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun evolve(id: Int) {
        viewModelScope.launch {
            critter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.evolveCritter(id).enqueue()
                Log.d(TAG, "evolve: $response")
                if (response.isSuccessful) {
                    println(response.body())
                    response.body()?.let {
                        critter.update { state ->
                            state.copy(critter = it, isLoading = false)
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
            critterService: CritterService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CritterProfileViewModel(
                        critterService,
                    ) as T
                }
            }
    }
}