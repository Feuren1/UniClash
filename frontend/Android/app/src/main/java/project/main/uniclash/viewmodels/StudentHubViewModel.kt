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
import project.main.uniclash.datatypes.Student
import project.main.uniclash.datatypes.StudentHub
import project.main.uniclash.datatypes.StudentPatch
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.enqueue


sealed interface StudentHubUIState {

    data class HasEntries(
        val studentHub: StudentHub?,
        val isLoading: Boolean,
    ) : StudentHubUIState
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

//sealed interface PatchStudentUIState {
//    data class HasEntries(
//        val student: Student?,
//        val isLoading: Boolean,
//    ) : PatchStudentUIState
//}

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

    val student = MutableStateFlow(
        StudentUIState.HasEntries(
            student = null,
            isLoading = false,
        )
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching initial studentHub data: ")
            loadItemTemplates() //TODO lässt tenplates immer laden auf wenn z.b. map ausfeührt wird.
        }
    }

    //loads one student with the ID
    fun loadStudent(id: Int) {
        viewModelScope.launch {
            student.update { it.copy(isLoading = true) }
            try {
                val response = studentHubService.getStudent(id).enqueue()
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

    fun loadItemsForStudent(studentId: Int) {
        viewModelScope.launch {
            itemsForStudent.update { it.copy(isLoading = true) }
            try {
                val response = studentHubService.getItemsFromStudent(studentId).enqueue()
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

    fun buyItem(currentStudent: Student?, itemTemplateId: Int, itemCost: Int): Boolean {

        println("Buy button was pressed in the ViewModel.")

        //loads all the ItemForStudent from the DB to have them saved in the viewModel
        loadItemsForStudent(currentStudent!!.id)
//        println("loadItemsForStudent was called.")

        val studentCredits = student.value.student!!.credits

        //checking if the student has enough money
        if (studentCredits >= itemCost) {

            patchStudentCredits(currentStudent, itemCost) //reduces credits of the student

            //calls the iterator method to get the same item as the selected one by the user
            val item = getSelectedItemForStudent(currentStudent!!.id, itemTemplateId)

            //See if the Item already exists in the DB
            return if (item.itemTemplateId != itemTemplateId /*As @PATCH does not work =*/ || item.itemTemplateId == itemTemplateId) {

                postItemForStudent(currentStudent!!.id, item) //Boolean that student has enough credits (true)

            } else { //Otherwise uses @PATCH to increase the quantity

                patchItemForStudent(currentStudent!!.id, itemTemplateId, item) //Boolean that student has enough credits (true)
            }
        } else {

            println("Student does not have enough credits!")
            return false //return boolean false, that the student does not have enough credits
        }
    }

    //helper method to only have to do the iteration as few times as possible
    private fun getSelectedItemForStudent(studentId: Int, itemTemplateId: Int): ItemForStudent {

        println("THE STUDENTID in helper method: $studentId")

        val itemListIterator = itemsForStudent.value.itemsForStudent.listIterator()

        //iterates over the list
        while (itemListIterator.hasNext()) {
            val currentItem = itemListIterator.next()
            println("currentItem, first call: $currentItem.")

            //checks if the itemTemplate is already in the DB by iterating over the list
            if (currentItem.itemTemplateId == itemTemplateId) {
                println("currentItem, itemTemplateID exists: $currentItem.")
                return currentItem
            }
        }

//        //makes sure the method works even when only one item is in the DB, as hasNext would skip it
//        if (!itemListIterator.hasNext()) {
//
//            val currentItem = itemListIterator.next()
//            println("currentItem, false hasNext() call: $currentItem.")
//
//            //checks if the itemTemplate is already in the DB by iterating over the list
//            if (currentItem.itemTemplateId == itemTemplateId) {
//                println("currentItem, itemTemplateID exists false hasNext() call: $currentItem.")
//                return currentItem
//            }
//        }

        //returns a standard ItemForStudent to be posted to the students DB
        return ItemForStudent(1, itemTemplateId, studentId)
    }

    private fun postItemForStudent(studentId: Int, item: ItemForStudent): Boolean {

        //@POST the ItemForStudent to the student DB
        viewModelScope.launch {
            itemForStudent.update { it.copy(isLoading = true) }
            try {
                var itemForStudent = item
                val response = studentHubService.postStudentItem(studentId, itemForStudent).enqueue()
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
                    Log.d(TAG, "Post Failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return true
    }

    private fun patchItemForStudent(studentId: Int, itemTemplateId: Int, item: ItemForStudent): Boolean {

//        //TODO: use the /item-templates/{id}/items => I get all items of the templateId <= here I get the itemId / I also need to filter the studentId to get the correct Item
//        //TODO: then use /item/[{id}, with this one I can use PATCH <= here I use the itemId to patch the correct Item
//        //@PATCH of the existing ItemForStudent, increasing its quantity
//        viewModelScope.launch {
//            itemForStudent.update { it.copy(isLoading = true) }
//            try {
//                var itemForStudent = ItemForStudent(item.quantity + 1, itemTemplateId, studentId)
//                val response = studentHubService.updateItemQuantityByTemplateId(
//                    studentId,
//                    itemTemplateId,
//                    itemForStudent
//                ).enqueue()
//                println(itemForStudent)
//                Log.d(TAG, "loadUpdateItemQuantity: $response")
//                if (response.isSuccessful) {
//                    Log.d(TAG, "Success: ${response.body()}")
//                    response.body()?.let {
//                        this@StudentHubViewModel.itemForStudent.update { state ->
//                            state.copy(itemForStudent = it, isLoading = false)
//                        }
//                    }
//                    println(itemForStudent)
//                } else {
//                    Log.d(TAG, "Patch Failed")
//                    println(itemForStudent)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
        return true
    }

    private fun patchStudentCredits(currentStudent: Student, itemCost: Int) {

        println("loadPatchStudentCredit: patchStudentCredits function called")
        println("loadPatchStudentCredit: currentStudent to be past into patch: $currentStudent")

        viewModelScope.launch {
            student.update { it.copy(isLoading = true) }
            try {
                val newCredits = currentStudent.credits - itemCost
                var student = StudentPatch(5, 1, 0, 0, newCredits, 0, "f96e0c04-c965-496a-8942-4fb7fcde9c30")
                println("loadPatchStudentCredit: Updating credits for student ${student.id} to $newCredits")
                println("loadPatchStudentCredit: student to be past into patch:BEFORE $student")

                val response = studentHubService.updateStudentCredits(student.id, student).enqueue()
                Log.d(TAG, "loadPatchStudentCredit request: $response")

                if (response.isSuccessful) {
                    println("loadPatchStudentCredit: student to be past into patch:SUCCESS $student")
                    Log.d(TAG, "loadPatchStudentCredit Success: ${response.body()}")
                    response.body()?.let {
                        this@StudentHubViewModel.student.update { state ->
                            state.copy(student = it, isLoading = false)
                        }
                    }

                } else {
                    println("loadPatchStudentCredit: student to be past into patch:FAILURE $student")
                    Log.d(TAG, "loadPatchStudentCredit Failed: ${response.code()}, ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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