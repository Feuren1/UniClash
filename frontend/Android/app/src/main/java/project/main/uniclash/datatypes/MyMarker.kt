package project.main.uniclash.datatypes

import android.graphics.drawable.Drawable
import com.utsman.osmandcompose.MarkerState

data class MyMarker(
    var id: String,
    var state : MarkerState,
    var icon : Drawable? = null,
    var visible : Boolean = true,
    var title :String? = "marker",
    var snippet : String? = null,
    var pic : Int = 0,
    var button : String ? = "MenuActivity",
    var critterUsable: CritterUsable ? = null
    //var building : Building ? = null
    )
