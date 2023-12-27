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
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.datatypes.ArenaCritterPatch
import project.main.uniclash.datatypes.CritterUsable
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
    val text: MutableStateFlow<String> = MutableStateFlow("")
    var selectedCritter : MutableStateFlow<CritterUsable?> = MutableStateFlow(null)


    val arenasUIState = MutableStateFlow(
        ArenaUIState.HasEntries(
            arena = null,
            isLoading = false
        )
    )



    val critterUsables = MutableStateFlow(
        CritterUsablesUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )
    init {
        loadCritterUsables()
    }


    fun patchArenaCritter(arenaId: Int){
        viewModelScope.launch{
            try{

                var arenaCritterPatchRequest = ArenaCritterPatch(selectedCritter.value!!.critterId, userDataManager.getStudentId()!!)
                val response = arenaCritterService.updateArenaCritter(arenaId,arenaCritterPatchRequest).enqueue()
                Log.d(TAG, "patchArenaCritterID: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "patchArenaCritterID: success")
                    val updatedArena = response.body()!!
                    Log.d(TAG, "patchArenaCritterID: $updatedArena")
                    arenasUIState.update {
                        it.copy(
                            arena = updatedArena,
                            isLoading = false
                        )
                    }
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
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