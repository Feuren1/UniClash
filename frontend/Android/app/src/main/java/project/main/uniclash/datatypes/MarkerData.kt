package project.main.uniclash.datatypes

import android.app.Activity
import android.graphics.drawable.Drawable
import org.osmdroid.util.GeoPoint
import project.main.uniclash.MainActivity

open class MarkerData(
    open var state : GeoPoint,
    open var icon : Drawable? = null,
    open var visible : Boolean = true,
    open var title :String? = "marker",
    open var snippet : String? = null,
    open var pic : Int = 0,
    open var button : Class<out Activity> = MainActivity::class.java,
    open var buttonText : String? = "button",
    //var critterUsable: CritterUsable ? = null,
    //var studentHub : StudentHub ? = null
    //todo drei data data class die extenden.
    )

data class MarkerWildEncounter(
    override var state : GeoPoint,
    override var icon : Drawable? = null,
    override var visible : Boolean = true,
    override var title :String? = "marker",
    override var snippet : String? = null,
    override var pic : Int = 0,
    override var button : Class<out Activity> = MainActivity::class.java,
    override var buttonText : String? = "button",
    var critterUsable: CritterUsable? = null,
) : MarkerData(
    state,icon,visible,title,snippet,pic,button, buttonText
)

data class MarkerStudentHub(
    override var state : GeoPoint,
    override var icon : Drawable? = null,
    override var visible : Boolean = true,
    override var title :String? = "marker",
    override var snippet : String? = null,
    override var pic : Int = 0,
    override var button : Class<out Activity> = MainActivity::class.java,
    override var buttonText : String? = "button",
    var studentHub : StudentHub ? = null
) : MarkerData(
    state,icon,visible,title,snippet,pic,button, buttonText
)

//TODO data class MarkerArena
