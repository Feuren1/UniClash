package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.Student
import project.main.uniclash.datatypes.StudentOnMap
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface StudentService {

    @GET("/students/{id}")
    fun getStudent(@Path("id")id : Int): Call<Student>

    @PATCH("/students/{id}/increaseBuilding")
    fun increasePlacedBuildingFromStudent(@Path("id") id : Int, @Body addedBuildings: Int) : Call<Student>

    @PATCH("/students/{id}/{lat}/{lon}/getStudentLocations")
    fun getStudentLocations(@Path("id") id : Int,@Path("lat") lat : String, @Path("lon") lon :String) : Call<List<StudentOnMap>>



    companion object {
        private var studentService: StudentService? = null
        fun getInstance(context: Context): StudentService {
            if (studentService == null) {
                studentService = Retrofit.getRetrofitInstance(context).create<StudentService>()
            }
            return studentService!!
        }
    }
}