package com.example.mymap.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mymap.datatypes.Arena
import com.example.mymap.datatypes.Critter
import com.example.mymap.retrofit.ArenaService
import com.example.mymap.retrofit.CritterService
import com.example.mymap.retrofit.enqueue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

public data class ArenaIdCallback(val success: Boolean, val id: String)

sealed interface ArenaUIState {
    data class HasEntries(
        val arena: Arena?,
        val isLoading: Boolean,
    ) : ArenaUIState
}

class ArenaViewModel(
    private val arenaService: ArenaService,
) : ViewModel() {
    private val TAG = ArenaViewModel::class.java.simpleName

    val arena = MutableStateFlow(
        ArenaUIState.HasEntries(
            isLoading = false,
            arena = null
        )
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial arena data: ")
        }
    }
    fun loadArena(id: Int) {
        viewModelScope.launch {
            arena.update { it.copy(isLoading = true) }
            try {
                val response = arenaService.getArena(id).enqueue()
                if (response.isSuccessful) {
                    println(response.body())
                    Log.d(TAG, "loadArena: $response")
                    response.body()?.let {
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
                        arenaService
                    ) as T
                }
            }
    }

}