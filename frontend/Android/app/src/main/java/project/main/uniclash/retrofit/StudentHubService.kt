package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.Item
import project.main.uniclash.datatypes.ItemForStudent
import project.main.uniclash.datatypes.ItemTemplate
import project.main.uniclash.datatypes.Student
import project.main.uniclash.datatypes.StudentHub
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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
    @PATCH("/students/{id}/items")
    fun updateItemQuantityByTemplateId(
        @Path("id") id: Int,
        @Query("itemTemplateId") itemTemplateId: Int,
        @Body itemForStudent: ItemForStudent): Call<ItemForStudent>
    @GET("/students/{id}/items")
    fun getItemsFromStudent(@Path("id") id: Int) : Call<List<ItemForStudent>>
    @GET("/students/{id}")
    fun getStudent(@Path("id")id : Int): Call<Student>
    @PATCH("/students/{id}")
    fun updateStudentCredits(
        @Path("id") id: Int,
        @Body student: Student): Call<Student>

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