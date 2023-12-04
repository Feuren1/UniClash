package project.main.uniclash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import project.main.uniclash.datatypes.Arena
import project.main.uniclash.datatypes.Student
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
                    if(arenaUIState.arena!=null){
                        studentViewModel.loadStudent(arenaUIState.arena!!.studentId)
                    }

                    // Check if the arena and arenas are not null before displaying
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        arenaUIState.arena?.let { arena ->
                            ArenaDetails(arena, studentUIState.student)
                        }

                        arenasUIstate.arenas?.let { arenas ->
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                items(arenas) { arena ->
                                    ArenaListItem(arena = arena)
                                }
                            }
                        }

                        // Add a button or other UI elements as needed
                    }
                }
            }
        }
    }
}

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
}