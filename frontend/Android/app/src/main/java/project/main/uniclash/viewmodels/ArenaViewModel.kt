package project.main.uniclash.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.enqueue


sealed interface ArenasUIState{
    data class HasEntries(
        val arenas: List<Arena?>,
        val isLoading: Boolean,
    ): ArenasUIState
}


sealed interface ArenaUIState {
    data class HasEntries(
        val arena: Arena?,
        val isLoading: Boolean,
    ): ArenaUIState
}

class ArenaViewModel(
    private val arenaService: ArenaService
) : ViewModel(){

    private val TAG = ArenaViewModel::class.java.simpleName


    val arena = MutableStateFlow(
        ArenaUIState.HasEntries(
            arena = null,
            isLoading = false,
        )
    )

    val arenas = MutableStateFlow(
        ArenasUIState.HasEntries(
            arenas = emptyList(),
            isLoading = false,
        )
    )

    init {
        loadArena(5)
    loadArenas()

    }
    fun loadArenas(){
        viewModelScope.launch {
            arenas.update {it.copy(isLoading = true)  }
            try {
                val response = arenaService.getArenas().enqueue()
                Log.d(TAG, "LoadAllArenas: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    val arenas = response.body()!!
                    this@ArenaViewModel.arenas.update {
                        it.copy(
                            arenas = arenas,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun loadArena(id : Int){
        viewModelScope.launch {
            arena.update {it.copy(isLoading = true)  }
            try {
                val response = arenaService.getArenas(id).enqueue()
                Log.d(TAG, "LoadArena: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body().let {
                        arena.update { state ->
                            state.copy(arena = it, isLoading = false)
                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    companion object {
        fun provideFactory(
            arenaService: ArenaService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ArenaViewModel(
                        arenaService,
                    ) as T
                }
            }
    }
}