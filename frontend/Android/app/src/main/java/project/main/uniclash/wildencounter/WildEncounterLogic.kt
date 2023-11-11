package project.main.uniclash.wildencounter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint
import project.main.uniclash.Battle
import project.main.uniclash.MapActivity
import project.main.uniclash.MenuActivity
import project.main.uniclash.R
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.datatypes.MyMarker
import java.lang.Math.PI
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt


 class WildEncounterLogic(private val context: Context) {


    val wildEncounters: ArrayList<CritterUsable> = ArrayList()
    //TODO set max amount of wildCritters
    var userLocation = Locations.USERLOCATION.getLocation()
    private var markerList = ArrayList<MyMarker>()

     companion object {
         private const val LOCATION_TAG = "MyLocationTag"
     }

     val attack1 = Attack(1, "Tackle", 1)
     val attack2 = Attack(2, "Scratch",2)

     val critter = CritterUsable(
         level = 5,
         name = "Pikachu",
         hp = 50,
         atk = 15,
         def = 10,
         spd = 20,
         attacks = listOf(attack1, attack2)
     )

     @Composable
    fun initMarkers() : ArrayList<MyMarker> {
         userLocation = Locations.USERLOCATION.getLocation()
             // Hier kannst du deine Marker initialisieren und zur markerList hinzufügen
             var randomLocation = generateRandomGeoPoints(userLocation, 2.0, 750)
             var i = 0
             while (i < 375) {
                 val state = rememberMarkerState(
                     geoPoint = GeoPoint(randomLocation.get(i).latitude, randomLocation.get(i).longitude),
                 )
                 var myMarker = MyMarker(
                     id = "1",
                     state = state,
                     icon = resizeDrawableTo50x50(context, CritterPic.QUIZIZZDRAGONM.getDrawable()),
                     visible = true,
                     title = "frist automatic marker",
                     snippet = "this is a discription",
                     pic = CritterPic.QUIZIZZDRAGON.getDrawable(),
                     button = Battle::class.java,
                     buttonText = "catch Critter",
                     critterUsable = critter
                 )
                 markerList.add(myMarker)
                 i++
             }
        // add more markers
        return markerList
    }

    fun getMarkerList(): ArrayList<MyMarker> {
        return markerList
    }

    fun resizeDrawableTo50x50(context: Context, @DrawableRes drawableRes: Int): Drawable? {
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

    fun generateRandomGeoPoints(center: GeoPoint, radiusInKm: Double, times : Int): ArrayList<GeoPoint> {
        if(MapSaver.WILDENCOUNTER.getMarker() == null) {
            val random = java.util.Random()
            var counter = times
            var geoLocations = ArrayList<GeoPoint>()
            while (counter > 0) {
                Log.d(WildEncounterLogic.LOCATION_TAG, "new Random Location")
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
            MapSaver.WILDENCOUNTER.setMarker(geoLocations)
            return geoLocations
        } else {
            return MapSaver.WILDENCOUNTER.getMarker()!!
        }
    }

}