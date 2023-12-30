package project.main.uniclash.datatypes

import com.google.gson.annotations.SerializedName

data class UserLoginRequest(
    @SerializedName("email") var email: String,
    @SerializedName("password") var password: String,
    @SerializedName("fcmToken") var fcmtoken: String,
)
