package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.retrofit.ArenaCritterService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.userDataManager.UserDataManager

class AddCritterToArenaViewModel(
    private val arenaCritterService: ArenaCritterService
) : ViewModel() {
    private val TAG = UniClashViewModel::class.java.simpleName
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(Application())
    }

    val critterUsables = MutableStateFlow(
        CritterUsablesUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )
    init {
        loadCritterUsables()
    }
    @SuppressLint("MissingPermission")
    fun loadCritterUsables() {
        viewModelScope.launch {
            critterUsables.update { it.copy(isLoading = true) }
            try {
                val response = arenaCritterService.getCritterUsables(userDataManager.getStudentId()!!).enqueue()
                Log.d(TAG, "loadCrittersUsable: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadCrittersUsables: success")
                    val crittersUsables = response.body()!!
                    Log.d(TAG, "loadCrittersUsables: $crittersUsables")
                    critterUsables.update {
                        it.copy(
                            critterUsables = crittersUsables,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
fun patchArena(){

}
    companion object {
        fun provideFactory(
            arenaCritterService: ArenaCritterService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddCritterToArenaViewModel(
                        arenaCritterService,
                    ) as T
                }
            }
    }

}