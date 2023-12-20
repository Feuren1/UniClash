package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.ItemFromItemTemplate
import project.main.uniclash.datatypes.Item
import project.main.uniclash.datatypes.ItemForStudent
import project.main.uniclash.datatypes.ItemTemplate
import project.main.uniclash.datatypes.NewStudentHub
import project.main.uniclash.datatypes.Student
import project.main.uniclash.datatypes.StudentHub
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface StudentHubService {

    @POST("/student-hubs")
    fun postStudentHub(@Body studentHub: NewStudentHub) : Call<StudentHub>

    @GET("/student-hubs/{id}")
    fun getStudentHub(@Path("id") id: Int): Call<StudentHub>

    @GET("/student-hubs")
    fun getStudentHubs(): Call<List<StudentHub>>

    @GET("/item-templates")
    fun getItemTemplates(): Call<List<ItemTemplate>>

    @GET("/students/{id}")
    fun getStudent(@Path("id")id : Int): Call<Student>

    @PATCH("/students/{studentId}/itemTemplate/{itemTemplateId}/buy")
    fun buyItem(@Path("studentId") studentId: Int, @Path("itemTemplateId") itemTemplateId: Int): Call<String>



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