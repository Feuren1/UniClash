package project.main.uniclash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberImagePainter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import project.main.uniclash.datatypes.Counter
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.datatypes.MarkerStudent
import project.main.uniclash.datatypes.MarkerWildEncounter
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.map.GeoCodingHelper
import project.main.uniclash.map.LocationPermissions
import project.main.uniclash.map.MapCalculations
import project.main.uniclash.viewmodels.MapMarkerListViewModel
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.StudentService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.MapItemViewModel
import project.main.uniclash.viewmodels.MapLocationViewModel
import project.main.uniclash.viewmodels.MapMarkerViewModel

/*
OpenStreetMap for Android Compose : https://utsmannn.github.io/osm-android-compose/usage/
 */
class MapActivity : ComponentActivity() {
    //is not in onCreate because it is used in complete class => solution would be parameters in methods
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissions = LocationPermissions(this, this)
    private val mapCalculations = MapCalculations() //business logic with were not related to specific class / view model "Single responsibility principle"
    private val geoCodingHelper = GeoCodingHelper(this)

    private val mapLocationViewModel: MapLocationViewModel by viewModels(factoryProducer = {
        MapLocationViewModel.provideFactory(locationPermissions)
    })

    private val mapMarkerViewModel: MapMarkerViewModel by viewModels(factoryProducer = {
        MapMarkerViewModel.provideFactory(CritterService.getInstance(this), StudentHubService.getInstance(this), ArenaService.getInstance(this), StudentService.getInstance(this),this,mapMarkerListViewModel)
    })

    private val mapItemViewModel : MapItemViewModel by viewModels(factoryProducer = {
        MapItemViewModel.provideFactory(InventoryService.getInstance(this))
    })

    private val mapMarkerListViewModel : MapMarkerListViewModel by viewModels(factoryProducer = null)

    private var reloadMap by mutableStateOf(true) //reloads all markers

    private var numberOfMarkersOnMap = 0

    private var startMapRequested by mutableStateOf(false)
    private var mainLatitude: Double by mutableStateOf(Locations.USERLOCATION.getLocation().latitude) //for gps location
    private var mainLongitude: Double by mutableStateOf(Locations.USERLOCATION.getLocation().longitude)//"

    private var movingCamera : Boolean ? = true //if camera follows your location

    private var shouldLoadFirstWildEncounter by mutableStateOf(false)

    private var newCritterNotification by mutableStateOf(11) //time left side at the bottom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //markerList observer and updater
        lifecycleScope.launch {
            mapMarkerListViewModel.mapMarkerList.collect {
               reloadMap = false
                reloadMap = true
            }
        }

        mapLocationViewModel.getUserLocation(this) { location -> //this step could also be in the mapLocationViewModel, updates USERLOCATION ENUM
            Locations.USERLOCATION.setLocation(GeoPoint(location.latitude,location.longitude))
            reloadMap = false //reload necessary otherwise critter visibility will not change if user is moving
            reloadMap = true
        }

        setContent {
            // todo uistate from viewmodel
            if(MapSaver.STUDENTHUB.getMarker().isEmpty()) {
                mapMarkerViewModel.loadStudentHubs()
                val markersStudentHubUIState by mapMarkerViewModel.markersStudentHub.collectAsState()
                val studentHubMarkers = markersStudentHubUIState.markersStudentHub
                mapMarkerListViewModel.addListOfMarkersQ(studentHubMarkers)
                MapSaver.STUDENTHUB.setMarker(studentHubMarkers)
            } else{
                mapMarkerListViewModel.addListOfMarkersQ(MapSaver.STUDENTHUB.getMarker()!!)
            }

            if(MapSaver.STUDENT.getMarker().isEmpty()) {
                mapMarkerViewModel.loadStudents()
                val markersStudentUIState by mapMarkerViewModel.markersStudent.collectAsState()
                val studentMarkers = markersStudentUIState.makersStudent
                mapMarkerListViewModel.addListOfMarkersQ(studentMarkers)
                MapSaver.STUDENT.setMarker(studentMarkers)
            } else{
                mapMarkerListViewModel.addListOfMarkersQ(MapSaver.STUDENT.getMarker()!!)
            }

            if(MapSaver.ARENA.getMarker().isEmpty()) {
                mapMarkerViewModel.loadArenas()
                val markersArenaUIState by mapMarkerViewModel.markersArena.collectAsState()
                val arenaMarkers = markersArenaUIState.makersArena
                mapMarkerListViewModel.addListOfMarkersQ(arenaMarkers)
                MapSaver.ARENA.setMarker(arenaMarkers)
            } else{
                mapMarkerListViewModel.addListOfMarkersQ(MapSaver.ARENA.getMarker()!!)
            }

            if(MapSaver.WILDENCOUNTER.getMarker().isEmpty()) {
                mapMarkerViewModel.loadCritterUsables(1)
                val markersWildEncounterUIState by mapMarkerViewModel.markersWildEncounter.collectAsState()
                val wildEncounterMarkers = markersWildEncounterUIState.markersWildEncounter
                //markerList.addListOfMarkersQ(wildEncounterMarkers) //should be set in LoadFirstWildEncounter
                MapSaver.WILDENCOUNTER.setMarker(wildEncounterMarkers)
            } else if (Counter.FIRSTSPAWN.getCounter() < 1){ //to avoid that wildencounter will spawn before time times to zero
                mapMarkerListViewModel.addListOfMarkersQ(MapSaver.WILDENCOUNTER.getMarker()!!)
            }


            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentUserLocation : MapLocationViewModel.LatandLong = MapLocationViewModel.LatandLong(Locations.USERLOCATION.getLocation().latitude,Locations.USERLOCATION.getLocation().longitude)
                    if (locationPermissions.hasPermissions() || startMapRequested) {
                        //  val currentUserLocation = mapLocationViewModel.getUserLocation(context = LocalContext.current)
                        if(currentUserLocation.latitude != 0.0 && currentUserLocation.longitude != 0.0) {
                            mainLatitude = currentUserLocation.latitude
                            mainLongitude = currentUserLocation.longitude
                        }
                        SettingUpMap()
                        Map()
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val drawableImage = painterResource(id = R.drawable.map)
                            Image(
                                painter = drawableImage,
                                contentDescription = null,
                                modifier = Modifier.size(240.dp)
                            )
                            Text(
                                text = "\nLocation permissions are not granted :(",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "You have to accept the location permissions to start the app.\n",
                                textAlign = TextAlign.Center
                            )

                            // Add a button to request location permissions and start the map
                            Button(onClick = {
                                locationPermissions.requestLocationPermissions()
                                if (locationPermissions.hasPermissions()) {
                                    startMapRequested = true
                                }
                            }) {
                                Text(text = "Go to map")
                            }
                        }
                    }
                }
            }
        }
    }

    lateinit var gpsLocation : MarkerState //arrow marker
    lateinit var cameraState : CameraState //camera (how you see the map)
    var alreadySetedUpMap = false
    @Composable
    fun SettingUpMap() {
        if(!alreadySetedUpMap)
         gpsLocation = rememberMarkerState()
         cameraState = rememberCameraState()
        cameraState.zoom = 20.0
        alreadySetedUpMap = true
    }

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun Map() {
        LoadFirstWildEncounter()

        val contextForLocation = LocalContext.current

        LaunchedEffect(Unit) {
            while (true) {
                mapLocationViewModel.getUserLocation(contextForLocation) { location -> //has to checked here too, because in line 120 is in onCreate (only executed once).
                    mainLatitude = location.latitude
                    mainLongitude = location.longitude
                }

                val tolerance = 0.0001 //0.0001 before

                if (Math.abs(mainLatitude - cameraState.geoPoint.latitude) > tolerance || Math.abs(
                        mainLongitude - cameraState.geoPoint.longitude
                    ) > tolerance
                ) {
                    Log.d(
                        LOCATION_TAG,
                        "$mainLatitude and ${cameraState.geoPoint.latitude} ---- $mainLongitude and ${cameraState.geoPoint.longitude}"
                    )
                    gpsLocation.geoPoint = GeoPoint(mainLatitude, mainLongitude)
                    gpsLocation.rotation = mapCalculations.calculateDirection(GeoPoint(cameraState.geoPoint.latitude,cameraState.geoPoint.longitude), GeoPoint(mainLatitude,mainLongitude))+270F
                    if (MapSettings.MOVINGCAMERA.getMapSetting()) {
                        cameraState.geoPoint = GeoPoint(mainLatitude,mainLongitude)
                    } else if (movingCamera == true) {
                        cameraState.geoPoint = GeoPoint(mainLatitude,mainLongitude)
                        movingCamera = false
                    }
                }
                delay(1000) //1sec. //a delay will not froze the complete application

                mapMarkerViewModel.counterLogic()
                if (Counter.FIRSTSPAWN.getCounter() < 1) {
                    shouldLoadFirstWildEncounter = true
                }
                newCritterNotification = Counter.RESPAWN.getCounter()


                //in case of markers were not loaded successfully, the map activity will completely reload.
                if(Counter.RESPAWN.getCounter()<1){
                    numberOfMarkersOnMap = 0
                }

                if(Counter.RESPAWN.getCounter() % 20 == 0){
                    mapMarkerListViewModel.removeMarkersQ(MapSaver.STUDENT.getMarker())
                    MapSaver.STUDENT.setMarker(ArrayList<MarkerData?>())
                    mapMarkerViewModel.students.value.students = emptyList()
                    mapMarkerViewModel.loadStudents()
                }
                if(Counter.RESPAWN.getCounter() % 20 == 2){
                    mapMarkerListViewModel.removeMarkersQ(MapSaver.STUDENT.getMarker())
                    mapMarkerListViewModel.addListOfMarkersQ(MapSaver.STUDENT.getMarker())
                }

                //is now working without OpenActivityButton() and could be move out to view model
                if(numberOfMarkersOnMap < 800 && Counter.RESPAWN.getCounter()>5&&Counter.RESPAWN.getCounter()<295){
                    println("complete map reload")
                    mapMarkerListViewModel.removeMarkersQ(MapSaver.WILDENCOUNTER.getMarker())
                    mapMarkerListViewModel.addListOfMarkersQ(MapSaver.WILDENCOUNTER.getMarker())
                    //OpenActivityButton(MapActivity::class.java)
                }

            }
        }

        val context = LocalContext.current

        // define marker icon
        val arrow: Drawable? by remember {
            mutableStateOf(mapCalculations.resizeDrawable(context, R.drawable.arrow, 50.0F))
        }

        val markerList by mapMarkerListViewModel.mapMarkerList.collectAsState()

        var mapProperties by remember {
            mutableStateOf(DefaultMapProperties)
        }

        // setup mapProperties in side effect
        SideEffect {
            mapProperties = mapProperties
                .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
        }

        // Use camera state and location in your OpenStreetMap Composable
            OpenStreetMap(
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState,
                properties = mapProperties
            ) {
                if (reloadMap) {
                var critterVisibility : Int
                if(MapSettings.CRITTERBINOCULARS.getMapSetting()){
                    critterVisibility = 1000
                } else {
                    critterVisibility = 250
                }

                // Add markers and other map components here s)
                    markerList.markerList.forEach() { marker ->
                    val distance = mapCalculations.distance(marker.state.latitude, marker.state.longitude, Locations.USERLOCATION.getLocation().latitude, Locations.USERLOCATION.getLocation().longitude)
                    Log.d(
                        LOCATION_TAG,
                        "set marker"
                    )

                    val state = rememberMarkerState(
                        geoPoint = marker.state
                        )

                        numberOfMarkersOnMap ++

                    Marker(
                        state = state,
                        icon = marker.icon,
                        title = marker.title,
                        snippet = marker.snippet,
                        visible = if(marker is MarkerWildEncounter && distance > critterVisibility){false}else{marker.visible},
                    ) {
                        if (distance < 501 || marker is MarkerStudent) {
                            Column(
                                modifier = Modifier
                                    .size(325.dp, 400.dp)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.75f),
                                        shape = RoundedCornerShape(7.dp)
                                    ),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = marker.title!!, fontSize = 20.sp, color = Color.White)
                                Text(text = marker.snippet!!, fontSize = 15.sp, color = Color.White)
                                Text(text = "${geoCodingHelper.getAddressFromLocation(marker.state.latitude,marker.state.longitude)}", fontSize = 15.sp, color = Color.White)
                                Spacer(modifier = Modifier.height(8.dp))
                                Image(
                                    painter = rememberImagePainter(marker.pic),
                                    contentDescription = null, // Provide a proper content description if needed
                                    modifier = Modifier.size(235.dp) // Adjust size as needed
                                )
                                OpenActivityButton(marker)
                            }
                        }
                    }
                }
                Marker(
                    state = gpsLocation,
                    icon = arrow,
                )
            }
        }
        MenuTaskBar()
    }

    private var openFartSprayInfo by mutableStateOf(false)
    @Composable
    fun MenuTaskBar() { //Includes complete graphic gui above the map, not the map and makes themself
        FartSprayInfo()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(16.dp))

                    // Timer
                        Image(
                            painter = painterResource(id = R.drawable.hourglass),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .offset(x = 130.dp)
                        )
                        var minutes: Int = newCritterNotification / 60 //20
                        Text(//newCritterNotification *3 = seconds
                            text = if (newCritterNotification < 60 && newCritterNotification > -1) { //20
                                "${newCritterNotification * 1} sec" //3
                            } else if(newCritterNotification <60){
                                "0 sec"
                            } else {
                                "${minutes}:${newCritterNotification * 1 - minutes * 60}min" //3
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(end = 24.dp)
                                .offset(x = (15).dp)
                        )
                }
            }
            val drawableImage = painterResource(id = R.drawable.profile)
            val drawableBinoculars = painterResource(id = R.drawable.binoculars)
            val drawableLocation = painterResource(id = R.drawable.location)
            val drawableLocationGray = painterResource(id = R.drawable.locationgray)
            val drawableZoomIn = painterResource(id = R.drawable.zoom)
            val drawableZoomOut = painterResource(id = R.drawable.zoomout)
            val fartSpray = painterResource(id = R.drawable.fartspray)
            Image(
                painter = drawableImage,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = (-10).dp)
                    .clickable { OpenActivityButton(MenuActivity::class.java) }
            )
            Image(
                painter = drawableBinoculars,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .offset(x = (130).dp)
                    .offset(y = (-5).dp)
                    .clickable {
                        MapSettings.CRITTERBINOCULARS.setMapSetting(!MapSettings.CRITTERBINOCULARS.getMapSetting())
                        reloadMap = false
                        reloadMap = true
                    }
            )
            Image(
                painter = if(MapSettings.MOVINGCAMERA.getMapSetting()){drawableLocation}else{drawableLocationGray},
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .offset(x = (180).dp)
                    .offset(y = (-5).dp)
                    .clickable {
                        MapSettings.MOVINGCAMERA.setMapSetting(!MapSettings.MOVINGCAMERA.getMapSetting())
                    }
            )
            if(Counter.FIRSTSPAWN.getCounter()<1&&Counter.RESPAWN.getCounter()<280&&Counter.RESPAWN.getCounter()>5) {
                Image(
                    painter = fartSpray,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopEnd)
                        .clickable {
                            openFartSprayInfo = true
                        }
                )
            }

            if(!openFartSprayInfo) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Image(
                        painter = drawableZoomIn,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .offset(y = (-5).dp)
                            .clickable { cameraState.zoom = cameraState.zoom + 1.0 }
                    )
                    Image(
                        painter = drawableZoomOut,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .offset(y = (-5).dp)
                            .clickable { cameraState.zoom = cameraState.zoom - 1.0 }
                    )
                }
            }
        }
    }

    @Composable
    fun FartSprayInfo() {
        val usedItem = mapItemViewModel.useFartSpray.collectAsState()
        if(usedItem.value.canBeUsed == 0){
            Toast.makeText(baseContext, "No fart spray was used.", Toast.LENGTH_SHORT).show()
            mapItemViewModel.resetCanBeUsedValue()
        } else if (usedItem.value.canBeUsed == 1) {
            Toast.makeText(baseContext, "You used a fart spray.", Toast.LENGTH_SHORT).show()
            mapItemViewModel.resetCanBeUsedValue()
        }

        if (openFartSprayInfo) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                ) {
                    Row(modifier = Modifier.padding(all = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.fartspray),
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                        )
                        Text(
                            text = "Use a fart spray to respawn all critters.",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            mapItemViewModel.useFartSpray()
                            openFartSprayInfo = false
                            },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "Use spray")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { openFartSprayInfo = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "No thanks")
                    }
                }
            }
        }
    }

    @Composable
    fun OpenActivityButton(marker : MarkerData) {
        val context = LocalContext.current
        val distance = mapCalculations.distance(marker.state.latitude, marker.state.longitude, Locations.USERLOCATION.getLocation().latitude, Locations.USERLOCATION.getLocation().longitude)
        if(distance < 76) {
            Button(
                onClick = {
                    // Handle the button click to open the new activity here
                    SelectedMarker.SELECTEDMARKER.setMarker(marker)
                    val intent = Intent(context,marker.button)
                    this.startActivity(intent, null)
                    finish()
                },
                modifier = Modifier
                    .padding(2.dp)
                    .width(200.dp)
                    .height(50.dp)

            ) {
                Text("${marker.buttonText}")
            }
        } else {
            Text(text ="to far away", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }

    private fun OpenActivityButton(activity: Class<out Activity> = MenuActivity::class.java) {
                // Handle the button click to open the new activity here
                val intent = Intent(this,activity)
                this.startActivity(intent, null)
                finish()
    }

    var alreadyLoaded = false //for LoadWildEncounter firstLoad
    //@SuppressLint("SuspiciousIndentation")
    @Composable
    fun LoadFirstWildEncounter(){
        if(shouldLoadFirstWildEncounter) {
            if(alreadyLoaded == false) {
                Log.d(LOCATION_TAG, "Executed first loadwildencounter")
                     mapMarkerListViewModel.addListOfMarkersQ(MapSaver.WILDENCOUNTER.getMarker())
                shouldLoadFirstWildEncounter = false
                if(MapSaver.WILDENCOUNTER.getMarker().isEmpty()){
                    mapMarkerViewModel.loadCritterUsables(1)
                    Counter.FIRSTSPAWN.setCounter(5)
                } else {
                    alreadyLoaded = true
                }
            }
        }
    }

    companion object {
        private const val LOCATION_TAG = "MyLocationTag"
    }
}