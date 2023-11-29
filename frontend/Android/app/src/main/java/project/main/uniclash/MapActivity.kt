package project.main.uniclash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import kotlinx.coroutines.delay
import org.osmdroid.util.GeoPoint
import project.main.uniclash.datatypes.Counter
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.datatypes.MarkerStudentHub
import project.main.uniclash.datatypes.MarkerWildEncounter
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.datatypes.StudentHub
import project.main.uniclash.map.LocationPermissions
import project.main.uniclash.map.MapCalculations
import project.main.uniclash.map.WildEncounterLogic
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.MapLocationViewModel
import project.main.uniclash.viewmodels.StudentHubViewModel
import project.main.uniclash.viewmodels.UniClashViewModel
import java.util.concurrent.TimeUnit

class MapActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissions = LocationPermissions(this, this)
    private val mapCalculations = MapCalculations()

    private val mapLocationViewModel: MapLocationViewModel by viewModels(factoryProducer = {
        MapLocationViewModel.provideFactory(locationPermissions)
    })

    private var startMapRequested by mutableStateOf(false)
    private var mainLatitude: Double by mutableStateOf(Locations.USERLOCATION.getLocation().latitude) //for gps location
    private var mainLongitude: Double by mutableStateOf(Locations.USERLOCATION.getLocation().longitude)//"

    private var markerList = ArrayList<MarkerData>()
    private var markersLoaded by mutableStateOf(false)
    private var movingCamera : Boolean ? = true

    private var shouldLoadFirstWildEncounter by mutableStateOf(false)
    private var shouldLoadWildEncounter by mutableStateOf(false)

    private var newCritterNotification by mutableStateOf(11)

    private var shouldLoadStudentHubs by mutableStateOf(false)
    private var shouldInitStudentHubs by mutableStateOf(false)
    private var alreadyLoadStudentHubs by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapLocationViewModel.getUserLocation(this) { location ->
            Locations.USERLOCATION.setLocation(GeoPoint(location.latitude,location.longitude))
        }

        setContent {
            // todo uistate from viewmodel

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
                        Map()
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val drawableImage = painterResource(id = R.drawable.icon)
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

    lateinit var gpsLocation : MarkerState
    lateinit var cameraState : CameraState
    var alreadySetedUpMap = false
    @Composable
    fun SettingUpMap() {
        if(!alreadySetedUpMap)
         gpsLocation = rememberMarkerState()
         cameraState = rememberCameraState()
        alreadySetedUpMap = true
    }

    @Composable
    fun Map() {
        LoadFirstWildEncounter()
        LoadWildEncounter()
        LoadStudentHubs()
        InitStudentHubMarker()

        SettingUpMap()

        val contextForLocation = LocalContext.current

        LaunchedEffect(Unit) {
            while (true) {

                mapLocationViewModel.getUserLocation(contextForLocation) { location ->
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
                        cameraState.geoPoint = GeoPoint(mainLatitude, mainLongitude)
                        cameraState.zoom = 20.0
                    } else if (movingCamera == true) {
                        cameraState.geoPoint = GeoPoint(mainLatitude, mainLongitude)
                        cameraState.zoom = 20.0
                        movingCamera = false
                    }
                }
                delay(3000) //3sec.
                Counter.FIRSTSPAWN.minusCounter(1)
                Counter.WILDENCOUNTERREFRESHER.minusCounter(1)

                if (Counter.FIRSTSPAWN.getCounter() < 1) {
                    shouldLoadFirstWildEncounter = true
                }

                if(Counter.WILDENCOUNTERREFRESHER.getCounter() <1){
                    MapSaver.WILDENCOUNTER.setMarker(null)
                    shouldLoadWildEncounter = true
                    Counter.WILDENCOUNTERREFRESHER.setCounter(60)
                }
                newCritterNotification = Counter.WILDENCOUNTERREFRESHER.getCounter()

                shouldLoadStudentHubs = true

                if(MapSaver.STUDENTHUB.getMarker() == null){
                    shouldInitStudentHubs = true
                } else if(MapSaver.STUDENTHUB.getMarker()!!.isEmpty()){
                    shouldInitStudentHubs = true
                }
            }
        }

        val context = LocalContext.current

        // define marker icon
        val arrow: Drawable? by remember {
            mutableStateOf(mapCalculations.resizeDrawableTo50x50(context, R.drawable.arrow, 50.0F))
        }

        // Use camera state and location in your OpenStreetMap Composable
        if (!markersLoaded) {
            var critterVisibility : Int
            if(MapSettings.CRITTERBINOCULARS.getMapSetting()){
                critterVisibility = 1000
            } else {
                critterVisibility = 250
            }

            OpenStreetMap(
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState
            ) {
                // Add markers and other map components here s)
                markerList.forEach { marker ->
                    val distance = mapCalculations.haversineDistance(marker.state.latitude, marker.state.longitude, Locations.USERLOCATION.getLocation().latitude, Locations.USERLOCATION.getLocation().longitude)
                    Log.d(
                        LOCATION_TAG,
                        "set marker"
                    )

                    val state = rememberMarkerState(
                        geoPoint = marker.state
                        )

                    Marker(
                        state = state,
                        icon = marker.icon,
                        title = marker.title,
                        snippet = marker.snippet,
                        visible = if(marker is MarkerWildEncounter && distance > critterVisibility){false}else{marker.visible},
                    ) {
                        if (distance < 501) {
                            Column(
                                modifier = Modifier
                                    .size(340.dp)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.75f),
                                        shape = RoundedCornerShape(7.dp)
                                    ),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = marker.title!!, fontSize = 20.sp, color = Color.White)
                                Text(text = marker.snippet!!, fontSize = 15.sp, color = Color.White)
                                Spacer(modifier = Modifier.height(8.dp))
                                val drawableImage = painterResource(id = marker.pic)
                                Image(
                                    painter = drawableImage,
                                    contentDescription = null, // Provide a proper content description if needed
                                    modifier = Modifier.size(220.dp) // Adjust size as needed
                                )
                                OpenActivityButton(marker)
                            }
                        }
                    }
                }
                Marker(
                    state = gpsLocation,
                    icon = arrow,
                    title = "Go to your profile",
                    snippet = ":)"
                ) {
                    Column(
                        modifier = Modifier
                            .size(250.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(7.dp)
                            ),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = it.title, fontSize = 20.sp, color = Color.White)
                        Text(text = it.snippet, fontSize = 15.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))

                        OpenActivityButton(MenuActivity::class.java, "User Menu")
                    }
                }
            }
        }
        NewCrittersAdvice()
    }

    @Composable
    fun OpenActivityButton(marker : MarkerData) {
        val context = LocalContext.current
        val distance = mapCalculations.haversineDistance(marker.state.latitude, marker.state.longitude, Locations.USERLOCATION.getLocation().latitude, Locations.USERLOCATION.getLocation().longitude)
        if(distance < 76) {
            Button(
                onClick = {
                    // Handle the button click to open the new activity here
                    SelectedMarker.SELECTEDMARKER.setMarker(marker)
                    val intent = Intent(context,marker.button)
                    this.startActivity(intent, null)
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

    @Composable
    fun OpenActivityButton(activity: Class<out Activity> = MenuActivity::class.java, text  : String) {
        val context = LocalContext.current
        Button(
            onClick = {
                // Handle the button click to open the new activity here
                val intent = Intent(context,activity)
                this.startActivity(intent, null)
            },
            modifier = Modifier
                .padding(2.dp)
                .width(200.dp)
                .height(50.dp)

        ) {
            Text("$text")
        }
    }

    private val wildEncounterLogic = WildEncounterLogic(context = this)

    @Composable
    fun NewCrittersAdvice(){
        if(newCritterNotification<11){
            Text(text = "New Critters will spawn soon!",color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }

    var alreadyLoaded = false //for LoadWildEncounter firstLoad
    //@SuppressLint("SuspiciousIndentation")
    @Composable
    fun LoadFirstWildEncounter(){
        if(shouldLoadFirstWildEncounter) {
            if(alreadyLoaded == false) {
                Log.d(LOCATION_TAG, "Excecuted first loadwildencounter")
                var critterUsables = WildEncounter(uniClashViewModel)
                println("${critterUsables.size} size")
                    addListOfMarkers(wildEncounterLogic.initMarkers(critterUsables))
                shouldLoadFirstWildEncounter = false
                if(critterUsables.isEmpty()){
                    Counter.FIRSTSPAWN.setCounter(2)
                } else {
                    alreadyLoaded = true
                }
            }
        }
    }

    fun LoadWildEncounter(){
        if(shouldLoadWildEncounter) {
            Log.d(LOCATION_TAG, "Excecuted second loadwildencounter")
            //addListOfMarkers(wildEncounterLogic.initMarkers())
            //shouldLoadWildEncounter = false
            val intent = Intent(this,MapActivity::class.java)
            this.startActivity(intent, null)
        }
    }


    fun LoadStudentHubs(){
        if(shouldLoadStudentHubs) {
            println("trying to load student hubs ${MapSaver.STUDENTHUB.getMarker().toString()}")
            if (MapSaver.STUDENTHUB.getMarker() != null && MapSaver.STUDENTHUB.getMarker()!!
                    .isNotEmpty() && !alreadyLoadStudentHubs
            ) {
                println("executed :( executed :( executed :( executed :( executed :(")
                alreadyLoadStudentHubs = true
                addListOfMarkers(MapSaver.STUDENTHUB.getMarker()!!)
            }else {
                shouldLoadStudentHubs = false
            }
        }
    }

    @Composable
    fun InitStudentHubMarker() {
        if(shouldInitStudentHubs) {
            val context = LocalContext.current
            val studentHubs = StudentHubs(studentHubViewModel)
            var studentHubMarkerList = ArrayList<MarkerData>()

            println("${studentHubs.size} size from the database")

            for (studentHub in studentHubs) {
                val geoPoint = GeoPoint(studentHub?.lat!!, studentHub?.lon!!)

                val icon: Drawable? by remember {
                    mutableStateOf(mapCalculations.resizeDrawableTo50x50(context, R.drawable.store, 50.0F))
                }

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
            }
            MapSaver.STUDENTHUB.setMarker(studentHubMarkerList)
            shouldInitStudentHubs = false
        }
    }

    fun addMarker(marker: MarkerData) {
        if(!(markerList.contains(marker))) {
            markerList.add(marker)
            updateMapMarkers()
        }
    }

    fun addListOfMarkers(markers: ArrayList<MarkerData>) {
        if(!(markerList.containsAll(markers))) {
            if (!markersLoaded!!) {
                for (marker in markers) {
                    markerList.add(marker)
                }
            }
            updateMapMarkers()
        }
    }

    fun removeMarker(marker: MarkerData) {
        print("${markerList.size} markers")
        markerList.remove(marker)
        print("${markerList.size} markers")
        updateMapMarkers()
    }

    private fun updateMapMarkers() {
        markersLoaded = true
        markersLoaded = false;
    }

    //gps stuff
    /**
     * Manages all location related tasks for the app.
     */

    companion object {
        private const val LOCATION_TAG = "MyLocationTag"
    }

 //BackendStuff

    private val uniClashViewModel: UniClashViewModel by viewModels(factoryProducer = {
        UniClashViewModel.provideFactory(CritterService.getInstance(this))
    })

    private val studentHubViewModel: StudentHubViewModel by viewModels(factoryProducer = {
        StudentHubViewModel.provideFactory(StudentHubService.getInstance(this))
    })

    @Composable
    fun WildEncounter(uniClashViewModel: UniClashViewModel):List<CritterUsable?> {
        val uniClashUiStateCritterUsables by uniClashViewModel.critterUsables.collectAsState()
        uniClashViewModel.loadCritterUsables(1)
        var critterUsables : List<CritterUsable?> = uniClashUiStateCritterUsables.critterUsables
        println("${critterUsables.size} size in methode")
        return critterUsables
    }

    @Composable
    fun StudentHubs(studentHubViewModel: StudentHubViewModel):List<StudentHub?> {
        val studentHubsUIState by studentHubViewModel.studentHubs.collectAsState()
        studentHubViewModel.loadStudentHubs()
        var studentHubs : List<StudentHub?> = studentHubsUIState.studentHubs
        println("${studentHubs.size} size in methode")
        return studentHubs
    }
}