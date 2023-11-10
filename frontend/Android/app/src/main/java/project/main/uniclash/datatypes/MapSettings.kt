package project.main.uniclash.datatypes

import org.osmdroid.util.GeoPoint

enum class MapSettings(private var selection : Boolean){
    MOVINGCAMERA(true);

    fun getMapSetting():Boolean{
        return selection
    }

    fun setMapSetting(setSelection : Boolean){
        selection = setSelection
    }
}
enum class MapSaver(private var markers: ArrayList<GeoPoint>?) {
    WILDENCOUNTER(null),
    ARENA(null),
    STUDENTHUB(null);

    fun getMarker(): ArrayList<GeoPoint>? {
        return markers
    }

    fun setMarker(setMarker: ArrayList<GeoPoint>?){
        markers = setMarker
    }
}

enum class Locations(private var location : GeoPoint){
    USERLOCATION(GeoPoint(0.0,0.0));

    fun getLocation(): GeoPoint {
        return location
    }

    fun setLocation(setLocation: GeoPoint){
        location = setLocation
    }
}
