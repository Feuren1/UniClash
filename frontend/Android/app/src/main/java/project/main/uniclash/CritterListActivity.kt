package project.main.uniclash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.UniClashViewModel


class CritterListActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var myCritters : List<CritterUsable?> = MyCritters(uniClashViewModel)
            //var myCritters = CritterList()
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                     .verticalScroll(rememberScrollState())
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.exit),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp) // Passe die Größe des Fotos nach Bedarf an
                            .clickable {
                                exitRequest = true
                            }
                            .align(Alignment.TopEnd) // Das Foto oben rechts ausrichten
                    )
                    Column {
                        MenuHeader()
                        for (critter in myCritters) {
                            CritterDetail(critter)
                        }

                        }
                    }
                }
            if(exitRequest) {
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
                exitRequest = false
            }
            }
        }

    @Composable
    fun MenuHeader() {
        Text(
            text = "Critters",
            fontSize = 50.sp, // Adjust the font size as needed
            fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
        )
    }

    @Composable
    fun CritterDetail(critter: CritterUsable?) {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // Füllt die Breite bis zum rechten Bildschirmrand aus
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp)) // Hintergrundfarbe und abgeflachte Ecken
                .clickable { }
        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Image(
                    painter = painterResource(CritterPic.MUSK.searchDrawable("${critter?.name}")),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = critter!!.name,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Level: ${critter?.level}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }


    val uniClashViewModel: UniClashViewModel by viewModels(factoryProducer = {
        UniClashViewModel.provideFactory(CritterService.getInstance(this))
    })

    @Composable
    fun MyCritters(uniClashViewModel: UniClashViewModel):List<CritterUsable?> {
        val uniClashUiStateCritterUsables by uniClashViewModel.critterUsables.collectAsState()
        uniClashViewModel.loadCritterUsables(1)
        var critterUsables : List<CritterUsable?> = uniClashUiStateCritterUsables.critterUsables
        return critterUsables
    }

    val attack1 = Attack(1, "Tackle", 1)
    val attack2 = Attack(2, "Scratch",2)

    fun CritterList() : ArrayList<CritterUsable>{
        val wildEncunterList = ArrayList<CritterUsable>()
        var i = 7
        while (i > 0){
            wildEncunterList.add(CritterUsable(1,"STUDENTASSISTANCE", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"STUDENTASSISTANCE", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"STUDENTASSISTANCE", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"DEMOMUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"DEMOMUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"DEMOMUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"DEMOMUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"DEMOMUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MOCKITO", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MOCKITO", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"QUIZIZZDRAGON", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"QUIZIZZDRAGON", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"QUIZIZZDRAGON", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"QUIZIZZDRAGON", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"FONTYS", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"LINUXPINGIUN", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"LINUXPINGIUN", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"LINUXPINGIUN", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFETURTLE", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFETURTLE", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFETURTLE", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"COOLDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"COOLDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"COOLDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"COOLDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"COOLDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"BORZOI", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"BORZOI", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"BORZOI", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"BORZOI", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PIKATCHU", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PIKATCHU", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PIKATCHU", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PIKATCHU", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"NUTCRACKER", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"NUTCRACKER", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"NUTCRACKER", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"NUTCRACKER", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"NUTCRACKER", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MATRYOSHKA", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MATRYOSHKA", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MATRYOSHKA", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MATRYOSHKA", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MUTANTDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"CROCODILEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"CROCODILEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"CROCODILEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"EGGGIVINGWOOLMILKPIG", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"EGGGIVINGWOOLMILKPIG", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"EGGGIVINGWOOLMILKPIG", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"EGGGIVINGWOOLMILKPIG", 1, 1, 1, 1,listOf(attack1, attack2)))

            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PRC2DUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"DEMOMUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"DEMOMUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"DEMOMUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"DEMOMUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"DEMOMUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MUSK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MOCKITO", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MOCKITO", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"QUIZIZZDRAGON", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"QUIZIZZDRAGON", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"QUIZIZZDRAGON", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"QUIZIZZDRAGON", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"LINUXPINGIUN", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"LINUXPINGIUN", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"LINUXPINGIUN", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFETURTLE", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFETURTLE", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"KNIFETURTLE", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"COOLDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"COOLDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"COOLDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"COOLDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"COOLDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"BORZOI", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"BORZOI", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"BORZOI", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"BORZOI", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PIKATCHU", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PIKATCHU", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PIKATCHU", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"PIKATCHU", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"NUTCRACKER", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"NUTCRACKER", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"NUTCRACKER", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"NUTCRACKER", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"NUTCRACKER", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MATRYOSHKA", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MATRYOSHKA", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MATRYOSHKA", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MATRYOSHKA", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"MUTANTDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"CROCODILEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"CROCODILEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"CROCODILEDUCK", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"EGGGIVINGWOOLMILKPIG", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"EGGGIVINGWOOLMILKPIG", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"EGGGIVINGWOOLMILKPIG", 1, 1, 1, 1,listOf(attack1, attack2)))
            wildEncunterList.add(CritterUsable(1,"EGGGIVINGWOOLMILKPIG", 1, 1, 1, 1,listOf(attack1, attack2)))
            i--
        }
        return wildEncunterList;
    }
}