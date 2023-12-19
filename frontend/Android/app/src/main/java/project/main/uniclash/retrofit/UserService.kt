package project.main.uniclash.retrofit

import android.content.Context
import com.google.gson.JsonObject
import project.main.uniclash.datatypes.Student
import project.main.uniclash.datatypes.StudentRegisterRequest
import project.main.uniclash.datatypes.User
import project.main.uniclash.datatypes.UserLoginRequest
import project.main.uniclash.datatypes.UserSignUpRequest
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @POST("/refresh")
    fun refresh(): Call<Unit>

    @POST("/users/login")
    fun login(@Body userLoginRequest: UserLoginRequest): Call<JsonObject>

    @POST("/users/refresh-login")
    fun refreshLogin(): Call<Unit>

    @POST("/users/signup")
    fun signup(@Body userSignUpRequest: UserSignUpRequest): Call<UserSignUpRequest>

    @GET("/whoAmI")
    fun whoAmI(): Call<User>

    @GET("/users/{id}/student")
    fun getStudent(@Path("id") id: String): Call<Student>

    @POST("/students")
    fun createStudent(@Body studentRegisterRequest: StudentRegisterRequest): Call<Student>


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
