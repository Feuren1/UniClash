package project.main.uniclash.datatypes

import android.app.Activity
import android.graphics.drawable.Drawable
import com.utsman.osmandcompose.MarkerState
import project.main.uniclash.MainActivity
import project.main.uniclash.MenuActivity

data class MyMarker(
    var id: String? = "id",
    var state : MarkerState,
    var icon : Drawable? = null,
    var visible : Boolean = true,
    var title :String? = "marker",
    var snippet : String? = null,
    var pic : Int = 0,
    var button : Class<out Activity> = MainActivity::class.java,
    var buttonText : String? = "button",
    var critterUsable: CritterUsable ? = null
    //var building : Building ? = null
    )
