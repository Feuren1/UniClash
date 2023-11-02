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
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.mymap.retrofit.CritterService
import com.example.mymap.ui.theme.MyMapTheme
import com.example.mymap.viewmodels.UniClashViewModel
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

class MenuActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MenuCategories()
            }
        }
    }

    @Composable
    fun MenuCategories() {
        val menuFontSize = 20.sp // Define a single font size

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            MenuHeader()
//            Button(onClick = ) {
                Text(text = "Critters List", fontSize = menuFontSize)
//            }
            Text(text = "Inventar", fontSize = menuFontSize)
            Text(text = "Pokedex", fontSize = menuFontSize)
            Text(text = "Fix Location Camera on/off", fontSize = menuFontSize)
            Text(text = "New Building", fontSize = menuFontSize)
            OpenBattleActivityButton()
        }
    }


    @Composable
    fun MenuHeader() {
        Text(
            text = "Menu",
            fontSize = 50.sp, // Adjust the font size as needed
            fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
        )
    }

    @Composable
    fun OpenBattleActivityButton() {
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

    @Preview(showBackground = true)
    @Composable
    fun MenuActivityPreview() {
        MyMapTheme {
            MenuCategories()
        }
    }
}