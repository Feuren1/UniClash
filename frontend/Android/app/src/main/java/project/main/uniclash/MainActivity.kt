package project.main.uniclash

import android.app.Application
import androidx.compose.runtime.getValue

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.runBlocking
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.userDataManager.UserDataManager
import project.main.uniclash.viewmodels.ProfileViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "UserData")
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val profileViewModel: ProfileViewModel by viewModels(factoryProducer = {
            ProfileViewModel.provideFactory(UserService.getInstance(this), Application())
        })
        super.onCreate(savedInstanceState)

        var activityContext = this

        val userDataManager: UserDataManager by lazy {
            UserDataManager(application)
        }
        var token: String?
        runBlocking {
            token = userDataManager.getJWTToken()
        }
        token?.let { profileViewModel.loadProfile(it, activityContext) }

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
                // Hintergrundbild hinzufügen
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    OpenLoginActivityButton()
                    Spacer(modifier = Modifier.height(8.dp))
                    OpenRegisterActivityButton()
                    CheckOldSession(profileViewModel = profileViewModel)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    @Composable
    fun CheckOldSession(profileViewModel: ProfileViewModel){
        val userUIState by profileViewModel.user.collectAsState()
        if (userUIState.user?.id != null) {
        Spacer(modifier = Modifier.height(8.dp))
        OpenProfileActivityButton()
        }
    }


    @Composable
    fun OpenLoginActivityButton() {
        val context = LocalContext.current
        Button(
           onClick = {
        val intent = Intent(context, LoginActivity::class.java)
        this.startActivity(intent)
               finish()
          },
           modifier = Modifier
               .padding(2.dp)
               .fillMaxWidth()
               .height(50.dp)

         ) {
           Text("Log in")
         }
    }

    @Composable
    fun OpenRegisterActivityButton() {
        val context = LocalContext.current
        Button(
            onClick = {
                val intent = Intent(context, RegisterActivity::class.java)
                this.startActivity(intent)
                finish()
            },
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
                .height(50.dp)

        ) {
            Text("Register")
        }
    }

    @Composable
    fun OpenProfileActivityButton() {
        val context = LocalContext.current
        Button(
            onClick = {
                val intent = Intent(context, ProfileActivity::class.java)
                this.startActivity(intent)
                finish()
            },
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
                .height(50.dp)

        ) {
            Text("Go to old session")
        }
    }
    // typically you want to retrieve the device token when the user logs in and save
// it in the backend when the login is success


}