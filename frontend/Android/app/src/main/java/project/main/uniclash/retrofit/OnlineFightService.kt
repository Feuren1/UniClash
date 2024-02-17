package project.main.uniclash.retrofit

import android.content.Context
import project.main.uniclash.datatypes.CritterInFight
import project.main.uniclash.datatypes.CritterInFightInformation
import project.main.uniclash.datatypes.FightState
import project.main.uniclash.datatypes.OnlineFight
import project.main.uniclash.datatypes.OnlineFightInformation
import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface OnlineFightService {

    @PUT("/checkIfFightCanStart/{fightConnectionId}")
    fun checkIfFightCanStart(@Path("fightConnectionId")fightConnectionId : Int):Call<Unit>

    @GET("/checkMyState/{fightConnectionId}/{studentId}")
    fun checkMyState(@Path("fightConnectionId")fightConnectionId : Int, @Path("studentId")studentId : Int): Call<FightState>

    @PUT("/createFight/{studentId}/{enemyStudentId}")
    fun createFight(@Path("studentId")studentId : Int, @Path("enemyStudentId")enemyStudentId : Int):Call<Unit>

    @PUT("/insertCritter/{fightConnectionId}/{studentId}/{critterId}")
    fun insertCritter(@Path("fightConnectionId")fightConnectionId : Int, @Path("studentId")studentId : Int, @Path("critterId")critterId : Int):Call<Unit>

    @PUT("/makingDamage/{fightConnectionId}/{studentId}/{amountOfDamage}/{kindOfDamage}/{effectiveness}")
    fun makingDamage(@Path("fightConnectionId")fightConnectionId : Int, @Path("studentId")studentId : Int, @Path("amountOfDamage")amountOfDamage : Int, @Path("kindOfDamage")kindOfDamage : String, @Path("effectiveness")effectiveness : Double):Call<Unit>

    @PUT("/sendMessageViaPushNotification/{fightConnectionId}/{studentId}/{message}")
    fun sendMessageViaPushNotification(@Path("fightConnectionId")fightConnectionId : Int, @Path("studentId")studentId : Int, @Path("message")message : String):Call<Unit>
    @GET("/fightInformationList/{studentId}")
    fun getFightInformationList(@Path("studentId")studentId : Int) : Call <List<OnlineFightInformation>>

    @GET("/getCritterInformation/{critterId}/{fightConnectionId}")
    fun getCritterInformation(@Path("critterId")critterId : Int,@Path("fightConnectionId")fightConnectionId : Int) : Call <CritterInFightInformation>

    @GET("/getCritterInformationFromEnemy/{fightConnectionId}/{studentId}")
    fun getCritterInformationFromEnemy(@Path("fightConnectionId")fightConnectionId : Int, @Path("studentId")studentId : Int) : Call <CritterInFightInformation>

    @GET("/online-fights")
    fun getOnlineFights() : Call <List<OnlineFight>>

    @GET("/critter-in-fights/{id}")
    fun getCritterInFight(@Path("id")id : Int) : Call <CritterInFight>


    companion object {
        private var onlineFightService: OnlineFightService? = null
        fun getInstance(context: Context): OnlineFightService {
            if (onlineFightService == null) {
                onlineFightService = Retrofit.getRetrofitInstance(context).create<OnlineFightService>()
            }
            return onlineFightService!!
        }
    }
}