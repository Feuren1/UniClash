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
    object WinCondition : AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainCombat)
    object PlayerHP : AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainPlayerHP)
    object CpuHP: AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainCpuHP)
    object Level : AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainLevel)
    object Attacks : AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainAttacks)
    object Stats : AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainStats)
    object SelectAttack : AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainSelectAttack)
    object ExecuteAttack : AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainExecuteAttack)
    object ExecuteCpuAttack: AdvancedTutorialStep(AdvancedTutorialDialogStep.ExplainExecuteCpuAttack)
    object LetPlayerPlay: AdvancedTutorialStep(AdvancedTutorialDialogStep.LetPlayerPlay)
    object FinishTutorial: AdvancedTutorialStep(AdvancedTutorialDialogStep.FinishTutorial)
}

sealed interface AdvancedTutorialDialogStep {
    object Welcome : AdvancedTutorialDialogStep
    object ExplainCombat : AdvancedTutorialDialogStep
    object ExplainPlayerHP : AdvancedTutorialDialogStep
    object ExplainCpuHP : AdvancedTutorialDialogStep
    object ExplainLevel : AdvancedTutorialDialogStep
    object ExplainAttacks : AdvancedTutorialDialogStep
    object ExplainStats : AdvancedTutorialDialogStep
    object ExplainSelectAttack : AdvancedTutorialDialogStep
    object ExplainExecuteAttack : AdvancedTutorialDialogStep
    object ExplainExecuteCpuAttack: AdvancedTutorialDialogStep
    object LetPlayerPlay: AdvancedTutorialDialogStep
    object FinishTutorial: AdvancedTutorialDialogStep
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
    val battleText: MutableStateFlow<String> get() = _battleText
    var advancedTutorialStep by mutableStateOf<AdvancedTutorialStep>(AdvancedTutorialStep.Introduction)
        private set

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

            val playerAttack1 = Attack(1, "Splash", 50, AttackType.DAMAGE_DEALER)
            val playerAttack2 = Attack(2, "Tackle", 60, AttackType.DAMAGE_DEALER)
            val playerAttack3 = Attack(3, "Defence Break", 10, AttackType.DEF_DeBuff)
            val playerAttack4 = Attack(4, "Beak Sharpener", 10, AttackType.ATK_Buff)
            val listOfPlayerAttacks = listOf(playerAttack1, playerAttack2, playerAttack3, playerAttack4)
            val playerTutorialCritter = CritterUsable(20, "Coolduck", 100, 50, 50, 50, listOfPlayerAttacks,1, 1)

            playerCritter.update { state ->
                state.copy(playerCritter = playerTutorialCritter, isLoading = false)
            }

            val cpuAttack1 = Attack(1, "Splash", 50, AttackType.DAMAGE_DEALER)
            val cpuAttack2 = Attack(2, "Tackle", 60, AttackType.DAMAGE_DEALER)
            val cpuAttack3 = Attack(3, "Fire Blast", 80, AttackType.DAMAGE_DEALER)
            val cpuAttack4 = Attack(4, "Thunderbolt", 75, AttackType.DAMAGE_DEALER)
            val listOfCpuAttacks = listOf(cpuAttack1, cpuAttack2, cpuAttack3, cpuAttack4)
            val cpuTutorialCritter = CritterUsable(20, "Coolduck", 100, 50, 50, 50, listOfCpuAttacks,1, 1)

            cpuCritter.update { state ->
                state.copy(cpuCritter = cpuTutorialCritter, isLoading = false)
            }
        }
    }

    fun nextTutorialStep() {
        val currentStep = advancedTutorialStep
        val nextStep = when (currentStep) {
            AdvancedTutorialStep.Introduction -> AdvancedTutorialStep.WinCondition
            AdvancedTutorialStep.WinCondition -> AdvancedTutorialStep.PlayerHP
            AdvancedTutorialStep.PlayerHP -> AdvancedTutorialStep.CpuHP
            AdvancedTutorialStep.CpuHP -> AdvancedTutorialStep.Level
            AdvancedTutorialStep.Level -> AdvancedTutorialStep.Stats
            AdvancedTutorialStep.Stats -> AdvancedTutorialStep.Attacks
            AdvancedTutorialStep.Attacks -> AdvancedTutorialStep.SelectAttack
            AdvancedTutorialStep.SelectAttack -> {
                if (playerInput.value.isPlayerAttackSelected) {
                    AdvancedTutorialStep.ExecuteAttack
                } else {
                    AdvancedTutorialStep.SelectAttack
                }
            }
            AdvancedTutorialStep.ExecuteAttack -> {
                if (!playerInput.value.isPlayerAttackSelected) {
                    AdvancedTutorialStep.ExecuteCpuAttack
                } else {
                    AdvancedTutorialStep.ExecuteAttack
                }
            }
            AdvancedTutorialStep.ExecuteCpuAttack ->
                if (cpuInput.value.isCpuAttackSelected) {
                    AdvancedTutorialStep.ExecuteCpuAttack
                } else {
                    AdvancedTutorialStep.LetPlayerPlay
                }
            AdvancedTutorialStep.LetPlayerPlay ->
                if(checkResult() == BattleResult.NOTOVER){
                    AdvancedTutorialStep.LetPlayerPlay
                } else {
                    AdvancedTutorialStep.FinishTutorial
                }
            AdvancedTutorialStep.FinishTutorial -> AdvancedTutorialStep.FinishTutorial
        }
        advancedTutorialStep = nextStep
        tutorialDialogStep.value = nextStep.associatedDialogStep
    }


    fun getTutorialMessage(step: AdvancedTutorialDialogStep): String {
        return when (step) {
            AdvancedTutorialDialogStep.Welcome -> "Welcome to the Basic Tutorial. Here you are going to learn how to battle " +
                    "against other Critters!"
            AdvancedTutorialDialogStep.ExplainCombat -> "The goal for each Critter is to bring the other Critters Health points (Hp) to Zero, " +
                    "effectively knocking out the poor thing... " +
                    "You and the enemy will exchange attacks until one of you goes down first. Thus deciding the winner! "
            AdvancedTutorialDialogStep.ExplainPlayerHP -> "Now let's talk about HP. This is your health. " +
                    "Based on your level and what kind of critter you are using to battle " +
                    "the hp will be different. You can see your HP at the top of the screen (Green bar) or below in a text format (HP:100)"
            AdvancedTutorialDialogStep.ExplainCpuHP -> "You can see the enemy's HP right below represented by a Red bar or again as text below"
            AdvancedTutorialDialogStep.ExplainLevel -> "Moving on to the LEVEL. It determines the strength of your Critter. " +
                    "As a rule of thumb: Everything from a range of -3 or +3 Levels is usually a fair battle"
            AdvancedTutorialDialogStep.ExplainAttacks -> "Now let's learn about Attacks. These are what damage the Enemy. Each Critter has a maximum of four attacks" +
                    "There are different types of attacks:"
            AdvancedTutorialDialogStep.ExplainStats -> "Let's dive into the Stats of your Critter. Each Critter has its own strengths and weaknesses" +
                    ""
            AdvancedTutorialDialogStep.ExplainSelectAttack -> "Lets select an attack now!"
            AdvancedTutorialDialogStep.ExplainExecuteAttack -> "Now click on the box to execute your attack!"
            AdvancedTutorialDialogStep.ExplainExecuteCpuAttack -> "Now its your enemy's turn, click on the box to " +
                    "execute the enemy's attack"
            AdvancedTutorialDialogStep.LetPlayerPlay -> "Okay, I explained the basics now its your turn to finish the battle, good luck"
            // Add more cases as needed
            AdvancedTutorialDialogStep.FinishTutorial -> "Congrats(or not) you finished the tutorial. You can now move on to the next one"
            else -> {return "null"}
        }
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
        chooseCpuAttack()?.let { selectCpuAttack(it) }
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

    private fun selectCpuAttack(attack: Attack) {
        viewModelScope.launch() {
            cpuInput.update { currentState ->
                currentState.copy(
                    isCpuAttackSelected = true,
                    selectedCpuAttack = attack,
                )
            }
        }
        _battleText.value = "${cpuCritter.value.cpuCritter!!.name} attacks with ${cpuCritter.value.cpuCritter!!.name}!"
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

    private fun applyDebuffToPlayer(attack: Attack){
        if(attack.attackType==AttackType.ATK_DeBuff) {
            viewModelScope.launch() {
                playerCritter.update { currentState ->
                    currentState.copy(
                        playerCritter = currentState.playerCritter?.copy(
                            atk = playerCritter.value.playerCritter!!.atk - attack.strength
                        ),
                    )
                }
                _battleText.value = "${playerCritter.value.playerCritter!!.name}´s Attack fell!"
            }
        }
        if(attack.attackType==AttackType.DEF_DeBuff) {
            viewModelScope.launch() {
                playerCritter.update { currentState ->
                    currentState.copy(
                        playerCritter = currentState.playerCritter?.copy(
                            def = playerCritter.value.playerCritter!!.def - attack.strength
                        ),
                    )
                }
                _battleText.value = "${playerCritter.value.playerCritter!!.name}´s Defence fell!"
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

    private fun applyDebuffToCpu(attack: Attack){
        if(attack.attackType==AttackType.ATK_DeBuff) {
            viewModelScope.launch() {
                cpuCritter.update { currentState ->
                    currentState.copy(
                        cpuCritter = currentState.cpuCritter?.copy(
                            atk = cpuCritter.value.cpuCritter!!.atk - attack.strength
                        ),
                    )
                }
                _battleText.value = "${cpuCritter.value.cpuCritter!!.name}´s Attack fell"
            }
        }
        if(attack.attackType==AttackType.DEF_DeBuff) {
            viewModelScope.launch() {
                cpuCritter.update { currentState ->
                    currentState.copy(
                        cpuCritter = currentState.cpuCritter?.copy(
                            def = cpuCritter.value.cpuCritter!!.def - attack.strength
                        ),
                    )
                }
                _battleText.value = "${cpuCritter.value.cpuCritter!!.name}´s Defence fell"
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