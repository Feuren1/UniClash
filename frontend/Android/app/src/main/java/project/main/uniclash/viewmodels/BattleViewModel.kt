
package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.CritterService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.battle.BattleResult
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.dataManagers.UserDataManager





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

sealed interface PlayerInputUIState {
    data class HasEntries(
        val isPlayerAttackSelected: Boolean,
        val selectedPlayerAttack: Attack?,
    ) : PlayerInputUIState
}

sealed interface CpuInputUIState {
    data class HasEntries(
        val isCpuAttackSelected: Boolean,
        val selectedCpuAttack: Attack?,
    ) : CpuInputUIState
}

class BattleViewModel(
    private val critterService: CritterService,
    private var playerTurn: Boolean,
) : ViewModel() {
    //TAG for logging
    private val TAG = UniClashViewModel::class.java.simpleName
    private val _battleText = MutableStateFlow("Battle started!")
    val userDataManager: UserDataManager by lazy {
        UserDataManager(Application())
    }
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

    val playerInput = MutableStateFlow(
        PlayerInputUIState.HasEntries(
            isPlayerAttackSelected = false,
            selectedPlayerAttack = null,
        )
    )

    val cpuInput = MutableStateFlow(
        CpuInputUIState.HasEntries(
            isCpuAttackSelected = false,
            selectedCpuAttack = null,
        )
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial critters data: ")
            loadPlayerCritter()
            loadCpuCritter(146)
        }
    }

    fun executePlayerAttack() {
        if (playerInput.value.isPlayerAttackSelected) {
            // Execute the attack logic here
            // For example, you can apply CPU damage based on the selected attack
            applyDamageToPCpu(playerInput.value.selectedPlayerAttack!!.strength)
            // Reset the state for the next turn
            viewModelScope.launch() {
                playerInput.update { currentState ->
                    currentState.copy(
                        isPlayerAttackSelected = false,
                        selectedPlayerAttack = null,
                    )
                }
            }
        }

        chooseCpuAttack()?.let { selectCpuAttack(it) }
    }

    fun executeCpuAttack() {
        if (cpuInput.value.isCpuAttackSelected) {
            // Execute the attack logic here
            // For example, you can apply CPU damage based on the selected attack
            applyDamageToPlayer(cpuInput.value.selectedCpuAttack!!.strength)
            // Reset the state for the next turn
            viewModelScope.launch() {
                cpuInput.update { currentState ->
                    currentState.copy(
                        isCpuAttackSelected = false,
                        selectedCpuAttack = null,
                    )
                }
            }
        }
    }


    fun selectPlayerAttack(attack: Attack) {
        viewModelScope.launch() {
            playerInput.update { currentState ->
                currentState.copy(
                    isPlayerAttackSelected = true,
                    selectedPlayerAttack = attack,
                )
            }
        }
    }

    fun selectCpuAttack(attack: Attack) {
        viewModelScope.launch() {
            cpuInput.update { currentState ->
                currentState.copy(
                    isCpuAttackSelected = true,
                    selectedCpuAttack = attack,
                )
            }
        }
    }

    fun doesPlayerStart(): Boolean{
        if(playerCritter.value.playerCritter!!.spd>cpuCritter.value.cpuCritter!!.spd){
            return true
        }
        if (playerCritter.value.playerCritter!!.spd==cpuCritter.value.cpuCritter!!.spd){
            return true;
        }
        if(playerCritter.value.playerCritter!!.spd<cpuCritter.value.cpuCritter!!.spd) {
            return false
        } else {
            return false
        }
    }

    private fun chooseCpuAttack(): Attack? {
        val cpuCritter = cpuCritter.value.cpuCritter
        cpuCritter?.let {
            val randomIndex = (0 until it.attacks.size).random()
            return  it.attacks[randomIndex]
        }
        return null
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

    private fun applyDamageToPlayer(damage: Int) {
        var damageAfterCalculation = calculatePlayerDamage(damage)
        viewModelScope.launch() {
            playerCritter.update { currentState ->
                currentState.copy(
                    playerCritter = currentState.playerCritter?.copy(
                        hp = (currentState.playerCritter.hp) - damageAfterCalculation
                    ),
                )
            }
        }
        val result = checkResult()
        _battleText.value = when (result) {
            BattleResult.PLAYER_WINS -> "Player wins!"
            BattleResult.CPU_WINS -> "CPU wins!"
            else -> "${playerCritter.value.playerCritter!!.name} took $damageAfterCalculation damage!"
        }
    }
    private fun applyDamageToPCpu(damage: Int) {
        var damageAfterCalculation = calculateCpuDamage(damage)
        viewModelScope.launch() {
            cpuCritter.update { currentState ->
                currentState.copy(
                    cpuCritter = currentState.cpuCritter?.copy(
                        hp = (currentState.cpuCritter.hp) - damageAfterCalculation
                    ),
                )
            }
        }
        val result = checkResult()
        _battleText.value = when (result) {
            BattleResult.PLAYER_WINS -> "Player wins!"
            BattleResult.CPU_WINS -> "CPU wins!"
            else -> "${cpuCritter.value.cpuCritter!!.name} took $damageAfterCalculation damage!"
        }
    }

    private fun calculatePlayerDamage(attack: Int): Int {
        val def = playerCritter.value.playerCritter!!.def;
        val atk = cpuCritter.value.cpuCritter!!.atk;
        val level = cpuCritter.value.cpuCritter!!.level;
        return (((((2*level)/5)+2)*attack*atk/def)/50)+2
    }

    private fun calculateCpuDamage(attack: Int): Int {
        val def = cpuCritter.value.cpuCritter!!.def;
        val atk = playerCritter.value.playerCritter!!.atk;
        val level = playerCritter.value.playerCritter!!.level;
        return (((((2*level)/5)+2)*attack*atk/def)/50)+2
    }

    @SuppressLint("MissingPermission")
    fun loadPlayerCritter() {
        viewModelScope.launch {
            playerCritter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritterUsables(userDataManager.getStudentId()!!).enqueue()
                Log.d(TAG, "loadCrittersUsable: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadCrittersUsables: success")
                    val crittersUsables = response.body()!!
                    Log.d(TAG, "loadCrittersUsables: $crittersUsables")
                    playerCritter.update {
                        it.copy(
                            playerCritter = crittersUsables[0],
                            isLoading = false
                        )
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