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
import project.main.uniclash.datatypes.UserLoginRequest
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.retrofit.enqueue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
public data class UserLoginTokenCallback(val success: Boolean, val token: String)

sealed interface LoginUIState {

    data class HasEntries(
        val UserloginRequest: UserLoginRequest?,
        val success: Boolean?,
        val isLoading: Boolean?,
    ) : LoginUIState
}
class LoginViewModel (private val userService: UserService, application: Application) : ViewModel(){

    private val TAG = LoginViewModel::class.java.simpleName
    private val context: Application = application
    private val userDataManager: UserDataManager by lazy {
        UserDataManager(application)
    }

    val text: MutableStateFlow<String> = MutableStateFlow("")

    val login = MutableStateFlow(
        LoginUIState.HasEntries(
            UserloginRequest = null,
            success = false,
            isLoading = false,
        )
    )

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
                        // Save the token to Datastore
                        runBlocking {
                            userDataManager.storeJWTToken(jwtToken)
                        }
                        // Update userData with the response if needed
                        // Invoke the callback with success
                        callback(UserLoginTokenCallback(true, jwtToken))
                        login.update { state ->
                            state.copy(success = true, isLoading = false)
                        }
                        text.value = "Login successful!"
                    } else {
                        // Handle missing token
                        Log.e(TAG, "Token not found in the response")
                        callback(UserLoginTokenCallback(false, ""))
                        text.value = "Wasn't able to retrieve token, are you registered yet?"
                    }
                } else {
                    // Handle non-successful response
                    Log.d(TAG, "Login failed with code: ${response.code()}")
                    Log.d(TAG, "Error: ${response.message()}")

                    // Invoke the callback with failure
                    callback(UserLoginTokenCallback(false, ""))
                    text.value = "Something went wrong check your username and password please"
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "Login: FAILED")
                t.printStackTrace()

                // Invoke the callback with failure
                callback(UserLoginTokenCallback(false, ""))
                text.value = "Something went wrong contacting the server, do you have an internet connection?"
            }
        })
    }

    private fun clearSelectedCritter(){
        viewModelScope.launch {
            userDataManager.storeFightingCritterID(0)
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
                    return LoginViewModel(
                        userService,
                        application
                    ) as T
                }
            }
    }

}
