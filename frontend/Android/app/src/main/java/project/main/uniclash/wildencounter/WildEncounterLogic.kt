package project.main.uniclash.wildencounter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint
import project.main.uniclash.R
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.MyMarker
import java.lang.Math.PI
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt


 class WildEncounterLogic(private val context: Context) {


    val wildEncounters: ArrayList<CritterUsable> = ArrayList()
    //TODO set max amount of wildCritters
    var userLocation = GeoPoint(0.0, 0.0)

    private val markerList = ArrayList<MyMarker>()

     @Composable
    fun initMarkers() : ArrayList<MyMarker> {
        // Hier kannst du deine Marker initialisieren und zur markerList hinzufügen
         var i = 0
         while(i < 500) {
             userLocation = GeoPoint(51.353576, 6.154071)
             var randomLocation = generateRandomGeoPoint(userLocation, 2.0)
             val state = rememberMarkerState(
                 geoPoint = GeoPoint(randomLocation.latitude, randomLocation.longitude),
             )
             var myMarker = MyMarker(
                 id = "1",
                 state = state,
                 icon = resizeDrawableTo50x50(context, CritterPic.LINUXPINGIUNM.getDrawable()),
                 visible = true,
                 title = "frist automatic marker",
                 snippet = "this is a discription",
                 pic = CritterPic.LINUXPINGIUN.getDrawable(),
                 button = "BattleActivity"
             )
             markerList.add(myMarker)
             println("initlasated new Marker")
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

    fun generateRandomGeoPoint(center: GeoPoint, radiusInKm: Double): GeoPoint {
        val random = java.util.Random()

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

        return GeoPoint(newLatitude, newLongitude)
    }

}