package com.example.mymap.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mymap.datatypes.Critter
import com.example.mymap.retrofit.CritterService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.mymap.retrofit.enqueue

public data class CritterIdCallback(val success: Boolean, val id: String)

sealed interface CrittersUIState {
    data class HasEntries(
        val critters: List<Critter>,
        val isLoading: Boolean,
    ) : CrittersUIState
}

sealed interface CritterUIState {
    data class HasEntries(
        val critter: Critter?,
        val isLoading: Boolean,
    ) : CritterUIState
}


class UniClashViewModel(
    private val critterService: CritterService,
) : ViewModel() {

    private val TAG = UniClashViewModel::class.java.simpleName
    val critters = MutableStateFlow(
        CrittersUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )
    val critter = MutableStateFlow(
        CritterUIState.HasEntries(
            isLoading = false,
            critter = null
        )
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial todo list data: ")
            loadCritters()
        }
    }

    @SuppressLint("MissingPermission")
    fun loadCritters() {
        viewModelScope.launch {
            critters.update { it.copy(isLoading = true) }
            try {
                println("Geht")
                val response = critterService.getCritters().enqueue()
                println("Geht2")
                Log.d(TAG, "loadCritters: $response")
                if (response.isSuccessful) {
                    println("Gehtsuccessfull")
                    Log.d(TAG, "loadCritters: success")
                    val critters = response.body()!!
                    Log.d(TAG, "loadCritters: $critters")
                    this@UniClashViewModel.critters.update {
                        it.copy(
                            critters = critters,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "critters: error")
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
                    return UniClashViewModel(
                        critterService
                    ) as T
                }
            }
    }

    fun loadCritter(id: Int) {
        viewModelScope.launch {
            critter.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritters(id).enqueue()
                if (response.isSuccessful) {
                    println(response.body())
                    response.body()?.let {
                        critter.update { state ->
                            state.copy(critter = it, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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