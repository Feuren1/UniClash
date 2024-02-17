package project.main.uniclash

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import project.main.uniclash.retrofit.OnlineFightService
import project.main.uniclash.viewmodels.CreateOnlineFightViewModel
import project.main.uniclash.viewmodels.OnlineFightListViewModel


class CreateOnlineFightActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)

    val createOnlineFightViewModel: CreateOnlineFightViewModel by viewModels(factoryProducer = {
        CreateOnlineFightViewModel.provideFactory(OnlineFightService.getInstance(this), Application())
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createOnlineFightViewModel.createOnlineFight()

        setContent {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(16.dp)
                ) {
                    MenuHeader()
                    Cycle()
                    LoadingCircle(Modifier)
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
            }

            if (exitRequest) {
                val intent = Intent(this, OnlineFightListActivity::class.java)
                this.startActivity(intent)
                finish()
                exitRequest = false
            }
        }
    }

    @Composable
    fun Cycle(){
        var counter = 1
        LaunchedEffect(Unit) {
            while (true) {
                println("$counter and $exitRequest")
                delay(1000) //1sec. //a delay will not froze the complete application
                counter --
                if(counter==0)exitRequest= true
            }
        }
    }

    @Composable
    fun MenuHeader() {
        Column {
            Text(
                text = "Create Online Fights",
                fontSize = 50.sp, // Adjust the font size as needed
                fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                textAlign = TextAlign.Start,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
            )
        }
    }
}