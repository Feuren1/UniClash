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
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.CritterUsable
import java.lang.Math.PI
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt


 class WildEncounterLogic(private val context: Context) {


    val wildEncounters: ArrayList<CritterUsable> = ArrayList()
    //TODO set max amount of wildCritters
    var userLocation = GeoPoint(0.0, 0.0)

    private val markerList = ArrayList<MarkerState>()

     @Composable
    fun initMarkers() : ArrayList<MarkerState> {
        // Hier kannst du deine Marker initialisieren und zur markerList hinzufügen
        val prc2duckMarker = createMarker(
            geoPoint = GeoPoint(51.353576, 6.154071),
            icon = resizeDrawableTo50x50(context, CritterPic.PRC2DUCK.getDrawable()),
            key = "1"
        )
        markerList.add(prc2duckMarker)

        // add more markers
        return markerList
    }

    @Composable
    private fun createMarker(
        geoPoint: GeoPoint,
        key: String? = null,
        icon: Drawable?,
        title: String? = null,
        snippet: String? = null
    ): MarkerState {
        return rememberMarkerState(
            geoPoint = geoPoint,
            key = key
        )
    }

    fun getMarkerList(): ArrayList<MarkerState> {
        return markerList
    }

    /*@Composable
    fun addMarker(
        geoPoint: GeoPoint,
        icon: Drawable?,
        title: String? = null,
        snippet: String? = null
    ) {
        val newMarker = createMarker(geoPoint, icon, title, snippet)
        markerList.add(newMarker)
        map.Marker(
            state = newMarker,
            onClick = {
                // Implementiere die Logik für Marker-Klicks, wenn nötig
            }
        )
    }*/

    //Marker(
    //state = googleHeadQuarter4,
    //icon = prc2duck
    //)

    //val fontys = rememberMarkerState(
    //    geoPoint = GeoPoint(51.353576, 6.154071)
    //)

    /*Marker(
    state = rememberMarkerState(geoPoint = GeoPoint(51.353576, 6.154071)),
    icon = resizeDrawableTo50x50(context, CritterPic.PRC2DUCK.getDrawable())
    )*/

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

    fun resizeDrawableTo60x60(context: Context, @DrawableRes drawableRes: Int): Drawable? {
        val originalDrawable: Drawable? = context.getDrawable(drawableRes)

        val originalBitmap = originalDrawable?.toBitmap()
        val originalWidth = originalBitmap?.width ?: 1 // Verhindert Division durch Null
        val originalHeight = originalBitmap?.height ?: 1

        val scaleRatio = 60.0f / originalHeight.toFloat()

        val scaledWidth = (originalWidth * scaleRatio).toInt()
        val scaledHeight = 60

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