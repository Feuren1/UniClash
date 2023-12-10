package project.main.uniclash
// ProfileActivity.kt

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.LoginViewModel
import project.main.uniclash.viewmodels.ProfileViewModel

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val profileViewModel: ProfileViewModel by viewModels(factoryProducer = {
            ProfileViewModel.provideFactory(UserService.getInstance(this), Application())
        })
        val preferences = this.getSharedPreferences("Ids", Context.MODE_PRIVATE)
        val token = preferences.getString("UserId", "") ?: ""

        profileViewModel.loadProfile(token, this)

        var exitRequest by mutableStateOf(false)

        super.onCreate(savedInstanceState)
        setContent {
            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        MenuHeader()
                        Box() {
                            Image(
                                painter = painterResource(id = R.drawable.exit),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {
                                        exitRequest = true
                                    }
                                    .align(Alignment.TopEnd)
                            )
                            PlayerProfile(profileViewModel)
                        }
                    }
                }
            }
            if (exitRequest) {
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
                finish()
                exitRequest = false
            }
        }
    }
    }

@Composable
fun MenuHeader() {
    Text(
        text = "Profile",
        fontSize = 50.sp, // Adjust the font size as needed
        fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
    )
}

@Composable
fun PlayerProfile(profileViewModel: ProfileViewModel) {
    val userUIState by profileViewModel.user.collectAsState()
    val responseText by profileViewModel.text.collectAsState()
    val hasStudent by profileViewModel.hasStudent.collectAsState()
    val text by profileViewModel.text.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Display user information
        Text("Username: ${userUIState.user?.name}")
        Text("User ID: ${userUIState.user?.id}")

        // Display student information
        Text("XP: ${userUIState.user?.student?.xp}")
        Text("Level: ${userUIState.user?.student?.level}")
        Text("Credits: ${userUIState.user?.student?.credits}")

        // Display other game progress details as needed
        if(hasStudent==true){
        // Display button to go back to the menu
        }
        if(hasStudent==false){
            // Display button to go back to the menu
            Button(
                onClick = {


                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Text("Start your journey and create your Student")
            }
        }
        Text(text)
        // Add a button or action to navigate to edit profile or other features
        Button(
            onClick = {
                // Handle the action (e.g., navigate to edit profile)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
        ) {
            Text("Edit Profile")
        }
    }
}
