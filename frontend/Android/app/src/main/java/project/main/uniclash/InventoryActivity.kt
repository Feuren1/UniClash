package project.main.uniclash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import project.main.uniclash.ui.theme.UniClashTheme

class InventoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudentHubScreen(
                        modifier = Modifier.fillMaxSize(),
                        //ViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryScreen(modifier: Modifier = Modifier,
                    /*TODO: inventoryViewModel: InventoryViewModel = viewModel()*/) {

    Column() {

    }
}