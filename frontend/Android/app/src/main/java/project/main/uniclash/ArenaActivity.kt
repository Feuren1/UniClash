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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext


import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.Student
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.StudentService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.datatypes.CustomColor
import project.main.uniclash.viewmodels.ArenaViewModel
import project.main.uniclash.viewmodels.StudentViewModel

class ArenaActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)

    private val arenaViewModel by viewModels<ArenaViewModel> {
        ArenaViewModel.provideFactory(ArenaService.getInstance(this))
    }

    private val studentViewModel by viewModels<StudentViewModel> {
        StudentViewModel.provideFactory(StudentService.getInstance(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userDataManager: UserDataManager by lazy {
            UserDataManager(Application())
        }
        val studentId: Int?
        runBlocking {
            studentId = userDataManager.getStudentId()
        }



        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Set the background image
                        Image(
                            painter = painterResource(id = R.drawable.arenabackground),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        val studentUIState by studentViewModel.student.collectAsState()
                        val arenaUIState by arenaViewModel.arena.collectAsState()
                        val arenasUIState by arenaViewModel.arenas.collectAsState()
                        val arenaCritterUIState by arenaViewModel.critterUsable.collectAsState()

                        if(arenaUIState.arena != null) {
                            arenaViewModel.loadArenaCritterFromBegin()
                        if (arenaViewModel.getselectedArena() != null) {
                            studentViewModel.loadStudentFromBegin(arenaViewModel.getselectedArena()!!.arena!!.studentId)
                        }
                                Image(
                                    painter = painterResource(id = R.drawable.exit),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .offset(x = (-10).dp, y = 10.dp)
                                        .size(40.dp)
                                        .clickable {
                                            exitRequest = true
                                        }
                                        .align(Alignment.TopEnd)
                                )

                            // Check if the arena and arenas are not null before displaying
                            Column(
                                modifier = Modifier
                                    .offset(y = 40.dp)
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {

                                //arenaUIState.arena?.let { arena ->
                                // ArenaDetails(arena, studentUIState.student)
                                //}
                                showArena()
                                if (studentUIState.student != null) {
                                    StudentDetail(studentUIState.student!!)
                                }

                                if (arenaViewModel.getselectedArena()!!.arena!!.critterId == 0 && arenaViewModel.getselectedArena()!!.arena!!.studentId == studentId) {
                                    addCritterToArenaButton()
                                }
                                if (arenaViewModel.getselectedArena()!!.arena!!.critterId != 0) {
                                    println("kommt hier hin")
                                    if (arenaCritterUIState.critterUsable != null) {
                                        CritterDetail(arenaCritterUIState.critterUsable!!)
                                    }
                                }
                                if (arenaViewModel.getselectedArena()!!.arena!!.studentId != studentId && arenaViewModel!!.getselectedArena()!!.arena!!.critterId >0) {
                                    startBattleButton()
                                } else if(arenaViewModel.getselectedArena()!!.arena!!.studentId == studentId) {
                                    Text("You own this arena!")
                                }
                                Row(Modifier.fillMaxSize()) {
                                    GifImage(
                                        gifName = "pokemonwalking",
                                        modifier = Modifier.size(140.dp)
                                    )
                                }
                                ArenaReload()
                                // Add a button or other UI elements as needed
                            }
                        }
                    }
                }
            }
            if (exitRequest) {
                val intent = Intent(this, MapActivity::class.java)
                this.startActivity(intent)
                finish()
                exitRequest = false
            }
        }
    }


    @Composable
    fun showArena() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = arenaViewModel.getselectedArena()?.arena?.name ?: "",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = arenaViewModel.getselectedArena()?.arena?.description ?: "",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberImagePainter(arenaViewModel.getselectedArena()?.pic),
                contentDescription = null, // Provide a proper content description if needed
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
        }
    }

    @Composable
    fun ArenaReload() {
        LaunchedEffect(Unit) {
            var i = 100
            while (i > 0) {
                println("reloadArena")
                delay(5000)
                arenaViewModel.loadArena(arenaViewModel.getselectedArena()!!.arena!!.id)
                arenaViewModel.loadArenaCritter()
                studentViewModel.loadStudent(arenaViewModel.getselectedArena()!!.arena!!.studentId)
                i--
            }
        }
    }

    @Composable
    fun startBattleButton() {
        if(arenaViewModel.checkIfCritterIsSelected()) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = CustomColor.DarkPurple.getColor()),
                onClick = {
                    val intent = Intent(this, FinalBattleActivity::class.java)
                    // creating a bundle object
                    val bundle = Bundle()
                    // storing the string value in the bundle
                    // which is mapped to key
                    bundle.putInt(
                        "CpuCritterId",
                        arenaViewModel.getselectedArena()?.arena?.critterId ?: 0
                    )
                    bundle.putInt(
                        "ArenaID",
                        arenaViewModel.getselectedArena()?.arena?.id?.toInt() ?: 0
                    )
                    intent.putExtras(bundle)
                    startActivity(intent)
                    finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Start Battle", style = MaterialTheme.typography.labelMedium, color = Color.White)
            }
        } else {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = CustomColor.DarkPurple.getColor()),
                onClick = {
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("No Critter to fight selected.", style = MaterialTheme.typography.labelMedium, color = Color.White)
            }
        }
    }

    @Composable
    fun addCritterToArenaButton() {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = CustomColor.DarkPurple.getColor()),
            onClick = {
                val intent = Intent(this, AddCritterToArenaActivity::class.java)
                // creating a bundle object
                val bundle = Bundle()
                // storing the string value in the bundle
                // which is mapped to key
                bundle.putInt("ArenaID", arenaViewModel.getselectedArena()?.arena?.id?.toInt() ?: 0)
                intent.putExtras(bundle)
                startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Add Critter to Arena", style = MaterialTheme.typography.labelMedium, color = Color.White)
        }
    }
    @Composable
    fun CritterDetail(critter: CritterUsable) {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth()
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    // Handle click event
                    // You can navigate to CritterProfileActivity or perform other actions
                }
        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                val context = LocalContext.current
                val name: String = critter.name.lowercase()
                val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
                Image(
                    painter = painterResource(if (resourceId > 0) resourceId else R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = critter.name,
                        fontSize = 18.sp,
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row {
                        Text(
                            text = "Level: ${critter.level} ",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Stats: HP: ${critter.hp} ATK: ${critter.atk} DEF: ${critter.def} SPD: ${critter.spd}",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    @Composable
    fun StudentDetail(student: Student) {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth()
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    // Handle click event
                    // You can navigate to CritterProfileActivity or perform other actions
                }
        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Owner ID: " + student.id.toString(),
                        fontSize = 18.sp,
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row {
                        Text(
                            text = "Student Level: ${student.level} ",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Row{
                        Text(
                            text = "Stats: Amount of Buildings Placed: ${student.placedBuildings} Credits: ${student.credits}",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

}
