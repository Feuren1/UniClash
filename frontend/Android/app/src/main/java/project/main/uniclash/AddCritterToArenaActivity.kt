package project.main.uniclash

import android.content.Context
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.ArenaCritterService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.AddCritterToArenaViewModel

class AddCritterToArenaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val addCritterToArenaViewModel: AddCritterToArenaViewModel by viewModels(factoryProducer = {
            AddCritterToArenaViewModel.provideFactory(ArenaCritterService.getInstance(this))
        })


        super.onCreate(savedInstanceState)
        setContent {

            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                }
            }
            CritterList(addCritterToArenaViewModel)
        }
}
    @Composable
    fun CritterList(addCritterToArenaViewModel: AddCritterToArenaViewModel)  {
        val critterListUIState by addCritterToArenaViewModel.critterUsables.collectAsState()
        val critterList = critterListUIState.critterUsables
        for (critter in critterList) {
            CritterDetail(critter = critter)
        }
    }
    @Composable
    fun CritterDetail(critter: CritterUsable?) {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .clickable {
                    val intent = Intent(this, CritterProfileActivity::class.java)
                    val b = Bundle()
                    b.putInt("critterId", critter!!.critterId)
                    intent.putExtras(b)
                    startActivity(intent)
                    finish()
                }

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Image(
                    painter = painterResource(CritterPic.MUSK.searchDrawable("${critter?.name}")),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = critter!!.name,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Level: ${critter?.level}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
