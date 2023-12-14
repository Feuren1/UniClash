package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.datatypes.CritterForStudent
import project.main.uniclash.datatypes.NewArena
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ArenaService {

    @GET ("/arenas/{id}")
    fun getArenas(@Path("id")id : Int): Call<Arena>

    @GET ("/arenas")
    fun getArenas() : Call <List<Arena>>

    @POST("/arenas")
    fun postArena(@Body newArena: NewArena):Call<Arena>



    companion object {
        private var arenaService: ArenaService? = null
        fun getInstance(context: Context): ArenaService {
            if (arenaService == null) {
                arenaService = Retrofit.getRetrofitInstance(context).create<ArenaService>()
            }
            return arenaService!!
        }
    }
}