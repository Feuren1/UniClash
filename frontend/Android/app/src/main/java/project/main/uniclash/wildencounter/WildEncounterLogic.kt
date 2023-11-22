package project.main.uniclash.wildencounter

import android.content.Context
import androidx.activity.viewModels
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint
import project.main.uniclash.WildEncounterActivity
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MyMarker
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.viewmodels.UniClashViewModel
import java.lang.Math.PI
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt


 class WildEncounterLogic(private val context: Context){


    val wildEncounters: ArrayList<CritterUsable> = ArrayList()
    //TODO set max amount of wildCritters
    var userLocation = Locations.USERLOCATION.getLocation()
    private var markerList = ArrayList<MyMarker>()

     companion object {
         private const val WILDENCOUNTERLOGIC_TAG = "WildEncounterLogic"
     }

     @Composable
    fun initMarkers(usableCritters : List<CritterUsable?>) : ArrayList<MyMarker> {
         println("nicht mehr")
         println("nicht mehr")
         println("nicht mehr")
         println("nicht mehr")
         if(usableCritters.isEmpty()){
             //throw IllegalArgumentException("Database contains no Critters")
             return markerList
         }
         Log.d(WILDENCOUNTERLOGIC_TAG, "--------")
         Log.d(WILDENCOUNTERLOGIC_TAG, "executed")
         var wildEncounterMax = usableCritters
         while(wildEncounterMax.size < 801){
             Log.d(WILDENCOUNTERLOGIC_TAG, "loop :) ${wildEncounterMax.size}")
             wildEncounterMax = wildEncounterMax + usableCritters
         }
         Log.d(WILDENCOUNTERLOGIC_TAG, "executed2")
         userLocation = Locations.USERLOCATION.getLocation()
         if(MapSaver.WILDENCOUNTER.getMarker() == null) {
             var randomLocation = generateRandomGeoPoints(userLocation, 2.0, 800) //400 pro km
             var i = 0
             val wildEncounter = wildEncounterMax

             while (i < 800) {
                 val state = rememberMarkerState(
                     geoPoint = GeoPoint(
                         randomLocation.get(i).latitude,
                         randomLocation.get(i).longitude
                     ),
                 )
                 var myMarker = MyMarker(
                     id = "1",
                     state = state,
                     icon = resizeDrawableTo50x50(context, CritterPic.MUSK.searchDrawableM("${wildEncounter.get(i)?.name}M")),
                     visible = true,
                     title = "${wildEncounter.get(i)?.name}",
                     snippet = "Level: ${wildEncounter.get(i)?.level}",
                     pic = CritterPic.MUSK.searchDrawable("${wildEncounter.get(i)?.name}"),
                     button = WildEncounterActivity::class.java,
                     buttonText = "catch Critter",
                     critterUsable = wildEncounter.get(i)
                 )
                 markerList.add(myMarker)
                 i++
             }
             // add more markers
             MapSaver.WILDENCOUNTER.setMarker(markerList)
             return markerList
         } else {
             return MapSaver.WILDENCOUNTER.getMarker()!!
         }
    }

    fun getMarkerList(): ArrayList<MyMarker> {
        return markerList
    }

    private fun resizeDrawableTo50x50(context: Context, @DrawableRes drawableRes: Int): Drawable? {
        val originalDrawable: Drawable? = context.getDrawable(drawableRes)

        val originalBitmap = originalDrawable?.toBitmap()
        val originalWidth = originalBitmap?.width ?: 1 // Verhindert Division durch Null
        val originalHeight = originalBitmap?.height ?: 1

        val scaleRatio = 50.0f / originalHeight.toFloat()

        val scaledWidth = (originalWidth * scaleRatio).toInt()
        val scaledHeight = 50

        val scaledBitmap =
            originalBitmap?.let { Bitmap.createScaledBitmap(it, scaledWidth, scaledHeight, true) }

        return BitmapDrawable(context.resources, scaledBitmap)
    }

    private fun generateRandomGeoPoints(center: GeoPoint, radiusInKm: Double, times : Int): ArrayList<GeoPoint> {
            val random = java.util.Random()
            var counter = times
            var geoLocations = ArrayList<GeoPoint>()
            while (counter > 0) {
                Log.d(WildEncounterLogic.WILDENCOUNTERLOGIC_TAG, "new Random Location")
                // Convert radius from kilometers to degrees
                val radiusInDegrees = radiusInKm / 111.32

                val u = random.nextDouble()
                val v = random.nextDouble()
                val w = radiusInDegrees * sqrt(u)
                val t = 2.0 * PI * v
                val x = w * cos(t)
                val y = w * sin(t)

                // Adjust the x-coordinate for the shrinking of the east-west distances
                val new_x = x / cos(Math.toRadians(center.latitude))

                val newLongitude = new_x + center.longitude
                val newLatitude = y + center.latitude
                geoLocations.add(GeoPoint(newLatitude, newLongitude))
                counter--
            }
            return geoLocations
    }

     val attack1 = Attack(1, "Tackle", 1)
     val attack2 = Attack(2, "Scratch",2)
}
