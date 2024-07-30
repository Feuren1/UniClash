package project.main.uniclash

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.messaging.FirebaseMessaging
import project.main.uniclash.ui.theme.UniClashTheme

class PokéActivity : ComponentActivity() {

    private val TAG = "PokéActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background

                ) {
                    // Box with the background image
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pokemail),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
                        // Content inside the Box
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Subscribe buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SubscribeButton("Blue", "color_blue")
                                SubscribeButton("Red", "color_red")
                            }

                            // Other content...
                        }
                    }
                }




    @Composable
    fun SubscribeButton(color: String, topic: String) {
        var subscribed by remember { mutableStateOf(false) }

        Button(
            onClick = {
                if (subscribed) {
                    unsubscribeFromTopic(topic)
                } else {
                    subscribeToTopic(topic)
                }
                subscribed = !subscribed
            }
        ) {
            Text(if (subscribed) "Unsubscribe from $color" else "Subscribe to $color")
        }
    }

    private fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully subscribed to $topic")
                    Toast.makeText(baseContext, "Subscribed", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = "Subscribe failed: ${task.exception?.message}"
                    Log.e(TAG, errorMsg)
                    Toast.makeText(baseContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully unsubscribed from $topic")
                    Toast.makeText(baseContext, "Unsubscribed", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = "Unsubscribe failed: ${task.exception?.message}"
                    Log.e(TAG, errorMsg)
                    Toast.makeText(baseContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
    }

}
