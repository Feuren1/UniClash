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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.datatypes.ItemTemplate
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
                    StudentHubScreen(
                        modifier = Modifier.fillMaxSize(),
                        studentHubViewModel = studentHubViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun StudentHubScreen(modifier: Modifier = Modifier,
    studentHubViewModel: StudentHubViewModel = viewModel()) {

    var context = LocalContext.current

//    val studentHubsState by studentHubViewModel.studentHubs.collectAsState()
//    studentHubViewModel.loadStudentHubs()
//    var studentHubList = studentHubsState.studentHubs

    val itemTemplatesState by studentHubViewModel.itemTemplates.collectAsState()
    studentHubViewModel.loadItemTemplates()
    var itemTemplateList = itemTemplatesState.itemTemplates

    val studentState by studentHubViewModel.student.collectAsState()
    studentHubViewModel.loadStudent(5)

    var creditValidation by rememberSaveable { mutableStateOf(true) }
    var buyingStatus by rememberSaveable { mutableStateOf("nothing") }

    Column(modifier = modifier) {

        Text("Credits: ${studentState.student?.credits}")

        if (creditValidation) {

            Text("You have last bought: $buyingStatus.")

        } else {
            buyingStatus = "Not enough credits!"
            Text("$buyingStatus")
        }

        Button(onClick = {
            println("Items: $itemTemplateList")
//            println("StudentHubs: $studentHubList")
            }) {
            Text(text = "Debug")
        }

        //Exit Box, image and position:
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
                        val intent = Intent(context, MenuActivity::class.java)
                        context.startActivity(intent)
                    }
                    .align(Alignment.TopEnd)
            )
        }

        ItemList(itemTemplateList,
            onButtonClicked = { itemTemplate ->
                println("ID: ${itemTemplate.id}, Name: ${itemTemplate.name}, Cost: ${itemTemplate.cost}")
                buyingStatus = itemTemplate.name
                println("Before buyItem")
                creditValidation = studentHubViewModel.buyItem(studentState.student , itemTemplate.id, itemTemplate.cost)
                println("After buyItem")
                println("Buy item clicked in StudentHub")
            })
    }
}

@Composable
fun ItemList(itemTemplateList: List<ItemTemplate>, onButtonClicked: (ItemTemplate) -> Unit, modifier: Modifier = Modifier) {

    LazyColumn(modifier = modifier) {

        items(items = itemTemplateList, key = { item -> item.name }) {

                item ->
            ItemRow(itemTemplate = item,
                onButtonClicked = {
                    println("BEFORE Buy Item clicked in ItemList")
                    onButtonClicked(item)
                    println("AFTER Buy Item clicked in ItemList")
                })
        }
    }
}

@Composable
fun ItemRow(
    itemTemplate: ItemTemplate,
    onButtonClicked: (ItemTemplate) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {

        Text(modifier = Modifier
            .weight(1f)
            .padding(start = 16.dp),
            text = "${itemTemplate.name} for ${itemTemplate.cost}"
        )

        Button(modifier = Modifier
            .weight(1f)
            .padding(end = 16.dp),
            onClick = {
                println("BEFORE Buy button clicked in ItemRow")
                onButtonClicked(itemTemplate)
                println("AFTER Buy button clicked in ItemRow")
            }) {

            Text(text = "Buy")
        }
    }
}