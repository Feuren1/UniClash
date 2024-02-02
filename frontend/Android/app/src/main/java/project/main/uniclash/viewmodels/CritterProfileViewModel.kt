package project.main.uniclash.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.dataManagers.CritterListDataManager
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.datatypes.CritterTemplate


sealed interface DelCritterUIState {
    data class HasEntries(
        val critter: String,
        val isLoading: Boolean,
    ) : DelCritterUIState
}

sealed interface UseRedbullUIState {
    data class HasEntries(
        val used: Boolean,
        val isLoading: Boolean,
    ) : DelCritterUIState
}

sealed interface IsSelectedCritterUIState {
    data class HasEntries(
        val isSelected: Boolean,
    ) : IsSelectedCritterUIState
}

sealed interface CritterTemplateUIState {
    data class HasEntries(
        val critterTemplate: CritterTemplate?,
        val isLoading: Boolean,
    ) : DelCritterUIState
}

class CritterProfileViewModel(
    private val critterService: CritterService,
    private val inventoryService : InventoryService
): ViewModel() {
    private val TAG = CritterProfileViewModel::class.java.simpleName
    val userDataManager: UserDataManager by lazy {
        UserDataManager(Application())
    }
    private val critterListDataManager: CritterListDataManager by lazy {
        CritterListDataManager(Application())
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

    val critterTemplate = MutableStateFlow(
        CritterTemplateUIState.HasEntries(
             critterTemplate = CritterTemplate(id = 1, name = "", baseAttack = 1, baseDefence = 1, baseHealth = 1, baseSpeed = 1, evolesAt = 0, evolvesIntoTemplateId = 0),
            isLoading = false,
        )
    )

    val critterTemplateEvo = MutableStateFlow(
        CritterTemplateUIState.HasEntries(
            critterTemplate = CritterTemplate(id = 1, name = "", baseAttack = 1, baseDefence = 1, baseHealth = 1, baseSpeed = 1, evolesAt = 0, evolvesIntoTemplateId = 0),
            isLoading = false,
        )
    )

    val redbullUsage = MutableStateFlow(
        UseRedbullUIState.HasEntries(
            used = false,
            isLoading = false,
        )
    )

    val isSelected = MutableStateFlow(
        IsSelectedCritterUIState.HasEntries(
            isSelected = false
        )
    )

    fun checkIfCritterIsSelected(critterId : Int){
        viewModelScope.launch {
            if (userDataManager.getFightingCritterID() != null && userDataManager.getFightingCritterID() == critterId){
                isSelected.update { it.copy(isSelected = true) }
            }
        }
    }

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

    fun loadCritterTemplate(templateId: Int) {
        viewModelScope.launch {
            critterUsable.update { it.copy(isLoading = true) }
            try {
                println("step1")
                val response = critterService.getCrittersTemplate(templateId).enqueue()
                println("step2")
                Log.d(TAG, "loadCritterTemplate: $response")
                if (response.isSuccessful) {
                    response.body()?.let {
                        critterTemplate.update { state ->
                            state.copy(critterTemplate = it, isLoading = false)
                        }
                    }
                }
                println(critterTemplate.value.critterTemplate.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadCritterTemplateEvo(templateId: Int) {
        viewModelScope.launch {
            critterUsable.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCrittersTemplate(templateId).enqueue()
                Log.d(TAG, "loadCritterTemplate: $response")
                if (response.isSuccessful) {
                    response.body()?.let {
                        critterTemplateEvo.update { state ->
                            state.copy(critterTemplate = it, isLoading = false)
                        }
                    }
                }
                println(critterTemplate.value.critterTemplate.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun delCritter(id: Int) {
        viewModelScope.launch {
            userDataManager.storeFightingCritterID(0)
            critterListDataManager.clearCritterList() //to refresh critterList

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
            checkIfCritterIsSelected(critter.value.critter!!.id)
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

            critterListDataManager.clearCritterList() //to refresh critterList

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
            loadCritterUsable(id)
        }
    }

    fun useRedBull(critterId: Int) {
        viewModelScope.launch {

            critterListDataManager.clearCritterList() //to refresh critterList

            redbullUsage.update { it.copy(isLoading = true) }
            try {
                val response = inventoryService.useRedBull(critterId).enqueue()
                if (response.isSuccessful) {
                    println(response.body())
                    response.body()?.let {
                        redbullUsage.update { state ->
                            state.copy(used = it, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            loadCritterUsable(critterId)
        }
    }
    companion object {
        fun provideFactory(
            critterService: CritterService,
            inventoryService : InventoryService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CritterProfileViewModel(
                        critterService,
                        inventoryService
                    ) as T
                }
            }
    }
}