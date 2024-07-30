package project.main.uniclash.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.MarkerData


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

    fun addMarker(marker: MarkerData) {
        if (!mapMarkerList.value.markerList.contains(marker)) {
        viewModelScope.launch {
            mapMarkerList.update {
                it.copy(isLoading =  false, markerList = ArrayList(mapMarkerList.value.markerList+marker))
            }
        }
        }
    }

    fun addListOfMarkers(markers: ArrayList<MarkerData>) {
        if (!(mapMarkerList.value.markerList.containsAll(markers))) {
            viewModelScope.launch{
                mapMarkerList.update {
                    it.copy(isLoading = false, markerList = ArrayList(mapMarkerList.value.markerList+markers))
                }
            }
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
        }
    }

    fun removeMarker(marker: MarkerData) {
        viewModelScope.launch {
            mapMarkerList.update {
                it.copy(isLoading =  false, markerList = ArrayList(mapMarkerList.value.markerList-marker))
            }
        }
    }

    fun removeMarkersQ(markers: ArrayList<MarkerData?>){
        val convertedMarkers = markers.filterNotNull()
        if (!(convertedMarkers.containsAll(mapMarkerList.value.markerList))) {
            mapMarkerList.update {
                it.copy(isLoading =  false, markerList = ArrayList(mapMarkerList.value.markerList-convertedMarkers))
            }
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
