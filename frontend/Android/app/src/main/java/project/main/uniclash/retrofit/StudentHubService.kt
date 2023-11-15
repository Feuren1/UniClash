package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.Item
import project.main.uniclash.datatypes.StudentHub
import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

interface StudentHubService {

    @GET("/student-hubs/{id}")
    fun getStudentHub(@Path("id") id: Int): Call<StudentHub>
    @GET("/item-templates")
    fun getItems(): Call<List<Item>>

    companion object {
        private var studentHubService: StudentHubService? = null
        fun getInstance(context: Context): StudentHubService {
            if (studentHubService == null) {
                studentHubService = Retrofit.getRetrofitInstance(context).create<StudentHubService>()
            }
            return studentHubService!!
        }
    }
}