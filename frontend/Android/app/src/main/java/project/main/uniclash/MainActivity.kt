package project.main.uniclash

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "UserData")
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", "onCreate: FCM Token retrieved successfully: $token")

                // Here, you can use the token as needed (e.g., display it in UI, send to server, etc.)
            } else {
                Log.e("FCM Token", "onCreate: Error getting FCM token: ${task.exception}")
            }
        }

        Log.d("FCM Token", "onCreate: End of onCreate")

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                OpenLoginActivity()
                OpenProfileActivity()
            }
        }
    }


    @Composable
    fun OpenLoginActivity() {
        val context = LocalContext.current
                val intent = Intent(context, LoginActivity::class.java)
                this.startActivity(intent)

    }

    @Composable
    fun OpenProfileActivity() {
        val context = LocalContext.current
        val intent = Intent(context, LoginActivity::class.java)
        this.startActivity(intent)

    }
    // typically you want to retrieve the device token when the user logs in and save
// it in the backend when the login is success


}