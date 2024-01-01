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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity

import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService

import androidx.compose.ui.platform.LocalUriHandler

import androidx.compose.ui.platform.LocalView


import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import kotlinx.coroutines.runBlocking
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.Student
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.StudentService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.viewmodels.ArenaViewModel
import project.main.uniclash.viewmodels.StudentViewModel

class ArenaActivity : ComponentActivity() {

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
                        val arenasUIstate by arenaViewModel.arenas.collectAsState()
                        val arenaCritterUIState by arenaViewModel.critterUsable.collectAsState()


                        if (arenaViewModel.getselectedArena() != null) {
                            studentViewModel.loadStudent(arenaViewModel.getselectedArena()!!.arena!!.studentId)
                        }

                        // Check if the arena and arenas are not null before displaying
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {

                            //arenaUIState.arena?.let { arena ->
                            // ArenaDetails(arena, studentUIState.student)
                            //}
                            showArena()
                            if(studentUIState.student!=null){
                                StudentDetail(studentUIState.student!!)
                            }

                            if(arenaViewModel.getselectedArena()!!.arena!!.critterId==0 && arenaViewModel.getselectedArena()!!.arena!!.studentId == studentId) {
                                addCritterToArenaButton()
                            }
                            if(arenaViewModel.getselectedArena()!!.arena!!.critterId !=0) {
                                if(arenaCritterUIState.critterUsable != null){
                                    CritterDetail(arenaCritterUIState.critterUsable!!)
                                }
                            }
                            if(arenaViewModel.getselectedArena()!!.arena!!.studentId != studentId){
                                startBattleButton()
                            }
                            else {
                                Text ("You own this arena!")
                            }
                            Row(Modifier.fillMaxSize()){
                                GifImage(gifName = "pokemonwalking", modifier = Modifier.size(140.dp) )
                            }

                            // Add a button or other UI elements as needed
                        }
                    }
                }
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
    fun startBattleButton() {
        Button(
            onClick = {
                val intent = Intent(this, FinalBattleActivity::class.java)
                // creating a bundle object
                val bundle = Bundle()
                // storing the string value in the bundle
                // which is mapped to key
                bundle.putInt("CpuCritterId", arenaViewModel.getselectedArena()?.arena?.critterId ?: 0)
                bundle.putInt("ArenaID", arenaViewModel.getselectedArena()?.arena?.id?.toInt() ?: 0)
                intent.putExtras(bundle)
                startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Start Battle", style = MaterialTheme.typography.labelMedium)
        }
    }

    @Composable
    fun addCritterToArenaButton() {
        Button(
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
            Text("Add Critter to Arena", style = MaterialTheme.typography.labelMedium)
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
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row {
                        Text(
                            text = "Level: ${critter.level} ",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Stats: HP: ${critter.hp} ATK: ${critter.atk} DEF: ${critter.def} SPD: ${critter.spd}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
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
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row {
                        Text(
                            text = "Student Level: ${student.level} ",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Row{
                        Text(
                            text = "Stats: Amount of Buildings Placed: ${student.placedBuildings} Credits: ${student.credits}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

}
