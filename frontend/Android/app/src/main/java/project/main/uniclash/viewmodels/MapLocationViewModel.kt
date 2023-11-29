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
import project.main.uniclash.map.LocationPermissions
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.enqueue
import retrofit2.Call
import java.util.concurrent.TimeUnit

class MapLocationViewModel(private val locationPermissions: LocationPermissions) : ViewModel() {

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching MapLocation ")
        }
    }

    //gps stuff
    lateinit var locationCallback: LocationCallback

    //The main entry point for interacting with the Fused Location Provider
    lateinit var locationProvider: FusedLocationProviderClient

    fun getUserLocation(context: Context, onLocationReceived: (LatandLong) -> Unit) {
        // The Fused Location Provider provides access to location APIs.
        locationProvider = LocationServices.getFusedLocationProviderClient(context)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    // Update data class with location data
                    val currentUserLocation = LatandLong(location.latitude, location.longitude)
                    Log.d(LOCATION_TAG, "${location.latitude},${location.longitude}")
                    onLocationReceived(currentUserLocation)
                }
            }
        }

        if (locationPermissions.hasPermissions()) {
            locationUpdate()
        } else {
            locationPermissions.requestLocationPermissions()
        }
    }


    //data class to store the user Latitude and longitude
    data class LatandLong( //set the first maker
        var latitude: Double = 0.0,
        var longitude: Double = 0.0
    )

    @SuppressLint("MissingPermission")
    fun locationUpdate() {
        locationCallback.let {
            println("locationUpdate tries updating")
            //An encapsulation of various parameters for requesting
            // location through FusedLocationProviderClient.
            val locationRequest: LocationRequest =
                LocationRequest.create().apply {
                    interval = TimeUnit.SECONDS.toMillis(3) //TimeUnit.SECONDS.toMillis(60)
                    fastestInterval = TimeUnit.SECONDS.toMillis(2) //TimeUnit.SECONDS.toMillis(30)
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
        private const val LOCATION_TAG = "MyLocationTag"

        fun provideFactory(locationPermissions: LocationPermissions): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MapLocationViewModel(locationPermissions) as T
                }
            }
    }
}