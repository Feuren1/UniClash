package project.main.uniclash.datatypes

import com.google.gson.annotations.SerializedName

data class UserSignUpRequest(
    @SerializedName("username") var username: String,
    @SerializedName("password") var password: String,
    @SerializedName("email") var email: String
)
