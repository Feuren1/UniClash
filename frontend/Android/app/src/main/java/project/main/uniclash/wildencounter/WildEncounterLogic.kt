package project.main.uniclash.wildencounter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.CritterUsable


abstract class WildEncounterLogic {


    val wildEncounters: ArrayList<CritterUsable> = ArrayList()
    //TODO set max amount of wildCritters
    abstract val userLocation : GeoPoint
    private val markerList = mutableListOf<MarkerState>()



    //Marker(
    //state = googleHeadQuarter4,
    //icon = prc2duck
    //)

    //val fontys = rememberMarkerState(
    //    geoPoint = GeoPoint(51.353576, 6.154071)
    //)

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

}