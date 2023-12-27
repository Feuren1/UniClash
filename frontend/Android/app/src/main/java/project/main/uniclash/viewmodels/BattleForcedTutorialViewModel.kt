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
sealed class ForcedTutorialStep(val associatedDialogStep: ForcedTutorialDialogStep) {
    object Introduction : ForcedTutorialStep(ForcedTutorialDialogStep.Welcome)
    object WinCondition : ForcedTutorialStep(ForcedTutorialDialogStep.ExplainCombat)
    object PlayerHP : ForcedTutorialStep(ForcedTutorialDialogStep.ExplainPlayerHP)
    object CpuHP: ForcedTutorialStep(ForcedTutorialDialogStep.ExplainCpuHP)
    object Level : ForcedTutorialStep(ForcedTutorialDialogStep.ExplainLevel)
    object Attacks : ForcedTutorialStep(ForcedTutorialDialogStep.ExplainAttacks)
    object Stats : ForcedTutorialStep(ForcedTutorialDialogStep.ExplainStats)
    object battleLog: ForcedTutorialStep(ForcedTutorialDialogStep.ExplainBattleLog)
    object SelectAttack : ForcedTutorialStep(ForcedTutorialDialogStep.ExplainSelectAttack)
    object ExecuteAttack : ForcedTutorialStep(ForcedTutorialDialogStep.ExplainExecuteAttack)
    object ExecuteCpuAttack: ForcedTutorialStep(ForcedTutorialDialogStep.ExplainExecuteCpuAttack)
    object LetPlayerPlay: ForcedTutorialStep(ForcedTutorialDialogStep.LetPlayerPlay)
    object FinishTutorial: ForcedTutorialStep(ForcedTutorialDialogStep.FinishTutorial)
}
sealed interface ForcedTutorialDialogStep {
    object Welcome : ForcedTutorialDialogStep
    object ExplainCombat : ForcedTutorialDialogStep
    object ExplainPlayerHP : ForcedTutorialDialogStep
    object ExplainCpuHP : ForcedTutorialDialogStep
    object ExplainLevel : ForcedTutorialDialogStep
    object ExplainAttacks : ForcedTutorialDialogStep
    object ExplainStats : ForcedTutorialDialogStep
    object ExplainBattleLog : ForcedTutorialDialogStep
    object ExplainSelectAttack : ForcedTutorialDialogStep
    object ExplainExecuteAttack : ForcedTutorialDialogStep
    object ExplainExecuteCpuAttack: ForcedTutorialDialogStep
    object LetPlayerPlay: ForcedTutorialDialogStep
    object FinishTutorial: ForcedTutorialDialogStep
    // Add more steps as needed
}
class BattleForcedTutorialViewModel(
    private val critterService: CritterService,
    private var playerTurn: Boolean,
) : ViewModel() {
    //TAG for logging
    private val TAG = BattleTutorialViewModel::class.java.simpleName
    private val _battleText = MutableStateFlow("Battle started!")
    val forcedTutorialDialogStep = MutableStateFlow<ForcedTutorialDialogStep>(ForcedTutorialDialogStep.Welcome)
    val battleText: MutableStateFlow<String> get() = _battleText
    val isPlayerTurn = MutableStateFlow<Boolean>(false)
    val playerWon = MutableStateFlow<Boolean?>(null)
    var forcedTutorialStep by mutableStateOf<ForcedTutorialStep>(ForcedTutorialStep.Introduction)
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
            val playerAttack3 = Attack(3, "Beak attack", 80, AttackType.DAMAGE_DEALER)
            val playerAttack4 = Attack(4, "Duck Noises", 75, AttackType.DAMAGE_DEALER)
            val listOfPlayerAttacks = listOf(playerAttack1, playerAttack2, playerAttack3, playerAttack4)
            val playerTutorialCritter = CritterUsable(20, "Coolduck", 100, 50, 50, 50, listOfPlayerAttacks,1, 1)

            playerCritter.update { state ->
                state.copy(playerCritter = playerTutorialCritter, isLoading = false)
            }

            val cpuAttack1 = Attack(1, "High Pitched Scream", 50, AttackType.DAMAGE_DEALER)
            val cpuAttack2 = Attack(2, "Tackle", 60, AttackType.DAMAGE_DEALER)
            val cpuAttack3 = Attack(3, "Splash", 80, AttackType.DAMAGE_DEALER)
            val cpuAttack4 = Attack(4, "Knife Attack", 75, AttackType.DAMAGE_DEALER)
            val listOfCpuAttacks = listOf(cpuAttack1, cpuAttack2, cpuAttack3, cpuAttack4)
            val cpuTutorialCritter = CritterUsable(20, "Knifeduck", 100, 50, 50, 50, listOfPlayerAttacks,1, 1)

            cpuCritter.update { state ->
                state.copy(cpuCritter = cpuTutorialCritter, isLoading = false)
            }
        }
        doesPlayerStart()
    }

    fun nextTutorialStep() {
        val currentStep = forcedTutorialStep
        val nextStep = when (currentStep) {
            ForcedTutorialStep.Introduction -> ForcedTutorialStep.WinCondition
            ForcedTutorialStep.WinCondition -> ForcedTutorialStep.PlayerHP
            ForcedTutorialStep.PlayerHP -> ForcedTutorialStep.CpuHP
            ForcedTutorialStep.CpuHP -> ForcedTutorialStep.Level
            ForcedTutorialStep.Level -> ForcedTutorialStep.Stats
            ForcedTutorialStep.Stats -> ForcedTutorialStep.battleLog
            ForcedTutorialStep.battleLog -> ForcedTutorialStep.Attacks
            ForcedTutorialStep.Attacks -> ForcedTutorialStep.SelectAttack
            ForcedTutorialStep.SelectAttack -> {
                if (playerInput.value.isPlayerAttackSelected) {
                    ForcedTutorialStep.ExecuteAttack
                } else {
                    ForcedTutorialStep.SelectAttack
                }
            }
            ForcedTutorialStep.ExecuteAttack -> {
                if (!playerInput.value.isPlayerAttackSelected) {
                    ForcedTutorialStep.ExecuteCpuAttack
                } else {
                    ForcedTutorialStep.ExecuteAttack
                }
            }
            ForcedTutorialStep.ExecuteCpuAttack ->
                if (cpuInput.value.isCpuAttackSelected) {
                    ForcedTutorialStep.ExecuteCpuAttack
                } else {
                    ForcedTutorialStep.LetPlayerPlay
                }
            ForcedTutorialStep.LetPlayerPlay ->
                if(checkResult() == BattleResult.NOTOVER){
                    ForcedTutorialStep.LetPlayerPlay
                } else {
                    ForcedTutorialStep.FinishTutorial
                }
            ForcedTutorialStep.FinishTutorial -> ForcedTutorialStep.FinishTutorial
        }
        forcedTutorialStep = nextStep
        forcedTutorialDialogStep.value = nextStep.associatedDialogStep
    }


    fun getTutorialMessage(step: ForcedTutorialDialogStep): String {
        return when (step) {
            ForcedTutorialDialogStep.Welcome -> "Welcome to the Basic Tutorial for Uniclash. Here you are going to learn how to battle!\n"+
                    "In Uniclash there are creatures, so called Critters which you can catch around the world and use in Battles."
            ForcedTutorialDialogStep.ExplainCombat -> "The goal in a battle is to bring the Enemy's Critters Health points (Hp) to Zero, " +
                    "effectively knocking out the poor thing...\n" +
                    "You and the enemy will exchange attacks until one of you goes down first. Thus deciding the winner! "
            ForcedTutorialDialogStep.ExplainPlayerHP -> "Now let's talk about HP. This is your health.\n" +
                    "Based on your level and what kind of Critter you are using in a battle, the hp will be different.\n" +
                    "Your Critter is Coolduck! And you can see its HP in the form of a green bar above its name or to the right in a text format (HP:100)"
            ForcedTutorialDialogStep.ExplainCpuHP -> "The opponent Critter the Knifeduck has a red health bar instead. "
            ForcedTutorialDialogStep.ExplainLevel -> "Moving on to the LEVEL (LVL). It determines the strength of your Critter. The higher the level of your Critter the higher are its stats. "
            ForcedTutorialDialogStep.ExplainStats -> "Let's dive into the Stats of your Critter. Each Critter has its own strengths and weaknesses." +
                    " Below the Critters name you can see its stats.\n" +
                    "ATK stands for Attack. The higher the Attack the more damage the Critter will deal.\n" +
                    "DEF stands for Defence. This decides how much damage the Critter will take."
            ForcedTutorialDialogStep.ExplainBattleLog -> "Right above this Tutorial text, you can see the BattleLog this will show what is happening during the battle. Reading the Battlelog is very important!"
            ForcedTutorialDialogStep.ExplainAttacks -> "Now let's learn about Attacks. These are what lower the Enemy's Health-points." +
                    "\nEach Critter has a maximum of four attacks. Next to the name of the Attack you can see the strength. The higher this stat is, the more damage it will deal."
            ForcedTutorialDialogStep.ExplainSelectAttack -> "But enough talking, lets select an attack now!"
            ForcedTutorialDialogStep.ExplainExecuteAttack -> "Now you can see that the BattleLog shows that you have selected an attack. Click on the BattleLog to execute your attack! "
            ForcedTutorialDialogStep.ExplainExecuteCpuAttack -> "Now its your enemy's turn, click on the BattleLog to " +
                    "execute the enemy's attack"
            ForcedTutorialDialogStep.LetPlayerPlay -> "Okay, I explained the basics. Now its your turn to finish the battle, good luck"
            // Add more cases as needed
            ForcedTutorialDialogStep.FinishTutorial -> "Congrats(or not) you finished the tutorial. You can now move on to the next one"
            else -> {return "null"}
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
        isPlayerTurn.value = false
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
            val randomIndex = (0 until it.attacks.size).random()
            val attack = it.attacks[randomIndex]

            viewModelScope.launch() {
                cpuInput.update { currentState ->
                    currentState.copy(
                        isCpuAttackSelected = true,
                        selectedCpuAttack = attack,
                    )
                }
            }
            _battleText.value = "${cpuCritter.name} attacks with ${attack.name}!"
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

    fun applyDamageToPlayer(damage: Int) {
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
    fun applyDamageToPCpu(damage: Int) {
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

    fun calculatePlayerDamage(attack: Int): Int {
        val def = playerCritter.value.playerCritter!!.def;
        val atk = cpuCritter.value.cpuCritter!!.atk;
        val level = cpuCritter.value.cpuCritter!!.level;
        return (((((2*level)/5)+2)*attack*atk/def)/50)+2
    }

    fun calculateCpuDamage(attack: Int): Int {
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
                    return BattleForcedTutorialViewModel(
                        critterService,
                        false
                    ) as T
                }
            }
    }

}