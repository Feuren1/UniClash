package project.main.uniclash
// ProfileActivity.kt

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.runBlocking
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.viewmodels.ProfileViewModel

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val profileViewModel: ProfileViewModel by viewModels(factoryProducer = {
            ProfileViewModel.provideFactory(UserService.getInstance(this), Application())
        })
        val userDataManager: UserDataManager by lazy {
            UserDataManager(application)
        }
        var token: String?
        runBlocking {
            token = userDataManager.getJWTToken()
        }
        profileViewModel.loadProfile(token!!, this)

        var exitRequest by mutableStateOf(false)

        super.onCreate(savedInstanceState)
        setContent {
            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                    Column {
                        Box(
                            Modifier.fillMaxWidth()
                        ) {
                            MenuHeader()
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
                        }
                        PlayerProfile(profileViewModel)
                    }
                }
            }
            if (exitRequest) {
                val hasStudent by profileViewModel.hasStudent.collectAsState()
                var intent = Intent(this, MainActivity::class.java)
                if(hasStudent == true) {
                    intent = Intent(this, MenuActivity::class.java)
                }
                this.startActivity(intent)
                finish()
                exitRequest = false
            }
        }
    }
    }

@Composable
fun MenuHeader() {
    Box(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth() // making box from left to right site
            .background(
                Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            ) // Hintergrundfarbe und abgeflachte Ecken

    ) {
        Text(
            text = "Profile",
            fontSize = 50.sp, // Adjust the font size as needed
            fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
        )
    }
}

@Composable
fun PlayerProfile(profileViewModel: ProfileViewModel) {
    val userUIState by profileViewModel.user.collectAsState()
    val responseText by profileViewModel.text.collectAsState()
    val hasStudent by profileViewModel.hasStudent.collectAsState()
    val text by profileViewModel.text.collectAsState()
    if(!userUIState.isLoading&&userUIState.user!=null) {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                val context = LocalContext.current

                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Trainer: ${userUIState.user!!.name}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Trainer Level: ${userUIState.user?.student?.level}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                val context = LocalContext.current

                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))

                    Row{
                        Text(
                            text = "Current XP: ${userUIState.user?.student?.expToNextLevel}",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        XpBarStudent(
                            currentXp = userUIState.user!!.student.expToNextLevel,
                            barColor = Color.Green
                        )

                    }

                    Text(
                        text = "Credits: ${userUIState.user?.student?.credits}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Amount of buildings placed: ${userUIState.user?.student?.placedBuildings}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }


            // Display other game progress details as needed
            if (hasStudent == true) {
                // Display button to go back to the menu
            }
            if (hasStudent == false) {
                // Display button to go back to the menu
                Button(
                    onClick = {
                        profileViewModel.createStudent(userUIState.user!!.id)

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    Text("Start your journey and create your Student")
                }
            }

        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                val context = LocalContext.current

                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = text,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )

                }
            }
        }
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
        } else {

        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                val context = LocalContext.current

                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Loading profile...",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )

                }
            }
        }
    }

}

@Composable
fun XpBarStudent(
    currentXp: Int,
    barColor: Color
) {
    // Calculate health percentage
    val levelPercentage = (currentXp / 500.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
    ) {
        // Apply the shake animation to the health bar
        Box(
            modifier = Modifier
                .fillMaxWidth(levelPercentage)
                .fillMaxHeight()
                .background(barColor, RoundedCornerShape(10.dp))
        )
    }
}
