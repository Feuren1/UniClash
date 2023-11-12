package project.main.uniclash

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import project.main.uniclash.ui.theme.UniClashTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import kotlinx.coroutines.delay
import org.osmdroid.util.GeoPoint
import project.main.uniclash.datatypes.Counter
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.datatypes.MyMarker
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.wildencounter.WildEncounterLogic
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class MapActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissions = arrayOf(
        //array ONLY for location Permissions!!!
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    private var startMapRequested by mutableStateOf(false)
    private var mainLatitude: Double by mutableStateOf(Locations.USERLOCATION.getLocation().latitude) //for gps location
    private var mainLongitude: Double by mutableStateOf(Locations.USERLOCATION.getLocation().longitude)//"

    private var markerList = ArrayList<MyMarker>()
    private var markersLoaded by mutableStateOf(false)
    private var movingCamera : Boolean ? = true

    private var shouldLoadFirstWildEncounter by mutableStateOf(false)
    private var shouldLoadWildEncounter by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        setContent {
            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (hasPermissions() || startMapRequested) {
                        val currentUserLocation = getUserLocation(context = LocalContext.current)
                        if(currentUserLocation.latitude != 0.0 && currentUserLocation.longitude != 0.0) {
                            mainLatitude = currentUserLocation.latitude
                            mainLongitude = currentUserLocation.longitude
                            Locations.USERLOCATION.setLocation(GeoPoint(currentUserLocation.latitude, currentUserLocation.longitude))
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
                                requestLocationPermissions()
                                if (hasPermissions()) {
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

    @Composable
    fun Map() {
        LoadMarkers()
        LoadFirstWildEncounter()
        LoadWildEncounter()
        var gpsLocation = rememberMarkerState()
        val cameraState = rememberCameraState()
        LaunchedEffect(Unit) {
            while (true) {
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
                    if (MapSettings.MOVINGCAMERA.getMapSetting()) {
                        cameraState.geoPoint = GeoPoint(mainLatitude, mainLongitude)
                        cameraState.zoom = 20.5
                    } else if (movingCamera == true) {
                        cameraState.geoPoint = GeoPoint(mainLatitude, mainLongitude)
                        cameraState.zoom = 20.5
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
                    Counter.WILDENCOUNTERREFRESHER.setCounter(20)
                }
            }
        }

        val context = LocalContext.current

        // define marker icon
        val arrow: Drawable? by remember {
            mutableStateOf(resizeDrawableTo50x50(context, R.drawable.location))
        }

        // Use camera state and location in your OpenStreetMap Composable
        if (!markersLoaded) {
            OpenStreetMap(
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState
            ) {
                // Add markers and other map components here
                markerList.forEach { marker ->
                    val distance = haversineDistance(marker.state.geoPoint.latitude, marker.state.geoPoint.longitude, Locations.USERLOCATION.getLocation().latitude, Locations.USERLOCATION.getLocation().longitude)
                    Log.d(
                        LOCATION_TAG,
                        "set marker"
                    )
                    Marker(
                        state = marker.state,
                        icon = marker.icon,
                        title = marker.title,
                        snippet = marker.snippet,
                        visible = marker.visible,
                        id = marker.id,
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
                    title = "NamePlaceholder",
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
    }

    @Composable
    fun OpenActivityButton(marker : MyMarker) {
        val context = LocalContext.current
        val distance = haversineDistance(marker.state.geoPoint.latitude, marker.state.geoPoint.longitude, Locations.USERLOCATION.getLocation().latitude, Locations.USERLOCATION.getLocation().longitude)
        if(distance < 76) {
            Button(
                onClick = {
                    // Handle the button click to open the new activity here
                    SelectedMarker.SELECTEDMARKER.setMarker(marker)
                    //removeMarker(SelectedMarker.SELECTEDMARKER.takeMarker()!!)
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
    fun LoadMarkers(){
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
                text = "\nMap has problems to load",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }

    var alreadyLoaded = false //for LoadWildEncounter firstLoad
    @Composable
    fun LoadFirstWildEncounter(){
        if(shouldLoadFirstWildEncounter) {
            if(alreadyLoaded == false) {
                Log.d(LOCATION_TAG, "Excecuted first loadwildencounter")
                addListOfMarkers(wildEncounterLogic.initMarkers())
                shouldLoadFirstWildEncounter = false
                alreadyLoaded = true
            }
        }
    }

    @Composable
    fun LoadWildEncounter(){
        if(shouldLoadWildEncounter) {
                Log.d(LOCATION_TAG, "Excecuted second loadwildencounter")
                //addListOfMarkers(wildEncounterLogic.initMarkers())
                //shouldLoadWildEncounter = false
            val intent = Intent(this,MapActivity::class.java)
            this.startActivity(intent, null)
        }
    }

    // Methode zum Hinzufügen eines Markers zur Liste und Aktualisieren der Karte
    fun addMarker(marker: MyMarker) {
        markerList.add(marker)
        // Aktualisiere die Karte hier, um den neuen Marker anzuzeigen
        updateMapMarkers()
    }

    fun addListOfMarkers(markers: ArrayList<MyMarker>) {
        if(!markersLoaded!!) {
            for (marker in markers) {
                markerList.add(marker)
            }
        }
        updateMapMarkers()
    }

    // Methode zum Entfernen eines Markers aus der Liste und Aktualisieren der Karte
    fun removeMarker(marker: MyMarker) {
        print("${markerList.size} markers")
        markerList.remove(marker)
        print("${markerList.size} markers")
        updateMapMarkers()
    }

    // Methode zum Aktualisieren der Karte mit den Markern aus der Liste
    private fun updateMapMarkers() {
        markersLoaded = true
        markersLoaded = false;
    }

    fun resizeDrawableTo50x50(context: Context, @DrawableRes drawableRes: Int): Drawable? {
        val originalDrawable: Drawable? = context.getDrawable(drawableRes)

        val originalBitmap = originalDrawable?.toBitmap()
        val originalWidth = originalBitmap?.width ?: 1 // Verhindert Division durch Null
        val originalHeight = originalBitmap?.height ?: 1

        val scaleRatio = 50.0f / originalHeight.toFloat()

        val scaledWidth = (originalWidth * scaleRatio).toInt()
        val scaledHeight = 50

        val scaledBitmap =
            originalBitmap?.let { Bitmap.createScaledBitmap(it, scaledWidth, scaledHeight, true) }

        return BitmapDrawable(context.resources, scaledBitmap)
    }

    fun resizeDrawableTo60x60(context: Context, @DrawableRes drawableRes: Int): Drawable? {
        val originalDrawable: Drawable? = context.getDrawable(drawableRes)

        val originalBitmap = originalDrawable?.toBitmap()
        val originalWidth = originalBitmap?.width ?: 1 // Verhindert Division durch Null
        val originalHeight = originalBitmap?.height ?: 1

        val scaleRatio = 60.0f / originalHeight.toFloat()

        val scaledWidth = (originalWidth * scaleRatio).toInt()
        val scaledHeight = 60

        val scaledBitmap =
            originalBitmap?.let { Bitmap.createScaledBitmap(it, scaledWidth, scaledHeight, true) }

        return BitmapDrawable(context.resources, scaledBitmap)
    }

    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radius = 6371000 // Radius der Erde in Metern

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radius * c
    }


    //gps stuff
    private fun requestLocationPermissions() {
        for (permission in locationPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, locationPermissions, 0)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Handle permission results if needed
    }

    private fun hasPermissions(): Boolean {
        for (permission in locationPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    //new gps stuff
    /**
     * Manages all location related tasks for the app.
     */

//A callback for receiving notifications from the FusedLocationProviderClient.
    lateinit var locationCallback: LocationCallback

    //The main entry point for interacting with the Fused Location Provider
    lateinit var locationProvider: FusedLocationProviderClient

    companion object {
        private const val LOCATION_TAG = "MyLocationTag"
    }

    @SuppressLint("MissingPermission")
    @Composable
    fun getUserLocation(context: Context): LatandLong {

        // The Fused Location Provider provides access to location APIs.
        locationProvider = LocationServices.getFusedLocationProviderClient(context)

        var currentUserLocation by remember { mutableStateOf(LatandLong()) }

        DisposableEffect(key1 = locationProvider) {
            locationCallback = object : LocationCallback() {
                //1
                override fun onLocationResult(result: LocationResult) {
                    /**
                     * Option 1
                     * This option returns the locations computed, ordered from oldest to newest.
                     * */
                    for (location in result.locations) {
                        // Update data class with location data
                            currentUserLocation = LatandLong(location.latitude, location.longitude)

                            Log.d(LOCATION_TAG, "${location.latitude},${location.longitude}")
                    }


                    /**
                     * Option 2
                     * This option returns the most recent historical location currently available.
                     * Will return null if no historical location is available
                     * */
                    locationProvider.lastLocation
                        .addOnSuccessListener { location ->
                            location?.let {
                                val lat = location.latitude
                                val long = location.longitude
                                // Update data class with location data
                                    currentUserLocation =
                                        LatandLong(latitude = lat, longitude = long)
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Location_error", "${it.message}")
                        }
                }
            }
            //2
            if (hasPermissions()) {
                locationUpdate()
            } else {
                requestLocationPermissions()
            }
            //3
            onDispose {
                stopLocationUpdate()
            }
        }
        //4
        return currentUserLocation
    }

    //data class to store the user Latitude and longitude
    data class LatandLong( //set the first maker
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )

    @SuppressLint("MissingPermission")
    fun locationUpdate() {
        locationCallback.let {
            println("locationUpdate tries updating")
            //An encapsulation of various parameters for requesting
            // location through FusedLocationProviderClient.
            val locationRequest: LocationRequest =
                LocationRequest.create().apply {
                    interval = TimeUnit.SECONDS.toMillis(5) //TimeUnit.SECONDS.toMillis(60)
                    fastestInterval = TimeUnit.SECONDS.toMillis(3) //TimeUnit.SECONDS.toMillis(30)
                    maxWaitTime = TimeUnit.SECONDS.toMillis(5) //TimeUnit.MINUTES.toMillis(2)
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
            //use FusedLocationProviderClient to request location update
            locationProvider.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }

    }

    fun stopLocationUpdate() {
        try {
            //Removes all location updates for the given callback.
            val removeTask = locationProvider.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(LOCATION_TAG, "Location Callback removed.")
                } else {
                    Log.d(LOCATION_TAG, "Failed to remove Location Callback.")
                }
            }
        } catch (se: SecurityException) {
            Log.e(LOCATION_TAG, "Failed to remove Location Callback.. $se")
        }
    }

}