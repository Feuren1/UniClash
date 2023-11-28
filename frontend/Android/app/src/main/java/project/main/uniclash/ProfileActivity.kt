package project.main.uniclash
// ProfileActivity.kt

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
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

        super.onCreate(savedInstanceState)
        setContent {
            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerProfile(profileViewModel)
                }
            }
        }
    }
    }

@Composable
fun PlayerProfile(profileViewModel: ProfileViewModel) {
    val userUIState by profileViewModel.user.collectAsState()

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
