package project.main.uniclash.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import org.osmdroid.util.GeoPoint
import kotlin.math.pow

class MapCalculations {

    fun resizeDrawable(context: Context, @DrawableRes drawableRes: Int, pixelSize : Float): Drawable? {
        val originalDrawable: Drawable? = context.getDrawable(drawableRes)

        val originalBitmap = originalDrawable?.toBitmap()
        val originalWidth = originalBitmap?.width ?: 1
        val originalHeight = originalBitmap?.height ?: 1

        val scaleRatio = pixelSize / originalHeight.toFloat()

        val scaledWidth = (originalWidth * scaleRatio).toInt()
        val scaledHeight : Int = pixelSize.toInt()

        val scaledBitmap =
            originalBitmap?.let { Bitmap.createScaledBitmap(it, scaledWidth, scaledHeight, true) }

        return BitmapDrawable(context.resources, scaledBitmap)
    }

    fun calculateDirection(startpoint: GeoPoint, endpoint: GeoPoint): Float {
        val dX = endpoint.longitude - startpoint.longitude
        val dY = endpoint.latitude - startpoint.latitude

        val winkel = Math.toDegrees(Math.atan2(dY, dX)).toFloat()
        return if (winkel < 0) {
            (winkel + 360) % 360 // like: *-1
        } else {
            winkel
        }
    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radius = 6371000 // radius of the earth in meters

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2)
            .pow(2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(
            dLon / 2
        ).pow(2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return radius * c
    }
}