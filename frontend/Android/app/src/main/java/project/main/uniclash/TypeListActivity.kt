package project.main.uniclash

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.viewmodels.CritterDexViewModel
import project.main.uniclash.viewmodels.UniClashViewModel
import androidx.compose.foundation.lazy.items
import project.main.uniclash.datatypes.CustomColor
import project.main.uniclash.type.TypeStrength


class TypeListActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
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
                        .background(Color.Black)
                        .verticalScroll(state = ScrollState(1), enabled = true)
                ) {
                    Column {
                        TypeDetail(TypeStrength.NORMAL)
                        TypeDetail(TypeStrength.WATER)
                        TypeDetail(TypeStrength.FIRE)
                        TypeDetail(TypeStrength.ICE)
                        TypeDetail(TypeStrength.DRAGON)
                        TypeDetail(TypeStrength.METAL)
                        TypeDetail(TypeStrength.ELECTRIC)
                        TypeDetail(TypeStrength.STONE)
                    }
                }
            }

            if (exitRequest) {
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
                finish()
                exitRequest = false
            }
        }
    }

    @Composable
    fun MenuHeader() {
        Column {
            Text(
                text = "Type List",
                fontSize = 50.sp, // Adjust the font size as needed
                fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                textAlign = TextAlign.Start,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
            )
            Text(
                text = "Effective: +25% more damage, Weak -25% damage,\nCritter and Attack same type +10% more damage.",
                fontSize = 12.sp, // Adjust the font size as needed
                fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                textAlign = TextAlign.Start,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
            )
        }
    }

    @Composable
    fun TypeDetail(type: TypeStrength) {
        var textColor: Color = Color.White
        var border = 3.dp
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    border,
                    CustomColor.Purple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                )

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                val context = LocalContext.current
                val name: String = type.toString().lowercase()
                val resourceId =
                    context.resources.getIdentifier(name, "drawable", context.packageName)
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
                        .size(70.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = type.toString().uppercase(),
                        fontSize = 18.sp,
                        color = textColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Effective: ${type.effective}\nWeak: ${type.weak}",
                        fontSize = 12.sp,
                        color = textColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}