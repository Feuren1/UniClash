package project.main.uniclash

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
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


class CritterListActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)
    private var sorter by mutableStateOf(CurrentSort.Sorter.getSort())

    val uniClashViewModel: UniClashViewModel by viewModels(factoryProducer = {
        UniClashViewModel.provideFactory(CritterService.getInstance(this), Application())
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val uniClashUiStateCritterUsables by uniClashViewModel.critterUsables.collectAsState()
            val myCritters = uniClashUiStateCritterUsables.critterUsables
            var isLoading by rememberSaveable { mutableStateOf(true) }
            isLoading = uniClashUiStateCritterUsables.isLoading

            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(16.dp)
                ) {
                    MenuHeader(myCritters.size)
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
                ) {
                            UsableList(uniClashViewModel,isLoading, sorter)
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
    fun UsableList(uniClashViewModel: UniClashViewModel, isLoading: Boolean, sorter : Sort) {
        val uniClashUiStateCritterUsables by uniClashViewModel.critterUsables.collectAsState()
        println(uniClashUiStateCritterUsables.critterUsables.toString())

        var sortedCritters = uniClashUiStateCritterUsables.critterUsables.sortedWith(compareBy{ it?.critterId })
        if(sorter == Sort.ID) sortedCritters = uniClashUiStateCritterUsables.critterUsables.sortedWith(compareBy{ it?.critterId })
        if(sorter == Sort.IDReversed) sortedCritters = uniClashUiStateCritterUsables.critterUsables.sortedWith(compareBy{ it?.critterId }).reversed()
        if(sorter == Sort.Name) sortedCritters = uniClashUiStateCritterUsables.critterUsables.sortedWith(compareBy({ it?.name }, { it?.critterId }))
        if(sorter == Sort.Level) sortedCritters = uniClashUiStateCritterUsables.critterUsables.sortedWith(compareBy({ it?.level }, { it?.critterId })).reversed()
        if(sorter == Sort.LevelReversed) sortedCritters = uniClashUiStateCritterUsables.critterUsables.sortedWith(compareBy({ it?.level }, { it?.critterId }))
        if(sorter == Sort.Speed) sortedCritters = uniClashUiStateCritterUsables.critterUsables.sortedWith(compareBy({ it?.spd }, { it?.critterId })).reversed()
        if(sorter == Sort.Atk) sortedCritters = uniClashUiStateCritterUsables.critterUsables.sortedWith(compareBy({ it?.atk }, { it?.critterId })).reversed()
        if(sorter == Sort.Def) sortedCritters = uniClashUiStateCritterUsables.critterUsables.sortedWith(compareBy({ it?.def }, { it?.critterId })).reversed()
        if(sorter == Sort.Hp) sortedCritters = uniClashUiStateCritterUsables.critterUsables.sortedWith(compareBy({ it?.hp }, { it?.critterId })).reversed()

        if (isLoading) {
            LoadingCircle(Modifier)
        } else {
            LazyColumn(modifier = Modifier) {
                items(items = sortedCritters, key = { critter -> critter!!.critterId }) { critter ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        CritterDetail(critter)
                    }
                }
            }
        }
    }




    @Composable
    fun MenuHeader(size : Int) {
        Column {
            Text(
                text = "Critters",
                fontSize = 50.sp, // Adjust the font size as needed
                fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                textAlign = TextAlign.Start,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
            )
            Text(
                text = "$size/200 Critters${if(size>= 200){"\nYou reached the max amount of critters.\nNew captured critters are not added to your team!!!"}else{""}}",
                fontSize = 12.sp, // Adjust the font size as needed
                color = Color.White,
                fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                textAlign = TextAlign.Start,
            )
            Box{
                Row{
                    Text(
                        text = "Sorters: ",
                        fontSize = 12.sp, // Adjust the font size as needed
                        fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                        textAlign = TextAlign.Start,
                        color = Color.White,
                    )
                    Text(
                        text = "ID ",
                        color = Color.LightGray,
                        fontSize = 12.sp, // Adjust the font size as needed
                        fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                        textAlign = TextAlign.Start,
                        modifier = Modifier.clickable {
                            sorter = Sort.ID
                            CurrentSort.Sorter.setSort(Sort.ID)
                        }
                    )
                    Text(
                        text = "IDReversed ",
                        color = Color.LightGray,
                        fontSize = 12.sp, // Adjust the font size as needed
                        fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                        textAlign = TextAlign.Start,
                        modifier = Modifier.clickable {
                            sorter = Sort.IDReversed
                            CurrentSort.Sorter.setSort(Sort.IDReversed)
                        }
                    )
                    Text(
                        text = "Name ",
                        color = Color.LightGray,
                        fontSize = 12.sp, // Adjust the font size as needed
                        fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                        textAlign = TextAlign.Start,
                        modifier = Modifier.clickable {
                            sorter = Sort.Name
                            CurrentSort.Sorter.setSort(Sort.Name)
                        }
                    )
                    Text(
                        text = "Level ",
                        color = Color.LightGray,
                        fontSize = 12.sp, // Adjust the font size as needed
                        fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                        textAlign = TextAlign.Start,
                        modifier = Modifier.clickable {
                            sorter = Sort.Level
                            CurrentSort.Sorter.setSort(Sort.Level)
                        }
                    )
                    Text(
                        text = "LevelReversed ",
                        color = Color.LightGray,
                        fontSize = 12.sp, // Adjust the font size as needed
                        fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                        textAlign = TextAlign.Start,
                        modifier = Modifier.clickable {
                            sorter = Sort.LevelReversed
                            CurrentSort.Sorter.setSort(Sort.LevelReversed)
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun CritterDetail(critter: CritterUsable?) {
        var textColor : Color = Color.White
        var selectedText = ""
        var border = 3.dp
        if(uniClashViewModel.checkIfSelectedCritter(critter!!.critterId)){
            selectedText = "| SELECTED CRITTER FOR THE FIGHT"
            border = 6.dp
        }
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
                .clickable {
                    val intent = Intent(this, CritterProfileActivity::class.java)
                    val b = Bundle()
                    b.putInt("critterId", critter!!.critterId)
                    b.putString("type", critter!!.type)
                    intent.putExtras(b)
                    startActivity(intent)
                    finish()
                }

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                val context = LocalContext.current
                val name: String = critter?.name!!.lowercase()
                val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
                Image(
                    painter = painterResource(if(resourceId > 0){resourceId}else{R.drawable.icon}),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = critter!!.name,
                        fontSize = 18.sp,
                        color = textColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Level: ${critter?.level} $selectedText",
                        fontSize = 12.sp,
                        color = textColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(
                            id = when (critter.type) {
                                "DRAGON" -> {
                                    R.drawable.dragon
                                }

                                "WATER" -> {
                                    R.drawable.water
                                }

                                "ELECTRIC" -> {
                                    R.drawable.electric
                                }

                                "FIRE" -> {
                                    R.drawable.fire
                                }

                                "STONE" -> {
                                    R.drawable.stone
                                }

                                "ICE" -> {
                                    R.drawable.ice
                                }

                                "METAL" -> {
                                    R.drawable.metal
                                }

                                else -> {
                                    R.drawable.normal
                                }
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .offset(y = 15.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

enum class Sort{
    ID,
    IDReversed,
    Name,
    Level,
    LevelReversed,
    Speed,
    Hp,
    Def,
    Atk,
}

enum class CurrentSort(private var sorter : Sort){
    Sorter(Sort.ID);

    fun getSort():Sort{
        return sorter
    }

    fun setSort(setSorter : Sort){
        sorter = setSorter
    }
}