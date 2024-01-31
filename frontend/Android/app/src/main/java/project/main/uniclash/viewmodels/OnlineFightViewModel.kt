package project.main.uniclash.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.retrofit.OnlineFightService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.StudentService
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

sealed interface FightConnectionIdUIState{
    data class HasEntries(
        val fightConnectionId: Int,
        val isLoading: Boolean,
    ): FightConnectionIdUIState
}

class OnlineFightViewModel (onlineFightService : OnlineFightService) : ViewModel() {
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(Application())
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