package project.main.uniclash.datatypes

import org.osmdroid.util.GeoPoint

enum class MapSettings(private var selection : Boolean){
    MOVINGCAMERA(true), //If camera follows the gps location (arrow marker)
    CRITTERBINOCULARS(false); //critter visibility 250 false -> 1km true

    fun getMapSetting():Boolean{
        return selection
    }

    fun setMapSetting(setSelection : Boolean){
        selection = setSelection
    }
}

//Saves all Markers
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

//saves markers which was selected by an user (button in the detailed marker window).
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
    USERLOCATION(GeoPoint(0.0,0.0)),
    //saves the userLocation from where the critters were spawn
    //this will be used to calculated the distance between userlocation and the point from where the ritter spawns
    INTERSECTION(GeoPoint(0.0,0.0));

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
    FIRSTSPAWN(30),//to start firstSpawn event
    RESPAWN(300);//to start reSpawn event

    fun getCounter():Int{
        return count
    }

    fun setCounter(newCounter : Int){
        count = newCounter
    }

    fun minusCounter(minusCounter : Int){
            count -= minusCounter
    }

    fun plusCounter(plusCounter : Int){
        count += plusCounter
    }
}

