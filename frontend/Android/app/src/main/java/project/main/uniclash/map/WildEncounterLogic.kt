package project.main.uniclash.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import org.osmdroid.util.GeoPoint
import project.main.uniclash.WildEncounterActivity
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.datatypes.MarkerWildEncounter
import java.lang.Math.PI
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt


 class WildEncounterLogic(private val context: Context){
    //TODO set max amount of wildCritters
    var userLocation = Locations.USERLOCATION.getLocation()
    private var markerList = ArrayList<MarkerData>()

     companion object {
         private const val WILDENCOUNTERLOGIC_TAG = "WildEncounterLogic"
     }

    fun initMarkers(usableCritters : List<CritterUsable?>) : ArrayList<MarkerData> {
        var mapCalculations = MapCalculations()
         if(usableCritters.isEmpty()){
             //throw IllegalArgumentException("Database contains no Critters")
             return markerList
         }
         var wildEncounterMax = usableCritters
         while(wildEncounterMax.size < 801){
             wildEncounterMax = wildEncounterMax + usableCritters
         }
         userLocation = Locations.USERLOCATION.getLocation()
         if(MapSaver.WILDENCOUNTER.getMarker() == null) {
             var randomLocation = generateRandomGeoPoints(userLocation, 2.0, 800) //400 pro km
             var i = 0
             val wildEncounter = wildEncounterMax

             while (i < 800) {
                 var myMarker = MarkerWildEncounter(
                     state = GeoPoint(randomLocation.get(i).latitude, randomLocation.get(i).longitude),
                     icon = mapCalculations.resizeDrawable(context, CritterPic.MUSK.searchDrawableM("${wildEncounter.get(i)?.name}M"),50.0F),
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

    private fun generateRandomGeoPoints(center: GeoPoint, radiusInKm: Double, times : Int): ArrayList<GeoPoint> {
            val random = java.util.Random()
            var counter = times
            var geoLocations = ArrayList<GeoPoint>()
            while (counter > 0) {
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
}
