package project.main.uniclash.viewmodels

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.MarkerWildEncounter
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.dataManagers.CritterListDataManager
import project.main.uniclash.dataManagers.UserDataManager

sealed interface PostCrittersUIState { //TODO: CritterS to Critter?
    data class HasEntries(
        val critter: CritterUsable?,
        val isLoading: Boolean,
    ) : PostCrittersUIState
}

sealed interface UseItemUIState {
    data class HasEntries(
        val quantitiy : Int,
        val itemAvail : Boolean,
        val isLoading: Boolean,
    ) : UseItemUIState
}
class WildEncounterViewModel(
    private val critterService: CritterService,
    private val inventoryService: InventoryService,
    private val application: Application
) : ViewModel() {

    private val markerData = SelectedMarker.SELECTEDMARKER.takeMarker()
    private val wildEncounterMarker = if(markerData is MarkerWildEncounter){markerData} else {null}
    var catchChance by mutableStateOf(0.0)
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(application)
    }

    private val critterListDataManager: CritterListDataManager by lazy {
        CritterListDataManager(Application())
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

    val usedItem = MutableStateFlow(
        UseItemUIState.HasEntries(
            quantitiy = 0,
            itemAvail = true,
            isLoading = false
        )
    )
    val usedItemQuantityFlow = usedItem.map { it.quantitiy }

    init {
        viewModelScope.launch {
            usedItemQuantityFlow.collect {
                calculateCatchChance()
            }
        }
    }

    private fun isCatchSuccessful(): Boolean {
        val randomValue = Math.random() * 100.0

        return randomValue <= catchChance
    }

     fun addWildEncounterToUser() : String {
        if(isCatchSuccessful()) {
            viewModelScope.launch {

                critterListDataManager.clearCritterList() //to refresh critterList

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
                                state.copy(critter = it,  isLoading = false)
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

    fun useChocolatewaffle(): Boolean {
        if (catchChance < 90) {
            viewModelScope.launch {
                usedItem.update { state ->
                    state.copy(isLoading = true)
                }
                try {
                    val response = inventoryService.useItem(userDataManager.getStudentId()!!, 2).enqueue()
                    if (response.isSuccessful) {
                        Log.d(TAG, "Success: ${response.body()}")
                        response.body()?.let {
                            if (it) {
                                usedItem.update { state ->
                                    state.copy(quantitiy = state.quantitiy + 1, itemAvail = it, isLoading = false)
                                }
                            } else {
                                usedItem.update { state ->
                                    state.copy(quantitiy = state.quantitiy, itemAvail = it, isLoading = false)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return true
    }


    fun calculateCatchChance() : Double{
        var chance = (usedItem.value.quantitiy*5).toDouble()
        viewModelScope.launch {
            val userLevel = userDataManager.getLevel()
            var difference = userLevel?.minus(wildEncounterMarker!!.critterUsable!!.level)
            if (difference != null) {
                if(difference > 0){
                    difference = 0
                }
                chance += (100 -difference*-1).toDouble()
            }
        }
        if(chance>90){
            chance = 90.0
        }
        catchChance = chance
        println("new catch Chance $catchChance")
        return chance
    }


    companion object {
        fun provideFactory(
            critterService: CritterService,
            inventoryService: InventoryService,
            application: Application,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WildEncounterViewModel(
                        critterService,
                        inventoryService,
                        application
                    ) as T
                }
            }
    }
}
