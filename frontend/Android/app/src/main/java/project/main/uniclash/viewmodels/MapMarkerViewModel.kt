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
import project.main.uniclash.datatypes.MyMarker
import project.main.uniclash.datatypes.SelectedMarker
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.enqueue
import retrofit2.Call
import java.util.concurrent.TimeUnit

class MapMarkerViewModel(
    private val critterService: CritterService,
    private val studenHubService : StudentHubService
) : ViewModel() {

    init {
        viewModelScope.launch {
            Log.d(TAG, "Fetching MapLocation ")
        }
    }





    companion object {
        fun provideFactory(
            critterService: CritterService,
            studenHubService: StudentHubService
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MapMarkerViewModel(
                        critterService,
                        studenHubService
                    ) as T
                }
            }
    }
}
