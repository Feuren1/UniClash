package project.main.uniclash

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.datatypes.ItemTemplate
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.StudentHubViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import project.main.uniclash.datatypes.CustomColor


class StudentHubActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val studentHubViewModel by viewModels<StudentHubViewModel> {
            StudentHubViewModel.provideFactory(StudentHubService.getInstance(this), Application())
        }

        setContent {
            UniClashTheme {
                var isLoading by rememberSaveable { mutableStateOf(true) }

                Box(modifier = Modifier.fillMaxSize()) {

                    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {

                        val studentState by studentHubViewModel.student.collectAsState()
                        isLoading = studentState.isLoading

                        StudentHubScreen(modifier = Modifier.fillMaxSize(),studentHubViewModel = studentHubViewModel,isLoading)
                    }
                }
            }
        }
    }
}

@Composable
fun StudentHubScreen(
    modifier: Modifier = Modifier,
    studentHubViewModel: StudentHubViewModel = viewModel(),
    isLoading: Boolean
) {

    var context = LocalContext.current

    val buyItemResponse by studentHubViewModel.buyItemResponse.collectAsState()

    val itemTemplatesState by studentHubViewModel.itemTemplates.collectAsState()
    var itemTemplateList = itemTemplatesState.itemTemplates

    val studentState by studentHubViewModel.student.collectAsState()
    val student = studentState.student

    var creditValidation = studentHubViewModel.buyItemSuccessful.collectAsState()
    var message = studentHubViewModel.message.collectAsState()

    var buyingStatus by remember { mutableStateOf("nothing") }
    var credits by remember { mutableIntStateOf(0) }

    if (student != null) {

        credits = student.credits
    }

    // Loading circle
    if (isLoading) {

        LoadingCircle(modifier)
    }

    Column(modifier = modifier) {

        Row {

            if (student != null) {

                Credits(modifier, credits)

            } else {
                // This branch should theoretically never happen, but logs a message, just in case
                Log.e("", "Unexpected null student case")
            }

            Title(modifier)

            Exit(modifier, context)
        }

        ItemList(
            itemTemplateList,
            onButtonClicked = { itemTemplate ->
                buyingStatus = itemTemplate.name
                studentHubViewModel.buyItem(itemTemplate.id, itemTemplate.name)
            },
            isLoading = isLoading
        )
    }
}

@Composable
fun LoadingCircle(modifier: Modifier) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent) // Transparent background
            .wrapContentSize(Alignment.Center)
            .zIndex(2f)
    ) {
        Box(
            modifier = Modifier
                .background(CustomColor.Purple.getColor()) // Change the background color to light gray
                .padding(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = CustomColor.DarkPurple.getColor()
            )
        }
    }
}

@Composable
fun Title(modifier: Modifier) {
    Text(
        text = "Student Hub",
        fontSize = 35.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 5.dp)
    )
}

@Composable
fun ItemList(
    itemTemplateList: List<ItemTemplate>,
    onButtonClicked: (ItemTemplate) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {

        items(items = itemTemplateList, key = { item -> item.name }) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        CustomColor.DarkPurple.getColor(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        3.dp,
                        CustomColor.Purple.getColor(),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                ItemRow(itemTemplate = item, onButtonClicked = onButtonClicked, isLoading = isLoading)
            }
        }
    }
}

@Composable
fun ItemRow(
    itemTemplate: ItemTemplate,
    onButtonClicked: (ItemTemplate) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(all = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        ItemImage(itemTemplate, modifier)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = CustomColor.Purple.getColor()),
            modifier = Modifier
                .width(100.dp)
                .padding(end = 16.dp),
            onClick = {
                if (!isLoading) {
                    onButtonClicked(itemTemplate)
                }
            },
            enabled = !isLoading // Enable the button only if not loading
        ) {
            Text(text = "Buy", color =Color.White)
        }
    }
}

@Composable
fun ItemImage(itemTemplate: ItemTemplate, modifier: Modifier) {

    val context = LocalContext.current
    val name: String = itemTemplate.name.lowercase()
    val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
    Image(
        painter = painterResource(
            if (resourceId > 0) {
                resourceId
            } else {
                R.drawable.icon
            }
        ),
        contentDescription = null,
        modifier = Modifier
            .size(60.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Column {
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = itemTemplate.name,
            fontSize = 18.sp,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "Price: ${itemTemplate.cost}",
            fontSize = 12.sp,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun Credits(modifier: Modifier, credits: Int) {

    // Get the context and the resource identifier for the "coin" drawable
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier("coin", "drawable", context.packageName)

    // Use the resourceId if it's greater than 0, otherwise use a default icon
    val iconResourceId = if (resourceId > 0) resourceId else R.drawable.icon

    Box(
        modifier = Modifier
            .padding(all = 8.dp)
            .background(
                CustomColor.DarkPurple.getColor(),
                shape = RoundedCornerShape(8.dp)
            ) // Hintergrundfarbe und abgeflachte Ecken
            .border(
                3.dp,
                CustomColor.Purple.getColor(),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {

        Row {

            // Display the image with the appropriate drawable
            Image(
                painter = painterResource(iconResourceId),
                contentDescription = "Credits",
                modifier = Modifier.size(30.dp)
            )

            // Spacer to add some space between image and text
            Spacer(modifier = Modifier.width(8.dp))

            // Display the credits text
            Text(
                text = "$credits",
                fontSize = 18.sp,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
fun Exit(modifier: Modifier, context: Context) {

    //Exit Box, image and position:
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Image(painter = painterResource(id = R.drawable.exit),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    val intent = Intent(context, MapActivity::class.java)
                    context.startActivity(intent)
                }
                .align(Alignment.TopEnd)
        )
    }
}