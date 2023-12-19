package project.main.uniclash.datatypes

import com.google.gson.annotations.SerializedName

data class UserSignUpRequest(
    //SerializedName Means that the following string is taken in the HTTP request and used to fill the body of the method
    @SerializedName("username") var username: String,
    @SerializedName("password") var password: String,
    @SerializedName("email") var email: String
)
