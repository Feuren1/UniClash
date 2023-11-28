package project.main.uniclash.datatypes

import android.app.Activity
import android.graphics.drawable.Drawable
import org.osmdroid.util.GeoPoint
import project.main.uniclash.MainActivity

open class MarkerData(
    var state : GeoPoint,
    var icon : Drawable? = null,
    var visible : Boolean = true,
    var title :String? = "marker",
    var snippet : String? = null,
    var pic : Int = 0,
    var button : Class<out Activity> = MainActivity::class.java,
    var buttonText : String? = "button",
    //var critterUsable: CritterUsable ? = null,
    //var studentHub : StudentHub ? = null
    //todo drei data data class die extenden.
    )

data class MarkerCritter(
    var critterUsable: CritterUsable? = null,
) : MarkerData()
