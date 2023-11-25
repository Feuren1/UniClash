package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.Critter
import project.main.uniclash.datatypes.CritterTemplate
import project.main.uniclash.datatypes.CritterUsable
import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CritterService {
    @GET("/critters")
    fun getCritters(): Call<List<Critter>>

    @GET("/critters/{id}")
    fun getCritters(@Path("id") id: Int): Call<Critter>
    @GET("/critters/{id}/usable")
    fun getCritterUsable(@Path("id") id: Int): Call<CritterUsable>

    @GET("/students/{id}/usables")
    fun getCritterUsables(@Path("id") id: Int): Call<List<CritterUsable>>

    @POST("/students/{id}/critters")
    fun postStudentCritters(@Path("id") id: Int): Call<List<CritterUsable>>

    @GET("/critter-templates")
    fun getCrittersTemplates(): Call<List<CritterTemplate>>
    /*
    @POST("/todo-lists")
    fun createTodoList(@Body todoListCreateRequest: TodoListCreateRequest): Call<TodoList>

    @POST("/todo-lists/{id}/todos")
    fun createTodo(@Path("id") todoListId: String, @Body todoList: TodoCreateRequest): Call<Todo>

    @PATCH("/todos/{id}")
    fun updateTodo(@Path("id") id: String, @Body todo: TodoPatchRequest): Call<Todo>

     */
    @GET("/critters/{id}/evolve")
    fun evolveCritter(@Path("id") id: Int): Call<Critter>
    companion object {
        private var critterService: CritterService? = null
        fun getInstance(context: Context): CritterService {
            if (critterService == null) {
                critterService = Retrofit.getRetrofitInstance(context).create<CritterService>()
            }
            return critterService!!
        }
    }
}
