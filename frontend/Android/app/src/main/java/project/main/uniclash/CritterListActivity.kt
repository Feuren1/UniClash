package project.main.uniclash

import android.app.Application
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.viewmodels.CritterDexViewModel
import project.main.uniclash.viewmodels.UniClashViewModel
import androidx.compose.foundation.lazy.items


class CritterListActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)

    val uniClashViewModel: UniClashViewModel by viewModels(factoryProducer = {
        UniClashViewModel.provideFactory(CritterService.getInstance(this), Application())
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val uniClashUiStateCritterUsables by uniClashViewModel.critterUsables.collectAsState()
            val myCritters = uniClashUiStateCritterUsables.critterUsables
            var isLoading by rememberSaveable { mutableStateOf(true) }
            isLoading = uniClashUiStateCritterUsables.isLoading

            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    MenuHeader(myCritters.size)
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                            UsableList(uniClashViewModel,isLoading)
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

    @Composable
    fun UsableList(uniClashViewModel: UniClashViewModel, isLoading: Boolean) {
        val uniClashUiStateCritterUsables by uniClashViewModel.critterUsables.collectAsState()
        if (isLoading) {
            LoadingCircle(Modifier)
        }else {
            LazyColumn(modifier = Modifier) {
                items(items = uniClashUiStateCritterUsables.critterUsables, key= { critter -> critter!!.critterId }) { critter ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    ) {
                        CritterDetail(critter)
                    }
                }
            }
        }
    }


    @Composable
    fun MenuHeader(size : Int) {
        Column {
            Text(
                text = "Critters",
                fontSize = 50.sp, // Adjust the font size as needed
                fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
            )
            Text(
                text = "$size/200 Critters${if(size>= 200){"\nYou reached the max amount of critters.\nNew captured critters are not added to your team!!!"}else{""}}",
                fontSize = 12.sp, // Adjust the font size as needed
                fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                textAlign = TextAlign.Start,
            )
        }
    }

    @Composable
    fun CritterDetail(critter: CritterUsable?) {
        var blockColor : Color = Color.LightGray
        var textColor : Color = MaterialTheme.colorScheme.secondary
        var selectedText = ""
        if(uniClashViewModel.checkIfSelectedCritter(critter!!.critterId)){
            blockColor = MaterialTheme.colorScheme.onPrimaryContainer
            textColor = Color.White
            selectedText = "| SELECTED CRITTER FOR THE FIGHT"
        }
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    blockColor,
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
                val context = LocalContext.current
                val name: String = critter?.name!!.lowercase()
                val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
                Image(
                    painter = painterResource(if(resourceId > 0){resourceId}else{R.drawable.icon}),
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
                        color = textColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Level: ${critter?.level} $selectedText",
                        fontSize = 12.sp,
                        color = textColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}