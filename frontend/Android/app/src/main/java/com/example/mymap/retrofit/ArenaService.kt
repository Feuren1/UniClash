package com.example.mymap.retrofit

import android.content.Context
import com.example.mymap.datatypes.Arena
import com.example.mymap.datatypes.Critter
import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

interface ArenaService {

    @GET("/arenas/{id}")
    fun getArena(@Path("id") id: Int): Call<Arena>
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

