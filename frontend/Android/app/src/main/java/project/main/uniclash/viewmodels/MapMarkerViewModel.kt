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
import project.main.uniclash.ArenaActivity
import project.main.uniclash.R
import project.main.uniclash.StudentHubActivity
import project.main.uniclash.WildEncounterActivity
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MarkerArena
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.datatypes.MarkerStudentHub
import project.main.uniclash.datatypes.MarkerWildEncounter
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
        val makersArena: ArrayList<MarkerData?>,
        val isLoading: Boolean,
    ) : MarkersArenaUIState
}

sealed interface MarkersWildEncounterUIState {
    data class HasEntries(
        val markersWildEncounter: ArrayList<MarkerData?>,
        val isLoading: Boolean,
    ) : MarkersWildEncounterUIState
}
class MapMarkerViewModel(
    private val critterService: CritterService,
    private val studentHubService : StudentHubService,
    private val arenaService : ArenaService,
    private val context : Context,
    private val studentHubViewModel: StudentHubViewModel,
    private val arenaViewModel : ArenaViewModel,
    private val critterViewModel: UniClashViewModel
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
            makersArena =   ArrayList<MarkerData?>()
        )
    )

    val markersWildEncounter = MutableStateFlow(
        MarkersWildEncounterUIState.HasEntries(
            isLoading = false,
            markersWildEncounter = ArrayList<MarkerData?>()
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
        }
    }


    fun initMarkersArena(){
        if (markersArena.value.isLoading == false) {
            if(arenaViewModel.arenas.value.arenas.isNotEmpty()) {
                this@MapMarkerViewModel.markersArena.update {
                    it.copy(
                        isLoading = true,
                    )
                }
            }//TODO Dirty solution to avoid while loop

            val arenas = arenaViewModel.arenas.value.arenas
            var arenasMarkerList = ArrayList<MarkerData?>()
            var i = 0
            while (i < arenas.size) {
                val arena = arenas.get(i)
                val geoPoint = GeoPoint(arena?.lat!!, arena?.lon!!)

                val icon: Drawable? =
                    mapCalculations.resizeDrawable(context, R.drawable.arena, 50.0F)

                val myMarker = MarkerArena(
                    state = geoPoint,
                    icon = icon,
                    visible = true,
                    title = "${arena?.name}",
                    snippet = "${arena?.description}",
                    pic = R.drawable.arena,
                    button = ArenaActivity::class.java,
                    buttonText = "Enter arena",
                    arena = arena
                )

                arenasMarkerList.add(myMarker)
                i++
            }
            this@MapMarkerViewModel.markersArena.update {
                it.copy(
                    //isLoading = false,
                    makersArena = arenasMarkerList,
                )
            }
        }
    }

    fun initWildEncounter(){
        if(markersArena.value.isLoading == false){
            if(critterViewModel.critterUsables.value.critterUsables.isNotEmpty()){
                this@MapMarkerViewModel.markersWildEncounter.update {
                    it.copy(
                        isLoading = true,
                    )
                }
            }//TODO Dirty solution to avoid while loop
            var wildEncounterMarkerList = ArrayList<MarkerData?>()
            val usableCritters = critterViewModel.critterUsables.value.critterUsables
            var mapCalculations = MapCalculations()
            var wildEncounterMax = usableCritters
            while(wildEncounterMax.size < 801){
                wildEncounterMax = wildEncounterMax + usableCritters
            }
            val userLocation = Locations.USERLOCATION.getLocation()
            if(MapSaver.WILDENCOUNTER.getMarker().isEmpty()) {
                var randomLocation = generateRandomGeoPoints(userLocation, 2.0, 800) //400 per km
                var i = 0
                val wildEncounter = wildEncounterMax

                while (i < 800) {
                    var myMarker = MarkerWildEncounter(
                        state = GeoPoint(randomLocation.get(i).latitude, randomLocation.get(i).longitude),
                        icon = mapCalculations.resizeDrawable(context, CritterPic.MUSK.searchDrawableM("${wildEncounter.get(i)?.name}M"),50.0F),
                        visible = true,
                        title = "${wildEncounter.get(i)?.name}",
                        snippet = "Level: ${wildEncounter.get(i)?.level}",
                        pic = CritterPic.MUSK.searchDrawable("${wildEncounter.get(i)?.name}"),
                        button = WildEncounterActivity::class.java,
                        buttonText = "catch Critter",
                        critterUsable = wildEncounter.get(i)
                    )
                    wildEncounterMarkerList.add(myMarker)
                    i++
                }
                // add more markers
                MapSaver.WILDENCOUNTER.setMarker(wildEncounterMarkerList)
            } else {
                wildEncounterMarkerList = MapSaver.WILDENCOUNTER.getMarker()
            }

            this@MapMarkerViewModel.markersWildEncounter.update {
                it.copy(
                    //isLoading = false,
                    markersWildEncounter = wildEncounterMarkerList,
                )
            }
        }
    }

    private fun generateRandomGeoPoints(center: GeoPoint, radiusInKm: Double, times : Int): ArrayList<GeoPoint> {
        val random = java.util.Random()
        var counter = times
        var geoLocations = ArrayList<GeoPoint>()
        while (counter > 0) {
            // Convert radius from kilometers to degrees
            val radiusInDegrees = radiusInKm / 111.32

            val u = random.nextDouble()
            val v = random.nextDouble()
            val w = radiusInDegrees * Math.sqrt(u)
            val t = 2.0 * Math.PI * v
            val x = w * Math.cos(t)
            val y = w * Math.sin(t)

            // Adjust the x-coordinate for the shrinking of the east-west distances
            val new_x = x / Math.cos(Math.toRadians(center.latitude))

            val newLongitude = new_x + center.longitude
            val newLatitude = y + center.latitude
            geoLocations.add(GeoPoint(newLatitude, newLongitude))
            counter--
        }
        return geoLocations
    }

    init {
        viewModelScope.launch {
            studentHubViewModel.studentHubs.collect {
                initMarkersStudentHub()
            }
        }
        viewModelScope.launch {
            arenaViewModel.arenas.collect {
                initMarkersArena()
            }
        }

        viewModelScope.launch {
            critterViewModel.critterUsables.collect{
                //initWildEncounter()
            }
        }
    }



    companion object {
        fun provideFactory(
            critterService: CritterService,
            studentHubService: StudentHubService,
            arenaService : ArenaService,
            context : Context,
            studentHubViewModel: StudentHubViewModel,
            arenaViewModel: ArenaViewModel,
            critterViewModel : UniClashViewModel
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
                        arenaViewModel,
                        critterViewModel
                    ) as T
                }
            }
    }
}
