package project.main.uniclash

import android.app.Application
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import android.util.Base64
import java.io.FileOutputStream
import java.io.File
import java.io.IOException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import project.main.uniclash.NewBuildingLogic.CameraLogic
import project.main.uniclash.NewBuildingLogic.NewBuildingSingleTon
import project.main.uniclash.datatypes.ActivitySaver
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.map.GeoCodingHelper
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.NewBuildingViewModel
import project.main.uniclash.viewmodels.WildEncounterViewModel


enum class BuildingType(){
    ARENA,
    STUDENTHUB;
}

class NewBuildingActivity : ComponentActivity() {
    private val newBuildingSingleTon = NewBuildingSingleTon.instance
    private var title by mutableStateOf(newBuildingSingleTon.getTitle())
    private var description by mutableStateOf(newBuildingSingleTon.getDescription())
    private var building by mutableStateOf(BuildingType.ARENA)
    private var confirmRequest by mutableStateOf(false)
    private var lat by mutableStateOf(Locations.USERLOCATION.getLocation().latitude)
    private var long by mutableStateOf(Locations.USERLOCATION.getLocation().longitude)

    private var exitRequest by mutableStateOf(false)
    private var startCamera by mutableStateOf(false)

    private val geoCodingHelper by lazy {
        GeoCodingHelper(this)
    }

    private lateinit var newBuildingViewModel: NewBuildingViewModel
    private var restoredImagePath by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        newBuildingViewModel = viewModels<NewBuildingViewModel> {
            NewBuildingViewModel.provideFactory(ArenaService.getInstance(this), StudentHubService.getInstance(this))
        }.value

        setContent {
            if(!(title.isNullOrEmpty()) && title.length < 31&&!(description.isNullOrEmpty())&&description.length<61&&lat!=0.0&&long!=0.0){
                confirmRequest = true
                println("correct")
                println("${description.length} die länge")
            } else {
                confirmRequest = false
                println("incorrect")
                println("${description.length} die länge")
            }

            val receivedIntent = intent
            val receivedBundle = receivedIntent.getBundleExtra("myBundle")

            if (receivedBundle != null) {
                // Extrahieren Sie den Base64-String des aufgenommenen Bilds
                val base64Image = receivedBundle.getString("base64Image")

                // Konvertieren Sie den Base64-String zurück in ein Bild, wenn vorhanden
                if (!base64Image.isNullOrBlank()) {
                    convertBase64ToImage(base64Image)
                }
            }

            geoCodingHelper.getAddressFromLocation(
                lat,long
            )
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White) // Hier wird der Hintergrund weiß gemacht
                ) {
                    Box(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column {
                            TitleAndDescription()
                            SelectBuilding()
                            Camera()
                            Location()
                            Confirm()
                        }
                    }
                }
            }
            if (exitRequest) {
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
                exitRequest = false
                finish()
            }
            if (startCamera) {
                val intent = Intent(this, CameraActivity::class.java)
                this.startActivity(intent)
                startCamera = false
                finish()
            }
        }
    }

    @Composable
    fun MenuHeader() {
        Text(
            text = "New Building",
            fontSize = 50.sp, // Adjust the font size as needed
            fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TitleAndDescription() {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth()
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Column {
                Box(modifier = Modifier
                    .clickable {}
                    .fillMaxWidth()) {
                    Row(modifier = Modifier.padding(all = 8.dp)) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Title:",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Add an input box (TextField) here
                            OutlinedTextField(
                                value = title,
                                onValueChange = { newBuildingSingleTon.setTitle(it)},
                                label = { Text("Title") },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                leadingIcon = {
                                    Icon(Icons.Filled.Info, contentDescription = "Title")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Description:",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Add an input box (TextField) here
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it
                                    newBuildingSingleTon.setDescription(it)},
                                label = { Text("Description") },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                leadingIcon = {
                                    Icon(Icons.Filled.Create, contentDescription = "Description")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SelectBuilding() {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth()
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .clickable {}
                        .fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(all = 8.dp)) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Building:",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Row {
                                Spacer(modifier = Modifier.height(8.dp))
                                // Add two images side by side
                                Image(
                                    painter = if(building== BuildingType.ARENA){painterResource(id = R.drawable.arenaconfirm)} else {painterResource(id = R.drawable.arena)},
                                    contentDescription = "arena", // Add appropriate content description
                                    modifier = Modifier
                                        .size(175.dp) // Adjust the size as needed
                                        .padding(horizontal = 8.dp)
                                        .clickable {/*newBuildingSingleTon.setBuilding(BuildingType.ARENA)*/}
                                        .offset(x = (1).dp)
                                )

                                Image(
                                    painter = if(building== BuildingType.STUDENTHUB){painterResource(id = R.drawable.studenthubconfirm)} else {painterResource(id = R.drawable.store)},
                                    contentDescription = "studenthub", // Add appropriate content description
                                    modifier = Modifier
                                        .size(175.dp) // Adjust the size as needed
                                        .padding(horizontal = 8.dp)
                                        .clickable {/*newBuildingSingleTon.setBuilding(BuildingType.STUDENTHUB)*/}
                                        .offset(x = (1).dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Camera() {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .clickable {startCamera = true}
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Photo:",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "click to take a photo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!restoredImagePath.isNullOrBlank()) {
                        // Zeigen Sie hier das wiederhergestellte Foto an
                        Image(
                            painter = rememberImagePainter(data = restoredImagePath),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }

    fun convertBase64ToImage(base64String: String) {
        try {
            val fileContent = Base64.decode(base64String, Base64.DEFAULT)
            val outputPath = "your_output_path.jpg" // Passen Sie den Dateipfad nach Bedarf an
            val outputStream = FileOutputStream(outputPath)
            outputStream.write(fileContent)
            outputStream.close()
            restoredImagePath = outputPath
        } catch (e: IOException) {
            e.printStackTrace()
            restoredImagePath = null
        }
    }
    @Composable
    fun Location() {
        val buildingAddress = geoCodingHelper.getAddressFromLocation(Locations.USERLOCATION.getLocation().latitude,Locations.USERLOCATION.getLocation().longitude)

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
                Image(
                    painter = if(lat!= 0.0 && long != 0.0){painterResource(id = R.drawable.location)} else {painterResource(id = R.drawable.warning)},
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Location of your building",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = if(lat!= 0.0 && long != 0.0){"Your building will be placed at:\n$buildingAddress\nWe will use your latest location on the map."} else {"Location is invalid!\nGo to map and load your current location."},
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    @Composable
    fun Confirm() {
        val buildingAddress = geoCodingHelper.getAddressFromLocation(Locations.USERLOCATION.getLocation().latitude,Locations.USERLOCATION.getLocation().longitude)
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .clickable {
                    if (confirmRequest) {
                        if (building == BuildingType.ARENA) {
                            newBuildingViewModel.addArena(
                                title,
                                description,
                                lat.toString(),
                                long.toString()
                            )
                        } else {
                            newBuildingViewModel.addStudentHub(
                                title,
                                description,
                                lat.toString(),
                                long.toString()
                            )
                        }
                        exitRequest = true
                        MapSaver.ARENA.setMarker(ArrayList<MarkerData?>())
                        MapSaver.STUDENTHUB.setMarker(ArrayList<MarkerData?>())
                        finish()
                    } else {
                    }
                }

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Image(
                    painter = if(confirmRequest){painterResource(id = R.drawable.checked)} else {painterResource(id = R.drawable.warning)},
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Confirm new building:",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Title (max 30char.): $title\nDescription (max 60 char.): $description\nBuilding Type: ${if(building== BuildingType.STUDENTHUB){"Student Hub"} else {"Arena"}}\nLocations: $buildingAddress",
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
