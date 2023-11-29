package project.main.uniclash.viewmodels

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import project.main.uniclash.R
import project.main.uniclash.StudentHubActivity
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.datatypes.MarkerStudentHub
import project.main.uniclash.map.MapCalculations
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService

sealed interface MarkersStudentHubUIState {
    data class HasEntries(
        val markersStudentHub: ArrayList<MarkerData?>,
        val isLoading: Boolean,
    ) : MarkersStudentHubUIState
}

sealed interface MarkersArenaUIState {
    data class HasEntries(
        val makersArenas: ArrayList<MarkerData?>,
        val isLoading: Boolean,
    ) : MarkersArenaUIState
}
class MapMarkerViewModel(
    private val critterService: CritterService,
    private val studentHubService : StudentHubService,
    private val arenaService : ArenaService,
    private val context : Context,
    private val studentHubViewModel: StudentHubViewModel
) : ViewModel() {

    var mapCalculations = MapCalculations()

    val markersStudentHub = MutableStateFlow(
        MarkersStudentHubUIState.HasEntries(
            isLoading = false,
            markersStudentHub =  ArrayList<MarkerData?>()
        )
    )
    val markersArena = MutableStateFlow(
        MarkersArenaUIState.HasEntries(
            isLoading = false,
            makersArenas =   ArrayList<MarkerData?>()
        )
    )

    fun initMarkersStudentHub() {
        if (markersStudentHub.value.isLoading == false) {
            if(studentHubViewModel.studentHubs.value.studentHubs.isNotEmpty()) {
                this@MapMarkerViewModel.markersStudentHub.update {
                    it.copy(
                        isLoading = true,
                    )
                }
            }//TODO Dirty solution to avoid while loop

            val studentHubs = studentHubViewModel.studentHubs.value.studentHubs
            var studentHubMarkerList = ArrayList<MarkerData?>()
            var i = 0
            while (i < studentHubs.size) {
                val studentHub = studentHubs.get(i)
                val geoPoint = GeoPoint(studentHub?.lat!!, studentHub?.lon!!)

                val icon: Drawable? =
                    mapCalculations.resizeDrawable(context, R.drawable.store, 50.0F)

                val myMarker = MarkerStudentHub(
                    state = geoPoint,
                    icon = icon,
                    visible = true,
                    title = "${studentHub?.name}",
                    snippet = "${studentHub?.description}",
                    pic = R.drawable.store,
                    button = StudentHubActivity::class.java,
                    buttonText = "Go to Hub",
                    studentHub = studentHub
                )

                studentHubMarkerList.add(myMarker)
                i++
            }
            this@MapMarkerViewModel.markersStudentHub.update {
                it.copy(
                    //isLoading = false,
                    markersStudentHub = studentHubMarkerList,
                )
            }
            println("size of $studentHubMarkerList in mapMarkerViewmodel")
        }
    }


    fun initMarkersArena(){
        var arenas = arenaService.getArenas()
    }

    fun initWildEncounter(){
    }

    init {
        viewModelScope.launch {
            studentHubViewModel.studentHubs.collect {
                initMarkersStudentHub()
            }
        }
    }



    companion object {
        fun provideFactory(
            critterService: CritterService,
            studentHubService: StudentHubService,
            arenaService : ArenaService,
            context : Context,
            studentHubViewModel: StudentHubViewModel
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MapMarkerViewModel(
                        critterService,
                        studentHubService,
                        arenaService,
                        context,
                        studentHubViewModel,
                    ) as T
                }
            }
    }
}
