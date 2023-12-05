package project.main.uniclash.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.StudentRegisterRequest
import project.main.uniclash.datatypes.UserSignUpRequest
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.retrofit.enqueue


sealed interface RegisterUIState {

    data class HasEntries(
        val email: String?,
        val username: String?,
        val password: String?,
    ) : RegisterUIState
}

sealed interface UserSignUpUIState {
    data class HasEntries(
        val userSignUpRequest: UserSignUpRequest?,
        val isLoading: Boolean,
    ) : UserSignUpUIState
}
class RegisterViewModel (private val userService: UserService, application: Application) : ViewModel() {
    private val TAG = RegisterViewModel::class.java.simpleName
    private val context: Application = application
    val text: MutableStateFlow<String> = MutableStateFlow("")
    var recievedUserId: String? = null

    val registerData = MutableStateFlow(
        RegisterUIState.HasEntries(
            email = null,
            username = null,
            password = null,
        )
    )

    val userSignUp = MutableStateFlow(
        UserSignUpUIState.HasEntries(
            userSignUpRequest = null,
            isLoading = false,
        )
    )

    fun createStudent(
        lat: String,
        lon: String,
        userId: String,
        ){
        val studentRegisterRequest = StudentRegisterRequest(
            level = 1,
            lat = lat,
            lon = lon,
            credits = 100,
            expToNextLevel = 0,
            userId = userId
        )
        viewModelScope.launch {
            try{
                val request = userService.createStudent(studentRegisterRequest)
                val response = request.enqueue()
                if(response.isSuccessful){
                    val string = response.body()!!
                    text.value = "Created Student Successfully"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                text.value = "Failed to create Student"
                // Invoke the callback with the failure

            }
        }
    }
    fun signup(
        email: String,
        password: String,
        username: String,
        callback: (UserIDCallback) -> Unit = {}
    ) {
        val userSignupRequest = UserSignUpRequest(
            username = username,
            password = password,
            email = email
        )

        viewModelScope.launch {
            Log.d(TAG, "Trying to register User...")
            text.value = "Trying to register User..."
            try {
                // Print the request URL before making the request
                val request = userService.signup(userSignupRequest)
                Log.d(TAG, "Request URL: ${request.request().url}")
                // Proceed with the request
                val response = request.enqueue()
                Log.d(TAG, "UserRegister: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Register: success")
                    val userSignupResponse = response.body()!!
                    Log.d(TAG, "Response: $userSignupResponse")
                    // Invoke the callback with the result
                    //createStudent("0","0", string)
                    text.value = "Registration successful"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                text.value = "Registration failed"
                // Invoke the callback with the failure
            }
        }
    }

    companion object {
        fun provideFactory(
            userService: UserService,
            application: Application
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RegisterViewModel(
                        userService,
                        application
                    ) as T
                }
            }
    }
}