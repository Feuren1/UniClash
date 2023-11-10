package project.main.uniclash.datatypes

import okhttp3.internal.connection.RouteSelector
import org.osmdroid.util.GeoPoint
import project.main.uniclash.R

enum class MapSettings(private var markers: ArrayList<GeoPoint>?) {
    WILDENCOUNTER(null);

    fun getMarker(): ArrayList<GeoPoint>? {
        return markers
    }

    fun setMarker(setMarker: ArrayList<GeoPoint>?){
        markers = setMarker
    }
}