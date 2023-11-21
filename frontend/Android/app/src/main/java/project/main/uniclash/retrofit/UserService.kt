package project.main.uniclash.retrofit

import android.content.Context
import com.google.gson.JsonObject
import project.main.uniclash.datatypes.UserLoginRequest
import project.main.uniclash.datatypes.UserSignUpRequest
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @POST("/refresh")
    fun refresh(): Call<Unit>

    @POST("/users/login")
    fun login(@Body userLoginRequest: UserLoginRequest): Call<JsonObject>

    @POST("/users/refresh-login")
    fun refreshLogin(): Call<Unit>

    @POST("/users/signup")
    fun signup(@Body userSignUpRequest: UserSignUpRequest): Call<String>

    @GET("/whoAmI")
    fun whoAmI(): Call<Unit>

    companion object {
        private var userService: UserService? = null
        fun getInstance(context: Context): UserService {
            if (userService == null) {
                userService = Retrofit.getRetrofitInstance(context).create<UserService>()
            }
            return userService!!
        }
    }

}
