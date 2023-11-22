package project.main.uniclash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.BattleViewModel
import project.main.uniclash.viewmodels.UserViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val userViewModel by viewModels<UserViewModel> {
            UserViewModel.provideFactory(UserService.getInstance(this),this.application)
        }
        super.onCreate(savedInstanceState)
        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting2("Android")
                }
            }
            Row {
                Button(
                    onClick = {
                        //userViewModel.signup("example@email.com", "password", "username")
                        userViewModel.login("example@email.com","password")
                    },
                    modifier = Modifier
                        .padding(2.dp)
                        .size(80.dp)
                ) {
                    Text(text = "Debug")
                }
                Button(
                    onClick = {
                        //userViewModel.signup("example@email.com", "password", "username")
                        userViewModel.getToken()
                    },
                    modifier = Modifier
                        .padding(2.dp)
                        .size(80.dp)
                ) {
                    Text(text = "Test Key")
                }
            }

        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    UniClashTheme {
        Greeting2("Android")
    }
}