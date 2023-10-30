package com.example.mymap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.mymap.ui.theme.MyMapTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.concurrent.TimeUnit
import kotlin.text.toDoubleOrNull

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissions = arrayOf( //array ONLY for location Permissions!!!
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    private var startMapRequested by mutableStateOf(false)
    private var mainLatitude : Double by mutableStateOf(0.0) //for gps location
    private var mainLongitude : Double by mutableStateOf(0.0)//"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            MyMapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (hasPermissions() || startMapRequested) {
                        val currentUserLocation = getUserLocation(context = LocalContext.current)
                        mainLatitude = currentUserLocation.latitude
                        mainLongitude = currentUserLocation.longitude
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
    fun RelocateArrow() {
        val gpsLocation = rememberMarkerState(
            geoPoint = GeoPoint(mainLatitude, mainLongitude)
        )
        val context = LocalContext.current

        val arrow: Drawable? by remember {
            mutableStateOf(context.getDrawable(R.drawable.location))
        }
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            //cameraState = cameraState
        ) {
            // Add markers and other map components here
            Marker(
                state = gpsLocation,
                icon = arrow
            )
        }
    }
    @Composable
    fun Map() {
        var gpsLocation = rememberMarkerState()
        val cameraState = rememberCameraState()
        LaunchedEffect(Unit) {
            while (true) {
                val tolerance = 0.001 //0.0001 before
                if (Math.abs(mainLatitude - cameraState.geoPoint.latitude) > tolerance || Math.abs(mainLongitude - cameraState.geoPoint.longitude) > tolerance) {
                    Log.d(LOCATION_TAG, "$mainLatitude and ${cameraState.geoPoint.latitude} ---- $mainLongitude and ${cameraState.geoPoint.longitude}")
                    cameraState.geoPoint = GeoPoint(mainLatitude, mainLongitude)
                    cameraState.zoom = 20.5
                    gpsLocation.geoPoint = GeoPoint(mainLatitude, mainLongitude)
                }
                delay(3000) //3sec.
            }
        }
        //gpsLocation = rememberMarkerState(
        //    geoPoint = GeoPoint(mainLatitude, mainLongitude)
        //)
        val fontys = rememberMarkerState(
            geoPoint = GeoPoint(51.353576, 6.154071)
        )
        val googleHeadQuarter = rememberMarkerState(
            geoPoint = GeoPoint(37.421304, -122.085330)
        )
        val googleHeadQuarter2 = rememberMarkerState(
            geoPoint = GeoPoint(37.421881, -122.083825)
        )
        val googleHeadQuarter3 = rememberMarkerState(
            geoPoint = GeoPoint(37.423214, -122.085024)
        )
        val googleHeadQuarter4 = rememberMarkerState(
            geoPoint = GeoPoint(37.421988, -122.085094)
        )
        val marker = rememberMarkerState(
            geoPoint = GeoPoint(37.422069, -122.084853)
        )
        val homeMarker = rememberMarkerState(
            geoPoint = GeoPoint(51.4959040534788, 6.294253058731556)
        )
        val homeArena = rememberMarkerState(
            geoPoint = GeoPoint(51.495723, 6.294844)
        )
        val context = LocalContext.current

        // define marker icon
        val prc2duck: Drawable? by remember {
            mutableStateOf(resizeDrawableTo50x50(context, R.drawable.prc2duck))
        }

        val arrow: Drawable? by remember {
            mutableStateOf(resizeDrawableTo50x50(context, R.drawable.location))
        }

        val studentHub: Drawable? by remember {
            mutableStateOf(resizeDrawableTo60x60(context, R.drawable.store))
        }

        val arena: Drawable? by remember {
            mutableStateOf(resizeDrawableTo60x60(context, R.drawable.arena))
        }

        // Use camera state and location in your OpenStreetMap Composable
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState
        ) {
            // Add markers and other map components here
            Marker(
                state = fontys,
                icon = prc2duck
            )
            Marker(
                state = gpsLocation,
                icon = arrow
            )
            Marker(
                state = googleHeadQuarter,
                icon = prc2duck
            )
            Marker(
                state = googleHeadQuarter2,
                icon = prc2duck
            )
            Marker(
                state = googleHeadQuarter3,
                icon = prc2duck
            )
            Marker(
                state = googleHeadQuarter4,
                icon = prc2duck
            )
            Marker(
                state = marker,
                icon = studentHub,
                title = "Google Headquarter",
                snippet = "Google Headquarter in California"
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
                    val drawableImage = painterResource(id = R.drawable.googleheadquarter)
                    Image(
                        painter = drawableImage,
                        contentDescription = null, // Provide a proper content description if needed
                        modifier = Modifier.size(240.dp) // Adjust size as needed
                    )
                }
            }
            Marker(
                state = homeMarker,
                icon = studentHub,
                title = "Home",
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
                    val drawableImage = painterResource(id = R.drawable.home)
                    Image(
                        painter = drawableImage,
                        contentDescription = null, // Provide a proper content description if needed
                        modifier = Modifier.size(240.dp) // Adjust size as needed
                    )
                }
            }
            Marker(
                state = homeArena,
                icon = arena
            )
        }
        OpenActivityButton()
    }

    @Composable
    fun OpenActivityButton() {
        Button(
            onClick = {
                // Handle the button click to open the new activity here
                val intent = Intent(this, Battle::class.java)
                this.startActivity(intent)
            },
            modifier = Modifier
                .padding(2.dp)
                .size(100.dp)

        ) {
            Text("Open Another Activity")
        }
    }

    fun resizeDrawableTo50x50(context: Context, @DrawableRes drawableRes: Int): Drawable?{
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

    fun resizeDrawableTo60x60(context: Context, @DrawableRes drawableRes: Int): Drawable?{
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

    companion object{
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
                                currentUserLocation = LatandLong(latitude = lat, longitude = long)
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
    data class LatandLong(
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