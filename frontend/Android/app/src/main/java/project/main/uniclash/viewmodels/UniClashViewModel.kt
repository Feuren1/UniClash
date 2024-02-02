package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.app.Application
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
import project.main.uniclash.dataManagers.CritterListDataManager
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.datatypes.CritterListable

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

sealed interface CritterUsableUIState {
    data class HasEntries(
        val critterUsable: CritterUsable?,
        val isLoading: Boolean,
    ) : CritterUsableUIState
}

sealed interface CritterUsablesUIState {
    data class HasEntries(
        val critterUsables: List<CritterUsable?>,
        val isLoading: Boolean,
    ) : CritterUsablesUIState
}

class UniClashViewModel(
    private val critterService: CritterService,
    private val application: Application
) : ViewModel() {
    //TAG for logging
    private val TAG = UniClashViewModel::class.java.simpleName
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(application)
    }
    private val critterListDataManager: CritterListDataManager by lazy {
        CritterListDataManager(application)
    }


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

    val critterUsable = MutableStateFlow(
        CritterUsableUIState.HasEntries(
            critterUsable = null,
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
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial critters data: ")
            loadCritterUsable(19)
        }
    }

    fun loadCritterUsable(id: Int) {
        viewModelScope.launch {
            critterUsable.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritterUsable(id).enqueue()
                Log.d(TAG, "loadCritterUsable: $response")
                if (response.isSuccessful) {
                    response.body()?.let {
                        critterUsable.update { state ->
                            state.copy(critterUsable = it, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun loadCritterUsables() {
        viewModelScope.launch {
            if (critterListDataManager.checkCritterListIsNotEmpty()) {
                critterUsables.update { it.copy(critterUsables = critterListDataManager.getCritterList()) }
            } else {

                critterUsables.update { it.copy(isLoading = true) }
                try {
                    val response =
                        critterService.getCritterUsables(userDataManager.getStudentId()!!).enqueue()
                    Log.d(TAG, "loadCrittersUsable: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "loadCrittersUsables: success")
                        val crittersUsables = response.body()!!
                        Log.d(TAG, "loadCrittersUsables: $crittersUsables")

                        critterListDataManager.storeCritterList(crittersUsables)

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
    }

    @SuppressLint("MissingPermission")
    fun loadCritterListables() {
        viewModelScope.launch {
            if (critterListDataManager.checkCritterListIsNotEmpty()) {
                critterUsables.update { it.copy(critterUsables = critterListDataManager.getCritterList()) }
            } else {

                critterUsables.update { it.copy(isLoading = true) }
                try {
                    val response =
                        critterService.getCritterListables(userDataManager.getStudentId()!!).enqueue()
                    Log.d(TAG, "loadCrittersUsable: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "loadCrittersUsables: success")
                        val crittersListable = response.body()!!
                        Log.d(TAG, "loadCrittersUsables: $crittersListable")

                        val crittersUsables: ArrayList<CritterUsable> = ArrayList()
                        for (critter in crittersListable) {
                            val myCritter = CritterUsable(
                                level = critter.level,
                                name = critter.name,
                                hp = 0,
                                atk = 0,
                                def = 0,
                                spd = 0,
                                attacks = emptyList(),
                                critterId = critter.critterId,
                                critterTemplateId = 0,
                                expToNextLevel = 0
                            )
                            crittersUsables.add(myCritter)
                        }

                        critterListDataManager.storeCritterList(crittersUsables)

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
    }

    fun checkIfSelectedCritter(id: Int) : Boolean{
        var isSelected = false
        viewModelScope.launch {
            if(userDataManager.getFightingCritterID() == id){
                isSelected = true
            }
        }
        return isSelected
    }

    @SuppressLint("MissingPermission")
    //loads all critters inside the database
    fun loadCritters() {
        viewModelScope.launch {
            critters.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritters().enqueue()
                Log.d(TAG, "loadCritters: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadCritters: success")
                    //creates a critters list based on the fetched data
                    val critters = response.body()!!
                    Log.d(TAG, "loadCritters: $critters")
                    //replaces the critters list inside the UI state with the fetched data
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

    companion object {
        fun provideFactory(
            critterService: CritterService,
            application: Application,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UniClashViewModel(
                        critterService,
                        application
                    ) as T
                }
            }
    }

    init {
        loadCritterListables()
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