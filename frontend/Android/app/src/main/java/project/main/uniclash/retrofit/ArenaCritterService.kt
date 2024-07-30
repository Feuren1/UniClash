package project.main.uniclash.retrofit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.datatypes.ArenaCritterPatch
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.Student
import project.main.uniclash.viewmodels.ArenaViewModel
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ArenaCritterService {


    @PATCH("/arenas/{id}")
    fun updateArenaCritter(@Path("id") id: Int, @Body arenaCritterPatch: ArenaCritterPatch): Call<Arena>


    @GET("/students/{id}/usables")
    fun getCritterUsables(@Path("id") id: Int): Call<List<CritterUsable>>

    companion object {
        private var arenaCritterService: ArenaCritterService? = null
        fun getInstance(context: Context): ArenaCritterService {
            if (arenaCritterService == null) {
                arenaCritterService = Retrofit.getRetrofitInstance(context).create<ArenaCritterService>()
            }
            return arenaCritterService!!
        }
    }

}

