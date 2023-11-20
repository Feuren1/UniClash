package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import project.main.uniclash.datatypes.Critter
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.CritterService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.battle.BattleLogic
import project.main.uniclash.battle.BattleLogicView
import project.main.uniclash.battle.BattleResult
import project.main.uniclash.retrofit.enqueue

public data class playerCritterIdCallback(val success: Boolean, val id: String)
public data class cpuCritterIdCallback(val success: Boolean, val id: String)

sealed interface PlayerCritterUIState {
    data class HasEntries(
        val playerCritter: CritterUsable?,
        val isLoading: Boolean,
        var hasTurn: Boolean,
    ) : CritterUsableUIState
}

sealed interface CpuCritterUIState {
    data class HasEntries(
        val cpuCritter: CritterUsable?,
        val isLoading: Boolean,
        var hasTurn: Boolean,
    ) : CritterUsableUIState
}


class BattleViewModel(
    private val critterService: CritterService,
    private var playerTurn: Boolean,
) : ViewModel() {
    //TAG for logging
    private val TAG = UniClashViewModel::class.java.simpleName
    private var battleLogic: BattleLogicView? = null;

    private val _battleText = MutableStateFlow("Battle started!")
    val battleText: MutableStateFlow<String> get() = _battleText

    val playerCritter = MutableStateFlow(
        PlayerCritterUIState.HasEntries(
            playerCritter = null,
            isLoading = true,
            hasTurn = false,
        )
    )

    val cpuCritter = MutableStateFlow(
        CpuCritterUIState.HasEntries(
            cpuCritter = null,
            isLoading = true,
            hasTurn = false,
        )
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial critters data: ")
            loadPlayerCritter(19)
            loadCpuCritter(20)
        }
        if(playerCritter.value.playerCritter!=null&&cpuCritter.value.cpuCritter!=null) {
            whoStarts()
        }
    }

    fun whoStarts(){
        if(playerCritter.value.playerCritter!!.spd>cpuCritter.value.cpuCritter!!.spd){
            playerTurn = true
        }
        if (playerCritter.value.playerCritter!!.spd==cpuCritter.value.cpuCritter!!.spd){

        }
        else {
            playerTurn = false
        }
    }

    fun checkResult(): BattleResult {
        if(playerCritter.value.playerCritter!!.hp<=0){
            return BattleResult.CPU_WINS;
        }
        if(cpuCritter.value.cpuCritter!!.hp<=0){
            return BattleResult.PLAYER_WINS
        }
        return BattleResult.NOTOVER
    }

    fun applyDamageToPlayer(damage: Int) {
        viewModelScope.launch() {
            playerCritter.update { currentState ->
                currentState.copy(
                    playerCritter = currentState.playerCritter?.copy(
                        hp = (currentState.playerCritter.hp) - damage
                    ),
                    // You might want to update other properties if needed
                    //hasTurn = true, // Update as needed
                    //isLoading = false // Update as needed
                )
            }
        }
        val result = checkResult()
        _battleText.value = when (result) {
            BattleResult.PLAYER_WINS -> "Player wins!"
            BattleResult.CPU_WINS -> "CPU wins!"
            else -> "CPU took $damage damage!"
        }
    }
    fun applyDamageToPCpu(damage: Int) {
        viewModelScope.launch() {
            cpuCritter.update { currentState ->
                currentState.copy(
                    cpuCritter = currentState.cpuCritter?.copy(
                        hp = (currentState.cpuCritter.hp) - damage
                    ),
                )
            }
        }
        val result = checkResult()
        _battleText.value = when (result) {
            BattleResult.PLAYER_WINS -> "Player wins!"
            BattleResult.CPU_WINS -> "CPU wins!"
            else -> "Player took $damage damage!"
        }
    }

    fun loadPlayerCritter(id: Int) {
        viewModelScope.launch {
            playerCritter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritterUsable(id).enqueue()
                Log.d(TAG, "LoadPlayerCritter: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body()?.let {
                        playerCritter.update { state ->
                            state.copy(playerCritter = it, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadCpuCritter(id: Int) {
        viewModelScope.launch {
            cpuCritter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritterUsable(id).enqueue()
                Log.d(TAG, "LoadCpuCritter: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body()?.let {
                        cpuCritter.update { state ->
                            state.copy(cpuCritter = it, isLoading = false)
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
                    return BattleViewModel(
                        critterService,
                        false
                    ) as T
                }
            }
    }

}