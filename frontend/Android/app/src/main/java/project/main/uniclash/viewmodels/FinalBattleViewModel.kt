package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import project.main.uniclash.type.Effectiveness
import project.main.uniclash.type.TypeCalculation


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
            Log.d(TAG, "Loading Player Critter ")
            loadPlayerCritter()
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
                applyDamageToPCpu(playerInput.value.selectedPlayerAttack!!)
            }
                playerInput.update { currentState ->
                    currentState.copy(
                        isPlayerAttackSelected = false,
                        selectedPlayerAttack = null,
                    )
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
                applyDamageToPlayer(cpuInput.value.selectedCpuAttack!!)
            }
            // Reset the state for the next turn
                cpuInput.update { currentState ->
                    currentState.copy(
                        isCpuAttackSelected = false,
                        selectedCpuAttack = null,
                    )
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
        cpuCritter?.attacks?.random()
        val selectedAttack = cpuCritter?.attacks?.random()
                cpuInput.update { currentState ->
                    currentState.copy(
                        isCpuAttackSelected = true,
                        selectedCpuAttack = selectedAttack,
                    )
                }

                _battleText.value = "${cpuCritter!!.name} attacks with ${selectedAttack!!.name}!"
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
        var typeCalculation = TypeCalculation()
        if (attack.attackType == AttackType.ATK_BUFF) {
            viewModelScope.launch() {
                val newAtk = (playerCritter.value.playerCritter!!.atk + calculateCpuDamage(attack.strength,typeCalculation.howEffective(attack.typeId,cpuCritter.value.cpuCritter!!.type, attack.typeId == playerCritter!!.value.playerCritter!!.type))).coerceAtMost(500)
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
                val newDef = (playerCritter.value.playerCritter!!.def + calculateCpuDamage(attack.strength,typeCalculation.howEffective(attack.typeId,cpuCritter.value.cpuCritter!!.type,attack.typeId == playerCritter!!.value.playerCritter!!.type))).coerceAtMost(500)
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
        var typeCalculation = TypeCalculation()
        var newAtk = 0
        var newDef = 0
            playerCritter.update { currentState ->
                val currentAtk = currentState.playerCritter?.atk ?: 0
                val currentDef = currentState.playerCritter?.def ?: 0

                newAtk = if (attack.attackType == AttackType.ATK_DEBUFF) {
                    (currentAtk - calculatePlayerDamage(attack.strength,typeCalculation.howEffective(attack.typeId,playerCritter.value.playerCritter!!.type,attack.typeId == cpuCritter!!.value.cpuCritter!!.type))).coerceAtLeast(20)
                } else currentAtk
                newDef = if (attack.attackType == AttackType.DEF_DEBUFF) {
                    (currentDef - calculatePlayerDamage(attack.strength,typeCalculation.howEffective(attack.typeId,playerCritter.value.playerCritter!!.type,attack.typeId == cpuCritter!!.value.cpuCritter!!.type))).coerceAtLeast(20)
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

    private fun applyDebuffToCpu(attack: Attack) {
        var typeCalculation = TypeCalculation()
        var newAtk = 0
        var newDef = 0
            cpuCritter.update { currentState ->
                val currentAtk = currentState.cpuCritter?.atk ?: 0
                val currentDef = currentState.cpuCritter?.def ?: 0

                newAtk = if (attack.attackType == AttackType.ATK_DEBUFF) {
                    (currentAtk - calculateCpuDamage(attack.strength,typeCalculation.howEffective(attack.typeId,cpuCritter.value.cpuCritter!!.type,attack.typeId == playerCritter!!.value.playerCritter!!.type))).coerceAtLeast(20)
                } else currentAtk

                newDef = if (attack.attackType == AttackType.DEF_DEBUFF) {
                    (currentDef - calculateCpuDamage(attack.strength,typeCalculation.howEffective(attack.typeId,cpuCritter.value.cpuCritter!!.type,attack.typeId == playerCritter!!.value.playerCritter!!.type))).coerceAtLeast(20)
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

    private fun applyBuffToCpu(attack: Attack) {
        var typeCalculation = TypeCalculation()
        if (attack.attackType == AttackType.ATK_BUFF) {
                val newAtk = (cpuCritter.value.cpuCritter!!.atk + calculatePlayerDamage(attack.strength,typeCalculation.howEffective(attack.typeId,playerCritter.value.playerCritter!!.type,attack.typeId == cpuCritter!!.value.cpuCritter!!.type))).coerceAtMost(500)
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
        if (attack.attackType == AttackType.DEF_BUFF) {
                val newDef = (cpuCritter.value.cpuCritter!!.def + calculatePlayerDamage(attack.strength,typeCalculation.howEffective(attack.typeId,cpuCritter.value.cpuCritter!!.type,attack.typeId == cpuCritter!!.value.cpuCritter!!.type))).coerceAtMost(500)
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

    private fun applyDamageToPlayer(attack: Attack) {
        var typeCalculation = TypeCalculation()
        var damageAfterCalculation = calculatePlayerDamage(attack.strength,typeCalculation.howEffective(attack.typeId,playerCritter.value.playerCritter!!.type,attack.typeId == cpuCritter!!.value.cpuCritter!!.type))
            playerCritter.update { currentState ->
                currentState.copy(
                    playerCritter = currentState.playerCritter?.copy(
                        hp = (currentState.playerCritter.hp) - damageAfterCalculation
                    ),
                )
            }

        val result = checkResult()
        _battleText.value = when (result) {
            BattleResult.PLAYER_WINS -> "Player wins!"
            BattleResult.CPU_WINS -> "CPU wins!"
            else -> "${playerCritter.value.playerCritter!!.name} took $damageAfterCalculation damage!"
        }
    }
    private fun applyDamageToPCpu(attack: Attack) {
        var typeCalculation = TypeCalculation()
        var damageAfterCalculation = calculateCpuDamage(attack.strength,typeCalculation.howEffective(attack.typeId,cpuCritter.value.cpuCritter!!.type,attack.typeId == playerCritter!!.value.playerCritter!!.type))
            cpuCritter.update { currentState ->
                currentState.copy(
                    cpuCritter = currentState.cpuCritter?.copy(
                        hp = (currentState.cpuCritter.hp) - damageAfterCalculation
                    ),
                )
            }
        val result = checkResult()
        _battleText.value = when (result) {
            BattleResult.PLAYER_WINS -> "Player wins!"
            BattleResult.CPU_WINS -> "CPU wins!"
            else -> "${cpuCritter.value.cpuCritter!!.name} took $damageAfterCalculation damage!"
        }
    }

    private fun calculatePlayerDamage(attack: Int,strength: Effectiveness): Int {
        var effec = 1.0
        if(strength == Effectiveness.NORMALSAMETYPE) effec = 1.1
        if(strength == Effectiveness.WEAK) effec = 0.75
        if(strength == Effectiveness.WEAKSAMETYP) effec = 0.85
        if(strength == Effectiveness.EFFECTIVE) effec = 1.25
        if(strength == Effectiveness.EFFECTIVESAMETYPE) effec = 1.35

        println("$effec effec from cpu")

        val def = playerCritter.value.playerCritter!!.def;
        val atk = cpuCritter.value.cpuCritter!!.atk;
        val level = cpuCritter.value.cpuCritter!!.level;
        var damage = ((((((2*level)/5)+2)*attack*atk/def)/50)+2)*effec
        return damage.toInt()
    }

    private fun calculateCpuDamage(attack: Int, strength: Effectiveness): Int {
        var effec = 1.0
        if(strength == Effectiveness.NORMALSAMETYPE) effec = 1.1
        if(strength == Effectiveness.WEAK) effec = 0.75
        if(strength == Effectiveness.WEAKSAMETYP) effec = 0.85
        if(strength == Effectiveness.EFFECTIVE) effec = 1.25
        if(strength == Effectiveness.EFFECTIVESAMETYPE) effec = 1.35

        println("$effec effec")

        val def = cpuCritter.value.cpuCritter!!.def;
        val atk = playerCritter.value.playerCritter!!.atk;
        val level = playerCritter.value.playerCritter!!.level;
        //println("Damage ${((((((2*level)/5)+2)*attack*atk/def)/50)+2)*effec}")
        var damage = ((((((2*level)/5)+2)*attack*atk/def)/50)+2)*effec
        return damage.toInt()
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
            println("ausgeführt1")
            try {
                val arenaLeaderPatchRequest = ArenaLeaderPatch(0, userDataManager.getStudentId()!!)
                println("ausgeführt2")
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
                    cpuCritter.update { it.copy(isLoading = false) }
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