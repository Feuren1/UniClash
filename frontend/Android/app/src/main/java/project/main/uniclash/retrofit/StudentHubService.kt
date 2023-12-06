package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.ItemFromItemTemplate
import project.main.uniclash.datatypes.ItemPatch
import project.main.uniclash.datatypes.ItemPost
import project.main.uniclash.datatypes.ItemTemplate
import project.main.uniclash.datatypes.Student
import project.main.uniclash.datatypes.StudentHub
import project.main.uniclash.datatypes.StudentPatch
import project.main.uniclash.datatypes.StudentPost
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface StudentHubService {

    @GET("/student-hubs/{id}")
    fun getStudentHub(@Path("id") id: Int): Call<StudentHub>

    @GET("/student-hubs")
    fun getStudentHubs(): Call<List<StudentHub>>

    @GET("/item-templates")
    fun getItemTemplates(): Call<List<ItemTemplate>>

    @GET("/students/{id}/items")
    fun getItemsFromStudent(@Path("id") id: Int) : Call<List<ItemPost>>

    @POST("/students/{id}/items")
    fun postStudentItem(@Path("id") id: Int, @Body itemPost: ItemPost) : Call<ItemPost>

    @GET("/students/{id}")
    fun getStudent(@Path("id")id : Int): Call<Student>

    @POST("/students")
    fun postStudent(@Body studentPost: StudentPost): Call<Student>

    @PATCH("/students/{id}")
    fun updateStudentCredits(@Path("id") id: Int, @Body studentPatch: StudentPatch): Call<Student>

    @GET("/item-templates/{id}/items")
    fun getItemsFromItemTemplate(@Path("id") id: Int): Call<List<ItemFromItemTemplate>>

    @PATCH("/items/{id}")
    fun patchItemsFromItemTemplate(@Path("id") id: Int, @Body itemPatch: ItemPatch): Call<ItemPatch>


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