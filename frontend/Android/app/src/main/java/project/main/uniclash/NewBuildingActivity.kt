package project.main.uniclash

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import project.main.uniclash.datatypes.CustomColor
import project.main.uniclash.newBuildingLogic.NewBuildingSingleTon
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.map.GeoCodingHelper
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.StudentService
import project.main.uniclash.viewmodels.NewBuildingViewModel
import java.io.ByteArrayOutputStream


enum class BuildingType(){
    ARENA,
    STUDENTHUB;
}

class NewBuildingActivity : ComponentActivity() {
    private val newBuildingSingleTon = NewBuildingSingleTon.instance
    private var title by mutableStateOf(newBuildingSingleTon.getTitle())
    private var description by mutableStateOf(newBuildingSingleTon.getDescription())
    private var building by mutableStateOf(newBuildingSingleTon.getBuilding())
    private var confirmRequest by mutableStateOf(false)
    private var lat by mutableStateOf(Locations.USERLOCATION.getLocation().latitude)
    private var long by mutableStateOf(Locations.USERLOCATION.getLocation().longitude)
    private var capturedImagePath by mutableStateOf("")
    private var freebuildings by mutableStateOf(0)

    private var exitRequest by mutableStateOf(false)
    private var startCamera by mutableStateOf(false)

    private val geoCodingHelper by lazy {
        GeoCodingHelper(this)
    }

    private lateinit var newBuildingViewModel: NewBuildingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.getStringExtra("capturedImagePath") != null) {
            capturedImagePath = intent.getStringExtra("capturedImagePath")!!
        }

        newBuildingViewModel = viewModels<NewBuildingViewModel> {
            NewBuildingViewModel.provideFactory(ArenaService.getInstance(this), StudentHubService.getInstance(this), StudentService.getInstance(this),Application())
        }.value

        setContent {
            confirmRequest = !(title.isEmpty()) && title.length < 31&&!(description.isNullOrEmpty())&&description.length<61&&lat!=0.0&&long!=0.0&&!(capturedImagePath.isNullOrBlank())&&freebuildings>0

            newBuildingViewModel.loadStudentFreeBuildingInfo()

            geoCodingHelper.getAddressFromLocation(
                lat,long
            )
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
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
                        .background(Color.Black) // Hier wird der Hintergrund weiß gemacht
                ) {
                    Box(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column {
                            Level()
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
                val intent = Intent(this, MapActivity::class.java)
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
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
        )
    }

    @Composable
    fun Level() {
        val freeBuildingsInformation by newBuildingViewModel.studentFreeBuildingInfo.collectAsState()
        freebuildings = freeBuildingsInformation.level / 5 *2-freeBuildingsInformation.placedBuildings
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .border(
                    3.dp,
                    CustomColor.Purple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Image(
                    painter = if(freebuildings>0){painterResource(id = R.drawable.checked)} else {painterResource(id = R.drawable.warning)},
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Student Level:",
                        fontSize = 18.sp,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "You can place 2 buildings, by reaching every fifth level.\n Free buildings left: $freebuildings\nAlready placed: ${freeBuildingsInformation.placedBuildings}\nLevel: ${freeBuildingsInformation.level} ",
                        fontSize = 12.sp,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TitleAndDescription() {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth()
                .background(
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .border(
                    3.dp,
                    CustomColor.Purple.getColor(),
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
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Add an input box (TextField) here
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it
                                    newBuildingSingleTon.setTitle(it)},
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
                                    .padding(8.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedLabelColor = Color.Black,
                                    focusedBorderColor = Color.Gray,
                                    unfocusedBorderColor = Color.Black,
                                    containerColor = Color.White)

                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Description:",
                                fontSize = 18.sp,
                                color = Color.White,
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
                                    .padding(8.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedLabelColor = Color.Black,
                                    focusedBorderColor = Color.Gray,
                                    unfocusedBorderColor = Color.Black,
                                    containerColor = Color.White)
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
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .border(
                    3.dp,
                    CustomColor.Purple.getColor(),
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
                                color = Color.White,
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
                                        .clickable {
                                            building = BuildingType.ARENA
                                            newBuildingSingleTon.setBuilding(BuildingType.ARENA)
                                        }
                                        .offset(x = (1).dp)
                                )

                                Image(
                                    painter = if(building== BuildingType.STUDENTHUB){painterResource(id = R.drawable.studenthubconfirm)} else {painterResource(id = R.drawable.store)},
                                    contentDescription = "studenthub", // Add appropriate content description
                                    modifier = Modifier
                                        .size(175.dp) // Adjust the size as needed
                                        .padding(horizontal = 8.dp)
                                        .clickable {
                                            building = BuildingType.STUDENTHUB
                                            newBuildingSingleTon.setBuilding(BuildingType.STUDENTHUB)
                                        }
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
                .clickable { startCamera = true }
                .background(
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .border(
                    3.dp,
                    CustomColor.Purple.getColor(),
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
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "click to take a photo\n$capturedImagePath",
                        fontSize = 12.sp,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!capturedImagePath.isNullOrBlank()) {
                        val originalBitmap = BitmapFactory.decodeFile(capturedImagePath)
                        Image(
                            painter = rememberImagePainter(originalBitmap),
                            contentDescription = null,
                            modifier = Modifier
                                .size(300.dp)
                                .padding(8.dp)
                        )
                    }
                }
            }
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
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .border(
                    3.dp,
                    CustomColor.Purple.getColor(),
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
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = if(lat!= 0.0 && long != 0.0){"Your building will be placed at:\n$buildingAddress\nWe will use your latest location on the map."} else {"Location is invalid!\nGo to map and load your current location."},
                        fontSize = 12.sp,
                        color = Color.White,
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
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .border(
                    3.dp,
                    CustomColor.Purple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                )// Hintergrundfarbe und abgeflachte Ecken
                .clickable {
                    if (confirmRequest) {
                        confirmRequest = false
                        val originalBitmap = BitmapFactory.decodeFile(capturedImagePath)
                        val targetWidth = originalBitmap.width * 400 / originalBitmap.height + 25
                        val targetHeight = 400
                        val scaledBitmap = Bitmap.createScaledBitmap(
                            originalBitmap,
                            targetWidth,
                            targetHeight,
                            false
                        )
                        val outputStream = ByteArrayOutputStream()
                        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 5, outputStream)
                        val bitmapBytes = outputStream.toByteArray()
                        val base64EncodedBitmap = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)

                        if (building == BuildingType.ARENA) {
                            newBuildingViewModel.addArena(
                                title,
                                description,
                                lat.toString(),
                                long.toString(),
                                base64EncodedBitmap
                            )
                        } else {
                            newBuildingViewModel.addStudentHub(
                                title,
                                description,
                                lat.toString(),
                                long.toString(),
                                base64EncodedBitmap
                            )
                        }
                        newBuildingSingleTon.setTitle("")
                        newBuildingSingleTon.setDescription("")
                        newBuildingSingleTon.setBuilding(BuildingType.ARENA)

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
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Title (max 30char.): $title\nDescription (max 60 char.): $description\nBuilding Type: ${if(building== BuildingType.STUDENTHUB){"Student Hub"} else {"Arena"}}\nLocations: $buildingAddress\nPicture: ${if(capturedImagePath.isNullOrBlank()){"missing"}else{"available"}}\nStudent Level high enough: ${if(freebuildings>0){"yes"}else{"no"}}",
                        fontSize = 12.sp,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
