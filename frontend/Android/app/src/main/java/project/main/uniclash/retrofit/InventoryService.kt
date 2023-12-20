package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.ItemUsable
import project.main.uniclash.datatypes.Student
import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface InventoryService {

    @GET("/students/{id}/Itemusables")
    fun getItemsFromStudent(@Path("id")id : Int): Call<List<ItemUsable>>

    @PATCH("/students/{studentId}/itemTemplate/{itemTemplateId}/use")
    fun useItem(@Path("studentId")studentId : Int, @Path("itemTemplateId") itemTemplateId : Int) : Call<Boolean>


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