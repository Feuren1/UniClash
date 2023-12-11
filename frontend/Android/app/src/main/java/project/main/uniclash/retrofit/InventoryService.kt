package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.Student
import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

interface InventoryService {

    @GET("/students/{id}/items")
    fun getItemsFromStudent(@Path("id")id : Int): Call<Item>



    companion object {
        private var inventoryService: InventoryService? = null
        fun getInstance(context: Context): InventoryService {
            if (inventoryService == null) {
                inventoryService = Retrofit.getRetrofitInstance(context).create<InventoryService>()
            }
            return inventoryService!!
        }
    }
}