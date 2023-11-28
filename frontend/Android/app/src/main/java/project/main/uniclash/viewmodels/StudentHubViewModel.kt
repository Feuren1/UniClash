package project.main.uniclash.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.ItemForStudent
import project.main.uniclash.datatypes.ItemTemplate
import project.main.uniclash.datatypes.StudentHub
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.enqueue


public data class BuyItemCallback(val success: Boolean, val item: String)

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

sealed interface ItemTemplatesUIState {
    data class HasEntries(
        val itemTemplates: List<ItemTemplate>,
        val isLoading: Boolean,
    ) : ItemTemplatesUIState
}

sealed interface ItemForStudentUIState {
    data class HasEntries(
        val itemForStudent: ItemForStudent?,
        val isLoading: Boolean,
    ) : ItemForStudentUIState
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

    val itemTemplates = MutableStateFlow(
        ItemTemplatesUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    val itemForStudent = MutableStateFlow(
        ItemForStudentUIState.HasEntries(
            isLoading = false,
            itemForStudent = null
        )
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial studentHub data: ")
            loadItemTemplates()
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
    fun loadItemTemplates() {
        viewModelScope.launch {
            itemTemplates.update { it.copy(isLoading = true) }
            try {
                val response = studentHubService.getItemTemplates().enqueue()
                Log.d(TAG, "loadItemTemplates: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadItemTemplates: success")
                    //creates an item list based on the fetched data
                    val itemTemplates = response.body()!!
                    Log.d(TAG, "loadItemTemplates: $itemTemplates")
                    //replaces the critters list inside the UI state with the fetched data
                    this@StudentHubViewModel.itemTemplates.update {
                        it.copy(
                            itemTemplates = itemTemplates,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "itemTemplate: error")
                e.printStackTrace()
            }
        }
    }


    fun buyItem(quantity: Int, itemTemplateId: Int) {

        println("Buy button was pressed in the ViewModel")

//        studentHubService.postStudentItem(studentId ,ItemForStudent(quantity = quantity, itemTemplateId = itemTemplateId, studentId = studentId))

        viewModelScope.launch {
            itemForStudent.update { it.copy(isLoading = true) }
            try {
                var itemForStudent = ItemForStudent(quantity,itemTemplateId, 2)
                val response = studentHubService.postStudentItem(2, itemForStudent).enqueue()
                println(itemForStudent)
                Log.d(TAG, "loadBuyItem: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body()?.let {
                        this@StudentHubViewModel.itemForStudent.update { state ->
                            state.copy(itemForStudent = it, isLoading = false)
                        }
                    }
                } else {
                    Log.d(TAG, "Failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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