package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.datatypes.ArenaCritterPatch
import project.main.uniclash.datatypes.ArenaLeaderPatch
import project.main.uniclash.datatypes.Critter
import project.main.uniclash.datatypes.CritterForStudent
import project.main.uniclash.datatypes.CritterListable
import project.main.uniclash.datatypes.CritterTemplate
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.PostArenaBattleUpdate
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CritterService {
    @GET("/critters")
    fun getCritters(): Call<List<Critter>>

    @GET("/critters/{id}")
    fun getCritters(@Path("id") id: Int): Call<Critter>

    @DELETE("/critters/{id}")
    fun delCritter(@Path("id") id :Int): Call<String>

    @GET("/critters/{id}/usable")
    fun getCritterUsable(@Path("id") id: Int): Call<CritterUsable>

    @GET("/students/{id}/listables")
    fun getCritterListables(@Path("id") id: Int): Call<List<CritterListable>>

    @GET("/students/{id}/usables")
    fun getCritterUsables(@Path("id") id: Int): Call<List<CritterUsable>>

    @POST("/students/{id}/critters")
    fun postStudentCritters(@Path("id") id: Int): Call<List<CritterUsable>>

    @POST("/students/{id}/critters")
    fun postStudentCritter(@Path("id") id: Int, @Body critterForStudent: CritterForStudent) : Call<CritterUsable>

    @POST("/students/{studentId}/critters/{critterId}/catchCritter")
    fun postCatchedCritter(@Path("studentId") studentId: Int?, @Path("critterId") critterId: Int) :Call<CritterUsable>

    @GET("/critter-templates")
    fun getCrittersTemplates(): Call<List<CritterTemplate>>

    @GET("/critter-templates/{id}")
    fun getCrittersTemplate(@Path("id") id : Int): Call<CritterTemplate>

    @PATCH("/arenas/{id}")
    fun patchArenaLeader(@Path("id") id: Int, @Body arenaLeaderPatch: ArenaLeaderPatch): Call<Arena>

    @POST("/critters/increaseXp")
    fun postArenaBattleUpdates(@Body postArenaBattleUpdate: PostArenaBattleUpdate): Call<Critter>

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
