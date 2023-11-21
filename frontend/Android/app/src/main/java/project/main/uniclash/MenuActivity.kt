package project.main.uniclash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.ui.theme.UniClashTheme

class MenuActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MenuContent()
                }
            }
        }
    }

    @Composable
    fun MenuContent() {
        var buttonRequest by remember { mutableStateOf(0) }

        Column {
            MenuHeader()
            LazyColumn {
                val categories = listOf(
                    Category("Critters List", R.drawable.prc2duck, 1),
                    Category("Inventar", R.drawable.energydrink, 2),
                    Category("Pokedex", R.drawable.prc2duck, 3),
                    Category(
                        if (MapSettings.MOVINGCAMERA.getMapSetting()) {"Following location arrow off"}
                        else {"Following location arrow on"},
                        R.drawable.location,
                        4
                    ),
                    Category("New Building", R.drawable.store, 5),
                    Category("Battle Activity", R.drawable.arena, 6),
                    Category("Back to map", R.drawable.map, 7) ,
                    Category("Messaging", R.drawable.mutantduck, 8)
                )

                items(categories) { category ->
                    MenuCard(category) {
                        buttonRequest = it
                    }
                }
            }
        }

        if (buttonRequest == 6) {
            startActivity<Battle>()
            buttonRequest = 0
        }

        if (buttonRequest == 7) {
            startActivity<MapActivity>()
            buttonRequest = 0
        }

        if (buttonRequest == 4) {
            MapSettings.MOVINGCAMERA.setMapSetting(!MapSettings.MOVINGCAMERA.getMapSetting())
            startActivity<MenuActivity>()
        }
        if (buttonRequest == 8) {
            startActivity<MapActivity>()
            buttonRequest = 0
        }
    }

    @Composable
    fun MenuHeader() {
        Text(
            text = "Menu",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Text(
            text = "Coins",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }

    @Composable
    fun MenuCard(category: Category, onClick: (Int) -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick(category.id) }
                .shadow(4.dp),
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = category.picture),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(MaterialTheme.shapes.small)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = category.title,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    data class Category(val title: String, val picture: Int, val id: Int)

    @Preview(showBackground = true)
    @Composable
    fun MenuActivityPreview() {
        UniClashTheme {
            MenuContent()
        }
    }

    inline fun <reified T : ComponentActivity> startActivity() {
        val intent = Intent(this, T::class.java)
        startActivity(intent)
    }
}
