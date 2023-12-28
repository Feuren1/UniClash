package project.main.uniclash.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService


sealed interface MapMarkerListUIState {
    data class HasEntries(
        val markerList: ArrayList<MarkerData>,
        val isLoading : Boolean
    ) : MapMarkerListUIState
}
class MapMarkerListViewModel : ViewModel() {

    val mapMarkerList = MutableStateFlow(
        MapMarkerListUIState.HasEntries(
            markerList = arrayListOf(),
            isLoading = false
        )
    )

        //val markerList = MutableStateFlow(ArrayList<MarkerData>())
    //val markerList = _markerList

    /*
    //Objects has to be "recreated" other the observer will do nothing.
     */

    fun addMarker(marker: MarkerData) {
        if (!mapMarkerList.value.markerList.contains(marker)) {
        viewModelScope.launch {
            mapMarkerList.update {
                it.copy(isLoading =  false, markerList = ArrayList(mapMarkerList.value.markerList+marker))
            }
        }
           // markerList.value = ArrayList(markerList.value + marker)
        }
    }

    fun addListOfMarkers(markers: ArrayList<MarkerData>) {
        if (!(mapMarkerList.value.markerList.containsAll(markers))) {
            viewModelScope.launch{
                mapMarkerList.update {
                    it.copy(isLoading = false, markerList = ArrayList(mapMarkerList.value.markerList+markers))
                }
            }
            //markerList.value = ArrayList(markerList.value + markers)
        }
    }

    fun addListOfMarkersQ(markers: ArrayList<MarkerData?>) {
        val convertedMarkers = markers.filterNotNull()

        if (!(mapMarkerList.value.markerList.containsAll(convertedMarkers))) {
            viewModelScope.launch {
                mapMarkerList.update {
                    it.copy(isLoading = false, markerList = ArrayList(mapMarkerList.value.markerList+convertedMarkers))
                }
            }
            //markerList.value = ArrayList(markerList.value + convertedMarkers)
        }
    }

    fun removeMarker(marker: MarkerData) {
        viewModelScope.launch {
            mapMarkerList.update {
                it.copy(isLoading =  false, markerList = ArrayList(mapMarkerList.value.markerList-marker))
            }
        }
        //markerList.value = ArrayList(markerList.value - marker)
    }

    fun removeMarkersQ(markers: ArrayList<MarkerData?>){
        val convertedMarkers = markers.filterNotNull()
        if (!(convertedMarkers.containsAll(mapMarkerList.value.markerList))) {
            mapMarkerList.update {
                it.copy(isLoading =  false, markerList = ArrayList(mapMarkerList.value.markerList-convertedMarkers))
            }
            //markerList.value.removeAll(markers) //toSet removes all double avail. values
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MapMarkerListViewModel() as T
                }
            }
    }
}
