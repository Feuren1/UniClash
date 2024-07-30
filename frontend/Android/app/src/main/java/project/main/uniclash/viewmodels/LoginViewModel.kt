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

    fun login(email: String, password: String, context: Context) {
        clearSelectedCritter()
        viewModelScope.launch {
            login.update { state ->
                state.copy(success = null, isLoading = true)
            }
            var fcmToken: String?
            runBlocking {
                fcmToken = userDataManager.getFCMToken()
            }
            //Creates a UserLoginRequest inside the parameter of login. Could also be done before in variable.
            val response = userService.login(
                UserLoginRequest(email = email, password = password, fcmtoken = fcmToken!!)).enqueue()
            if (response.isSuccessful) {
                Log.d(TAG, "Login: success, Token: ${response.body()}")

                val jsonObject = response.body()
                //Method: login of userService returns a jsonObject. Therefore we have to extract the Token manually.
                //Could possibly be done better but works.
                val jwtToken = jsonObject?.get("token")?.asString
                if (jwtToken != null) {
                    //saves the response body (JWT Token) in datastore (UserDataManager)
                    userDataManager.storeJWTToken(jwtToken)
                    login.update { state ->
                        state.copy(success = true, isLoading = false)
                    }
                    text.value = "Login successful!"
                } else {
                    // if token not found
                    Log.e(TAG, "Token not found in the response")
                    text.value = "Wasn't able to retrieve token, are you registered yet?"
                }
            } else {
                // If response was not successful
                Log.d(TAG, "Login failed with code: ${response.code()}")
                Log.d(TAG, "Error: ${response.message()}")
                text.value = "Something went wrong check your username and password please. Are you registered yet?"
            }
        }
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
