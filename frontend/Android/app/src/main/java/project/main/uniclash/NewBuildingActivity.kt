package project.main.uniclash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.datatypes.ActivitySaver
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.ui.theme.UniClashTheme


enum class BuildingType(){
    ARENA,
    STUDENTHUB;
}

class NewBuildingActivity : ComponentActivity() {
    private var title by mutableStateOf("")
    private var description by mutableStateOf("")
    private var building by mutableStateOf(BuildingType.ARENA)

    private var exitRequest by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
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
                        .background(Color.White) // Hier wird der Hintergrund weiß gemacht
                ) {
                    Box(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column {
                            TitleAndDescription()
                            SelectBuilding()
                            //photo
                            //verify
                        }
                    }
                }
            }
            if (exitRequest) {
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
                exitRequest = false
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
            modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TitleAndDescription() {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth()
                .background(
                    Color.LightGray,
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
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Add an input box (TextField) here
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
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
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Description:",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Add an input box (TextField) here
                            OutlinedTextField(
                                value = description,
                                onValueChange = { title = it },
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
                                    .padding(8.dp)
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
                    Color.LightGray,
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
                                color = MaterialTheme.colorScheme.secondary,
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
                                        .clickable { building = BuildingType.ARENA }
                                        .offset(x = (35).dp)
                                )

                                Image(
                                    painter = if(building== BuildingType.STUDENTHUB){painterResource(id = R.drawable.studenthubconfirm)} else {painterResource(id = R.drawable.store)},
                                    contentDescription = "studenthub", // Add appropriate content description
                                    modifier = Modifier
                                        .size(175.dp) // Adjust the size as needed
                                        .padding(horizontal = 8.dp)
                                        .clickable { building = BuildingType.STUDENTHUB }
                                        .offset(x = (35).dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}
