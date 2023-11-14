package project.main.uniclash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.StudentHubViewModel

class StudentHubActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initializes a viewmodel for further use. Uses the StudentHubService in order to talk to the backend
        val studentHubViewModel: StudentHubViewModel by viewModels(factoryProducer = {
            StudentHubViewModel.provideFactory(StudentHubService.getInstance(this))
        })
        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudentHub(modifier = Modifier.fillMaxSize(), studentHubViewModel)
                }
            }
        }
    }
}

@Composable
fun StudentHub(modifier: Modifier = Modifier, studentHubViewModel: StudentHubViewModel) {

    val studentHubState by studentHubViewModel.studentHub.collectAsState()
    studentHubViewModel.loadStudentHub(1)

    val itemsHubState by studentHubViewModel.items.collectAsState()
    studentHubViewModel.loadItems()

    var studentHub = studentHubState.studentHub
    var itemList = itemsHubState.items

    Column() {

        Button(onClick = {
            println("StudentHub: $studentHub")
            println("Items: $itemList")
        } ) {

            Text(text = "Debug")
        }

        Row() {


        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    UniClashTheme {
//        StudentHub()
//    }
//}