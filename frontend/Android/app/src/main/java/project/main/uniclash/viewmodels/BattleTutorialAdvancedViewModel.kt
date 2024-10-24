package project.main.uniclash.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

sealed class AdvancedTutorialStep(val associatedDialogStep: AdvancedTutorialDialogStep) {
    object Introduction : AdvancedTutorialStep(AdvancedTutorialDialogStep.Welcome)
    object TypesOfAttacks : AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainTypesOfAttacks)
    object Buffs : AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainBuffs)
    object Debuffs : AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainDebuffs)
    object Usage: AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainUsage)

    object LetplayerPlay: AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainLetPlayerPlay)


}

sealed interface AdvancedTutorialDialogStep {
    object Welcome : AdvancedTutorialDialogStep
    object ExplainTypesOfAttacks : AdvancedTutorialDialogStep
    object ExplainBuffs : AdvancedTutorialDialogStep
    object ExplainDebuffs : AdvancedTutorialDialogStep
    object ExplainUsage : AdvancedTutorialDialogStep
    object ExplainLetPlayerPlay: AdvancedTutorialDialogStep



    // Add more steps as needed
}

class BattleTutorialAdvancedViewModel(
    private val critterService: CritterService,
    private var playerTurn: Boolean,
) : ViewModel() {
    //TAG for logging
    private val TAG = BattleTutorialViewModel::class.java.simpleName
    private val _battleText = MutableStateFlow("Battle started!")
    private val _tutorialDialogStep = MutableStateFlow<TutorialDialogStep>(TutorialDialogStep.Welcome)
    val tutorialDialogStep = MutableStateFlow<AdvancedTutorialDialogStep>(AdvancedTutorialDialogStep.Welcome)
    val isPlayerTurn = MutableStateFlow<Boolean>(false)
    val playerWon = MutableStateFlow<Boolean?>(null)
    val battleText: MutableStateFlow<String> get() = _battleText
    var advancedTutorialStep by mutableStateOf<AdvancedTutorialStep>(AdvancedTutorialStep.Introduction)
        private set
    private val cpuAttackOrder = listOf(
        Attack(4, "QuizzQuestion", 55, AttackType.DAMAGE_DEALER,"NORMAL"),
        Attack(4, "QuizzQuestion", 55, AttackType.DAMAGE_DEALER,"NORMAL"),
        Attack(2, "SuperGuard", 15, AttackType.DEF_BUFF,"NORMAL"),
        Attack(3, "AttackBreak", 15, AttackType.ATK_DEBUFF,"NORMAL"),
        Attack(1, "Rollout", 70, AttackType.DAMAGE_DEALER,"NORMAL"),
        Attack(2, "SuperGuard", 15, AttackType.DEF_BUFF,"NORMAL"),
        Attack(3, "AttackBreak", 15, AttackType.ATK_DEBUFF,"NORMAL"),
        Attack(4, "QuizzQuestion", 55, AttackType.DAMAGE_DEALER,"NORMAL"),
        Attack(2, "SuperGuard", 15, AttackType.DEF_BUFF,"NORMAL"),
        Attack(3, "AttackBreak", 15, AttackType.ATK_DEBUFF,"NORMAL"),
        Attack(4, "QuizzQuestion", 55, AttackType.DAMAGE_DEALER,"NORMAL"),
        Attack(2, "SuperGuard", 15, AttackType.DEF_BUFF,"NORMAL"),
        Attack(1, "Rollout", 70, AttackType.DAMAGE_DEALER,"NORMAL"),
        Attack(2, "SuperGuard", 15, AttackType.DEF_BUFF,"NORMAL"),
        Attack(3, "AttackBreak", 15, AttackType.ATK_DEBUFF,"NORMAL"),
        Attack(4, "QuizzQuestion", 55, AttackType.DAMAGE_DEALER,"NORMAL"),
        Attack(2, "SuperGuard", 15, AttackType.DEF_BUFF,"NORMAL"),
        Attack(3, "AttackBreak", 15, AttackType.ATK_DEBUFF,"NORMAL"),
        Attack(4, "QuizzQuestion", 55, AttackType.DAMAGE_DEALER,"NORMAL"),
    )
    private var cpuAttackIndex = 0

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

            val playerAttack1 = Attack(1, "Splash", 60, AttackType.DAMAGE_DEALER,"NORMAL")
            val playerAttack2 = Attack(2, "HyperBeam", 70, AttackType.DAMAGE_DEALER,"NORMAL")
            val playerAttack3 = Attack(3, "Defence Break", 15, AttackType.DEF_DEBUFF,"NORMAL")
            val playerAttack4 = Attack(4, "Beak Sharpener", 25, AttackType.ATK_BUFF,"NORMAL")
            val listOfPlayerAttacks = listOf(playerAttack1, playerAttack2, playerAttack3, playerAttack4)
            val playerTutorialCritter = CritterUsable(24, "TerrorFirstEvo", 100, 70, 80, 50, listOfPlayerAttacks,1, 1,0,"NORMAL")

            playerCritter.update { state ->
                state.copy(playerCritter = playerTutorialCritter, isLoading = false)
            }

            val cpuAttack1 = Attack(1, "Rollout", 70, AttackType.DAMAGE_DEALER,"NORMAL")
            val cpuAttack2 = Attack(2, "SuperGuard", 25, AttackType.DEF_BUFF,"NORMAL")
            val cpuAttack3 = Attack(3, "ShieldBreak", 15, AttackType.DEF_DEBUFF,"NORMAL")
            val cpuAttack4 = Attack(4, "SkullCrush", 70, AttackType.DAMAGE_DEALER,"NORMAL")
            val listOfCpuAttacks = listOf(cpuAttack1, cpuAttack2, cpuAttack3, cpuAttack4)
            val cpuTutorialCritter = CritterUsable(23, "terrorsecondevo", 130, 60, 115, 30, listOfCpuAttacks,1, 1,0,"NORMAL")

            cpuCritter.update { state ->
                state.copy(cpuCritter = cpuTutorialCritter, isLoading = false)
            }
        }
        doesPlayerStart()
    }

    fun nextTutorialStep() {
        val currentStep = advancedTutorialStep
        val nextStep = when (currentStep) {
            AdvancedTutorialStep.Introduction -> AdvancedTutorialStep.TypesOfAttacks
            AdvancedTutorialStep.TypesOfAttacks -> AdvancedTutorialStep.Buffs
            AdvancedTutorialStep.Buffs -> AdvancedTutorialStep.Debuffs
            AdvancedTutorialStep.Debuffs -> AdvancedTutorialStep.Usage
            AdvancedTutorialStep.Usage -> AdvancedTutorialStep.LetplayerPlay
            AdvancedTutorialStep.LetplayerPlay -> AdvancedTutorialStep.Introduction
        }
        advancedTutorialStep = nextStep
        tutorialDialogStep.value = nextStep.associatedDialogStep
    }


    fun getTutorialMessage(step: AdvancedTutorialDialogStep): String {
        return when (step) {
            AdvancedTutorialDialogStep.Welcome -> "Welcome to the advanced Tutorial. Here you are going to learn more advanced mechanics."
            AdvancedTutorialDialogStep.ExplainTypesOfAttacks -> "In Uniclash there are 3 types of attacks:\n" +
                    "NORMAL Attacks: These are the normal Attacks that deal DAMAGE to your opponent. You already used them in the previous Tutorial.\n" +
                    "BUFFS: These will INCREASE your Critters Attack or Defence during the battle.\n" +
                    "DEBUFFS: which will DECREASE your opponents Defence or Attack."
            AdvancedTutorialDialogStep.ExplainBuffs -> "Buffs can easily be spotted by the green sword or shield next to the attack.\n" +
                    "Attacks with a green sword will INCREASE your own Attack stat.\n" +
                    "Attacks with a green shield will INCREASE your own Defence stat.\n" +
                    "But you cannot buff infinitely. There is a maximum value of 180 for both defence and attack."
            AdvancedTutorialDialogStep.ExplainDebuffs -> "DeBuffs on the other hand are displayed by a Red sword/shield next to the attack.\n" +
                    "Attacks with a red sword will DECREASE the enemy´s Attack stat.\n" +
                    "Attacks with a red shield will DECREASE the enemy´s Defence stat.\n" +
                    "But you cannot debuff infinitely. There is a minimum value of 20 for both defence and attack."
            AdvancedTutorialDialogStep.ExplainUsage -> "You might ask yourself: Why should I use Buffs and Debuffs when I can just deal damage instead...\n" +
                    "And you are right, but this little dragon over there is a tanky dude. He has a Defence stat of 115 and 130 HP. It is very important to lower that defence before using an normal attack!"
            AdvancedTutorialDialogStep.ExplainLetPlayerPlay -> "Okay now its your turn! But be aware that buffing and debuffing several times is crucial in a battle so use them often!\n" +
                    "Also be careful the Quizizzdragon is able to use Buffs and Debuffs as well! Read the BattleLog carefully."

            else -> {return "null"}
        }
    }


    fun executePlayerAttack() {
        if (playerInput.value.isPlayerAttackSelected) {
            if(playerInput.value.selectedPlayerAttack!!.attackType==AttackType.ATK_BUFF ||
                playerInput.value.selectedPlayerAttack!!.attackType==AttackType.DEF_BUFF ){
                applyBuffToPlayer(playerInput.value.selectedPlayerAttack!!)
            }
            if(playerInput.value.selectedPlayerAttack!!.attackType==AttackType.ATK_DEBUFF ||
                playerInput.value.selectedPlayerAttack!!.attackType==AttackType.DEF_DEBUFF){
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
            if(cpuInput.value.selectedCpuAttack!!.attackType==AttackType.ATK_BUFF ||
                cpuInput.value.selectedCpuAttack!!.attackType==AttackType.DEF_BUFF ){
                applyBuffToCpu(cpuInput.value.selectedCpuAttack!!)
            }
            if(cpuInput.value.selectedCpuAttack!!.attackType==AttackType.ATK_DEBUFF ||
                cpuInput.value.selectedCpuAttack!!.attackType==AttackType.DEF_DEBUFF){
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

    private fun chooseCpuAttack(): Attack? {

        return null
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

    private fun applyBuffToPlayer(attack: Attack) {
        if (attack.attackType == AttackType.ATK_BUFF) {
            viewModelScope.launch() {
                val newAtk = (playerCritter.value.playerCritter!!.atk + attack.strength).coerceAtMost(180)
                val increased = newAtk > playerCritter.value.playerCritter!!.atk
                playerCritter.update { currentState ->
                    currentState.copy(
                        playerCritter = currentState.playerCritter?.copy(
                            atk = newAtk
                        ),
                    )
                }
                _battleText.value = if (increased) {
                    "${playerCritter.value.playerCritter!!.name}'s Attack Increased to $newAtk!"
                } else {
                    "${playerCritter.value.playerCritter!!.name}'s Attack is already at maximum!"
                }
            }
        }
        if (attack.attackType == AttackType.DEF_BUFF) {
            viewModelScope.launch() {
                val newDef = (playerCritter.value.playerCritter!!.def + attack.strength).coerceAtMost(180)
                val increased = newDef > playerCritter.value.playerCritter!!.def
                playerCritter.update { currentState ->
                    currentState.copy(
                        playerCritter = currentState.playerCritter?.copy(
                            def = newDef
                        ),
                    )
                }
                _battleText.value = if (increased) {
                    "${playerCritter.value.playerCritter!!.name}'s Defense Increased to $newDef!"
                } else {
                    "${playerCritter.value.playerCritter!!.name}'s Defense is already at maximum!"
                }
            }
        }
    }

    private fun applyDebuffToPlayer(attack: Attack) {
        var newAtk = 0
        var newDef = 0
        viewModelScope.launch {
            playerCritter.update { currentState ->
                val currentAtk = currentState.playerCritter?.atk ?: 0
                val currentDef = currentState.playerCritter?.def ?: 0

                newAtk = if (attack.attackType == AttackType.ATK_DEBUFF) {
                    (currentAtk - attack.strength).coerceAtLeast(20)
                } else currentAtk
                newDef = if (attack.attackType == AttackType.DEF_DEBUFF) {
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
                attack.attackType == AttackType.ATK_DEBUFF && playerCritter.value.playerCritter!!.atk == 1 ->
                    "${playerCritter.value.playerCritter!!.name}'s Attack can't be decreased any further!"
                attack.attackType == AttackType.ATK_DEBUFF ->
                    "${playerCritter.value.playerCritter!!.name}'s Attack fell to $newAtk!"
                attack.attackType == AttackType.DEF_DEBUFF && playerCritter.value.playerCritter!!.def == 1 ->
                    "${playerCritter.value.playerCritter!!.name}'s Defence can't be decreased any further!"
                attack.attackType == AttackType.DEF_DEBUFF ->
                    "${playerCritter.value.playerCritter!!.name}'s Defence fell to $newDef!"
                else -> ""
            }
        }
    }

    private fun applyDebuffToCpu(attack: Attack) {
        var newAtk = 0
        var newDef = 0
        viewModelScope.launch {
            cpuCritter.update { currentState ->
                val currentAtk = currentState.cpuCritter?.atk ?: 0
                val currentDef = currentState.cpuCritter?.def ?: 0

                newAtk = if (attack.attackType == AttackType.ATK_DEBUFF) {
                    (currentAtk - attack.strength).coerceAtLeast(20)
                } else currentAtk

                newDef = if (attack.attackType == AttackType.DEF_DEBUFF) {
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
                attack.attackType == AttackType.ATK_DEBUFF && cpuCritter.value.cpuCritter!!.atk == 1 ->
                    "${cpuCritter.value.cpuCritter!!.name}'s Attack can't be decreased any further!"
                attack.attackType == AttackType.ATK_DEBUFF ->
                    "${cpuCritter.value.cpuCritter!!.name}'s Attack fell to $newAtk"
                attack.attackType == AttackType.DEF_DEBUFF && cpuCritter.value.cpuCritter!!.def == 1 ->
                    "${cpuCritter.value.cpuCritter!!.name}'s Defence can't be decreased any further!"
                attack.attackType == AttackType.DEF_DEBUFF ->
                    "${cpuCritter.value.cpuCritter!!.name}'s Defence fell to $newDef"
                else -> ""
            }
        }
    }

    private fun applyBuffToCpu(attack: Attack) {
        if (attack.attackType == AttackType.ATK_BUFF) {
            viewModelScope.launch() {
                val newAtk = (cpuCritter.value.cpuCritter!!.atk + attack.strength).coerceAtMost(180)
                val increased = newAtk > cpuCritter.value.cpuCritter!!.atk
                cpuCritter.update { currentState ->
                    currentState.copy(
                        cpuCritter = currentState.cpuCritter?.copy(
                            atk = newAtk
                        ),
                    )
                }
                _battleText.value = if (increased) {
                    "${cpuCritter.value.cpuCritter!!.name}'s Attack Increased to $newAtk!"
                } else {
                    "${cpuCritter.value.cpuCritter!!.name}'s Attack is already at maximum!"
                }
            }
        }
        if (attack.attackType == AttackType.DEF_BUFF) {
            viewModelScope.launch() {
                val newDef = (cpuCritter.value.cpuCritter!!.def + attack.strength).coerceAtMost(180)
                val increased = newDef > cpuCritter.value.cpuCritter!!.def
                cpuCritter.update { currentState ->
                    currentState.copy(
                        cpuCritter = currentState.cpuCritter?.copy(
                            def = newDef
                        ),
                    )
                }
                _battleText.value = if (increased) {
                    "${cpuCritter.value.cpuCritter!!.name}'s Defense Increased to $newDef!"
                } else {
                    "${cpuCritter.value.cpuCritter!!.name}'s Defense is already at maximum!"
                }
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
                    return BattleTutorialAdvancedViewModel(
                        critterService,
                        false
                    ) as T
                }
            }
    }

}