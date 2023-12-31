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
import project.main.uniclash.datatypes.ArenaLeaderPatch
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.AttackType
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.datatypes.PostArenaBattleUpdate
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.dataManagers.UserDataManager


class FinalBattleViewModel(
    private val critterService: CritterService,
    private var playerTurn: Boolean,
) : ViewModel() {
    val userDataManager: UserDataManager by lazy {
        UserDataManager(Application())
    }
    //TAG for logging
    private val TAG = BattleTutorialViewModel::class.java.simpleName
    private val _battleText = MutableStateFlow("Battle started!")
    val isPlayerTurn = MutableStateFlow<Boolean>(false)
    val playerWon = MutableStateFlow<Boolean?>(null)
    private val cpuAttackOrder = listOf(
        Attack(2, "Super Guard", 25, AttackType.DEF_BUFF),
        Attack(2, "Super Guard", 25, AttackType.DEF_BUFF),
        Attack(4, "Beak Sharpener", 25, AttackType.ATK_BUFF),
        Attack(3, "ShieldBreak", 20, AttackType.DEF_DEBUFF),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(2, "Super Guard", 25, AttackType.DEF_BUFF),
        Attack(3, "ShieldBreak", 20, AttackType.DEF_DEBUFF),
        Attack(4, "Beak Sharpener", 25, AttackType.ATK_BUFF),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(2, "Super Guard", 25, AttackType.DEF_BUFF),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(3, "ShieldBreak", 20, AttackType.DEF_DEBUFF),
        Attack(4, "Beak Sharpener", 25, AttackType.ATK_BUFF),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(2, "Super Guard", 25, AttackType.DEF_BUFF),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(3, "ShieldBreak", 20, AttackType.DEF_DEBUFF),
        Attack(4, "Beak Sharpener", 25, AttackType.ATK_BUFF),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(3, "ShieldBreak", 20, AttackType.DEF_DEBUFF),
        Attack(3, "ShieldBreak", 20, AttackType.DEF_DEBUFF),
        Attack(2, "Super Guard", 25, AttackType.DEF_BUFF),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(2, "Super Guard", 25, AttackType.DEF_BUFF),
        Attack(2, "Super Guard", 25, AttackType.DEF_BUFF),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(3, "ShieldBreak", 20, AttackType.DEF_DEBUFF),
        Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER),
        Attack(2, "Super Guard", 25, AttackType.DEF_BUFF),
        Attack(3, "ShieldBreak", 20, AttackType.DEF_DEBUFF),
        Attack(4, "Beak Sharpener", 25, AttackType.ATK_BUFF),
    )

    private var cpuAttackIndex = 0

    val battleText: MutableStateFlow<String> get() = _battleText
    val arenaId = MutableStateFlow<Int?>(null)

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
            loadPlayerCritter()

            val cpuAttack1 = Attack(1, "Dive Attack", 70, AttackType.DAMAGE_DEALER)
            val cpuAttack2 = Attack(2, "Super Guard", 25, AttackType.DEF_BUFF)
            val cpuAttack3 = Attack(3, "Shield Break", 15, AttackType.DEF_DEBUFF)
            val cpuAttack4 = Attack(4, "Beak Sharpener", 25, AttackType.ATK_BUFF)
            val listOfCpuAttacks = listOf(cpuAttack1, cpuAttack2, cpuAttack3, cpuAttack4)
            val cpuTutorialCritter = CritterUsable(40, "Linuxpenguin", 250, 80, 85, 40, listOfCpuAttacks,1, 1)

            cpuCritter.update { state ->
                state.copy(cpuCritter = cpuTutorialCritter, isLoading = false)
            }
        }
        if(!playerCritter.value.isLoading) {
            doesPlayerStart()
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

    fun checkResult(): BattleResult {
        if(playerCritter.value.playerCritter!!.hp<=0){
            playerWon.value = false
            MapSaver.ARENA.setMarker(ArrayList<MarkerData?>())
            return BattleResult.CPU_WINS;
        }
        if(cpuCritter.value.cpuCritter!!.hp<=0){
            playerWon.value = true
            MapSaver.ARENA.setMarker(ArrayList<MarkerData?>())
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

    @SuppressLint("MissingPermission")
    fun loadPlayerCritter() {
        viewModelScope.launch {
            playerCritter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritterUsable(userDataManager.getFightingCritterID()!!).enqueue()
                Log.d(TAG, "loadCrittersUsable: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadCrittersUsables: success")
                    val crittersUsable = response.body()!!
                    Log.d(TAG, "loadCrittersUsables: $crittersUsable")
                    playerCritter.update {
                        it.copy(
                            playerCritter = crittersUsable,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveArenaId(arenaIdToSave: Int?){
    arenaId.value = arenaIdToSave
    }

    fun updateArenaLeader(){
        viewModelScope.launch {
            try {
                val arenaLeaderPatchRequest = ArenaLeaderPatch(0, userDataManager.getStudentId()!!)
                val response = critterService.patchArenaLeader(arenaId.value!!, arenaLeaderPatchRequest).enqueue()
                Log.d(TAG, "Patching Arena Leader: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Patching Arena Leader: success")
                }
            } catch (e: Exception) {
                Log.d(TAG, "Patching Arena Leader: failed!")
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun loadCpuCritter(cpuCritterId: Int) {
        viewModelScope.launch {
            cpuCritter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritterUsable(cpuCritterId).enqueue()
                Log.d(TAG, "loadCrittersUsable: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadCrittersUsables: success")
                    val crittersUsable = response.body()!!
                    Log.d(TAG, "loadCrittersUsables: $crittersUsable")
                    cpuCritter.update {
                        it.copy(
                            cpuCritter = crittersUsable,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun arenaBattleUpdates(){
        viewModelScope.launch {
            cpuCritter.update { it.copy(isLoading = true) }
            try {

                //TODO dirty fix by elias
                var postArenaBattleUpdate = PostArenaBattleUpdate(userDataManager.getStudentId()!!,playerCritter.value.playerCritter!!.critterId,20)
                val response = critterService.postArenaBattleUpdates(postArenaBattleUpdate).enqueue()
                Log.d(TAG, "postArenaBattleUpdates: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "postArenaBattleUpdates: success")
                    val critterUpdated = response.body()!!
                    Log.d(TAG, "postArenaBattleUpdates: $critterUpdated")
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
                    return FinalBattleViewModel(
                        critterService,
                        false
                    ) as T
                }
            }
    }

}