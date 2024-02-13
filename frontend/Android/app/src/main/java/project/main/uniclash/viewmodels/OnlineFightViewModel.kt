package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.BattleAction
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.datatypes.CritterInFightInformation
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.OnlineFightState
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.OnlineFightService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.type.Effectiveness
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed interface TitleUIState{
    data class HasEntries(
        val title: String,
        val isLoading: Boolean,
    ): TitleUIState
}

sealed interface TimerUIState{
    data class HasEntries(
        val timer: Int,
    ): TimerUIState
}

sealed interface StateUIState{
    data class HasEntries(
        val state: OnlineFightState,
    ): StateUIState
}

sealed interface FightConnectionIdUIState {
    data class HasEntries(
        var fightConnectionId: Int,
        val isLoading: Boolean,
    ) : FightConnectionIdUIState
}

sealed interface CritterInFightUIState {
    data class HasEntries(
        var critterInFightInformation: CritterInFightInformation?,
    ) : CritterInFightUIState
}

sealed interface EnemyCritterInFightUIState {
    data class HasEntries(
        var critterInFightInformation: CritterInFightInformation?,
    ) : EnemyCritterInFightUIState
}

sealed interface CritterUsableFightUIState {
    data class HasEntries(
        var critterUsable: CritterUsable?,
        var loaded : Boolean
    ) : CritterUsableFightUIState
}

sealed interface EnemyCritterUsableFightUIState {
    data class HasEntries(
        var critterUsable: CritterUsable?,
        var loaded : Boolean
    ) : EnemyCritterUsableFightUIState
}

class OnlineFightViewModel (private val onlineFightService: OnlineFightService, private val critterService: CritterService) : ViewModel() {
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(Application()) }

    val fightConnectionID = MutableStateFlow(
        FightConnectionIdUIState.HasEntries(
            fightConnectionId = 0,
            isLoading = false
        )
    )

    val timer = MutableStateFlow(
        TimerUIState.HasEntries(
            timer = 0,
        )
    )

    val state = MutableStateFlow(
        StateUIState.HasEntries(
            state = OnlineFightState.WAITING,
        )
    )

    val critterInFight = MutableStateFlow(
        CritterInFightUIState.HasEntries(
            critterInFightInformation = null,
        )
    )

    val enemyCritterInFight = MutableStateFlow(
        EnemyCritterInFightUIState.HasEntries(
            critterInFightInformation = null,
        )
    )

    val critterUsable = MutableStateFlow(
        CritterUsableFightUIState.HasEntries(
            critterUsable = null,
            loaded = false
        )
    )

    val enemyCritterUsable = MutableStateFlow(
        EnemyCritterUsableFightUIState.HasEntries(
            critterUsable = null,
            loaded = false
        )
    )

    @SuppressLint("MissingPermission")
    fun checkIfFightCanStart() {
        if(state.value.state==OnlineFightState.WAITING || state.value.state == OnlineFightState.PREPERATION) {
            viewModelScope.launch {
                try {
                    val response =
                        onlineFightService.checkIfFightCanStart(fightConnectionID.value.fightConnectionId)
                            .enqueue()
                    Log.d(TAG, "loadCheckIfFightCanStart: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "loadCheckIfFightCanStart: success")
                        val checkIfFightCanStart = response.body()!!
                        Log.d(TAG, "loadCheckIfFightCanStart: $checkIfFightCanStart")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun checkMyState() {
        viewModelScope.launch {
            try {
                val response =
                    onlineFightService.checkMyState(fightConnectionID.value.fightConnectionId, userDataManager.getStudentId()!!).enqueue()
                Log.d(TAG, "loadState: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadState: success")
                    val stateRes = response.body()!!
                    Log.d(TAG, "loadState: $stateRes")

                    var currentState = OnlineFightState.WAITING
                    if(stateRes.state == "yourTurn") currentState = OnlineFightState.YOURTURN
                    if(stateRes.state == "enemyTurn") currentState = OnlineFightState.ENEMYTURN
                    if(stateRes.state == "waiting") currentState = OnlineFightState.WAITING
                    if(stateRes.state == "preparation") currentState = OnlineFightState.PREPERATION
                    if(stateRes.state == "winner") currentState = OnlineFightState.WINNER
                    if(stateRes.state == "loser") currentState = OnlineFightState.LOSER
                    if(stateRes.state == "404") currentState = OnlineFightState.NOTFOUND

                    if(stateRes.state == "yourTurn" && timer.value.timer < 1 || stateRes.state == "enemyTurn" && timer.value.timer < 1){
                        timer.update {
                            it.copy(
                                timer = 27
                            )
                        }
                    } else if(stateRes.state != "yourTurn" && stateRes.state != "enemyTurn") {
                        timer.update {
                            it.copy(
                                timer = 0
                            )
                        }
                    }

                    state.update {
                        it.copy(
                            state = currentState,
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCritterInformation() {
        if(state.value.state != OnlineFightState.WAITING) {
        viewModelScope.launch {
            try {
                val response =
                    onlineFightService.getCritterInformation(userDataManager.getFightingCritterID()!!,fightConnectionID.value.fightConnectionId)
                        .enqueue()
                Log.d(TAG, "loadCritter: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadCritter: success")
                    val critter = response.body()!!
                    Log.d(TAG, "loadCritter: $critter")

                    if (critter != null) {
                        critterInFight.update {
                            it.copy(
                                critterInFightInformation = critter,
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        }
    }

    @SuppressLint("MissingPermission")
    fun getEnemyCritterInformation() {
        if(state.value.state != OnlineFightState.WAITING) {
            viewModelScope.launch {
                try {
                    val response =
                        onlineFightService.getCritterInformationFromEnemy(fightConnectionID.value.fightConnectionId, userDataManager.getStudentId()!!)
                            .enqueue()
                    Log.d(TAG, "loadCritter: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "loadCritter: success")
                        val critter = response.body()!!
                        Log.d(TAG, "loadCritter: $critter")

                        if (critter != null) {
                            enemyCritterInFight.update {
                                it.copy(
                                    critterInFightInformation = critter,
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCritterUsable() {
        if(!critterUsable.value.loaded && critterInFight.value.critterInFightInformation != null){
            viewModelScope.launch {
                try {
                    val response =
                        critterService.getCritterUsable(critterInFight.value.critterInFightInformation!!.critterId)
                            .enqueue()
                    Log.d(TAG, "loadCritter: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "loadCritter: success")
                        val critter = response.body()!!
                        Log.d(TAG, "loadCritter: $critter")

                        if (critter != null) {
                            critterUsable.update {
                                it.copy(
                                    critterUsable = critter,
                                    loaded = true
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getEnemyCritterUsable() {
        if(!enemyCritterUsable.value.loaded && enemyCritterInFight.value.critterInFightInformation != null){
            viewModelScope.launch {
                try {
                    val response =
                        critterService.getCritterUsable(enemyCritterInFight.value.critterInFightInformation!!.critterId)
                            .enqueue()
                    Log.d(TAG, "loadCritter: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "loadCritter: success")
                        val critter = response.body()!!
                        Log.d(TAG, "loadCritter: $critter")

                        if (critter != null) {
                            enemyCritterUsable.update {
                                it.copy(
                                    critterUsable = critter,
                                    loaded = true
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    fun makingDamage(amountOfDamage : Int, kindOfDamage : BattleAction, effectiveness : Effectiveness) {
        var effectValue = 1.0
        if(effectiveness == Effectiveness.WEAK) effectValue = 0.75
        if(effectiveness == Effectiveness.EFFECTIVE) effectValue = 1.25

            viewModelScope.launch {
                try {
                    val response =
                        onlineFightService.makingDamage(fightConnectionID.value.fightConnectionId,userDataManager.getStudentId()!!,amountOfDamage, kindOfDamage.toString().uppercase() , effectValue)
                            .enqueue()
                    Log.d(TAG, "loadMakingDamage: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "loadMakingDamage: success")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun sendMessageViaPushNotification(message : String) {
        viewModelScope.launch {
            try {
                val response =
                    onlineFightService.sendMessageViaPushNotification(fightConnectionID.value.fightConnectionId,userDataManager.getStudentId()!!,message)
                        .enqueue()
                Log.d(TAG, "loadSendMessageViaPushNotification: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadSendMessageViaPushNotification: success")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        fun provideFactory(
            onlineFightService: OnlineFightService,
            critterService: CritterService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OnlineFightViewModel(
                        onlineFightService,
                        critterService
                    ) as T
                }
            }
    }
}