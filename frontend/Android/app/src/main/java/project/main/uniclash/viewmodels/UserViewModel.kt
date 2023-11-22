package project.main.uniclash.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import project.main.uniclash.JWT.TokenManager
import project.main.uniclash.datatypes.UserLoginRequest
import project.main.uniclash.datatypes.UserSignUpRequest
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.retrofit.enqueue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

public data class UserIDCallback(val success: Boolean, val id: String)
public data class UserLoginTokenCallback(val success: Boolean, val token: String)
sealed interface UserUIState {
    data class HasEntries(
        val userSignUpRequest: UserSignUpRequest?,
        val isLoading: Boolean,
    ) : UserUIState
}
sealed interface RegisterUIState {
    data class HasEntries(
        val email: String?,
        val username: String?,
        val password: String?,
    ) : RegisterUIState
}
class UserViewModel(private val userService: UserService, application: Application) : ViewModel() {
    private val tokenManager: TokenManager = TokenManager(application)
    private val TAG = UserViewModel::class.java.simpleName
    val user = MutableStateFlow(
        UserUIState.HasEntries(
            userSignUpRequest = null,
            isLoading = false,
        )
    )

    val registerData = MutableStateFlow(
        RegisterUIState.HasEntries(
            email = "Test@gmail.com",
            username = "Test",
            password = "TestPassword",
            )
    )
    // Example function to perform user actions
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
            try {
                // Print the request URL before making the request
                val request = userService.signup(userSignupRequest)
                Log.d(TAG, "Request URL: ${request.request().url}")
                // Proceed with the request
                val response = request.enqueue()
                Log.d(TAG, "UserRegister: $response")
                if (response.isSuccessful) {
                    Log.d(TAG, "Register: success")
                    val string = response.body()!!
                    Log.d(TAG, "Response: $string")

                    // Invoke the callback with the result
                    callback(UserIDCallback(success = true, id = string))

                } else {
                    // Invoke the callback with the failure
                    callback(UserIDCallback(success = false, id = ""))
                }
            } catch (e: Exception) {
                e.printStackTrace()

                // Invoke the callback with the failure
                callback(UserIDCallback(success = false, id = ""))
            }
        }
    }

        // ...

    fun login(
        email: String,
        password: String,
        callback: (UserLoginTokenCallback) -> Unit = {}
    ) {
        val call = userService.login(UserLoginRequest(email = email, password = password))
        call.enqueue(object : Callback<JsonObject> {
            // Change JsonObject to the actual type of your response
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Login: success, Token: ${response.body()}")

                    // Save the JWT token securely
                    val jsonObject = response.body()
                    val jwtToken = jsonObject?.get("token")?.asString

                    if (jwtToken != null) {
                        tokenManager.saveToken(jwtToken)
                    }

                    // Update _userData with the response if needed
                    // Invoke the callback with success
                    callback(UserLoginTokenCallback(true, jwtToken ?: ""))
                } else {
                    // Handle non-successful response
                    Log.d(TAG, "Login failed with code: ${response.code()}")
                    Log.d(TAG, "Error: ${response.message()}")

                    // Invoke the callback with failure
                    callback(UserLoginTokenCallback(false, ""))
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "Login: FAILED")
                t.printStackTrace()

                // Invoke the callback with failure
                callback(UserLoginTokenCallback(false, ""))
            }
        })
    }

 fun getToken(){
     println(tokenManager.getToken())
 }

    init {

}
    fun refresh() {
        viewModelScope.launch {
            try {
                val response = userService.refresh().execute()
                if (response.isSuccessful) {
                    // Update _userData with the response
                    // Handle the response body if needed
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    fun refreshLogin() {
        viewModelScope.launch {
            try {
                val response = userService.refreshLogin().execute()
                if (response.isSuccessful) {
                    // Update _userData with the response
                    // Handle the response body if needed
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }



    fun whoAmI() {
        viewModelScope.launch {
            try {
                val response = userService.whoAmI().execute()
                if (response.isSuccessful) {
                    // Update _userData with the response
                    // Handle the response body if needed
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle exception
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
                    if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                        return UserViewModel(
                            userService,
                            application
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }


    // Add other functions as needed

}
