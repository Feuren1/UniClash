package project.main.uniclash.viewmodels

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
import project.main.uniclash.datatypes.AttackType



class FinalBattleViewModel(
    private val critterService: CritterService,
    private var playerTurn: Boolean,
) : ViewModel() {
    //TAG for logging
    private val TAG = BattleTutorialViewModel::class.java.simpleName
    private val _battleText = MutableStateFlow("Battle started!")
    val isPlayerTurn = MutableStateFlow<Boolean>(false)
    val playerWon = MutableStateFlow<Boolean?>(null)
    private val cpuAttackOrder = listOf(
    Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
    Attack(2, "SuperGuard", 15, AttackType.DEF_Buff),
    Attack(3, "ShieldBreak", 15, AttackType.DEF_DeBuff),
    Attack(4, "Beak Sharpener", 20, AttackType.ATK_Buff),
    )

    private var cpuAttackIndex = 0

    val battleText: MutableStateFlow<String> get() = _battleText

    val playerCritter = MutableStateFlow(
        PlayerCritterTutorialUIState.HasEntries(
            playerCritter = null,
            isLoading = true,
            hasTurn = false,
        )
    )

    val cpuCritter = MutableStateFlow(
        CpuCritterTutorialUIState.HasEntries(
            cpuCritter = null,
            isLoading = true,
            hasTurn = false,
        )
    )

    val playerInput = MutableStateFlow(
        PlayerInputTutorialUIState.HasEntries(
            isPlayerAttackSelected = false,
            selectedPlayerAttack = null,
        )
    )

    val cpuInput = MutableStateFlow(
        CpuInputTutorialUIState.HasEntries(
            isCpuAttackSelected = false,
            selectedCpuAttack = null,
        )
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial critters data: ")

            val playerAttack1 = Attack(1, "Attack Break", 15, AttackType.ATK_DeBuff)
            val playerAttack2 = Attack(2, "Hyper Beam", 90, AttackType.DAMAGE_DEALER)
            val playerAttack3 = Attack(3, "Super Guard", 15, AttackType.DEF_Buff)
            val playerAttack4 = Attack(4, "Beak Sharpener", 15, AttackType.ATK_Buff)
            val listOfPlayerAttacks = listOf(playerAttack1, playerAttack2, playerAttack3, playerAttack4)
            val playerTutorialCritter = CritterUsable(40, "Prc2Duck", 160, 70, 80, 50, listOfPlayerAttacks,1, 1)

            playerCritter.update { state ->
                state.copy(playerCritter = playerTutorialCritter, isLoading = false)
            }

            val cpuAttack1 = Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER)
            val cpuAttack2 = Attack(2, "Super Guard", 15, AttackType.DEF_Buff)
            val cpuAttack3 = Attack(3, "Shield Break", 15, AttackType.DEF_DeBuff)
            val cpuAttack4 = Attack(4, "Beak Sharpener", 15, AttackType.ATK_Buff)
            val listOfCpuAttacks = listOf(cpuAttack1, cpuAttack2, cpuAttack3, cpuAttack4)
            val cpuTutorialCritter = CritterUsable(40, "Linuxpenguin", 130, 100, 65, 70, listOfCpuAttacks,1, 1)

            cpuCritter.update { state ->
                state.copy(cpuCritter = cpuTutorialCritter, isLoading = false)
            }
        }
        doesPlayerStart()
    }

    fun executePlayerAttack() {
        if (playerInput.value.isPlayerAttackSelected) {
            if(playerInput.value.selectedPlayerAttack!!.attackType==AttackType.ATK_Buff ||
                playerInput.value.selectedPlayerAttack!!.attackType==AttackType.DEF_Buff ){
                applyBuffToPlayer(playerInput.value.selectedPlayerAttack!!)
            }
            if(playerInput.value.selectedPlayerAttack!!.attackType==AttackType.ATK_DeBuff ||
                playerInput.value.selectedPlayerAttack!!.attackType==AttackType.DEF_DeBuff){
                applyDebuffToCpu(playerInput.value.selectedPlayerAttack!!)
            }
            if(playerInput.value.selectedPlayerAttack!!.attackType==AttackType.DAMAGE_DEALER){
                applyDamageToPCpu(playerInput.value.selectedPlayerAttack!!.strength)
            }
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
        isPlayerTurn.value = false
    }

    fun executeCpuAttack() {
        if (cpuInput.value.isCpuAttackSelected) {
            if(cpuInput.value.selectedCpuAttack!!.attackType==AttackType.ATK_Buff ||
                cpuInput.value.selectedCpuAttack!!.attackType==AttackType.DEF_Buff ){
                applyBuffToCpu(cpuInput.value.selectedCpuAttack!!)
            }
            if(cpuInput.value.selectedCpuAttack!!.attackType==AttackType.ATK_DeBuff ||
                cpuInput.value.selectedCpuAttack!!.attackType==AttackType.DEF_DeBuff){
                applyDebuffToPlayer(cpuInput.value.selectedCpuAttack!!)
            }
            if(cpuInput.value.selectedCpuAttack!!.attackType==AttackType.DAMAGE_DEALER){
                applyDamageToPlayer(cpuInput.value.selectedCpuAttack!!.strength)
            }
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
        isPlayerTurn.value = true
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
        _battleText.value = "${playerCritter.value.playerCritter!!.name} attacks with ${playerInput.value.selectedPlayerAttack!!.name}!"
    }

    fun selectCpuAttack() {
        val cpuCritter = cpuCritter.value.cpuCritter
        cpuCritter?.let {
            val attack = if (cpuAttackIndex < cpuAttackOrder.size) {
                cpuAttackOrder[cpuAttackIndex]
            } else {
                // If the predefined attack order is exhausted, choose a random attack or handle as needed
                // For simplicity, this example chooses a random attack
                it.attacks.random()
            }

            viewModelScope.launch() {
                cpuInput.update { currentState ->
                    currentState.copy(
                        isCpuAttackSelected = true,
                        selectedCpuAttack = attack,
                    )
                }

                _battleText.value = "${cpuCritter.name} attacks with ${attack.name}!"
            }

            cpuAttackIndex++
        }
    }


    fun doesPlayerStart(){
        if(playerCritter.value.playerCritter!!.spd>cpuCritter.value.cpuCritter!!.spd){
            isPlayerTurn.value = true
        }
        if (playerCritter.value.playerCritter!!.spd==cpuCritter.value.cpuCritter!!.spd){
            isPlayerTurn.value = true
        }
        if(playerCritter.value.playerCritter!!.spd<cpuCritter.value.cpuCritter!!.spd) {
            isPlayerTurn.value = false
        }
    }

    fun checkResult(): BattleResult {
        if(playerCritter.value.playerCritter!!.hp<=0){
            playerWon.value = false
            return BattleResult.CPU_WINS;
        }
        if(cpuCritter.value.cpuCritter!!.hp<=0){
            playerWon.value = true
            return BattleResult.PLAYER_WINS
        }
        return BattleResult.NOTOVER
    }

    private fun applyBuffToPlayer(attack: Attack){
        if(attack.attackType==AttackType.ATK_Buff) {
            viewModelScope.launch() {
                playerCritter.update { currentState ->
                    currentState.copy(
                        playerCritter = currentState.playerCritter?.copy(
                            atk = playerCritter.value.playerCritter!!.atk + attack.strength
                        ),
                    )
                }
                _battleText.value = "${playerCritter.value.playerCritter!!.name}´s Attack Increased!"
            }
        }
        if(attack.attackType==AttackType.DEF_Buff) {
            viewModelScope.launch() {
                playerCritter.update { currentState ->
                    currentState.copy(
                        playerCritter = currentState.playerCritter?.copy(
                            def = playerCritter.value.playerCritter!!.def + attack.strength
                        ),
                    )
                }
                _battleText.value = "${playerCritter.value.playerCritter!!.name}´s Defense Increased!"
            }
        }
    }

    private fun applyDebuffToPlayer(attack: Attack) {
        viewModelScope.launch {
            playerCritter.update { currentState ->
                val currentAtk = currentState.playerCritter?.atk ?: 0
                val currentDef = currentState.playerCritter?.def ?: 0

                val newAtk = if (attack.attackType == AttackType.ATK_DeBuff) {
                    (currentAtk - attack.strength).coerceAtLeast(20)
                } else currentAtk

                val newDef = if (attack.attackType == AttackType.DEF_DeBuff) {
                    (currentDef - attack.strength).coerceAtLeast(20)
                } else currentDef

                currentState.copy(
                    playerCritter = currentState.playerCritter?.copy(
                        atk = newAtk,
                        def = newDef
                    )
                )
            }

            _battleText.value = when {
                attack.attackType == AttackType.ATK_DeBuff && playerCritter.value.playerCritter!!.atk == 1 ->
                    "${playerCritter.value.playerCritter!!.name}'s Attack can't be decreased any further!"
                attack.attackType == AttackType.ATK_DeBuff ->
                    "${playerCritter.value.playerCritter!!.name}'s Attack fell!"
                attack.attackType == AttackType.DEF_DeBuff && playerCritter.value.playerCritter!!.def == 1 ->
                    "${playerCritter.value.playerCritter!!.name}'s Defence can't be decreased any further!"
                attack.attackType == AttackType.DEF_DeBuff ->
                    "${playerCritter.value.playerCritter!!.name}'s Defence fell!"
                else -> ""
            }
        }
    }

    private fun applyDebuffToCpu(attack: Attack) {
        viewModelScope.launch {
            cpuCritter.update { currentState ->
                val currentAtk = currentState.cpuCritter?.atk ?: 0
                val currentDef = currentState.cpuCritter?.def ?: 0

                val newAtk = if (attack.attackType == AttackType.ATK_DeBuff) {
                    (currentAtk - attack.strength).coerceAtLeast(20)
                } else currentAtk

                val newDef = if (attack.attackType == AttackType.DEF_DeBuff) {
                    (currentDef - attack.strength).coerceAtLeast(20)
                } else currentDef

                currentState.copy(
                    cpuCritter = currentState.cpuCritter?.copy(
                        atk = newAtk,
                        def = newDef
                    )
                )
            }

            _battleText.value = when {
                attack.attackType == AttackType.ATK_DeBuff && cpuCritter.value.cpuCritter!!.atk == 1 ->
                    "${cpuCritter.value.cpuCritter!!.name}'s Attack can't be decreased any further!"
                attack.attackType == AttackType.ATK_DeBuff ->
                    "${cpuCritter.value.cpuCritter!!.name}'s Attack fell"
                attack.attackType == AttackType.DEF_DeBuff && cpuCritter.value.cpuCritter!!.def == 1 ->
                    "${cpuCritter.value.cpuCritter!!.name}'s Defence can't be decreased any further!"
                attack.attackType == AttackType.DEF_DeBuff ->
                    "${cpuCritter.value.cpuCritter!!.name}'s Defence fell"
                else -> ""
            }
        }
    }

    private fun applyBuffToCpu(attack: Attack){
        if(attack.attackType==AttackType.ATK_Buff) {
            viewModelScope.launch() {
                cpuCritter.update { currentState ->
                    currentState.copy(
                        cpuCritter = currentState.cpuCritter?.copy(
                            atk = cpuCritter.value.cpuCritter!!.atk + attack.strength
                        ),
                    )
                }
                _battleText.value = "${cpuCritter.value.cpuCritter!!.name}´s Attack was increased!"
            }
        }
        if(attack.attackType==AttackType.DEF_Buff) {
            viewModelScope.launch() {
                cpuCritter.update { currentState ->
                    currentState.copy(
                        cpuCritter = currentState.cpuCritter?.copy(
                            def = cpuCritter.value.cpuCritter!!.def + attack.strength
                        ),
                    )
                }
                _battleText.value = "${cpuCritter.value.cpuCritter!!.name}´s Defence was increased!"
            }
        }

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

    companion object {
        fun provideFactory(
            critterService: CritterService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FinalBattleViewModel(
                        critterService,
                        false
                    ) as T
                }
            }
    }

}