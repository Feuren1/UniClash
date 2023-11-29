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

sealed interface PostItemForStudentUIState {
    data class HasEntries(
        val itemForStudent: ItemForStudent?,
        val isLoading: Boolean,
    ) : PostItemForStudentUIState
}

sealed interface ItemsForStudentUIState {
    data class HasEntries(
        val itemsForStudent: List<ItemForStudent>,
        val isLoading: Boolean,
    ) : ItemsForStudentUIState
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
        PostItemForStudentUIState.HasEntries(
            isLoading = false,
            itemForStudent = null
        )
    )

    val itemsForStudent = MutableStateFlow(
        ItemsForStudentUIState.HasEntries(
            emptyList(),
            isLoading = false
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

    fun loadItemsForStudent() {
        viewModelScope.launch {
            itemsForStudent.update { it.copy(isLoading = true) }
            try {
                val response = studentHubService.getItemsFromStudent(3).enqueue()
                Log.d(TAG, "loadItemsForStudent: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadItemsForStudent: success")
                    //creates an item list based on the fetched data
                    val itemsForStudent = response.body()!!
                    Log.d(TAG, "loadItemsForStudent: $itemsForStudent")
                    //replaces the critters list inside the UI state with the fetched data
                    this@StudentHubViewModel.itemsForStudent.update {
                        it.copy(
                            itemsForStudent = itemsForStudent,
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


    fun buyItem(itemTemplateId: Int) {

        println("Buy button was pressed in the ViewModel")

//        studentHubService.postStudentItem(studentId ,ItemForStudent(quantity = quantity, itemTemplateId = itemTemplateId, studentId = studentId))

        val boolean = false

        if(boolean == true /*StudentItem ItemTemplateId doesn't exist = POST a new one*/) {

            viewModelScope.launch {
                itemForStudent.update { it.copy(isLoading = true) }
                try {
                    var itemForStudent = ItemForStudent(1,itemTemplateId, 2)
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
        } else {

            //TODO: !!!Before doing all this work,
            // I should first try whether it works, by giving this part of the load function a hardcoded ItemForStudent!!!

            //TODO: I need to get a list of all ItemForStudent, filter it for the itemTemplateId,
            // increase its quantity, and then place it in the response.body,
            // where it replaces old data with the new.

            //TODO: Do the calculations of the quantity and filter in a separate logic file.

            //TODO: ***The problem here is that the value itemForStudent null is***,
            // so it has no values to PATCH into the DB. Or something.
            // I'll need to give this vvv the itemForStudent from the get method. Maybe.
            // As such I need to filter for the correct ItemForStudent to give?

            //TODO: I could also, after creating the logic class for the filter and quantity increase,
            // pass over an ItemForStudent instead of an ItemTemplateId,
            // I then would have to first call the function of the logic class to filter the correct ItemForStudent Data class,
            // per the ItemTemplateId I'll pass over to it.
            // **Possible problem could be that I can't call the function from the composable function?**.

            //TODO: If nothing else works, I could try @PATCH, though it isn'T available yet.

            viewModelScope.launch {
                itemForStudent.update { it.copy(isLoading = true) }
                try {
                    var itemForStudent = ItemForStudent(1,itemTemplateId, 3)
                    val response = studentHubService.updateItemQuantityByTemplateId(3, itemTemplateId, itemForStudent).enqueue()
                    println(itemForStudent)
                    Log.d(TAG, "loadUpdateItemQuantity: $response")
                    if (response.isSuccessful) {
                        Log.d(TAG, "Success: ${response.body()}")
                        response.body()?.let {
                            this@StudentHubViewModel.itemForStudent.update { state ->
                                state.copy(itemForStudent = it, isLoading = false)
                            }
                        }
                        println(itemForStudent)
                    } else {
                        Log.d(TAG, "Failed")
                        println(itemForStudent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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