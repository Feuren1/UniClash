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
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.datatypes.OnlineFightState
import project.main.uniclash.retrofit.OnlineFightService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.StudentService
import project.main.uniclash.retrofit.enqueue
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
        val isLoading: Boolean,
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

class OnlineFightViewModel (private val onlineFightService: OnlineFightService) : ViewModel() {
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(Application()) }

    val fightConnectionID = MutableStateFlow(
        FightConnectionIdUIState.HasEntries(
            fightConnectionId = 0,
            isLoading = false
        )
    )

    val state = MutableStateFlow(
        StateUIState.HasEntries(
            state = OnlineFightState.WAITING,
        )
    )

    @SuppressLint("MissingPermission")
    fun checkIfFightCanStart() {
        if(state.value.state==OnlineFightState.WAITING) {
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
            println("step1")
            try {
                println("step2")
                val response =
                    onlineFightService.checkMyState(fightConnectionID.value.fightConnectionId, userDataManager.getStudentId()!!).enqueue()
                Log.d(TAG, "loadState: $response")
                println("step3")
                if (response.isSuccessful) {
                    println("step4")
                    Log.d(TAG, "loadState: success")
                    val stateRes = response.body()!!
                    Log.d(TAG, "loadState: $stateRes")

                    var currentState = OnlineFightState.WAITING
                    if(stateRes.toString() == "yourTurn") currentState = OnlineFightState.YOURTURN
                    if(stateRes.toString() == "enemyTurn") currentState = OnlineFightState.ENEMYTURN
                    if(stateRes.toString() == "waiting") currentState = OnlineFightState.WAITING
                    if(stateRes.toString() == "winner") currentState = OnlineFightState.WINNER
                    if(stateRes.toString() == "loser") currentState = OnlineFightState.LOSER

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

    companion object {
        fun provideFactory(
            onlineFightService: OnlineFightService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OnlineFightViewModel(
                        onlineFightService,
                    ) as T
                }
            }
    }
}