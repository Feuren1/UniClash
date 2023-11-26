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
import project.main.uniclash.JWT.TokenManager
import project.main.uniclash.ProfileActivity
import project.main.uniclash.datatypes.User
import project.main.uniclash.datatypes.UserLoginRequest
import project.main.uniclash.datatypes.UserSignUpRequest
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.retrofit.enqueue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

public data class UserIDCallback(val success: Boolean, val id: String)
public data class UserLoginTokenCallback(val success: Boolean, val token: String)

public data class UserCallback(val success: Boolean, val user: User?)
sealed interface UserSignUpUIState {
    data class HasEntries(
        val userSignUpRequest: UserSignUpRequest?,
        val isLoading: Boolean,
    ) : UserSignUpUIState
}
sealed interface RegisterUIState {
    data class HasEntries(
        val email: String?,
        val username: String?,
        val password: String?,
    ) : RegisterUIState
}

sealed interface UserUIState {
    data class HasEntries(
        val user: User?,
        val isLoading: Boolean,
    ) : UserSignUpUIState
}

class UserViewModel(private val userService: UserService, application: Application) : ViewModel() {
    private val tokenManager: TokenManager = TokenManager(application)
    private val TAG = UserViewModel::class.java.simpleName
    private val context: Application = application
    val text: MutableStateFlow<String> = MutableStateFlow("")


    val userSignUp = MutableStateFlow(
        UserSignUpUIState.HasEntries(
            userSignUpRequest = null,
            isLoading = false,
        )
    )

    val user = MutableStateFlow(
        UserUIState.HasEntries(
            user = null,
            isLoading = false,
        )
    )

    val registerData = MutableStateFlow(
        RegisterUIState.HasEntries(
            email = null,
            username = null,
            password = null,
            )
    )

    fun loadProfile(token: String, context: Context) {
        whoAmI(token,context)
    }

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
                    text.value = "Registration successful"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                text.value = "Registration failed"
                // Invoke the callback with the failure

                callback(UserIDCallback(success = false, id = ""))
            }
        }
    }

        // ...

    fun login(
        email: String,
        password: String,
        context: Context,
        callback: (UserLoginTokenCallback) -> Unit = {}
    ) {
        val call = userService.login(UserLoginRequest(email = email, password = password))
        call.enqueue(object : Callback<JsonObject> {
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
                        // Save the token to SharedPreferences
                        saveTokenToSharedPreferences(jwtToken,context)

                        // Update _userData with the response if needed
                        // Invoke the callback with success
                        callback(UserLoginTokenCallback(true, jwtToken))

                        // Call whoAmI with the retrieved token
                        whoAmI(jwtToken,context)
                    } else {
                        // Handle missing token
                        Log.e(TAG, "Token not found in the response")
                        callback(UserLoginTokenCallback(false, ""))
                    }
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

    private fun saveTokenToSharedPreferences(token: String, context: Context) {
        val preferences = context.getSharedPreferences("Token", Context.MODE_PRIVATE)
        preferences.edit().putString("JWT-Token", token).apply()
    }

    private fun saveUserIdToSharedPreferences(id: String, context: Context) {
        val preferences = context.getSharedPreferences("Ids", Context.MODE_PRIVATE)
        preferences.edit().putString("UserId", id).apply()
    }

    fun whoAmI(token: String?,
               context: Context,
               callback: (UserCallback) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val call = userService.whoAmI()
                call.enqueue(object : Callback<User> {
                    override fun onResponse(
                        call: Call<User>,
                        response: Response<User>
                    ) {
                        if (response.isSuccessful) {
                            // Parse the response body to get user information
                            val userResponse = response.body()
                            Log.d(TAG, response.body().toString())
                            // Assuming UserResponse is the data type returned by /whoAmI
                            if (userResponse != null) {
                                // Extract the user details
                                response.body().let {
                                    user.update { state ->
                                        state.copy(user = it, isLoading = false)
                                    }
                                }
                                getStudent(user.value.user!!.id)
                            }
                            saveUserIdToSharedPreferences(user.value.user!!.id, context)
                            callback(UserCallback(true, response.body()))
                            text.value = "You are logged in!"
                        } else{
                            Log.d(TAG, "Who Am I: FAILED" + response.body().toString())
                        }
                    }
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.d(TAG, "Who Am I: FAILED")
                        t.printStackTrace()
                        // Invoke the callback with failure
                        callback(UserCallback(false, null))
                        text.value = "Error during whoAmI request"
                    }
                })

            } catch (e: Exception) {
                // Handle exception
                Log.e(TAG, "Error during whoAmI request", e)
                text.value = "Error during whoAmI request"
            }
        }
    }

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
                    }
                    Log.d(TAG, "Student/Game-progress has been loaded ${user.value.user}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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

    companion object {
        fun provideFactory(
            userService: UserService,
            application: Application
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return UserViewModel(
                            userService,
                            application
                        ) as T
                }
            }
    }


    // Add other functions as needed

}
