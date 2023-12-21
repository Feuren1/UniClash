package project.main.uniclash.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.compose.ui.platform.LocalContext
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
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MarkerArena
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.datatypes.MarkerStudentHub
import project.main.uniclash.datatypes.MarkerWildEncounter
import project.main.uniclash.datatypes.StudentHub
import project.main.uniclash.map.MapCalculations
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.enqueue

sealed interface MarkersStudentHubUIState {
    data class HasEntries(
        val markersStudentHub: ArrayList<MarkerData?>,
        val isLoading: Boolean,
    ) : MarkersStudentHubUIState
}

sealed interface StudentHubsForMapUIState {
    data class HasEntries(
        val studentHubs: List<StudentHub>,
        val isLoading: Boolean,
    ) : StudentHubsForMapUIState
}

sealed interface MarkersArenaUIState {
    data class HasEntries(
        val makersArena: ArrayList<MarkerData?>,
        val isLoading: Boolean,
    ) : MarkersArenaUIState
}

sealed interface ArenasForMapUIState {
    data class HasEntries(
        val arenas: List<Arena?>,
        val isLoading: Boolean,
    ) : ArenasForMapUIState
}

sealed interface MarkersWildEncounterUIState {
    data class HasEntries(
        val markersWildEncounter: ArrayList<MarkerData?>,
        val isLoading: Boolean,
    ) : MarkersWildEncounterUIState
}

sealed interface CritterUsablesForMapUIState {
    data class HasEntries(
        val critterUsables: List<CritterUsable?>,
        val isLoading: Boolean,
    ) : CritterUsablesForMapUIState
}
class MapMarkerViewModel(
    private val critterService: CritterService,
    private val studentHubService : StudentHubService,
    private val arenaService : ArenaService,
    private val context : Context,
) : ViewModel() {

    var mapCalculations = MapCalculations()

    val markersStudentHub = MutableStateFlow(
        MarkersStudentHubUIState.HasEntries(
            isLoading = false,
            markersStudentHub =  ArrayList<MarkerData?>()
        )
    )

    val studentHubs = MutableStateFlow(
        StudentHubsForMapUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    val markersArena = MutableStateFlow(
        MarkersArenaUIState.HasEntries(
            isLoading = false,
            makersArena =   ArrayList<MarkerData?>()
        )
    )

    val arenas = MutableStateFlow(
        ArenasForMapUIState.HasEntries(
            arenas = emptyList(),
            isLoading = false,
        )
    )

    val critterUsables = MutableStateFlow(
        CritterUsablesForMapUIState.HasEntries(
            emptyList(),
            isLoading = false
        )
    )

    val markersWildEncounter = MutableStateFlow(
        MarkersWildEncounterUIState.HasEntries(
            isLoading = false,
            markersWildEncounter = ArrayList<MarkerData?>()
        )
    )

    fun loadStudentHubs() {
        println("try to load hubs")
        viewModelScope.launch {
            studentHubs.update { it.copy(isLoading = true) }
            try {
                val response = studentHubService.getStudentHubs().enqueue()
                if (response.isSuccessful) {
                    //creates an item list based on the fetched data
                    val studentHubs = response.body()!!
                    //replaces the critters list inside the UI state with the fetched data
                    this@MapMarkerViewModel.studentHubs.update {
                        it.copy(
                            studentHubs = studentHubs,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun initMarkersStudentHub() {
            if(studentHubs.value.studentHubs.isNotEmpty()) {
                this@MapMarkerViewModel.markersStudentHub.update {
                    it.copy(
                        isLoading = true,
                    )
                }

            val studentHubs = studentHubs.value.studentHubs
            var studentHubMarkerList = ArrayList<MarkerData?>()
            var i = 0
            while (i < studentHubs.size) {
                val studentHub = studentHubs.get(i)
                val geoPoint = GeoPoint(studentHub?.lat!!, studentHub?.lon!!)

                val icon: Drawable? =
                    mapCalculations.resizeDrawable(context, R.drawable.store, 50.0F)

                val base64EncodedBitmap = studentHub.picture
                val decodedBytes: ByteArray = Base64.decode(base64EncodedBitmap, Base64.DEFAULT)
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                val bitmapDrawable = BitmapDrawable(Resources.getSystem(), bitmap)

                val myMarker = MarkerStudentHub(
                    state = geoPoint,
                    icon = icon,
                    visible = true,
                    title = "${studentHub?.name}",
                    snippet = "${studentHub?.description}",
                    pic = bitmapDrawable,
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


    fun loadArenas(){
        viewModelScope.launch {
            arenas.update {it.copy(isLoading = true)  }
            try {
                val response = arenaService.getArenas().enqueue()
                if (response.isSuccessful) {
                    val arenas = response.body()!!
                    this@MapMarkerViewModel.arenas.update {
                        it.copy(
                            arenas = arenas,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun initMarkersArena(){
            if(arenas.value.arenas.isNotEmpty()) {
                this@MapMarkerViewModel.markersArena.update {
                    it.copy(
                        isLoading = true,
                    )
                }

            val arenas = arenas.value.arenas
            var arenasMarkerList = ArrayList<MarkerData?>()
            var i = 0
            while (i < arenas.size) {
                val arena = arenas.get(i)
                val geoPoint = GeoPoint(arena?.lat!!, arena?.lon!!)

                val icon: Drawable? =
                    mapCalculations.resizeDrawable(context, R.drawable.arena, 50.0F)

                val base64EncodedBitmap = arena.picture
                val decodedBytes: ByteArray = Base64.decode(base64EncodedBitmap, Base64.DEFAULT)
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                val bitmapDrawable = BitmapDrawable(Resources.getSystem(), bitmap)

                val myMarker = MarkerArena(
                    state = geoPoint,
                    icon = icon,
                    visible = true,
                    title = "${arena?.name}",
                    snippet = "${arena?.description}",
                    pic = bitmapDrawable,
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

    @SuppressLint("MissingPermission")
    fun loadCritterUsables(id: Int) {
        viewModelScope.launch {
            critterUsables.update { it.copy(isLoading = true) }
            try {
                val response = critterService.getCritterUsables(id).enqueue()
                if (response.isSuccessful) {
                    val crittersUsables = response.body()!!
                    critterUsables.update {
                        it.copy(
                            critterUsables = crittersUsables,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun initWildEncounter(){
            if(critterUsables.value.critterUsables.isNotEmpty()){
                this@MapMarkerViewModel.markersWildEncounter.update {
                    it.copy(
                        isLoading = true,
                    )
                }
            var wildEncounterMarkerList = ArrayList<MarkerData?>()
            val usableCritters = critterUsables.value.critterUsables
            var mapCalculations = MapCalculations()
            var wildEncounterMax = usableCritters
            while(wildEncounterMax.size < 901 && wildEncounterMax.isNotEmpty()){
                wildEncounterMax = wildEncounterMax + usableCritters
            }
            val userLocation = Locations.USERLOCATION.getLocation()
                Locations.INTERSECTION.setLocation(Locations.USERLOCATION.getLocation())

            if(MapSaver.WILDENCOUNTER.getMarker().isEmpty() &&wildEncounterMax.isNotEmpty()) {
                var randomLocation = generateRandomGeoPoints(userLocation, 2.0, 900) //400 per km
                var i = 0
                val wildEncounter = wildEncounterMax

                while (i < 800) {

                    val name: String = wildEncounter.get(i)?.name!!.lowercase()
                    val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
                    val resourceIdM = context.resources.getIdentifier(name+"m", "drawable", context.packageName)

                    var myMarker = MarkerWildEncounter(
                        state = GeoPoint(randomLocation.get(i).latitude, randomLocation.get(i).longitude),
                        icon = mapCalculations.resizeDrawable(context, if(resourceIdM > 0){resourceIdM}else{R.drawable.icon},50.0F),
                        visible = true,
                        title = "${wildEncounter.get(i)?.name}",
                        snippet = "Level: ${wildEncounter.get(i)?.level}",
                        pic =  context.getDrawable(if(resourceId > 0){resourceId}else{R.drawable.icon}),
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
            studentHubs.collect {
                initMarkersStudentHub()
            }
        }
        viewModelScope.launch {
            arenas.collect {
                initMarkersArena()
            }
        }

        viewModelScope.launch {
            critterUsables.collect{
                initWildEncounter()
            }
        }
    }



    companion object {
        fun provideFactory(
            critterService: CritterService,
            studentHubService: StudentHubService,
            arenaService : ArenaService,
            context : Context,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MapMarkerViewModel(
                        critterService,
                        studentHubService,
                        arenaService,
                        context,
                    ) as T
                }
            }
    }
}
