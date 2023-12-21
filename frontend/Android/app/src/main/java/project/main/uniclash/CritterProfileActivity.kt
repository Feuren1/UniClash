package project.main.uniclash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.CritterProfileViewModel
import project.main.uniclash.viewmodels.CritterUsableUIState
import project.main.uniclash.viewmodels.UniClashViewModel

class CritterProfileActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)
    val critterProfileViewModel: CritterProfileViewModel by viewModels(factoryProducer = {
        CritterProfileViewModel.provideFactory(CritterService.getInstance(this), InventoryService.getInstance(this))
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = intent.extras
        var critterId = -1 // or other values

        if (b != null) critterId = b.getInt("critterId")
        critterProfileViewModel.loadCritter(critterId)
        critterProfileViewModel.loadCritterUsable(critterId)
        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val critterUsableUIState by critterProfileViewModel.critterUsable.collectAsState()
                    val critterUIState by critterProfileViewModel.critter.collectAsState()
                    val critterUsable = critterUsableUIState.critterUsable
                    val critter = critterUIState.critter

                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(16.dp)
                        ) {
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
                        Box {
                            if (critterUsable != null) {
                                CritterProfileScreen()
                            }
                        }
                    }

                }
                if (exitRequest) {
                    val intent = Intent(this, CritterListActivity::class.java)
                    this.startActivity(intent)
                    exitRequest = false
                    finish()
                }
            }
        }
    }
}

    @Composable
    fun CritterProfile(critterProfileViewModel: CritterProfileViewModel = viewModel()) {
        val critterUsableUIState by critterProfileViewModel.critterUsable.collectAsState()
        val critterUIState by critterProfileViewModel.critter.collectAsState()
        if (critterUsableUIState.critterUsable != null && critterUIState.critter != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Display other critter information as needed
                Text(text = "Level: ${critterUsableUIState.critterUsable!!.level}")
                Text(text = "Name: ${critterUsableUIState.critterUsable!!.name}")
                Text(text = "HP: ${critterUsableUIState.critterUsable!!.hp}")
                Text(text = "Attack: ${critterUsableUIState.critterUsable!!.atk}")
                Text(text = "Defense: ${critterUsableUIState.critterUsable!!.def}")
                Text(text = "Speed: ${critterUsableUIState.critterUsable!!.spd}")

                // Display attacks
                Text(text = "Attacks:")
                if (!critterUsableUIState.critterUsable!!.attacks.isNullOrEmpty()) {
                    critterUsableUIState.critterUsable!!.attacks.forEach { attack ->
                        Text(text = "Attack Name: $attack")
                        // Display other attack information as needed
                    }
                }

                // Display critterId and critterTemplateId
                Text(text = "Critter ID: ${critterUsableUIState.critterUsable!!.critterId}")
                Text(text = "Critter Template ID: ${critterUsableUIState.critterUsable!!.critterTemplateId}")

                Button(
                    onClick = {
                        //critterProfileViewModel.evolve(critterUsableUIState.critterUsable!!.critterId)
                        //critterProfileViewModel.loadCritterUsable(critterUIState.critter!!.id)
                        critterProfileViewModel.storeFightingCritter()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Select this critter to fight!")
                }
                Button(
                    onClick = {
                        critterProfileViewModel.loadCritterUsable(critterUIState.critter!!.id)
                        critterProfileViewModel.evolve(critterUsableUIState.critterUsable!!.critterId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Evolve")
                }
                Button(
                    onClick = {
                        critterProfileViewModel.loadCritterUsable(critterUIState.critter!!.id)
                        critterProfileViewModel.useRedBull(critterUsableUIState.critterUsable!!.critterId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Use Rebull")
                }
                Button(
                    onClick = {
                        critterProfileViewModel.delCritter(critterUsableUIState.critterUsable!!.critterId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Delete Critter :(")
                }
            }
        }else{
            Text("Critter data not available.")
        }
    }

    @Composable
    fun CritterProfileScreen(critterProfileViewModel: CritterProfileViewModel = viewModel()) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CritterProfile(critterProfileViewModel)
        }
    }
