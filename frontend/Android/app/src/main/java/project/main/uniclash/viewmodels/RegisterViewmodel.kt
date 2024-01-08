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
class RegisterViewModel (private val userService: UserService) : ViewModel() {
    private val TAG = RegisterViewModel::class.java.simpleName
    val text: MutableStateFlow<String> = MutableStateFlow("")

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
    fun signup(email: String, password: String, username: String) {
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
                val response = userService.signup(userSignupRequest).enqueue()
                Log.d(TAG, "UserRegister: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Register: success")
                    val userSignupResponse = response.body()!!
                    Log.d(TAG, "Response: $userSignupResponse")
                    text.value = "Registration successful"
                } else{
                    text.value = "Registration failed please check your internet Connection."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                text.value = "Registration failed"
            }
        }
    }

    companion object {
        fun provideFactory(
            userService: UserService
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RegisterViewModel(
                        userService
                    ) as T
                }
            }
    }
}