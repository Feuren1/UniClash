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
import androidx.compose.runtime.mutableIntStateOf
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

    private val studentHubViewModel by viewModels<StudentHubViewModel> {
        StudentHubViewModel.provideFactory(StudentHubService.getInstance(this), Application())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        //initializes a viewmodel for further use. Uses the StudentHubService in order to talk to the backend
//        val studentHubViewModel: StudentHubViewModel by viewModels(factoryProducer = {
//            StudentHubViewModel.provideFactory(StudentHubService.getInstance(this), Application())
//        })

        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    studentHubViewModel.loadStudent()
//                    studentHubViewModel.loadItemsFromStudent()
                    studentHubViewModel.loadItemTemplates()

                    StudentHubScreen(modifier = Modifier.fillMaxSize(), studentHubViewModel = studentHubViewModel)
                }
            }
        }
    }
}

@Composable
fun StudentHubScreen(modifier: Modifier = Modifier,
    studentHubViewModel: StudentHubViewModel = viewModel()) {

    var context = LocalContext.current

    val itemTemplatesState by studentHubViewModel.itemTemplates.collectAsState()
    var itemTemplateList = itemTemplatesState.itemTemplates

    val studentState by studentHubViewModel.student.collectAsState()
    val student = studentState.student

    var itemCost by rememberSaveable { mutableIntStateOf(0) }
    var buyingStatus by rememberSaveable { mutableStateOf("nothing") }
    println("1")
    Column(modifier = modifier) {

        Text("Credits: TODO: get credits => make a loadCredit function in viewmodel")
        println("2")
//        if (student!!.credits - itemCost >= 0) {
//            println("3")
//            Text("You have last bought: $buyingStatus.")
//
//        } else {
//            buyingStatus = "Not enough credits!"
//            Text("$buyingStatus")
//            println("4")
//        }

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
                buyingStatus = itemTemplate.name
                studentHubViewModel.buyItem(itemTemplate.id)
                itemCost = itemTemplate.cost
            })
    }
}

@Composable
fun ItemList(itemTemplateList: List<ItemTemplate>, onButtonClicked: (ItemTemplate) -> Unit, modifier: Modifier = Modifier) {
    println("5")
    LazyColumn(modifier = modifier) {

        items(items = itemTemplateList, key = { item -> item.name }) {

                item ->
            ItemRow(itemTemplate = item,
                onButtonClicked = {
                    onButtonClicked(item)
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
    println("6")
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
                onButtonClicked(itemTemplate)
            }) {

            Text(text = "Buy")
        }
    }
}