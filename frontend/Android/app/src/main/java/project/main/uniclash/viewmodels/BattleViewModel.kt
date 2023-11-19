package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import project.main.uniclash.datatypes.Critter
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.CritterService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.retrofit.enqueue

public data class playerCritterIdCallback(val success: Boolean, val id: String)
public data class cpuCritterIdCallback(val success: Boolean, val id: String)

sealed interface playerCritterUIState {
    data class HasEntries(
        val playerCritter: CritterUsable?,
        val isLoading: Boolean,
        var hasTurn: Boolean,
    ) : CritterUsableUIState
}

sealed interface cpuCritterUIState {
    data class HasEntries(
        val cpuCritter: CritterUsable?,
        val isLoading: Boolean,
        var hasTurn: Boolean,
    ) : CritterUsableUIState
}


class BattleViewModel(
    private val critterService: CritterService,
) : ViewModel() {
    //TAG for logging
    private val TAG = UniClashViewModel::class.java.simpleName

    val playerCritter = MutableStateFlow(
        playerCritterUIState.HasEntries(
            playerCritter = null,
            isLoading = true,
            hasTurn = false,
        )
    )

    val cpuCritter = MutableStateFlow(
        cpuCritterUIState.HasEntries(
            cpuCritter = null,
            isLoading = true,
            hasTurn = false,
        )
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial critters data: ")
            loadPlayerCritter(19)
            loadCpuCritter(20)
        }
    }

    fun loadPlayerCritter(id: Int) {
        viewModelScope.launch {
            playerCritter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritterUsable(id).enqueue()
                Log.d(TAG, "LoadPlayerCritter: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body()?.let {
                        playerCritter.update { state ->
                            state.copy(playerCritter = it, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadCpuCritter(id: Int) {
        viewModelScope.launch {
            cpuCritter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritterUsable(id).enqueue()
                Log.d(TAG, "LoadCpuCritter: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body()?.let {
                        cpuCritter.update { state ->
                            state.copy(cpuCritter = it, isLoading = false)
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
            critterService: CritterService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BattleViewModel(
                        critterService
                    ) as T
                }
            }
    }


    /*

        fun createTodoList(
            title: String,
            color: String,
            callback: (TodoListIdCallback) -> Unit = {}
        ) {
            val call = todoListService.createTodoList(
                TodoListCreateRequest(
                    title = title,
                    color = color
                )
            )
            call.enqueue(object : Callback<TodoList> {
                override fun onResponse(
                    call: Call<TodoList>,
                    response: Response<TodoList>
                ) {
                    response.body().let {
                        if (response.code() == 200) {
                            println("Saved successfully")
                            callback(TodoListIdCallback(true, response.body()!!.id.toString()))
                            return
                        } else {
                            println("Saving failed")
                            println("Error: " + response.message())
                        }
                    }
                    callback(TodoListIdCallback(false, ""))
                }

                override fun onFailure(call: Call<TodoList>, t: Throwable) {
                    t.printStackTrace()
                    callback(TodoListIdCallback(false, ""))
                }
            })
        }

        fun createTodo(
            todoListId: String,
            title: String,
            description: String,
            callback: (TodoListIdCallback) -> Unit = {}
        ) {
            val call = todoListService.createTodo(
                todoListId,
                TodoCreateRequest(
                    title = title,
                    desc = description
                )
            )
            call.enqueue(object : Callback<Todo> {
                override fun onResponse(
                    call: Call<Todo>,
                    response: Response<Todo>
                ) {
                    response.body().let {
                        if (response.code() == 200) {
                            println("Saved successfully")
                            callback(TodoListIdCallback(true, todoListId))
                            loadTodoList(todoListId)
                            return
                        } else {
                            println("Saving failed")
                            println("Error: ${response.code()}" + response.message())
                        }
                    }
                    callback(TodoListIdCallback(false, ""))
                }

                override fun onFailure(call: Call<Todo>, t: Throwable) {
                    t.printStackTrace()
                    callback(TodoListIdCallback(false, ""))
                }
            })
        }

        fun patchTodo(
            todoId: String,
            title: String? = null,
            description: String? = null,
            isComplete: Boolean? = null,
            callback: (TodoListIdCallback) -> Unit = {}
        ) {
            val call = todoListService.updateTodo(
                todoId,
                TodoPatchRequest(
                    title = title,
                    desc = description,
                    isComplete = isComplete
                )
            )
            call.enqueue(object : Callback<Todo> {
                override fun onResponse(
                    call: Call<Todo>,
                    response: Response<Todo>
                ) {
                    response.body().let {
                        if (response.code() == 204) {
                            println("Saved successfully")
                            callback(TodoListIdCallback(true, todoId))
                            return
                        } else {
                            println("Saving failed")
                            println("Error: ${response.code()}" + response.message())
                        }
                    }
                    callback(TodoListIdCallback(false, ""))
                }

                override fun onFailure(call: Call<Todo>, t: Throwable) {
                    t.printStackTrace()
                    callback(TodoListIdCallback(false, ""))
                }
            })
        }
    */

}