package project.main.uniclash.datatypes

import org.osmdroid.util.GeoPoint

enum class MapSettings(private var selection : Boolean){
    MOVINGCAMERA(true),
    CRITTERBINOCULARS(false);

    fun getMapSetting():Boolean{
        return selection
    }

    fun setMapSetting(setSelection : Boolean){
        selection = setSelection
    }
}
enum class MapSaver(private var markers: ArrayList<MarkerData?>) {
    WILDENCOUNTER(ArrayList<MarkerData?>()),
    ARENA(ArrayList<MarkerData?>()),
    STUDENTHUB(ArrayList<MarkerData?>());

    fun getMarker(): ArrayList<MarkerData?> {
        return markers
    }

    fun setMarker(setMarker: ArrayList<MarkerData?>){
        markers = setMarker
    }

    override fun toString(): String {
        return "MapSaver(markers=$markers)"
    }
}

enum class SelectedMarker(private var marker:MarkerData?) {
    SELECTEDMARKER(null);

    fun takeMarker(): MarkerData? {
        val getter = marker
        marker = null
        return getter
    }

    fun setMarker(setMarker: MarkerData?){
        marker = setMarker
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
