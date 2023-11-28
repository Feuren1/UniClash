package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.Item
import project.main.uniclash.datatypes.ItemForStudent
import project.main.uniclash.datatypes.ItemTemplate
import project.main.uniclash.datatypes.StudentHub
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StudentHubService {

    @GET("/student-hubs/{id}")
    fun getStudentHub(@Path("id") id: Int): Call<StudentHub>
    @GET("/student-hubs")
    fun getStudentHubs(): Call<List<StudentHub>>
    @GET("/item-templates")
    fun getItemTemplates(): Call<List<ItemTemplate>>
    @GET("/items")
    fun getItems(): Call<List<Item>>
    @POST("/students/{id}/items")
    fun postStudentItem(@Path("id") id: Int, @Body itemForStudent: ItemForStudent) : Call<ItemForStudent>

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