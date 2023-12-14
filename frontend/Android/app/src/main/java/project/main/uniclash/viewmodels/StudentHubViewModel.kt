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
import project.main.uniclash.datatypes.ItemPatch
import project.main.uniclash.datatypes.ItemPost
import project.main.uniclash.datatypes.ItemTemplate
import project.main.uniclash.datatypes.StudentPatch
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

sealed interface ItemUIState {
    data class HasEntries(
        val itemPatch: ItemPatch?,
        val isLoading: Boolean,
    ) : ItemUIState
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

    val item = MutableStateFlow(
        ItemUIState.HasEntries(
            itemPatch = null,
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

    fun loadItemsFromStudent() {
        viewModelScope.launch {
            itemsFromStudent.update { it.copy(isLoading = true) }
            try {
                val response = studentHubService.getItemsFromStudent(userDataManager.getStudentId()!!).enqueue()
                Log.d(TAG, "loadItemsFromStudent: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadItemsFromStudent: success")

                    val itemsFromStudent = response.body()!!
                    Log.d(TAG, "loadItemsFromStudent: $itemsFromStudent")

                    this@StudentHubViewModel.itemsFromStudent.update {
                        it.copy(
                            itemsFromStudent = itemsFromStudent,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "itemsFromStudent: error")
                e.printStackTrace()
            }
        }
    }

    fun loadItemsFromItemTemplate(studentId: Int) {
        viewModelScope.launch {
            itemsFromItemTemplate.update { it.copy(isLoading = true) }
            try {
                val response = studentHubService.getItemsFromItemTemplate(studentId).enqueue()
                Log.d(TAG, "loadItemsFromItemTemplate: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "loadItemsFromItemTemplate: success")

                    val itemsFromItemTemplate = response.body()!!
                    Log.d(TAG, "loadItemsFromItemTemplate: $itemsFromItemTemplate")

                    this@StudentHubViewModel.itemsFromItemTemplate.update {
                        it.copy(
                            itemsFromItemTemplate = itemsFromItemTemplate,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "itemsFromItemTemplate: error")
                e.printStackTrace()
            }
        }
    }

    fun buyItem(itemTemplateId: Int, itemCost: Int, quantityIncrease: Int): Boolean {


        println("Buy button was pressed in the ViewModel.")

        val currentStudent = student.value.student

        val newCredits = currentStudent!!.credits - itemCost

        println("newCredit value")
        //checking if the student has enough money
        if (newCredits >= itemCost) {

            decreaseStudentCredits(newCredits) //reduces credits of the student

            //See if the Item already exists in the DB
            return if (itemExists(itemTemplateId)) {

                println("BUY ITEM PATCH WAS CALLED")
                //Boolean that student has enough credits (true)
                increaseItemQuantity(getSelectedItemPatch(currentStudent!!.id,itemTemplateId), quantityIncrease)

            } else { //Otherwise uses @POST to increase the quantity

                println("BUY ITEM POST WAS CALLED")
                val itemPostData = getSelectedItemPost(currentStudent!!.id, itemTemplateId)
                //Boolean that student has enough credits (true)
                postItemFromStudent(itemPostData)
            }

        } else {

            println("Student does not have enough credits!")
            return false //return boolean false, that the student does not have enough credits
        }
    }

    private fun postItemFromStudent(item: ItemPost): Boolean {

        //@POST the ItemForStudent to the student DB
        viewModelScope.launch {
            itemFromStudent.update { it.copy(isLoading = true) }

            var itemFromStudent = item
            val response = studentHubService.postStudentItem(item.studentId, itemFromStudent).enqueue()
            println(itemFromStudent)
            Log.d(TAG, "loadBuyItem: $response")
            if (response.isSuccessful) {
                Log.d(TAG, "Success: ${response.body()}")
                response.body()?.let {
                    this@StudentHubViewModel.itemFromStudent.update { state ->
                        state.copy(itemPost = it, isLoading = false)
                    }
                }
            } else {
                Log.d(TAG, "PostItem Failed")
            }
        }

        return true
    }

    private fun increaseItemQuantity(itemPatch: ItemPatch, quantityIncrease: Int): Boolean {

        val newQuantity = itemPatch.quantity + quantityIncrease

        println("loadPatchItemQuantity: patchItemQuantity function called")

        viewModelScope.launch {
            student.update { it.copy(isLoading = true) }

            var updatedItemPatch = ItemPatch(itemPatch.id, newQuantity, itemPatch.itemTemplateId, itemPatch.studentId)

            val response = studentHubService.patchItemsFromItemTemplate(itemPatch.id, updatedItemPatch).enqueue()
            Log.d(TAG, "loadPatchItemQuantity request: $response")

            if (response.isSuccessful) {

                Log.d(TAG, "loadPatchItemQuantity Success: ${response.body()}")
                response.body()?.let {
                    this@StudentHubViewModel.item.update { state ->
                        state.copy(itemPatch = it, isLoading = false)
                    }
                }

            } else {

                Log.d(
                    TAG,
                    "loadPatchItemQuantity Failed: ${response.code()}, ${response.message()}"
                )
            }
        }
        return true
    }

    private fun decreaseStudentCredits(newCredits: Int) {

        println("loadPatchStudentCredit: patchStudentCredits function called")

        viewModelScope.launch {
            student.update { it.copy(isLoading = true) }

//            var updatedStudent1 = StudentPatch(5, 1, 0, 0, newCredits, 0, "f96e0c04-c965-496a-8942-4fb7fcde9c30")
            val currentStudent = student.value.student

            var updatedStudent = StudentPatch(currentStudent!!.id, currentStudent!!.level, currentStudent!!.lat,
                currentStudent!!.lon, newCredits, currentStudent!!.expToNextLevel, currentStudent!!.userId)

            val response = studentHubService.updateStudentCredits(updatedStudent.id, updatedStudent).enqueue()
            Log.d(TAG, "loadPatchStudentCredit request: $response")

            if (response.isSuccessful) {
                Log.d(TAG, "loadPatchStudentCredit Success: ${response.body()}")
                response.body()?.let {
                    this@StudentHubViewModel.student.update { state ->
                        state.copy(student = it, isLoading = false)
                    }
                }

            } else {
                Log.d(
                    TAG,
                    "loadPatchStudentCredit Failed: ${response.code()}, ${response.message()}"
                )
            }
        }
    }

    private fun itemExists(itemTemplateId: Int): Boolean {

        loadItemsFromStudent()

        val itemPostListIterator = itemsFromStudent.value.itemsFromStudent.listIterator()

        itemPostListIterator.forEach {item ->

            // Check if the itemTemplate is already in the DB by iterating over the list
            if (item.itemTemplateId == itemTemplateId) {
                return true
            }
        }

        return false
    }

    //helper method to only have to do the iteration as few times as possible
    private fun getSelectedItemPost(studentId: Int, itemTemplateId: Int): ItemPost {

        println("THE STUDENTID in helper method: $studentId")

        val itemPostListIterator = itemsFromStudent.value.itemsFromStudent.listIterator()

        //iterates over the list
        while (itemPostListIterator.hasNext()) {
            val currentItem = itemPostListIterator.next()
            println("currentItem, first call: $currentItem.")

            //checks if the itemTemplate is already in the DB by iterating over the list
            if (currentItem.itemTemplateId == itemTemplateId) {
                println("currentItem, itemTemplateID exists: $currentItem.")
                return ItemPost(currentItem.quantity, currentItem.itemTemplateId, currentItem.studentId)
            }
        }

        //returns a standard ItemFromStudent to be posted to the students DB
        return ItemPost(1, itemTemplateId, studentId)
    }

    private fun getSelectedItemPatch(studentId: Int, itemTemplateId: Int): ItemPatch {

        //TODO: returns ItemId 0 and a whole bunch of other nonesense with the other values

        loadItemsFromItemTemplate(studentId)

        val itemPatchListIterator = itemsFromItemTemplate.value.itemsFromItemTemplate.listIterator()

        //iterates over the list
        while (itemPatchListIterator.hasNext()) {
            val currentItem = itemPatchListIterator.next()

            //checks if the itemTemplate is already in the DB by iterating over the list
            if (currentItem.itemTemplateId == itemTemplateId) {
                return ItemPatch(
                    currentItem.id,
                    currentItem.quantity,
                    currentItem.itemTemplateId,
                    currentItem.studentId
                )
            }
        }

        return ItemPatch(0, 0, 0, 0) //Should never be returned
    }

//    private fun postStudent(currentStudent: Student, itemCost: Int) {
//
//        println("loadPostStudentCredits: PostStudentCredits function called")
//        println("loadPostStudentCredits: currentStudent to be past into patch: $currentStudent")
//
//        viewModelScope.launch {
//            student.update { it.copy(isLoading = true) }
//            try {
//                var student = StudentPost(1, 0, 0, 100, 0, "69487be5-d92c-4829-b647-e0343cb24860")
//                println("loadPostStudentCredits: student to be past into patch:BEFORE $student")
//
//                val response = studentHubService.postStudent(student).enqueue()
//                Log.d(TAG, "loadPostStudentCredits request: $response")
//
//                if (response.isSuccessful) {
//                    Log.d(TAG, "loadPostStudentCredits Success: ${response.body()}")
//                    response.body()?.let {
//                        this@StudentHubViewModel.student.update { state ->
//                            state.copy(student = it, isLoading = false)
//                        }
//                    }
//
//                } else {
//                    println("loadPostStudentCredits: student to be past into patch:FAILURE $student")
//                    Log.d(TAG, "loadPostStudentCredits Failed: ${response.code()}, ${response.message()}")
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

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