package project.main.uniclash.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.Item
import project.main.uniclash.datatypes.StudentHub
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.enqueue

public data class StudentHubIdCallback(val success: Boolean, val id: String)

sealed interface StudentHubUIState {
    data class HasEntries(
        val studentHub: StudentHub?,
        val isLoading: Boolean,
    ) : StudentHubUIState
}

sealed interface StudentHubsUIState {
    data class HasEntries(
        val studentHubs: List<StudentHub>,
        val isLoading: Boolean,
    ) : StudentHubsUIState
}

sealed interface ItemUIState {
    data class HasEntries(
        val items: List<Item>,
        val isLoading: Boolean,
    ) : ItemUIState
}

class StudentHubViewModel(
    private val studentHubService: StudentHubService,
) : ViewModel() {
    private val TAG = StudentHubViewModel::class.java.simpleName

    val studentHub = MutableStateFlow(
        StudentHubUIState.HasEntries(
            isLoading = false,
            studentHub = null
        )
    )

    val studentHubs = MutableStateFlow(
        StudentHubsUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    val items = MutableStateFlow(
        ItemUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial studentHub data: ")
            loadItems()
        }
    }

    //loads one studentHub with the ID
    fun loadStudentHub(id: Int) {
        viewModelScope.launch {
            studentHub.update { it.copy(isLoading = true) }
            try {
                val response = studentHubService.getStudentHub(id).enqueue()
                if (response.isSuccessful) {
                    println(response.body())
                    Log.d(TAG, "loadStudentHub: $response")
                    response.body()?.let {
                        studentHub.update { state ->
                            state.copy(studentHub = it, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //loads all StudentHubs inside the database
    fun loadStudentHubs() {
        viewModelScope.launch {
            studentHubs.update { it.copy(isLoading = true) }
            try {
                val response = studentHubService.getStudentHubs().enqueue()
                Log.d(TAG, "loadStudentHubs: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadStudentHubs: success")
                    //creates an item list based on the fetched data
                    val studentHubs = response.body()!!
                    Log.d(TAG, "loadStudentHubs: $studentHubs")
                    //replaces the critters list inside the UI state with the fetched data
                    this@StudentHubViewModel.studentHubs.update {
                        it.copy(
                            studentHubs = studentHubs,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "items: error")
                e.printStackTrace()
            }
        }
    }

    //loads all itemTemplates inside the database
    fun loadItems() {
        viewModelScope.launch {
            items.update { it.copy(isLoading = true) }
            try {
                val response = studentHubService.getItems().enqueue()
                Log.d(TAG, "loadItems: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadItems: success")
                    //creates an item list based on the fetched data
                    val items = response.body()!!
                    Log.d(TAG, "loadItems: $items")
                    //replaces the critters list inside the UI state with the fetched data
                    this@StudentHubViewModel.items.update {
                        it.copy(
                            items = items,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "items: error")
                e.printStackTrace()
            }
        }
    }

    fun buyItem() {

        //TODO: buying stuff logic
    }

    companion object {
        fun provideFactory(
            studentHubService: StudentHubService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StudentHubViewModel(
                        studentHubService
                    ) as T
                }
            }
    }

}