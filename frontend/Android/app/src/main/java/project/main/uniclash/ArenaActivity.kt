package project.main.uniclash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.ArenaViewModel

class ArenaActivity : ComponentActivity() {

    private val arenaViewModel by viewModels<ArenaViewModel> {
        ArenaViewModel.provideFactory(ArenaService.getInstance(this))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val arenaUIstate by arenaViewModel.arena.collectAsState()
                    Text(text = arenaUIstate.arena.toString())
                    /*Button(
                        onClick = {


                        },
                        modifier = Modifier
                            .padding(2.dp)
                            .size(80.dp)
                    ) {
                        Text(text = "Debug")
                    }
                    */

                }
            }
        }
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {

}