package project.main.uniclash.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import project.main.uniclash.MapActivity
import project.main.uniclash.WildEncounterActivity
import project.main.uniclash.datatypes.CritterForStudent
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.enqueue
import retrofit2.Call
import java.util.concurrent.TimeUnit

class MapLocationViewModel(
) : ViewModel() {

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching MapLocation ")
        }
    }


    //gps stuff
    /**
     * Manages all location related tasks for the app.
     */

    //data class to store the user Latitude and longitude
    data class LatandLong( //set the first maker
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )

//A callback for receiving notifications from the FusedLocationProviderClient.
    lateinit var locationCallback: LocationCallback

    //The main entry point for interacting with the Fused Location Provider
    lateinit var locationProvider: FusedLocationProviderClient

        private val LOCATION_TAG = "MyLocationTag"

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
            onDispose {
                stopLocationUpdate()
            }
        }
        return currentUserLocation
    }

    @SuppressLint("MissingPermission")
    fun locationUpdate() {
        locationCallback.let {
            println("locationUpdate tries updating")
            //An encapsulation of various parameters for requesting
            // location through FusedLocationProviderClient.
            val locationRequest: LocationRequest =
                LocationRequest.create().apply {
                    interval = TimeUnit.SECONDS.toMillis(3) //TimeUnit.SECONDS.toMillis(60)
                    fastestInterval = TimeUnit.SECONDS.toMillis(3) //TimeUnit.SECONDS.toMillis(30)
                    maxWaitTime = TimeUnit.SECONDS.toMillis(3) //TimeUnit.MINUTES.toMillis(2)
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

    companion object {
        fun provideFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MapLocationViewModel(
                    ) as T
                }
            }
    }
}
