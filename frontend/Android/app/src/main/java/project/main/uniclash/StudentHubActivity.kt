package project.main.uniclash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import project.main.uniclash.datatypes.Item
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

//    val studentHubState by studentHubViewModel.studentHub.collectAsState()
//    studentHubViewModel.loadStudentHub(1)
//
//    val itemsHubState by studentHubViewModel.items.collectAsState()
//    studentHubViewModel.loadItems()
//
//    var studentHub = studentHubState.studentHub
//    var itemList = itemsHubState.items

    var itemList = List(2) { i -> Item("Item$i", 5) }

    println("Items: $itemList")

    Column(modifier = modifier) {

        

//        Button(onClick = {
//            println("Items: $itemList")
//            println("StudentHub: $studentHub")}) {
//            Text(text = "Debug")
//        }

        ItemList(itemList, onButtonClicked = {/*TODO: Buying the item*/})
    }
}

@Composable
fun ItemList(itemList: List<Item>, onButtonClicked: () -> Unit, modifier: Modifier = Modifier) {

    LazyColumn(modifier = modifier) {

        items(items = itemList, key = {item -> item.name}) {

            item -> ItemRow(itemName = item.name, itemCost = item.cost, onButtonClicked = {/*TODO: Buying the item*/})
        }
    }
}

@Composable
fun ItemRow(itemName: String, itemCost: Int, onButtonClicked: () -> Unit, modifier: Modifier = Modifier) {

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {

        Text(modifier = Modifier
            .weight(1f)
            .padding(start = 16.dp),
            text = "Item: $itemName = Cost: $itemCost")
        
        Button(modifier = Modifier
            .weight(1f)
            .padding(end = 16.dp),
            onClick = onButtonClicked) {
            
            Text(text = "Buy")
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