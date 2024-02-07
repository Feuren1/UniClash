package project.main.uniclash.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.Student
import project.main.uniclash.retrofit.StudentService
import project.main.uniclash.retrofit.enqueue


sealed interface StudentUIState{
    data class HasEntries(
        val student: Student?,
        val isLoading: Boolean,
    ): StudentUIState
}
class StudentViewModel( private val studentService: StudentService): ViewModel() {
    private val TAG = StudentViewModel::class.java.simpleName


    val student = MutableStateFlow(
        StudentUIState.HasEntries(
            student = null,
            isLoading = false,
        )
    )

    init {

    }
    fun loadStudentFromBegin(id : Int){
        if(student.value.student == null)loadStudent(id)
    }
    fun loadStudent(id : Int){
        viewModelScope.launch {
            student.update {it.copy(isLoading = true)  }
            try {
                val response = studentService.getStudent(id).enqueue()
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

    companion object {
        fun provideFactory(
            studentService: StudentService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StudentViewModel(
                        studentService,
                    ) as T
                }
            }
    }




}
