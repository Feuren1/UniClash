package project.main.uniclash.map

import kotlinx.coroutines.flow.MutableStateFlow
import project.main.uniclash.datatypes.MarkerData

class MarkerList() {

    private val _markerList = MutableStateFlow(ArrayList<MarkerData>())
    val markerList = _markerList

    /*
    //Objects has to be "recreated" other the observer will do nothing.
     */

    fun getMarkerList(): ArrayList<MarkerData> {
        return _markerList.value
    }

    fun addMarker(marker: MarkerData) {
        if (!(_markerList.value.contains(marker))) {
            _markerList.value = ArrayList(_markerList.value + marker)
        }
    }

    fun addListOfMarkers(markers: ArrayList<MarkerData>) {
        if (!(_markerList.value.containsAll(markers))) {
            _markerList.value = ArrayList(_markerList.value + markers)
        }
    }

    fun addListOfMarkersQ(markers: ArrayList<MarkerData?>) {
        val convertedMarkers = markers.filterNotNull()

        if (!(_markerList.value.containsAll(convertedMarkers))) {
            _markerList.value = ArrayList(_markerList.value + convertedMarkers)
        }
    }

    fun removeMarker(marker: MarkerData) {
        _markerList.value = ArrayList(_markerList.value - marker)
    }

    fun removeMarkersQ(markers: ArrayList<MarkerData?>){
        val convertedMarkers = markers.filterNotNull()
        if (!(_markerList.value.containsAll(convertedMarkers))) {
            _markerList.value.removeAll(markers.toSet()) //toSet removes all double avail. values
        }
    }
}
