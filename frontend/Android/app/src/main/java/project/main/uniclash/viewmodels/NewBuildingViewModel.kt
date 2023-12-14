package project.main.uniclash.viewmodels

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.MarkerWildEncounter
import project.main.uniclash.datatypes.NewArena
import project.main.uniclash.datatypes.NewStudentHub
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.datatypes.Student
import project.main.uniclash.datatypes.StudentHub
import project.main.uniclash.datatypes.User
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.StudentService
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.userDataManager.UserDataManager

sealed interface PostArenaUIState {
    data class HasEntries(
        val arena: Arena?,
        val isLoading: Boolean,
    ) : PostArenaUIState
}
sealed interface PostStudentHubUIState {
    data class HasEntries(
        val studentHub: StudentHub?,
        val isLoading: Boolean,
    ) : PostStudentHubUIState
}

sealed interface StudentFreeBuildingInfo {
    data class HasEntries(
        val level: Int,
        val placedBuildings: Int,
    ) : StudentFreeBuildingInfo
}

sealed interface ReturnedStudent {
    data class HasEntries(
        val student: Student?,
        val isLoading: Boolean,
    ) : ReturnedStudent
}
class NewBuildingViewModel(
    private  val arenaService: ArenaService,
    private val studentHubService: StudentHubService,
    private val studentService : StudentService,
    private val application: Application
) : ViewModel() {

    private val userDataManager: UserDataManager by lazy {
        UserDataManager(application)
    }

    val arena = MutableStateFlow(
        PostArenaUIState.HasEntries(
            arena = null,
            isLoading = false
        )
    )

    val studentHub = MutableStateFlow(
        PostStudentHubUIState.HasEntries(
            studentHub = null,
            isLoading = false
        )
    )

    val studentFreeBuildingInfo = MutableStateFlow(
        StudentFreeBuildingInfo.HasEntries(
            level = 0,
            placedBuildings = 0
        )
    )

    val returnedStudent = MutableStateFlow(
        ReturnedStudent.HasEntries(
            student = null,
            isLoading = false
        )
    )

    fun loadStudentFreeBuildingInfo() {
            viewModelScope.launch { //if needed to avoid suspend
                studentFreeBuildingInfo.update {
                    it.copy(
                        placedBuildings = userDataManager!!.getPlacedBuildings()!!,
                        level = userDataManager!!.getLevel()!!
                    )
                }
            }
    }

    fun addArena(name : String, description : String, lat : String, long : String, pic : String){
        increasePlacedBuildingFromStudent()
        viewModelScope.launch {
            arena.update { it.copy(isLoading = true) }
            try {
                var newArena = NewArena(name,description,lat,long,0,pic)
                val response = arenaService.postArena(newArena).enqueue()
                Log.d(TAG, "loadArena: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body()?.let {
                        arena.update { state ->
                            state.copy(arena = it, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addStudentHub(name : String, description : String, lat : String, long : String, pic : String){
        increasePlacedBuildingFromStudent()
        viewModelScope.launch {
            studentHub.update { it.copy(isLoading = true) }
            try {
                var newStudentHub = NewStudentHub(name,description,lat,long,pic)
                val response = studentHubService.postStudentHub(newStudentHub).enqueue()
                Log.d(TAG, "loadStudentHub: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
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

    private fun increasePlacedBuildingFromStudent(){
        viewModelScope.launch {
            returnedStudent.update { it.copy(isLoading = true) }
            try {
                val response = studentService.increasePlacedBuildingFromStudent(userDataManager.getStudentId()!!,1).enqueue()
                Log.d(TAG, "Student: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: ${response.body()}")
                    response.body()?.let {
                        returnedStudent.update { state ->
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
            arenaService: ArenaService,
            studentHubService: StudentHubService,
            studentService: StudentService,
            application: Application
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NewBuildingViewModel(
                        arenaService,
                        studentHubService,
                        studentService,
                        application
                    ) as T
                }
            }
    }
}
