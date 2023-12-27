package project.main.uniclash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.StudentService
import project.main.uniclash.ui.theme.UniClashTheme
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
        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val studentUIState by studentViewModel.student.collectAsState()
                    val arenaUIState by arenaViewModel.arena.collectAsState()
                    val arenasUIstate by arenaViewModel.arenas.collectAsState()
                    if (arenaUIState.arena != null) {
                        studentViewModel.loadStudent(arenaUIState.arena!!.studentId)
                    }


                    // Check if the arena and arenas are not null before displaying
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        //arenaUIState.arena?.let { arena ->
                           // ArenaDetails(arena, studentUIState.student)
                        //}
                        showArena()
                        startBattleButton()
                        addCritterToArenaButton()
                        arenasUIstate.arenas?.let { arenas ->
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                items(arenas) { arena ->
                                    if (arena != null) {
                                        //ArenaListItem(arena = arena)
                                    } //add not null check due to map activity changes
                                }
                            }
                        }

                        // Add a button or other UI elements as needed
                    }
                }
            }

        }
    }

    /*
@Composable
fun ArenaDetails(arena: Arena, student: Student?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text("Name: ${arena.name}")
            Spacer(modifier = Modifier.size(8.dp))
            Text("Description: ${arena.description}")
            Spacer(modifier = Modifier.size(8.dp))
            Text("Student: $student")
            // Add more details as needed
        }
    }
}

@Composable
fun ArenaListItem(arena: Arena) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text("Name: ${arena.name}")
            Spacer(modifier = Modifier.size(8.dp))
            Text("Description: ${arena.description}")
            // Add more details as needed
        }
    }
}*/
    @Composable
    fun showArena() {
        Column {
            Text(text = "${arenaViewModel.getselectedArena()!!.arena!!.name}\n " +
                    "${arenaViewModel.getselectedArena()!!.arena!!.description}\n ")
            Image(
                painter = rememberImagePainter(arenaViewModel.getselectedArena()!!.pic),
                contentDescription = null, // Provide a proper content description if needed
                modifier = Modifier.size(235.dp) // Adjust size as needed
            )
        }
    }
    @Composable
    fun startBattleButton(){
        Button(onClick = {
            val intent = Intent(this, Battle::class.java)
            // creating a bundle object
            val bundle = Bundle()
            // storing the string value in the bundle
            // which is mapped to key
            bundle.putString("CpuCritterId", "${arenaViewModel.getselectedArena()!!.arena!!.critterId}")

            intent.putExtras(bundle)
            startActivity(intent)
        }) {

        }
    }
    @Composable
    fun addCritterToArenaButton(){
        Button(onClick = {
            val intent = Intent(this, AddCritterToArenaActivity::class.java)
            // creating a bundle object
            val bundle = Bundle()
            // storing the string value in the bundle
            // which is mapped to key
            var arenaIdInt = arenaViewModel.getselectedArena()!!.arena!!.id.toInt()
            bundle.putInt("ArenaID", arenaViewModel.getselectedArena()!!.arena!!.id)

            intent.putExtras(bundle)
            startActivity(intent)
        }) {

        }
    }

}