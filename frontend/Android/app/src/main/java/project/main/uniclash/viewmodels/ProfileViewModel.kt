package project.main.uniclash.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import project.main.uniclash.datatypes.Student
import project.main.uniclash.datatypes.StudentRegisterRequest
import project.main.uniclash.datatypes.User
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.retrofit.enqueue
import project.main.uniclash.dataManagers.UserDataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


public data class UserIDCallback(val success: Boolean, val id: JsonObject)

public data class UserCallback(val success: Boolean, val user: User?)
public data class StudentCallback(val success: Boolean, val student: Student?)

sealed interface UserUIState {
    data class HasEntries(
        val user: User?,
        val isLoading: Boolean,
    ) : UserUIState
}

sealed interface StudentRequestUIState {
    data class HasEntries(
        val student: Student?,
        val isLoading: Boolean,
    ) : StudentRequestUIState
}

class ProfileViewModel (private val userService: UserService, application: Application) : ViewModel() {
    private val TAG = LoginViewModel::class.java.simpleName
    private val context: Application = application
    val text: MutableStateFlow<String> = MutableStateFlow("")
    val hasStudent: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(application)
    }

    val studentRequest = MutableStateFlow(
        StudentRequestUIState.HasEntries(
            student = null,
            isLoading = false
        )
    )

    val user = MutableStateFlow(
        UserUIState.HasEntries(
            user = null,
            isLoading = false,
        )
    )

    fun getStudent(id: String) {
        viewModelScope.launch {
            user.update { it.copy(isLoading = true) }
            try {
                val response = userService.getStudent(id).enqueue()
                Log.d(TAG, "Load Student of User: $response")
                if (response.isSuccessful) {

                    Log.d(TAG, "Success: ${response.body()}")
                    response.body()?.let { student ->
                        user.update { state ->
                            state.copy(user = state.user!!.copy(student = student), isLoading = false)
                        }
                        userDataManager.storeStudentId(response.body()!!.id)
                        userDataManager.storePlacedBuildings(response.body()!!.placedBuildings)
                        userDataManager.storeLevel(response.body()!!.level)
                    }
                    hasStudent.value= true
                    text.value = "Game progress loaded successfully"
                    Log.d(TAG, "Student/Game-progress has been loaded ${user.value.user}")
                }else{
                    user.update { state ->
                        state.copy(user = state.user!!, isLoading = false)
                    }
                    hasStudent.value= false
                    text.value = "Loading Game Progress failed, Have you created a Student yet?"
                }
            } catch (e: Exception) {
                hasStudent.value= false
                text.value = "Loading Game Progress failed, Have you created a Student yet?"
                e.printStackTrace()
            }
        }
    }

    fun createStudent(userId: String){
        viewModelScope.launch {
            val studentRegisterRequest = StudentRegisterRequest(
                level = 1,
                lat = "0.0",
                lon = "0.0",
                time = "0",
                credits = 100,
                expToNextLevel = 0,
                placedBuildings = 0,
                userId = userId,
            )
            try{
                val response = userService.createStudent(studentRegisterRequest).enqueue()

                        if (response.isSuccessful) {
                            val studentResponse = response.body()
                            Log.d(TAG, response.body().toString())
                            if (studentResponse != null) {
                                response.body().let {
                                    user.update { state ->
                                        state.copy(user = state.user!!.copy(student = it!!), isLoading = false)
                                    }
                                }
                                hasStudent.value = true
                            }
                            userDataManager.storeStudentId(user.value.user!!.student.id)
                            text.value = "Student has been created!"
                        } else{
                            Log.d(TAG, "Creating Student has failed!" + response.code())
                        }

            }catch (e: Exception) {
                Log.e(TAG, "Error while trying to create Student", e)
                text.value = "Error while trying to create Student"
            }
        }
    }

    //This method gets Data from the user by sending a JWT Token in the request. The token is added by the authentication interceptor in the Retrofit class.
    fun whoAmI(token: String?, context: Context) {
        viewModelScope.launch {
            try {
                val response = userService.whoAmI().enqueue()

                        if (response.isSuccessful) {
                            val userResponse = response.body()
                            Log.d(TAG, response.body().toString())
                            if (userResponse != null) {
                                //Let : Calls .update with the user itself as a value. Lambda is used to copy the state and change values. it = user
                                response.body().let {
                                    user.update { state ->
                                        state.copy(user = it, isLoading = false)
                                    }
                                }
                                //Get the student for this user.
                                getStudent(userResponse.id)
                            }
                            userDataManager.storeUserId(user.value.user!!.id)
                            text.value = "You are logged in!"
                        } else{
                            text.value = "Could not reach server do you have an internet connection?"
                            Log.d(TAG, "Who Am I: FAILED" + response.message())
                        }
                    } catch (e: Exception) {
                Log.e(TAG, "Error during whoAmI request", e)
                text.value = "Error during whoAmI request"
            }
        }
    }
    //not implemented yet
    fun refresh() {
        viewModelScope.launch {
            try {
                val response = userService.refresh().execute()
                if (response.isSuccessful) {
                } else {
                }
            } catch (e: Exception) {
            }
        }
    }
    //not implemented yet
    fun refreshLogin() {
        viewModelScope.launch {
            try {
                val response = userService.refreshLogin().execute()
                if (response.isSuccessful) {
                } else {
                }
            } catch (e: Exception) {
            }
        }
    }
    fun loadProfile(token: String, context: Context) {
        whoAmI(token,context)
    }

    companion object {
        fun provideFactory(
            userService: UserService,
            application: Application
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
                        userService,
                        application
                    ) as T
                }
            }
    }

}