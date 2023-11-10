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
        if(setLocation.latitude != 0.0 || setLocation.longitude != 0.0) {
            location = setLocation
        }
    }
}

enum class Counter(private var count : Int){
    FIRSTSPAWN(5),
    WILDENCOUNTERREFRESHER(20);

    fun getCounter():Int{
        return count
    }

    fun setCounter(newCounter : Int){
        count = newCounter
    }

    fun minusCounter(minusCounter : Int){
        if(count > 0) {
            count -= minusCounter
        }
    }

    fun plusCounter(plusCounter : Int){
        count += plusCounter
    }
}
