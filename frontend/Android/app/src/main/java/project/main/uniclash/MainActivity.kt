package project.main.uniclash

import android.content.ContentValues.TAG
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

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
                OpenMenuActivityButton()
            }
        }
    }


    @Composable
    fun OpenMenuActivityButton() {
        val context = LocalContext.current
        //Button(
         //   onClick = {
                // Handle the button click to open the new activity here
                val intent = Intent(context, MenuActivity::class.java)
                this.startActivity(intent)
          //  },
         //   modifier = Modifier
         //       .padding(2.dp)
          //      .width(200.dp)
         //       .height(50.dp)

       // ) {
        //    Text("Menu")
       // }
    }
    // typically you want to retrieve the device token when the user logs in and save
// it in the backend when the login is success


}