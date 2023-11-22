package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.Student
import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

interface StudentService {

    @GET("/students/{id}")
    fun getStudent(@Path("id")id : Int): Call<Student>



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