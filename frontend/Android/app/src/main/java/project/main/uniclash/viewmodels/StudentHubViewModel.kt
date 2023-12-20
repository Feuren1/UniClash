package project.main.uniclash.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.ItemFromItemTemplate
import project.main.uniclash.datatypes.Item
import project.main.uniclash.datatypes.ItemForStudent
import project.main.uniclash.datatypes.ItemPost
import project.main.uniclash.datatypes.ItemTemplate
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.userDataManager.UserDataManager

sealed interface ItemTemplatesUIState {
    data class HasEntries(
        val itemTemplates: List<ItemTemplate>,
        val isLoading: Boolean,
    ) : ItemTemplatesUIState
}

sealed interface PostItemFromStudentUIState {
    data class HasEntries(
        val itemPost: ItemPost?,
        val isLoading: Boolean,
    ) : PostItemFromStudentUIState
}

sealed interface ItemsFromStudentUIState {
    data class HasEntries(
        val itemsFromStudent: List<Item>,
        val isLoading: Boolean,
    ) : ItemsFromStudentUIState
}

sealed interface ItemsFromItemTemplateUIState {
    data class HasEntries(
        val itemsFromItemTemplate: List<ItemFromItemTemplate>,
        val isLoading: Boolean,
    ) : ItemsFromItemTemplateUIState
}

sealed interface ItemForStudentUIState {
    data class HasEntries(
        val itemForStudent: String?,
        val isLoading: Boolean,
    ) : ItemForStudentUIState
}

class StudentHubViewModel(
    private val studentHubService: StudentHubService,
    private val application: Application,
) : ViewModel() {
    private val TAG = StudentHubViewModel::class.java.simpleName

    private val userDataManager : UserDataManager by lazy {
        UserDataManager(application)
    }

    val itemTemplates = MutableStateFlow(
        ItemTemplatesUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    val itemFromStudent = MutableStateFlow(
        PostItemFromStudentUIState.HasEntries(
            isLoading = false,
            itemPost = null
        )
    )

    val itemsFromStudent = MutableStateFlow(
        ItemsFromStudentUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    val student = MutableStateFlow(
        StudentUIState.HasEntries(
            student = null,
            isLoading = false
        )
    )

    val itemsFromItemTemplate = MutableStateFlow(
        ItemsFromItemTemplateUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    val itemForStudent = MutableStateFlow(
        ItemForStudentUIState.HasEntries(
            itemForStudent = null,
            isLoading = false
        )
    )

//    init {
//        viewModelScope.launch {
//            Log.d(TAG, "Fetching student: ")
//            loadStudent() //TODO lässt tenplates immer laden auf wenn z.b. map ausfeührt wird.
//        }
//    }

    //loads the current student per userDataManager studentID
    fun loadStudent() {
        viewModelScope.launch {
            student.update { it.copy(isLoading = true) }
            try {
                println("studentID: ${userDataManager.getStudentId()}")
                val response = studentHubService.getStudent(userDataManager.getStudentId()!!).enqueue()
                Log.d(TAG, "LoadStudent: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body().let {
                        student.update { state ->
                            state.copy(student = it, isLoading = false)
                        }

                    }
                }
            } catch (e: Exception) {
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

                    val itemTemplates = response.body()!!
                    Log.d(TAG, "loadItemTemplates: $itemTemplates")

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

    fun buyItem(itemTemplateId: Int) {

        viewModelScope.launch {
            itemForStudent.update { it.copy(isLoading = true) }

            val response = studentHubService.buyItem(userDataManager.getStudentId()!!, itemTemplateId).enqueue()
            Log.d(TAG, "loadBuyItem: $response")

            if (response.isSuccessful) {

                Log.d(TAG, "Success: ${response.body()}")


            } else {
                Log.d(TAG, "buyItem Failed")
            }
        }
    }

    companion object {
        fun provideFactory(
            studentHubService: StudentHubService,
            application: Application,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StudentHubViewModel(
                        studentHubService,
                        application,
                    ) as T
                }
            }
    }

}